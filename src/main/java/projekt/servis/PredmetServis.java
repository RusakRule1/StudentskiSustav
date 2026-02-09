package projekt.servis;

import projekt.baza.repozitorij.PredmetRepozitorij;
import projekt.model.Predmet;

import java.util.List;

public class PredmetServis {
    private final PredmetRepozitorij repo = new PredmetRepozitorij();

    public List<Predmet> dohvatiDostupnePredmete() {
        return repo.pronadiPredmeteBezProfesora();
    }
}
