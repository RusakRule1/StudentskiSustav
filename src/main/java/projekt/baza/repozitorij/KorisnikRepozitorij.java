package projekt.baza.repozitorij;

import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import jakarta.persistence.TypedQuery;
import projekt.baza.dao.OpciDAO;
import projekt.model.Korisnik;
import projekt.upravitelj.UpraviteljBaze;

import java.util.List;

public class KorisnikRepozitorij {
    private final OpciDAO<Korisnik> korisnikDAO;

    public KorisnikRepozitorij() {
        this.korisnikDAO = new OpciDAO<>(Korisnik.class);
    }

    public Korisnik pronadjiPoId(Integer id) {
        return korisnikDAO.vratiPoID(id);
    }

    public List<Korisnik> pronadjiSve() {
        return korisnikDAO.vratiSve();
    }

    public void spremi(Korisnik korisnik) {
        korisnikDAO.spremi(korisnik);
    }

    public void azuriraj(Korisnik korisnik) {
        korisnikDAO.azuriraj(korisnik);
    }

    public void obrisi(Korisnik korisnik) {
        korisnikDAO.obrisi(korisnik);
    }

    public void obrisiPoId(Integer id) {
        korisnikDAO.obrisiPoID(id);
    }

    public Korisnik pronadiPoEmailu(String email) {
        EntityManager em = UpraviteljBaze.dohvatiEntityManager();
        try {
            String jpql = "SELECT k FROM Korisnik k WHERE k.email = :email";
            TypedQuery<Korisnik> query = em.createQuery(jpql, Korisnik.class);
            query.setParameter("email", email);
            return query.getSingleResult();
        } catch (NoResultException e) {
            return null;
        } finally {
            em.close();
        }
    }
}
