package projekt.util;

import projekt.adapter.JNIAdapter;
import projekt.upravitelj.Prijevod;

public class Validacija {

    private final JNIAdapter jniAdapter;

    public Validacija() {
        this.jniAdapter = Prijevod.getInstanca().getJniAdapter();
    }

    public boolean validirajEmail(String email) {
        if (email == null || email.trim().isEmpty()) {
            return false;
        }

        try {
            return jniAdapter.validirajEmail(email);
        } catch (Exception e) {
            System.err.println("Greška pri validaciji emaila: " + e.getMessage());
            return false;
        }
    }

    public boolean validirajLozinku(String lozinka) {
        if (lozinka == null || lozinka.trim().isEmpty()) {
            return false;
        }

        try {
            return jniAdapter.validirajLozinku(lozinka);
        } catch (Exception e) {
            System.err.println("Greška pri validaciji lozinke: " + e.getMessage());
            return false;
        }
    }

    public boolean validirajJMBAG(String jmbag) {
        if (jmbag == null || jmbag.trim().isEmpty()) {
            return false;
        }

        try {
            return jniAdapter.validirajJMBAG(jmbag);
        } catch (Exception e) {
            System.err.println("Greška pri validaciji JMBAG-a: " + e.getMessage());
            return false;
        }
    }

    public String validirajEmailSaPorukom(String email) {
        if (email == null || email.trim().isEmpty()) {
            return "greska_email_obavezan";
        }

        if (!validirajEmail(email)) {
            return "greska_email_format";
        }

        return null;
    }

    public String validirajLozinkuSaPorukom(String lozinka) {
        if (lozinka == null || lozinka.trim().isEmpty()) {
            return "greska_lozinka_obavezna";
        }

        if (!validirajLozinku(lozinka)) {
            return "greska_lozinka_format";
        }

        return null;
    }

    public String validirajJMBAGSaPorukom(String jmbag) {
        if (jmbag == null || jmbag.trim().isEmpty()) {
            return "greska_jmbag_obavezan";
        }

        if (!validirajJMBAG(jmbag)) {
            return "greska_jmbag_format";
        }

        return null;
    }
}