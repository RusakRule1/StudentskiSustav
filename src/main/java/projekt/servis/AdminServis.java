package projekt.servis;

import projekt.model.Admin;
import projekt.model.Korisnik;
import projekt.model.Zapis;
import projekt.model.ZapisAkcija;
import projekt.repozitorij.AdminRepozitorij;
import projekt.upravitelj.Sesija;
import projekt.upravitelj.UpraviteljZapisima;

import java.time.LocalDateTime;

public class AdminServis {

    private final AdminRepozitorij adminRepozitorij = new AdminRepozitorij();

    public void spremiAdmina(Admin noviAdmin) {
        adminRepozitorij.spremi(noviAdmin);
        zapisi(ZapisAkcija.KREIRAN_KORISNIK, "Admin: " + noviAdmin.getEmail());
    }

    private void zapisi(ZapisAkcija akcija, String detalji) {
        Korisnik korisnik = Sesija.getInstanca().getPrijavljeniKorisnik();
        String email = korisnik != null ? korisnik.getEmail() : "sustav";
        UpraviteljZapisima.getInstanca().dodajZapis(
                new Zapis(email, akcija, detalji, LocalDateTime.now()));
    }
}
