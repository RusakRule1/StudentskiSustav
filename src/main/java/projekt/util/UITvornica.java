package projekt.util;

import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableList;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.util.Callback;

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

    public static class ScrollPaneGraditelj {
        private final ScrollPane scrollPane = new ScrollPane();

        private ScrollPaneGraditelj(Node sadrzaj) {
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

    public static class VBoxGraditelj {
        private final VBox vbox = new VBox();

        private VBoxGraditelj(Node... djeca) {
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

    public static class HBoxGraditelj {
        private final HBox hbox = new HBox();

        private HBoxGraditelj(Node... djeca) {
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

    public static class GumbGraditelj {
        private final Button gumb = new Button();

        private GumbGraditelj(String stil, Runnable akcija) {
            gumb.getStyleClass().add(stil);
            gumb.setOnAction(e -> akcija.run());
        }

        public GumbGraditelj stil(String... klase) {
            gumb.getStyleClass().addAll(klase);
            return this;
        }

        public GumbGraditelj tekst(String tekst) {
            gumb.setText(tekst);
            return this;
        }

        public GumbGraditelj onemogucen(boolean onemogucen) {
            gumb.setDisable(onemogucen);
            return this;
        }

        public GumbGraditelj vidljivo(boolean vidljivo) {
            gumb.setVisible(vidljivo);
            return this;
        }

        public Button build() {
            return gumb;
        }
    }

    public static class LabelGraditelj {
        private final Label labela = new Label();

        private LabelGraditelj() {
        }

        private LabelGraditelj(String tekst) {
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

    public static class TextGraditelj {
        private final Text tekst = new Text();

        private TextGraditelj() {
        }

        private TextGraditelj(String sadrzaj) {
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

    public static class TextFieldGraditelj {
        private final TextField polje = new TextField();

        private TextFieldGraditelj() {
        }

        public TextFieldGraditelj stil(String... klase) {
            polje.getStyleClass().addAll(klase);
            return this;
        }

        public TextFieldGraditelj onemogucen(boolean onemogucen) {
            polje.setDisable(onemogucen);
            return this;
        }

        public TextField build() {
            return polje;
        }
    }

    public static class PasswordFieldGraditelj {
        private final PasswordField polje = new PasswordField();

        private PasswordFieldGraditelj() {
        }

        public PasswordFieldGraditelj stil(String... klase) {
            polje.getStyleClass().addAll(klase);
            return this;
        }

        public PasswordField build() {
            return polje;
        }
    }

    public static class TableViewGraditelj<T> {
        private final TableView<T> tablica = new TableView<>();

        private TableViewGraditelj() {
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

    public static <S, T> TableColumn<S, T> kolona(String property, String... stilKlase) {
        TableColumn<S, T> kolona = new TableColumn<>();
        kolona.setCellValueFactory(new PropertyValueFactory<>(property));
        kolona.getStyleClass().addAll(stilKlase);
        return kolona;
    }

    public static <S, T> TableColumn<S, T> kolona(
            Callback<TableColumn.CellDataFeatures<S, T>, ObservableValue<T>> factory,
            String... stilKlase) {
        TableColumn<S, T> kolona = new TableColumn<>();
        kolona.setCellValueFactory(factory);
        kolona.getStyleClass().addAll(stilKlase);
        return kolona;
    }

    public static class ListViewGraditelj<T> {
        private final ListView<T> lista = new ListView<>();

        private ListViewGraditelj() {
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

    public static class ComboBoxGraditelj<T> {
        private final ComboBox<T> comboBox = new ComboBox<>();

        private ComboBoxGraditelj() {
        }

        public ComboBoxGraditelj<T> stil(String... klase) {
            comboBox.getStyleClass().addAll(klase);
            return this;
        }

        public ComboBoxGraditelj<T> onemogucen(boolean onemogucen) {
            comboBox.setDisable(onemogucen);
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
}
