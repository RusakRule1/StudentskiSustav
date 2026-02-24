package projekt.upravitelj;

import projekt.model.Korisnik;

public class Sesija {

    private static Sesija instanca;
    private Korisnik prijavljeniKorisnik;

    private Sesija() {
    }

    public static synchronized Sesija getInstanca() {
        if (instanca == null) {
            instanca = new Sesija();
        }
        return instanca;
    }

    public Korisnik getPrijavljeniKorisnik() {
        return prijavljeniKorisnik;
    }

    public void postaviPrijavljenogKorisnika(Korisnik korisnik) {
        this.prijavljeniKorisnik = korisnik;
    }

    public void odjaviKorisnika() {
        this.prijavljeniKorisnik = null;
    }
}
