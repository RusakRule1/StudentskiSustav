package projekt.util;

import javax.crypto.Cipher;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;

public class RSA {

    private static final String ALGORITAM = "RSA";
    private static final String DIREKTORIJ_PODACI = "podaci";
    private static final String DATOTEKA_JAVNOG_KLJUCA = "rsa_javni.key";
    private static final String DATOTEKA_PRIVATNOG_KLJUCA = "rsa_privatni.key";

    private static RSA instanca;
    private PublicKey javniKljuc;
    private PrivateKey privatniKljuc;

    private RSA() {
        try {
            ucitajKljuceve();
        } catch (Exception e) {
            throw new RuntimeException("Greška pri učitavanju RSA ključeva: " + e.getMessage(), e);
        }
    }

    public static synchronized RSA getInstance() {
        if (instanca == null) {
            instanca = new RSA();
        }
        return instanca;
    }

    private void ucitajKljuceve() throws Exception {
        Path putanjaJavnogKljuca = Paths.get(DIREKTORIJ_PODACI, DATOTEKA_JAVNOG_KLJUCA);
        Path putanjaPrivatnogKljuca = Paths.get(DIREKTORIJ_PODACI, DATOTEKA_PRIVATNOG_KLJUCA);

        if (!Files.exists(putanjaJavnogKljuca)) {
            throw new RuntimeException("Datoteka s RSA javnim ključem ne postoji: " + putanjaJavnogKljuca);
        }
        if (!Files.exists(putanjaPrivatnogKljuca)) {
            throw new RuntimeException("Datoteka s RSA privatnim ključem ne postoji: " + putanjaPrivatnogKljuca);
        }

        String javniKljucString = Files.readString(putanjaJavnogKljuca).trim();
        byte[] bajtoviJavnogKljuca = Base64.getDecoder().decode(javniKljucString);
        X509EncodedKeySpec specifikacijaJavnogKljuca = new X509EncodedKeySpec(bajtoviJavnogKljuca);
        KeyFactory tvornicaKljuceva = KeyFactory.getInstance(ALGORITAM);
        this.javniKljuc = tvornicaKljuceva.generatePublic(specifikacijaJavnogKljuca);

        String privatniKljucString = Files.readString(putanjaPrivatnogKljuca).trim();
        byte[] bajtoviPrivatnogKljuca = Base64.getDecoder().decode(privatniKljucString);
        PKCS8EncodedKeySpec specifikacijaPrivatnogKljuca = new PKCS8EncodedKeySpec(bajtoviPrivatnogKljuca);
        this.privatniKljuc = tvornicaKljuceva.generatePrivate(specifikacijaPrivatnogKljuca);
    }

    public String sifriraj(String tekst) {
        if (tekst == null || tekst.trim().isEmpty()) {
            return tekst;
        }
        try {
            Cipher sifrarnik = Cipher.getInstance(ALGORITAM);
            sifrarnik.init(Cipher.ENCRYPT_MODE, javniKljuc);

            byte[] sifriraniBajtovi = sifrarnik.doFinal(tekst.getBytes(StandardCharsets.UTF_8));
            return Base64.getEncoder().encodeToString(sifriraniBajtovi);

        } catch (Exception e) {
            throw new RuntimeException("Greška pri RSA šifriranju: " + tekst, e);
        }
    }

    public String desifriraj(String sifriraniTekst) {
        if (sifriraniTekst == null || sifriraniTekst.trim().isEmpty()) {
            return sifriraniTekst;
        }
        try {
            Cipher sifrarnik = Cipher.getInstance(ALGORITAM);
            sifrarnik.init(Cipher.DECRYPT_MODE, privatniKljuc);

            byte[] originalniBajtovi = sifrarnik.doFinal(
                    Base64.getDecoder().decode(sifriraniTekst)
            );
            return new String(originalniBajtovi, StandardCharsets.UTF_8);

        } catch (Exception e) {
            String porukaGreske = "Greška pri RSA dešifriranju";
            if (e instanceof javax.crypto.BadPaddingException) {
                porukaGreske += " (pogrešan ključ ili oštećeni podaci)";
            }
            throw new RuntimeException(porukaGreske, e);
        }
    }
}