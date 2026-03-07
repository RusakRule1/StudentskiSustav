package projekt.servis;

import projekt.model.*;
import projekt.repozitorij.ProfesorRepozitorij;
import projekt.upravitelj.Sesija;
import projekt.upravitelj.UpraviteljZapisima;

import java.time.LocalDateTime;
import java.util.List;

public class ProfesorServis {

    private final ProfesorRepozitorij profesorRepozitorij = new ProfesorRepozitorij();

    public List<Profesor> vratiSve() {
        return profesorRepozitorij.vratiSve();
    }

    public void spremiProfesoraSPredmetima(Profesor noviProfesor, List<Predmet> predmeti) {
        profesorRepozitorij.spremi(noviProfesor, predmeti);
        zapisi(ZapisAkcija.KREIRAN_KORISNIK, "Profesor: " + noviProfesor.getEmail());
    }

    private void zapisi(ZapisAkcija akcija, String detalji) {
        Korisnik korisnik = Sesija.getInstanca().getPrijavljeniKorisnik();
        String email = korisnik != null ? korisnik.getEmail() : "sustav";
        UpraviteljZapisima.getInstanca().dodajZapis(
                new Zapis(email, akcija, detalji, LocalDateTime.now()));
    }
}
