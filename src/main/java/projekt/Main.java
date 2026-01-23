package projekt;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import projekt.dao.IKorisnikDAO;
import projekt.dao.impl.KorisnikDAO;
import projekt.model.Korisnik;
import projekt.model.Uloga;

public class Main extends Application {

    @Override
    public void start(Stage stage) {
        IKorisnikDAO korisnikDAO = new KorisnikDAO();
        try {

            Korisnik korisnik1 = new Korisnik(
                    "ivan.horvat@fer.hr",
                    "$2a$10$hashedPassword123",
                    "Ivan",
                    "Horvat",
                    Uloga.STUDENT
            );
            korisnikDAO.spremiKorisnika(korisnik1);
            System.out.println("1. Korisnik spremljen: " + korisnik1.getEmail());
        } catch (Exception e) {
            System.err.println("GREŠKA: " + e.getMessage());
            e.printStackTrace();
        }

        Label label = new Label("Studentski sustav");
        Scene scene = new Scene(new StackPane(label), 800, 600);
        stage.setScene(scene);
        stage.setTitle("Studentski sustav");
        stage.show();
    }

    static void main(String[] args) {
        launch(args);
    }
}