package projekt.dao.impl;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;
import projekt.dao.IKorisnikDAO;
import projekt.model.Korisnik;
import projekt.util.HibernateUtil;

public class KorisnikDAO implements IKorisnikDAO {

    @Override
    public void spremiKorisnika(Korisnik korisnik) {
        EntityManager em = HibernateUtil.dohvatiEntityManager();
        EntityTransaction transakcija = em.getTransaction();

        try {
            transakcija.begin();
            em.persist(korisnik);
            transakcija.commit();
            System.out.println("Korisnik '" + korisnik.getEmail() + "' spremljen u bazu!");
        } catch (Exception e) {
            if (transakcija.isActive()) {
                transakcija.rollback();
            }
            System.err.println("Greška pri spremanju korisnika: " + e.getMessage());
            throw e;
        } finally {
            em.close();
        }
    }
}
