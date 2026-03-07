package projekt.util.graditelj;

import javafx.scene.control.TextArea;

public class TextAreaGraditelj {
    private final TextArea polje = new TextArea();

    public TextAreaGraditelj() {
    }

    public TextAreaGraditelj stil(String... klase) {
        polje.getStyleClass().addAll(klase);
        return this;
    }

    public TextAreaGraditelj promptTekst(String tekst) {
        polje.setPromptText(tekst);
        return this;
    }

    public TextAreaGraditelj wrapText(boolean wrap) {
        polje.setWrapText(wrap);
        return this;
    }

    public TextAreaGraditelj brojRedaka(int redaka) {
        polje.setPrefRowCount(redaka);
        return this;
    }

    public TextAreaGraditelj onemogucen(boolean onemogucen) {
        polje.setDisable(onemogucen);
        return this;
    }

    public TextArea build() {
        return polje;
    }
}
