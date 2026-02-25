package projekt;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.control.Alert;
import javafx.stage.Stage;
import projekt.pogled.PrijavaPogled;
import projekt.upravitelj.Konfiguracija;
import projekt.upravitelj.UpraviteljBaze;
import projekt.upravitelj.UpraviteljPogleda;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class Main extends Application {

    @Override
    public void start(Stage glavniProzor) {
        try {
            validirajKriptoKljuceve();
            UpraviteljPogleda.inicijaliziraj(glavniProzor);
            UpraviteljPogleda.prikaziBezPovijesti(new PrijavaPogled());
        } catch (Throwable t) {
            prikaziKriticnuGresku(t.getMessage());
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

    private void validirajKriptoKljuceve() {
        Path baza = Paths.get(System.getProperty("user.home"), ".studentski-sustav", "podaci");
        Path[] kljucevi = {
                baza.resolve("aes_kljuc.key"),
                baza.resolve("rsa_javni.key"),
                baza.resolve("rsa_privatni.key")
        };
        for (Path kljuc : kljucevi) {
            if (!Files.exists(kljuc)) {
                throw new IllegalStateException(
                        "Enkripcijski ključ nije pronađen: " + kljuc.getFileName() +
                                "\nPokrenite GeneratorKljuceva za generiranje ključeva."
                );
            }
        }
    }

    private void prikaziKriticnuGresku(String poruka) {
        System.err.println("Kritična greška: " + poruka);
        try {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Greška pri pokretanju");
            alert.setHeaderText("Aplikacija ne može biti pokrenuta");
            alert.setContentText(poruka);
            alert.showAndWait();
        } catch (Throwable ignored) {
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}