package projekt.util;

import projekt.adapter.JNIAdapter;

public class Validacija {

    private static final JNIAdapter jniAdapter = new JNIAdapter();

    public Validacija() {
    }

    public static String validirajEmail(String email) {
        if (email == null || email.trim().isEmpty()) {
            return "greska_email_obavezan";
        }

        try {
            if (!jniAdapter.validirajEmail(email)) {
                return "greska_email_format";
            }
            return null;
        } catch (Exception e) {
            System.err.println("Greška pri validaciji emaila: " + e.getMessage());
            return "greska_validacija_email";
        }
    }

    public static String validirajLozinku(String lozinka) {
        if (lozinka == null || lozinka.trim().isEmpty()) {
            return "greska_lozinka_obavezna";
        }

        try {
            if (!jniAdapter.validirajLozinku(lozinka)) {
                return "greska_lozinka_format";
            }
            return null;
        } catch (Exception e) {
            System.err.println("Greška pri validaciji lozinke: " + e.getMessage());
            return "greska_validacija_lozinka";
        }
    }

    public static String validirajJMBAG(String jmbag) {
        if (jmbag == null || jmbag.trim().isEmpty()) {
            return "greska_jmbag_obavezan";
        }

        try {
            if (!jniAdapter.validirajJMBAG(jmbag)) {
                return "greska_jmbag_format";
            }
            return null;
        } catch (Exception e) {
            System.err.println("Greška pri validaciji JMBAG-a: " + e.getMessage());
            return "greska_validacija_jmbag";
        }
    }

    public static String validirajIme(String ime) {
        if (ime == null || ime.isBlank()) {
            return "greska_ime_obavezno";
        }
        if (!ime.matches("[A-ZČĆŽŠĐ][a-zčćžšđ]+")) {
            return "greska_ime_format";
        }
        return null;
    }

    public static String validirajPrezime(String prezime) {
        if (prezime == null || prezime.isBlank()) {
            return "greska_prezime_obavezno";
        }
        if (!prezime.matches("[A-ZČĆŽŠĐ][a-zčćžšđ]+")) {
            return "greska_prezime_format";
        }
        return null;
    }
}