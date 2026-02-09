package projekt.servis;

import projekt.baza.repozitorij.KorisnikRepozitorij;
import projekt.model.Korisnik;

public class KorisnikServis {

    private final KorisnikRepozitorij korisnikRepozitorij = new KorisnikRepozitorij();

    public Korisnik pronadiKorisnikaPoEmailu(String email) {
        return korisnikRepozitorij.pronadiPoEmailu(email);
    }

    public void spremiKorisnika(Korisnik noviKorisnik) {
        korisnikRepozitorij.spremi(noviKorisnik);
    }
}
