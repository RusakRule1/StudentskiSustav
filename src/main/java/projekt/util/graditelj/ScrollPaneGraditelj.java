package projekt.util.graditelj;

import javafx.scene.Node;
import javafx.scene.control.ScrollPane;

public class ScrollPaneGraditelj {

    private final ScrollPane scrollPane = new ScrollPane();

    public ScrollPaneGraditelj(Node sadrzaj) {
        scrollPane.setContent(sadrzaj);
    }

    public ScrollPaneGraditelj fitSirinu(boolean fit) {
        scrollPane.setFitToWidth(fit);
        return this;
    }

    public ScrollPaneGraditelj fitVisinu(boolean fit) {
        scrollPane.setFitToHeight(fit);
        return this;
    }

    public ScrollPaneGraditelj pannable(boolean pannable) {
        scrollPane.setPannable(pannable);
        return this;
    }

    public ScrollPaneGraditelj stil(String... klase) {
        scrollPane.getStyleClass().addAll(klase);
        return this;
    }

    public ScrollPane build() {
        return scrollPane;
    }
}
