package projekt.repozitorij;

import projekt.dao.OpciDAO;
import projekt.model.Predmet;

import java.util.List;

public class PredmetRepozitorij {
    private final OpciDAO<Predmet> predmetDAO;

    public PredmetRepozitorij() {
        this.predmetDAO = new OpciDAO<>(Predmet.class);
    }

    public List<Predmet> vratiSve() {
        return predmetDAO.vratiSve();
    }

    public List<Predmet> pronadjiPredmeteBezProfesora() {
        return predmetDAO.pronadjiListu(
                "SELECT p FROM Predmet p WHERE p.profesor IS NULL",
                Predmet.class);
    }

    public List<Predmet> pronadjiPredmeteProfesora(Integer profesorId) {
        return predmetDAO.pronadjiListu(
                "SELECT p FROM Predmet p WHERE p.profesor.id = :profesorId",
                Predmet.class, "profesorId", profesorId);
    }
}
