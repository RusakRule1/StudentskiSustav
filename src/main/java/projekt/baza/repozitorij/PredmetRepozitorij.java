package projekt.baza.repozitorij;

import jakarta.persistence.EntityManager;
import projekt.baza.dao.OpciDAO;
import projekt.model.Predmet;
import projekt.upravitelj.UpraviteljBaze;

import java.util.List;

public class PredmetRepozitorij {
    private final OpciDAO<Predmet> predmetDAO;

    public PredmetRepozitorij() {
        this.predmetDAO = new OpciDAO<>(Predmet.class);
    }

    public List<Predmet> pronadjiSve() {
        return predmetDAO.vratiSve();
    }

    public List<Predmet> pronadiPredmeteBezProfesora() {
        EntityManager em = UpraviteljBaze.dohvatiEntityManager();
        try {
            List<Predmet> lista = em.createQuery(
                    "SELECT p FROM Predmet p WHERE p.profesor IS NULL",
                    Predmet.class
            ).getResultList();
            return lista;
        } finally {
            em.close();
        }
    }
}
