package projekt.util.graditelj;

import javafx.scene.text.Text;

public class TextGraditelj {

    private final Text tekst = new Text();

    public TextGraditelj() {
    }

    public TextGraditelj(String sadrzaj) {
        tekst.setText(sadrzaj);
    }

    public TextGraditelj stil(String... klase) {
        tekst.getStyleClass().addAll(klase);
        return this;
    }

    public Text build() {
        return tekst;
    }
}
