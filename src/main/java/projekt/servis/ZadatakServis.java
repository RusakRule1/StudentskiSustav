package projekt.servis;

import projekt.model.*;
import projekt.repozitorij.ZadatakRepozitorij;
import projekt.upravitelj.Sesija;
import projekt.upravitelj.UpraviteljZapisima;

import java.time.LocalDateTime;
import java.util.List;

public class ZadatakServis {

    private final ZadatakRepozitorij zadatakRepozitorij = new ZadatakRepozitorij();

    public List<Zadatak> vratiZadatkeProfesora(Integer profesorId) {
        return zadatakRepozitorij.vratiZadatkeProfesora(profesorId);
    }

    public List<Zadatak> vratiZadatkeStudenta(Integer studentId) {
        return zadatakRepozitorij.vratiZadatkeStudenta(studentId);
    }

    public void kreirajZadatak(String naziv, String opis, Predmet predmet, LocalDateTime rokPredaje) {
        Zadatak zadatak = new Zadatak(naziv, opis, predmet, rokPredaje);
        zadatakRepozitorij.spremi(zadatak);
        zapisi(ZapisAkcija.OBJAVLJEN_ZADATAK,
                naziv + " | " + predmet.getNaziv());
    }

    public void azurirajZadatak(Zadatak zadatak) {
        zadatakRepozitorij.azuriraj(zadatak);
        zapisi(ZapisAkcija.IZMIJENJEN_ZADATAK,
                zadatak.getNaziv() + " | " + zadatak.getPredmet().getNaziv());
    }

    public void obrisiZadatak(Zadatak zadatak) {
        zadatakRepozitorij.obrisi(zadatak);
        zapisi(ZapisAkcija.OBRISAN_ZADATAK,
                zadatak.getNaziv() + " | " + zadatak.getPredmet().getNaziv());
    }

    private void zapisi(ZapisAkcija akcija, String detalji) {
        Korisnik korisnik = Sesija.getInstanca().getPrijavljeniKorisnik();
        String email = korisnik != null ? korisnik.getEmail() : "sustav";
        UpraviteljZapisima.getInstanca().dodajZapis(
                new Zapis(email, akcija, detalji, LocalDateTime.now()));
    }

    public Zadatak vratiZadatakSPredajamaPoId(Integer zadatakId) {
        return zadatakRepozitorij.vratiZadatakSPredajamaPoId(zadatakId);
    }
}
