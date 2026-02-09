package projekt.util;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

public class Hash {

    private static final String BAZA_ZA_PAPAR = "studentskiSustav";
    private static final String BAZA_ZA_SOL = "OvoJeSol";

    public static String hashirajLozinku(String lozinka, String email) {
        String sol = generirajSol(email);
        String papar = pronadiIspravanPapar(lozinka, sol, email);
        String kombinacija = lozinka + sol + papar;
        return hashirajSHA256(kombinacija);
    }

    public static boolean provjeriLozinku(String unesenaLozinka, String spremljeniHash, String email) {
        String sol = generirajSol(email);
        for (int i = 0; i < 100; i++) {
            int indeksPapra = (Math.abs(email.hashCode() % 100) + i) % 100;
            String papar = BAZA_ZA_PAPAR + indeksPapra;
            String kombinacija = unesenaLozinka + sol + papar;
            String hash = hashirajSHA256(kombinacija);
            if (spremljeniHash.equals(hash)) {
                return true;
            }
        }
        return false;
    }

    private static String generirajSol(String email) {
        String sol = email + BAZA_ZA_SOL;
        return Base64.getEncoder().encodeToString(sol.getBytes());
    }

    private static String pronadiIspravanPapar(String lozinka, String sol, String email) {
        int pocetniIndeks = Math.abs(email.hashCode() % 100);

        for (int i = 0; i < 100; i++) {
            int indeksPapra = (pocetniIndeks + i) % 100;
            String papar = BAZA_ZA_PAPAR + indeksPapra;
            String kombinacija = lozinka + sol + papar;
            String hash = hashirajSHA256(kombinacija);
            char prviZnak = hash.charAt(0);
            if (Character.isLetter(prviZnak)) {
                return papar;
            }
        }
        return BAZA_ZA_PAPAR + pocetniIndeks;
    }

    private static String hashirajSHA256(String tekst) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hashBajtovi = digest.digest(tekst.getBytes());

            StringBuilder hexString = new StringBuilder();
            for (byte bajt : hashBajtovi) {
                String hex = Integer.toHexString(0xff & bajt);
                if (hex.length() == 1) {
                    hexString.append('0');
                }
                hexString.append(hex);
            }
            return hexString.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("SHA-256 algoritam nije dostupan", e);
        }
    }
}