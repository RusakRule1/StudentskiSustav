package projekt;

import javafx.application.Application;
import javafx.stage.Stage;
import projekt.upravitelj.UpraviteljScena;
import projekt.util.Konfiguracija;

public class Main extends Application {

    @Override
    public void start(Stage glavniProzor) {
        UpraviteljScena.inicijaliziraj(glavniProzor);
    }

    @Override
    public void stop() {
        Konfiguracija konfig = Konfiguracija.getInstanca();
        konfig.spremiSvePostavke();
    }

    static void main(String[] args) {
        launch(args);
    }
}