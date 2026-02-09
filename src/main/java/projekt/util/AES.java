package projekt.util;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Base64;

public class AES {

    private static final String ALGORITAM = "AES";
    private static final String TRANSFORMACIJA = "AES/ECB/PKCS5Padding";
    private static final String PODACI_DIR = "podaci";
    private static final String AES_KLJUC_DATOTEKA = "aes_kljuc.key";
    private static final SecretKey tajniKljuc;

    static {
        try {
            tajniKljuc = ucitajKljuc();
        } catch (Exception e) {
            throw new ExceptionInInitializerError("Greška pri učitavanju AES ključa: " + e.getMessage());
        }
    }

    private static SecretKey ucitajKljuc() throws Exception {
        Path kljucPath = Paths.get(PODACI_DIR, AES_KLJUC_DATOTEKA);

        if (!Files.exists(kljucPath)) {
            throw new RuntimeException("AES ključ datoteka ne postoji: " + kljucPath);
        }

        String kljucBase64 = Files.readString(kljucPath).trim();
        byte[] kljucBytes = Base64.getDecoder().decode(kljucBase64);
        return new SecretKeySpec(kljucBytes, ALGORITAM);
    }

    public static String sifriraj(String tekst) {
        if (tekst == null || tekst.trim().isEmpty()) {
            return tekst;
        }
        try {
            Cipher cipher = Cipher.getInstance(TRANSFORMACIJA);
            cipher.init(Cipher.ENCRYPT_MODE, tajniKljuc);
            byte[] sifriraniBajtovi = cipher.doFinal(tekst.getBytes(StandardCharsets.UTF_8));
            return Base64.getEncoder().encodeToString(sifriraniBajtovi);

        } catch (Exception e) {
            throw new RuntimeException("Greška pri šifriranju: " + e.getMessage(), e);
        }
    }

    public static String desifriraj(String sifriraniTekst) {
        if (sifriraniTekst == null || sifriraniTekst.trim().isEmpty()) {
            return sifriraniTekst;
        }
        try {
            Cipher cipher = Cipher.getInstance(TRANSFORMACIJA);
            cipher.init(Cipher.DECRYPT_MODE, tajniKljuc);
            byte[] originalniBajtovi = cipher.doFinal(
                    Base64.getDecoder().decode(sifriraniTekst)
            );
            return new String(originalniBajtovi, StandardCharsets.UTF_8);
        } catch (Exception e) {
            return sifriraniTekst;
        }
    }
}