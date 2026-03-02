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
        return predmetDAO.pronadjiListu(
                "SELECT p FROM Predmet p LEFT JOIN FETCH p.profesor",
                Predmet.class);
    }

    public void obrisi(Predmet predmet) {
        predmetDAO.obrisi(predmet);
    }

    public void obrisiPoId(Integer id) {
        predmetDAO.obrisiPoId(id);
    }

    public void spremi(Predmet predmet) {
        predmetDAO.spremi(predmet);
    }

    public void azuriraj(Predmet predmet) {
        predmetDAO.azuriraj(predmet);
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
