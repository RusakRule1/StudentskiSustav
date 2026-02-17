package projekt.baza.repozitorij;

import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
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

    public List<Predmet> dohvatiPredmeteProfesora(Integer profesorId) {
        EntityManager em = UpraviteljBaze.dohvatiEntityManager();
        try {
            String jpql = "SELECT p FROM Predmet p " +
                    "WHERE p.profesor.id = :profesorId";

            TypedQuery<Predmet> query = em.createQuery(jpql, Predmet.class);
            query.setParameter("profesorId", profesorId);

            return query.getResultList();
        } finally {
            em.close();
        }
    }
}
