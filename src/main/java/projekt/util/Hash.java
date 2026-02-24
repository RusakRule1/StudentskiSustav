package projekt.util;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

public class Hash {

    private static final String ALGORITAM = "SHA-256";
    private static final String PAPAR_PREFIKS = "studentskiSustav";
    private static final String SOL_SUFIKS = "OvoJeSol";
    private static final int BROJ_PAPARA = 100;

    private Hash() {
    }

    public static String hashirajLozinku(String lozinka, String email) {
        String sol = generirajSol(email);
        String papar = generirajPapar(email);
        return sha256(lozinka + sol + papar);
    }

    public static boolean provjeriLozinku(String unesenaLozinka, String spremljeniHash, String email) {
        String sol = generirajSol(email);
        for (int i = 0; i < BROJ_PAPARA; i++) {
            String papar = PAPAR_PREFIKS + i;
            if (sha256(unesenaLozinka + sol + papar).equals(spremljeniHash)) {
                return true;
            }
        }
        return false;
    }

    private static String generirajSol(String email) {
        return Base64.getEncoder().encodeToString(
                (email + SOL_SUFIKS).getBytes(StandardCharsets.UTF_8)
        );
    }

    private static String generirajPapar(String email) {
        int indeks = ((email.hashCode() % BROJ_PAPARA) + BROJ_PAPARA) % BROJ_PAPARA;
        return PAPAR_PREFIKS + indeks;
    }

    private static String sha256(String tekst) {
        try {
            MessageDigest digest = MessageDigest.getInstance(ALGORITAM);
            byte[] hash = digest.digest(tekst.getBytes(StandardCharsets.UTF_8));
            StringBuilder hex = new StringBuilder(hash.length * 2);
            for (byte b : hash) {
                hex.append(String.format("%02x", 0xff & b));
            }
            return hex.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Algoritam " + ALGORITAM + " nije dostupan", e);
        }
    }
}