package projekt.servis;

import projekt.baza.repozitorij.ProfesorRepozitorij;
import projekt.model.Predmet;
import projekt.model.Profesor;

import java.util.List;

public class ProfesorServis {

    private final ProfesorRepozitorij profesorRepozitorij = new ProfesorRepozitorij();

    public void spremiProfesoraSPredmetima(Profesor noviProfesor, List<Predmet> predmeti) {
        profesorRepozitorij.spremi(noviProfesor, predmeti);
    }
}
