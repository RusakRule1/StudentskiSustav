package projekt.servis;

import projekt.model.Predmet;
import projekt.repozitorij.PredmetRepozitorij;

import java.util.List;

public class PredmetServis {
    private final PredmetRepozitorij predmetRepozitorij = new PredmetRepozitorij();

    public List<Predmet> pronadjiPredmeteBezProfesora() {
        return predmetRepozitorij.pronadjiPredmeteBezProfesora();
    }

    public List<Predmet> pronadjiPredmeteProfesora(Integer profesorId) {
        return predmetRepozitorij.pronadjiPredmeteProfesora(profesorId);
    }
}
