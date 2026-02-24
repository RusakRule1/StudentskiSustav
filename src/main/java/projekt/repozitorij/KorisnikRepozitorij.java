package projekt.repozitorij;

import projekt.dao.OpciDAO;
import projekt.model.Korisnik;

import java.util.List;

public class KorisnikRepozitorij {
    private final OpciDAO<Korisnik> korisnikDAO;

    public KorisnikRepozitorij() {
        this.korisnikDAO = new OpciDAO<>(Korisnik.class);
    }

    public Korisnik pronadjiPoId(Integer id) {
        return korisnikDAO.pronadjiPoId(id);
    }

    public List<Korisnik> vratiSve() {
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
        korisnikDAO.obrisiPoId(id);
    }

    public Korisnik pronadjiKorisnikaPoEmailu(String email) {
        if (email == null || email.isBlank()) return null;
        return korisnikDAO.pronadjiJedan(
                "SELECT k FROM Korisnik k WHERE k.email = :email",
                Korisnik.class, "email", email);
    }
}
