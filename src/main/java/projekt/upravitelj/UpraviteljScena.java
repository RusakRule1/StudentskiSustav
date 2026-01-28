package projekt.upravitelj;

import javafx.stage.Stage;
import projekt.model.Uloga;
import projekt.pogled.GlavniIzbornikPogled;
import projekt.pogled.LoginPogled;
import projekt.util.Konfiguracija;

public class UpraviteljScena {

    private static Stage glavniProzor;
    private static Konfiguracija konfig;

    public static void inicijaliziraj(Stage prozor) {
        glavniProzor = prozor;
        konfig = Konfiguracija.getInstanca();
        pokreniLogin();
        glavniProzor.show();
    }

    public static void pokreniLogin() {
        LoginPogled loginPogled = new LoginPogled();
        loginPogled.prikazi(glavniProzor);
    }

    public static void prikaziGlavniIzbornik(Uloga uloga, String email) {
        GlavniIzbornikPogled izbornik = new GlavniIzbornikPogled(uloga, email);
        izbornik.prikazi(glavniProzor);
    }
}