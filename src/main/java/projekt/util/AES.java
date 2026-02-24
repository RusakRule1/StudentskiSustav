package projekt.util;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.SecureRandom;
import java.util.Base64;

public class AES {

    private static final String ALGORITAM = "AES";
    private static final String TRANSFORMACIJA = "AES/CBC/PKCS5Padding";
    private static final Path KLJUC_PUTANJA = Paths.get(
            System.getProperty("user.home"),
            ".studentski-sustav",
            "podaci",
            "aes_kljuc.key"
    );
    private static final SecretKey tajniKljuc;

    static {
        try {
            tajniKljuc = ucitajKljuc();
        } catch (Exception e) {
            throw new ExceptionInInitializerError("Greška pri učitavanju AES ključa: " + e.getMessage());
        }
    }

    private static SecretKey ucitajKljuc() throws Exception {
        if (!Files.exists(KLJUC_PUTANJA)) {
            throw new RuntimeException("AES ključ datoteka ne postoji: " + KLJUC_PUTANJA);
        }
        String kljucBase64 = Files.readString(KLJUC_PUTANJA).trim();
        byte[] kljucBytes = Base64.getDecoder().decode(kljucBase64);
        return new SecretKeySpec(kljucBytes, ALGORITAM);
    }

    public static String sifriraj(String tekst) {
        if (tekst == null || tekst.trim().isEmpty()) return tekst;
        try {
            byte[] iv = new byte[16];
            new SecureRandom().nextBytes(iv);
            IvParameterSpec ivSpec = new IvParameterSpec(iv);

            Cipher cipher = Cipher.getInstance(TRANSFORMACIJA);
            cipher.init(Cipher.ENCRYPT_MODE, tajniKljuc, ivSpec);
            byte[] sifriraniBajtovi = cipher.doFinal(tekst.getBytes(StandardCharsets.UTF_8));

            byte[] rezultat = new byte[iv.length + sifriraniBajtovi.length];
            System.arraycopy(iv, 0, rezultat, 0, iv.length);
            System.arraycopy(sifriraniBajtovi, 0, rezultat, iv.length, sifriraniBajtovi.length);

            return Base64.getEncoder().encodeToString(rezultat);
        } catch (Exception e) {
            throw new RuntimeException("Greška pri šifriranju: " + e.getMessage(), e);
        }
    }

    public static String desifriraj(String sifriraniTekst) {
        if (sifriraniTekst == null || sifriraniTekst.trim().isEmpty()) return sifriraniTekst;
        try {
            byte[] podatci = Base64.getDecoder().decode(sifriraniTekst);

            byte[] iv = new byte[16];
            byte[] sifriraniBajtovi = new byte[podatci.length - 16];
            System.arraycopy(podatci, 0, iv, 0, 16);
            System.arraycopy(podatci, 16, sifriraniBajtovi, 0, sifriraniBajtovi.length);

            Cipher cipher = Cipher.getInstance(TRANSFORMACIJA);
            cipher.init(Cipher.DECRYPT_MODE, tajniKljuc, new IvParameterSpec(iv));
            return new String(cipher.doFinal(sifriraniBajtovi), StandardCharsets.UTF_8);
        } catch (Exception e) {
            throw new RuntimeException("Greška pri dešifriranju: " + e.getMessage(), e);
        }
    }
}
