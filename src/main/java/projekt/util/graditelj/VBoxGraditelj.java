package projekt.util.graditelj;

import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

public class VBoxGraditelj {

    private final VBox vbox = new VBox();

    public VBoxGraditelj(Node... djeca) {
        vbox.getChildren().addAll(djeca);
    }

    public VBoxGraditelj pozicija(Pos pozicija) {
        vbox.setAlignment(pozicija);
        return this;
    }

    public VBoxGraditelj grow(Priority priority) {
        VBox.setVgrow(vbox, priority);
        return this;
    }

    public VBoxGraditelj stil(String... klase) {
        vbox.getStyleClass().addAll(klase);
        return this;
    }

    public VBox build() {
        return vbox;
    }
}
