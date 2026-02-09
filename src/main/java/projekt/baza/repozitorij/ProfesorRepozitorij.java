package projekt.baza.repozitorij;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;
import projekt.model.Predmet;
import projekt.model.Profesor;
import projekt.upravitelj.UpraviteljBaze;

import java.util.List;

public class ProfesorRepozitorij {

    public ProfesorRepozitorij() {
    }

    public void spremi(Profesor profesor, List<Predmet> predmeti) {
        EntityManager em = UpraviteljBaze.dohvatiEntityManager();
        EntityTransaction tx = em.getTransaction();

        try {
            tx.begin();
            em.persist(profesor);
            for (Predmet p : predmeti) {
                Predmet managed = em.merge(p);
                profesor.dodajPredmet(managed);
            }
            tx.commit();
        } catch (Exception e) {
            if (tx.isActive()) tx.rollback();
            throw e;
        } finally {
            em.close();
        }
    }
}
