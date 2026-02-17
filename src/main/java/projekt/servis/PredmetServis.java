package projekt.servis;

import projekt.baza.repozitorij.PredmetRepozitorij;
import projekt.model.Predmet;

import java.util.List;

public class PredmetServis {
    private final PredmetRepozitorij predmetRepozitorij = new PredmetRepozitorij();

    public List<Predmet> dohvatiDostupnePredmete() {
        return predmetRepozitorij.pronadiPredmeteBezProfesora();
    }

    public List<Predmet> dohvatiPredmeteProfesora(Integer profesorId) {
        return predmetRepozitorij.dohvatiPredmeteProfesora(profesorId);
    }

}
