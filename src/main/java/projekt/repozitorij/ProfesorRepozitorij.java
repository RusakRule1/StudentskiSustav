package projekt.repozitorij;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;
import projekt.dao.OpciDAO;
import projekt.model.Predmet;
import projekt.model.Profesor;
import projekt.upravitelj.UpraviteljBaze;

import java.util.List;

public class ProfesorRepozitorij {

    private final OpciDAO<Profesor> profesorDAO;

    public ProfesorRepozitorij() {
        this.profesorDAO = new OpciDAO<>(Profesor.class);
    }

    public List<Profesor> vratiSve() {
        return profesorDAO.vratiSve();
    }

    public void azuriraj(Profesor profesor) {
        profesorDAO.azuriraj(profesor);
    }

    public void obrisi(Profesor profesor) {
        profesorDAO.obrisi(profesor);
    }

    public void spremi(Profesor profesor, List<Predmet> predmeti) {
        EntityManager em = UpraviteljBaze.dohvatiEntityManager();
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();
            em.persist(profesor);
            for (Predmet p : predmeti) {
                profesor.dodajPredmet(em.merge(p));
            }
            tx.commit();
        } catch (Exception e) {
            if (tx.isActive()) tx.rollback();
            System.err.println("Greška pri spremanju profesora: " + e.getMessage());
            throw e;
        } finally {
            em.close();
        }
    }
}
