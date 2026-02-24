package projekt.servis;

import projekt.model.Korisnik;
import projekt.repozitorij.KorisnikRepozitorij;

public class KorisnikServis {

    private final KorisnikRepozitorij korisnikRepozitorij = new KorisnikRepozitorij();

    public Korisnik pronadjiKorisnikaPoEmailu(String email) {
        return korisnikRepozitorij.pronadjiKorisnikaPoEmailu(email);
    }
}
