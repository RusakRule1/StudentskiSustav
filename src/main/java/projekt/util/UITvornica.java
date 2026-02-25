package projekt.util;

import javafx.beans.value.ObservableValue;
import javafx.scene.Node;
import javafx.scene.control.TableColumn;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.util.Callback;
import projekt.util.graditelj.*;

public class UITvornica {

    private UITvornica() {
    }

    public static ScrollPaneGraditelj scrollPane(Node sadrzaj) {
        return new ScrollPaneGraditelj(sadrzaj);
    }

    public static VBoxGraditelj vbox(Node... djeca) {
        return new VBoxGraditelj(djeca);
    }

    public static HBoxGraditelj hbox(Node... djeca) {
        return new HBoxGraditelj(djeca);
    }

    public static LabelGraditelj labela() {
        return new LabelGraditelj();
    }

    public static LabelGraditelj labela(String tekst) {
        return new LabelGraditelj(tekst);
    }

    public static GumbGraditelj gumb(String stil, Runnable akcija) {
        return new GumbGraditelj(stil, akcija);
    }

    public static TextGraditelj tekst() {
        return new TextGraditelj();
    }

    public static TextGraditelj tekst(String sadrzaj) {
        return new TextGraditelj(sadrzaj);
    }

    public static TextFieldGraditelj textField() {
        return new TextFieldGraditelj();
    }

    public static PasswordFieldGraditelj passwordField() {
        return new PasswordFieldGraditelj();
    }

    public static <T> TableViewGraditelj<T> tableView() {
        return new TableViewGraditelj<>();
    }

    public static <S, T> KolonaGraditelj<S, T> kolona(String property, String... stilKlase) {
        return new KolonaGraditelj<>(property, stilKlase);
    }

    public static <S, T> KolonaGraditelj<S, T> kolona(
            Callback<TableColumn.CellDataFeatures<S, T>, ObservableValue<T>> factory,
            String... stilKlase) {
        return new KolonaGraditelj<>(factory, stilKlase);
    }

    public static <T> ListViewGraditelj<T> listView() {
        return new ListViewGraditelj<>();
    }

    public static <T> ComboBoxGraditelj<T> comboBox() {
        return new ComboBoxGraditelj<>();
    }

    public static HBox razmak() {
        HBox spacer = new HBox();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        return spacer;
    }
}
