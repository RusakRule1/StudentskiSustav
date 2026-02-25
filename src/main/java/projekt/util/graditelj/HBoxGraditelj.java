package projekt.util.graditelj;

import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;

public class HBoxGraditelj {

    private final HBox hbox = new HBox();

    public HBoxGraditelj(Node... djeca) {
        hbox.getChildren().addAll(djeca);
    }

    public HBoxGraditelj pozicija(Pos pozicija) {
        hbox.setAlignment(pozicija);
        return this;
    }

    public HBoxGraditelj stil(String... klase) {
        hbox.getStyleClass().addAll(klase);
        return this;
    }

    public HBoxGraditelj grow(Priority priority) {
        HBox.setHgrow(hbox, priority);
        return this;
    }

    public HBoxGraditelj maxSirina(double sirina) {
        hbox.setMaxWidth(sirina);
        return this;
    }

    public HBoxGraditelj fillVisinu(boolean fill) {
        hbox.setFillHeight(fill);
        return this;
    }

    public HBoxGraditelj childGrow(Node dijete, Priority priority) {
        HBox.setHgrow(dijete, priority);
        return this;
    }

    public HBoxGraditelj vidljivo(boolean vidljivo) {
        hbox.setVisible(vidljivo);
        return this;
    }

    public HBox build() {
        return hbox;
    }
}
