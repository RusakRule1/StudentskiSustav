package projekt.util.graditelj;

import javafx.collections.ObservableList;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;

public class TableViewGraditelj<T> {

    private final TableView<T> tablica = new TableView<>();

    public TableViewGraditelj() {
    }

    public TableViewGraditelj<T> stil(String... klase) {
        tablica.getStyleClass().addAll(klase);
        return this;
    }

    public TableViewGraditelj<T> stavke(ObservableList<T> stavke) {
        tablica.setItems(stavke);
        return this;
    }

    @SafeVarargs
    public final TableViewGraditelj<T> kolone(TableColumn<T, ?>... kolone) {
        tablica.getColumns().setAll(kolone);
        return this;
    }

    public TableViewGraditelj<T> constrained() {
        tablica.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        return this;
    }

    public TableView<T> build() {
        return tablica;
    }
}
