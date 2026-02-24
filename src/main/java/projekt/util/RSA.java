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
    private static final String TRANSFORMACIJA = "RSA/ECB/OAEPWithSHA-256AndMGF1Padding";
    private static final Path PUTANJA_JAVNOG_KLJUCA = Paths.get(
            System.getProperty("user.home"),
            ".studentski-sustav",
            "podaci",
            "rsa_javni.key"
    );
    private static final Path PUTANJA_PRIVATNOG_KLJUCA = Paths.get(
            System.getProperty("user.home"),
            ".studentski-sustav",
            "podaci",
            "rsa_privatni.key"
    );

    private static PublicKey javniKljuc;
    private static PrivateKey privatniKljuc;

    static {
        try {
            ucitajKljuceve();
        } catch (Exception e) {
            throw new ExceptionInInitializerError("Greška pri učitavanju RSA ključa: " + e.getMessage());
        }
    }

    private static void ucitajKljuceve() throws Exception {
        if (!Files.exists(PUTANJA_JAVNOG_KLJUCA)) {
            throw new RuntimeException("Datoteka s RSA javnim ključem ne postoji: " + PUTANJA_JAVNOG_KLJUCA);
        }
        if (!Files.exists(PUTANJA_PRIVATNOG_KLJUCA)) {
            throw new RuntimeException("Datoteka s RSA privatnim ključem ne postoji: " + PUTANJA_PRIVATNOG_KLJUCA);
        }

        KeyFactory tvornicaKljuceva = KeyFactory.getInstance(ALGORITAM);

        String javniKljucString = Files.readString(PUTANJA_JAVNOG_KLJUCA).trim();
        byte[] bajtoviJavnogKljuca = Base64.getDecoder().decode(javniKljucString);
        javniKljuc = tvornicaKljuceva.generatePublic(new X509EncodedKeySpec(bajtoviJavnogKljuca));

        String privatniKljucString = Files.readString(PUTANJA_PRIVATNOG_KLJUCA).trim();
        byte[] bajtoviPrivatnogKljuca = Base64.getDecoder().decode(privatniKljucString);
        privatniKljuc = tvornicaKljuceva.generatePrivate(new PKCS8EncodedKeySpec(bajtoviPrivatnogKljuca));
    }

    public static String sifriraj(String tekst) {
        if (tekst == null || tekst.trim().isEmpty()) return tekst;
        try {
            Cipher sifrarnik = Cipher.getInstance(TRANSFORMACIJA);
            sifrarnik.init(Cipher.ENCRYPT_MODE, javniKljuc);
            byte[] sifriraniBajtovi = sifrarnik.doFinal(tekst.getBytes(StandardCharsets.UTF_8));
            return Base64.getEncoder().encodeToString(sifriraniBajtovi);
        } catch (Exception e) {
            throw new RuntimeException("Greška pri RSA šifriranju: " + e.getMessage(), e);
        }
    }

    public static String desifriraj(String sifriraniTekst) {
        if (sifriraniTekst == null || sifriraniTekst.trim().isEmpty()) return sifriraniTekst;
        try {
            Cipher sifrarnik = Cipher.getInstance(TRANSFORMACIJA);
            sifrarnik.init(Cipher.DECRYPT_MODE, privatniKljuc);
            byte[] originalniBajtovi = sifrarnik.doFinal(
                    Base64.getDecoder().decode(sifriraniTekst)
            );
            return new String(originalniBajtovi, StandardCharsets.UTF_8);
        } catch (Exception e) {
            throw new RuntimeException("Greška pri dešifriranju: " + e.getMessage(), e);
        }
    }
}
