package projekt.util.graditelj;

import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;

public class LabelGraditelj {

    private final Label labela = new Label();

    public LabelGraditelj() {
    }

    public LabelGraditelj(String tekst) {
        labela.setText(tekst);
    }

    public LabelGraditelj stil(String... klase) {
        labela.getStyleClass().addAll(klase);
        return this;
    }

    public LabelGraditelj wrapText(boolean wrap) {
        labela.setWrapText(wrap);
        return this;
    }

    public LabelGraditelj pozicija(Pos pozicija) {
        labela.setAlignment(pozicija);
        return this;
    }

    public LabelGraditelj vidljivo(boolean vidljivo) {
        labela.setVisible(vidljivo);
        return this;
    }

    public LabelGraditelj grow(Priority priority) {
        HBox.setHgrow(labela, priority);
        return this;
    }

    public Label build() {
        return labela;
    }
}
