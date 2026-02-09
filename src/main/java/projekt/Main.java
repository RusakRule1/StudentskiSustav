package projekt;

import javafx.application.Application;
import javafx.stage.Stage;
import projekt.pogled.LoginPogled;
import projekt.upravitelj.Konfiguracija;
import projekt.upravitelj.UpraviteljBaze;
import projekt.upravitelj.UpraviteljPogleda;

public class Main extends Application {

    @Override
    public void start(Stage glavniProzor) {
        try {
            konfigurirajProzor(glavniProzor);
            pokreniAplikaciju(glavniProzor);
        } catch (Exception e) {
            System.err.println("Greška pri pokretanju aplikacije: " + e.getMessage());
            e.printStackTrace();
            System.exit(1);
        }
    }

    private void konfigurirajProzor(Stage prozor) {
        prozor.setOnCloseRequest(event -> {
            System.out.println("Zatvaranje aplikacije...");
        });
    }

    private void pokreniAplikaciju(Stage glavniProzor) {
        UpraviteljPogleda.inicijaliziraj(glavniProzor);
        UpraviteljPogleda.prikaziBezPovijesti(new LoginPogled());
    }

    @Override
    public void stop() {
        try {
            zatvoriResurse();
            spremiPostavke();
            System.out.println("Aplikacija uspješno zatvorena");
        } catch (Exception e) {
            System.err.println("Greška pri zatvaranju aplikacije: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void zatvoriResurse() {
        try {
            UpraviteljBaze.zatvori();
        } catch (Exception e) {
            System.err.println("Greška pri zatvaranju Hibernate: " + e.getMessage());
        }
    }

    private void spremiPostavke() {
        try {
            Konfiguracija konfig = Konfiguracija.getInstanca();
            boolean uspjeh = konfig.spremiSvePostavke();

            if (!uspjeh) {
                System.err.println("Upozorenje: Nisu sve postavke uspješno spremljene");
            }
        } catch (Exception e) {
            System.err.println("Greška pri spremanju postavki: " + e.getMessage());
        }
    }

    static void main(String[] args) {
        launch(args);
    }
}