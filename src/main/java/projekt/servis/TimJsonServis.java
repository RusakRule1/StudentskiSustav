package projekt.servis;

import projekt.model.TimJson;
import projekt.repozitorij.TimJsonRepozitorij;

import java.util.List;

public class TimJsonServis {

    private final TimJsonRepozitorij repozitorij;

    public TimJsonServis() {
        this.repozitorij = new TimJsonRepozitorij();
    }

    public List<TimJson> dohvatiSveTimove() {
        return repozitorij.ucitaj();
    }

    public boolean spremiTim(TimJson tim) {
        try {
            List<TimJson> timovi = repozitorij.ucitaj();
            timovi.add(tim);
            repozitorij.spremi(timovi);
            return true;
        } catch (Exception e) {
            System.err.println("Greška pri spremanju tima: " + e.getMessage());
            return false;
        }
    }

    public boolean azurirajTim(TimJson azuriraniTim) {
        try {
            List<TimJson> timovi = repozitorij.ucitaj();
            boolean pronadjen = timovi.stream()
                    .anyMatch(t -> t.getId().equals(azuriraniTim.getId()));
            if (!pronadjen) return false;
            timovi.replaceAll(t -> t.getId().equals(azuriraniTim.getId()) ? azuriraniTim : t);
            repozitorij.spremi(timovi);
            return true;
        } catch (Exception e) {
            System.err.println("Greška pri ažuriranju tima: " + e.getMessage());
            return false;
        }
    }

    public boolean obrisiTim(String timId) {
        try {
            List<TimJson> timovi = repozitorij.ucitaj();
            boolean uklonjen = timovi.removeIf(tim -> tim.getId().equals(timId));
            if (uklonjen) repozitorij.spremi(timovi);
            return uklonjen;
        } catch (Exception e) {
            System.err.println("Greška pri brisanju tima: " + e.getMessage());
            return false;
        }
    }
}
