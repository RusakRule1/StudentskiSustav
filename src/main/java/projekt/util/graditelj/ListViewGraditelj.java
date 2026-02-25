package projekt.util.graditelj;

import javafx.scene.control.ListView;
import javafx.scene.control.SelectionMode;

public class ListViewGraditelj<T> {

    private final ListView<T> lista = new ListView<>();

    public ListViewGraditelj() {
    }

    public ListViewGraditelj<T> stil(String... klase) {
        lista.getStyleClass().addAll(klase);
        return this;
    }

    public ListViewGraditelj<T> nacinOdabira(SelectionMode nacin) {
        lista.getSelectionModel().setSelectionMode(nacin);
        return this;
    }

    public ListView<T> build() {
        return lista;
    }
}
