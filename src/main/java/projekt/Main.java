package projekt;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.stage.Stage;
import projekt.pogled.PrijavaPogled;
import projekt.upravitelj.Konfiguracija;
import projekt.upravitelj.UpraviteljBaze;
import projekt.upravitelj.UpraviteljPogleda;

public class Main extends Application {

    @Override
    public void start(Stage glavniProzor) {
        try {
            UpraviteljPogleda.inicijaliziraj(glavniProzor);
            UpraviteljPogleda.prikaziBezPovijesti(new PrijavaPogled());
        } catch (Exception e) {
            System.err.println("Greška pri pokretanju aplikacije: " + e.getMessage());
            e.printStackTrace();
            Platform.exit();
        }
    }

    @Override
    public void stop() {
        try {
            UpraviteljBaze.zatvori();
        } catch (Exception e) {
            System.err.println("Greška pri zatvaranju baze: " + e.getMessage());
            e.printStackTrace();
        }

        try {
            if (!Konfiguracija.getInstanca().spremiSvePostavke()) {
                System.err.println("Upozorenje: Nisu sve postavke uspješno spremljene");
            }
        } catch (Exception e) {
            System.err.println("Greška pri spremanju postavki: " + e.getMessage());
            e.printStackTrace();
        }

        System.out.println("Aplikacija uspješno zatvorena");
    }

    public static void main(String[] args) {
        launch(args);
    }
}