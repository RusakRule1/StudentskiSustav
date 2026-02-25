package projekt.model;

import java.time.LocalDateTime;

public class Zapis {

    private LocalDateTime vrijeme;
    private String korisnik;
    private ZapisAkcija akcija;
    private String detalji;

    public Zapis() {
    }

    public Zapis(String korisnik, ZapisAkcija akcija, String detalji) {
        this.vrijeme = LocalDateTime.now();
        this.korisnik = korisnik;
        this.akcija = akcija;
        this.detalji = detalji;
    }

    public Zapis(String korisnik, ZapisAkcija akcija, String detalji, LocalDateTime vrijeme) {
        this.vrijeme = vrijeme;
        this.korisnik = korisnik;
        this.akcija = akcija;
        this.detalji = detalji;
    }

    public LocalDateTime getVrijeme() {
        return vrijeme;
    }

    public void setVrijeme(LocalDateTime vrijeme) {
        this.vrijeme = vrijeme;
    }

    public String getKorisnik() {
        return korisnik;
    }

    public void setKorisnik(String korisnik) {
        this.korisnik = korisnik;
    }

    public ZapisAkcija getAkcija() {
        return akcija;
    }

    public void setAkcija(ZapisAkcija akcija) {
        this.akcija = akcija;
    }

    public String getDetalji() {
        return detalji;
    }

    public void setDetalji(String detalji) {
        this.detalji = detalji;
    }

    @Override
    public String toString() {
        return String.format("[%s] %s - %s: %s",
                vrijeme != null ? vrijeme.toString() : "N/A",
                korisnik,
                akcija,
                detalji);
    }
}
