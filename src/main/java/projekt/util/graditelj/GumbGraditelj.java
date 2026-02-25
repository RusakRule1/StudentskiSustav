package projekt.util.graditelj;

import javafx.scene.control.Button;

public class GumbGraditelj {

    private final Button gumb = new Button();

    public GumbGraditelj(String stil, Runnable akcija) {
        gumb.getStyleClass().add(stil);
        gumb.setOnAction(e -> akcija.run());
    }

    public GumbGraditelj stil(String... klase) {
        gumb.getStyleClass().addAll(klase);
        return this;
    }

    public GumbGraditelj tekst(String tekst) {
        gumb.setText(tekst);
        return this;
    }

    public GumbGraditelj onemogucen(boolean onemogucen) {
        gumb.setDisable(onemogucen);
        return this;
    }

    public GumbGraditelj vidljivo(boolean vidljivo) {
        gumb.setVisible(vidljivo);
        return this;
    }

    public Button build() {
        return gumb;
    }
}
