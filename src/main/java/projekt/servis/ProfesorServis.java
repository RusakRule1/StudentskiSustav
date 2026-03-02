package projekt.servis;

import projekt.model.Predmet;
import projekt.model.Profesor;
import projekt.repozitorij.ProfesorRepozitorij;

import java.util.List;

public class ProfesorServis {

    private final ProfesorRepozitorij profesorRepozitorij = new ProfesorRepozitorij();
    
    public List<Profesor> vratiSve() {
        return profesorRepozitorij.vratiSve();
    }

    public void spremiProfesoraSPredmetima(Profesor noviProfesor, List<Predmet> predmeti) {
        profesorRepozitorij.spremi(noviProfesor, predmeti);
    }
}
