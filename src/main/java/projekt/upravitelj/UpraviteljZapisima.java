package projekt.upravitelj;

import projekt.model.Zapis;
import projekt.model.ZapisAkcija;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.List;

public class UpraviteljZapisima {

    private static final Path DIREKTORIJ_ZAPISA = Paths.get(
            System.getProperty("user.home"),
            ".studentski-sustav",
            "zapisi"
    );
    private static final Path ZAPIS_PATH = DIREKTORIJ_ZAPISA.resolve("zapisi.dat");

    private static UpraviteljZapisima instanca;

    private UpraviteljZapisima() {
        try {
            Files.createDirectories(DIREKTORIJ_ZAPISA);
        } catch (IOException e) {
            throw new RuntimeException("Greška pri kreiranju direktorija za zapise", e);
        }
    }

    public static synchronized UpraviteljZapisima getInstanca() {
        if (instanca == null) {
            instanca = new UpraviteljZapisima();
        }
        return instanca;
    }

    public void dodajZapis(Zapis zapis) {
        try (DataOutputStream dos = new DataOutputStream(
                new BufferedOutputStream(
                        new FileOutputStream(ZAPIS_PATH.toFile(), true)))) {

            dos.writeLong(zapis.getVrijeme().toInstant(ZoneOffset.UTC).toEpochMilli());

            byte[] korisnikBytes = zapis.getKorisnik().getBytes(StandardCharsets.UTF_8);
            dos.writeInt(korisnikBytes.length);
            dos.write(korisnikBytes);

            dos.writeInt(zapis.getAkcija().ordinal());

            byte[] detaljiBytes = zapis.getDetalji().getBytes(StandardCharsets.UTF_8);
            dos.writeInt(detaljiBytes.length);
            dos.write(detaljiBytes);

        } catch (IOException e) {
            System.err.println("Greška pri dodavanju zapisa: " + e.getMessage());
        }
    }

    public List<Zapis> ucitajSveZapise() {
        if (!Files.exists(ZAPIS_PATH)) return new ArrayList<>();

        List<Zapis> zapisi = new ArrayList<>();
        try (DataInputStream dis = new DataInputStream(
                new BufferedInputStream(
                        new FileInputStream(ZAPIS_PATH.toFile())))) {
            while (true) {
                try {
                    long epochMilli = dis.readLong();
                    LocalDateTime vrijeme = LocalDateTime.ofInstant(
                            Instant.ofEpochMilli(epochMilli), ZoneOffset.UTC);

                    int korisnikLen = dis.readInt();
                    String korisnik = new String(dis.readNBytes(korisnikLen), StandardCharsets.UTF_8);

                    ZapisAkcija akcija = ZapisAkcija.values()[dis.readInt()];

                    int detaljiLen = dis.readInt();
                    String detalji = new String(dis.readNBytes(detaljiLen), StandardCharsets.UTF_8);

                    zapisi.add(new Zapis(korisnik, akcija, detalji, vrijeme));
                } catch (EOFException e) {
                    break;
                }
            }
        } catch (IOException e) {
            System.err.println("Greška pri čitanju zapisa: " + e.getMessage());
        }
        return zapisi;
    }
}
