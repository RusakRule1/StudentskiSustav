package projekt.kontroler;

import projekt.model.Uloga;

public class AuthKontroler {

    public Uloga prijaviKorisnika(String email, String lozinka) {

        return Uloga.ADMIN;
    }
}
