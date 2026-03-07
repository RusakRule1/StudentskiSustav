package projekt.util.graditelj;

import javafx.scene.control.ComboBox;

public class ComboBoxGraditelj<T> {

    private final ComboBox<T> comboBox = new ComboBox<>();

    public ComboBoxGraditelj() {
    }

    public ComboBoxGraditelj<T> stil(String... klase) {
        comboBox.getStyleClass().addAll(klase);
        return this;
    }

    public ComboBoxGraditelj<T> onemogucen(boolean onemogucen) {
        comboBox.setDisable(onemogucen);
        return this;
    }

    public ComboBoxGraditelj<T> stavke(javafx.collections.ObservableList<T> stavke) {
        comboBox.setItems(stavke);
        return this;
    }

    @SafeVarargs
    public final ComboBoxGraditelj<T> stavke(T... stavke) {
        comboBox.getItems().setAll(stavke);
        return this;
    }

    public ComboBox<T> build() {
        return comboBox;
    }
}
