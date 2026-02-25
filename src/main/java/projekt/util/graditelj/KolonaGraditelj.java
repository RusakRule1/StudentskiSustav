package projekt.util.graditelj;

import javafx.beans.value.ObservableValue;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.util.Callback;

public class KolonaGraditelj<S, T> {

    private final TableColumn<S, T> kolona = new TableColumn<>();

    public KolonaGraditelj(String property, String... stilKlase) {
        kolona.setCellValueFactory(new PropertyValueFactory<>(property));
        kolona.getStyleClass().addAll(stilKlase);
    }

    public KolonaGraditelj(
            Callback<TableColumn.CellDataFeatures<S, T>, ObservableValue<T>> factory,
            String... stilKlase) {
        kolona.setCellValueFactory(factory);
        kolona.getStyleClass().addAll(stilKlase);
    }

    public KolonaGraditelj<S, T> prefSirina(double sirina) {
        kolona.setPrefWidth(sirina);
        return this;
    }

    public KolonaGraditelj<S, T> maxSirina(double sirina) {
        kolona.setMaxWidth(sirina);
        return this;
    }

    public KolonaGraditelj<S, T> tvornicaCelija(Callback<TableColumn<S, T>, TableCell<S, T>> tvornica) {
        kolona.setCellFactory(tvornica);
        return this;
    }
    
    public TableColumn<S, T> build() {
        return kolona;
    }
}
