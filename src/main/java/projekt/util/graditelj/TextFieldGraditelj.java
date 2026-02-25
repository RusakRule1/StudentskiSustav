package projekt.util.graditelj;

import javafx.scene.control.TextField;

public class TextFieldGraditelj {

    private final TextField polje = new TextField();

    public TextFieldGraditelj() {
    }

    public TextFieldGraditelj stil(String... klase) {
        polje.getStyleClass().addAll(klase);
        return this;
    }

    public TextFieldGraditelj onemogucen(boolean onemogucen) {
        polje.setDisable(onemogucen);
        return this;
    }

    public TextField build() {
        return polje;
    }
}
