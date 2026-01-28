package projekt.pogled;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import projekt.util.Konfiguracija;
import projekt.util.StiloviUtil;

import java.util.Objects;

public abstract class OsnovniPogled {

    protected Stage prozor;
    protected String trenutniJezik;
    protected Konfiguracija konfig;

    protected Text naslovTekst;
    protected BorderPane korijen;
    protected Hyperlink hrVeza;
    protected Hyperlink enVeza;
    protected VBox glavniSadrzajKontejner;

    public OsnovniPogled() {
        this.konfig = Konfiguracija.getInstanca();
        this.trenutniJezik = konfig.getJezik();
    }

    protected abstract VBox kreirajSadrzaj();

    protected abstract void postaviHrvatski();

    protected abstract void postaviEngleski();

    public void prikazi(Stage glavniProzor) {
        this.prozor = glavniProzor;
        kreirajGUI();
        postaviEventHandlere();
    }

    private void kreirajGUI() {
        korijen = new BorderPane();
        korijen.getStyleClass().add(StiloviUtil.POZADINA_GRADIJENT);

        HBox gornjaTraka = kreirajGornjuTraku();
        korijen.setTop(gornjaTraka);

        glavniSadrzajKontejner = kreirajSadrzaj();
        korijen.setCenter(glavniSadrzajKontejner);

        Scene scena = new Scene(korijen, konfig.getSirinaProzora(), konfig.getVisinaProzora());

        scena.getStylesheets().addAll(
                Objects.requireNonNull(getClass().getResource("/css/globalni.css")).toExternalForm(),
                Objects.requireNonNull(getClass().getResource("/css/komponente.css")).toExternalForm()
        );

        prozor.setScene(scena);
        prozor.setResizable(false);

        osvjeziNaslovProzora();
    }

    private HBox kreirajGornjuTraku() {
        HBox traka = new HBox();
        traka.setPadding(new Insets(15, 20, 15, 20));
        traka.setAlignment(Pos.CENTER_LEFT);
        traka.setSpacing(20);
        traka.getStyleClass().add("gornja-traka");

        naslovTekst = new Text();
        naslovTekst.getStyleClass().add(StiloviUtil.NASLOV_TEKST);
        osvjeziNaslovTekst();

        HBox jeziciBox = kreirajJezicniBox();

        HBox spacer = new HBox();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        traka.getChildren().addAll(naslovTekst, spacer, jeziciBox);
        return traka;
    }

    private HBox kreirajJezicniBox() {
        HBox jeziciBox = new HBox(5);
        jeziciBox.setAlignment(Pos.CENTER_RIGHT);

        hrVeza = new Hyperlink("HR");
        enVeza = new Hyperlink("EN");

        Label razdvajac = new Label("|");
        razdvajac.getStyleClass().add(StiloviUtil.LABELA_PRIGUSENA);

        postaviPocetniStilLinkova();

        jeziciBox.getChildren().addAll(hrVeza, razdvajac, enVeza);
        return jeziciBox;
    }

    private void postaviPocetniStilLinkova() {
        if ("HR".equals(trenutniJezik)) {
            hrVeza.getStyleClass().add(StiloviUtil.HIPERVEZA_AKTIVNA);
            enVeza.getStyleClass().add(StiloviUtil.HIPERVEZA_NEAKTIVNA);
        } else {
            hrVeza.getStyleClass().add(StiloviUtil.HIPERVEZA_NEAKTIVNA);
            enVeza.getStyleClass().add(StiloviUtil.HIPERVEZA_AKTIVNA);
        }
    }

    private void postaviEventHandlere() {
        hrVeza.setOnAction(e -> {
            trenutniJezik = "HR";
            postaviHrvatski();
            osvjeziNaslovTekst();
            osvjeziNaslovProzora();
            promijeniStilLinkova(true);
            konfig.setJezik(trenutniJezik);
        });

        enVeza.setOnAction(e -> {
            trenutniJezik = "EN";
            postaviEngleski();
            osvjeziNaslovTekst();
            osvjeziNaslovProzora();
            promijeniStilLinkova(false);
            konfig.setJezik(trenutniJezik);
        });
    }

    private void promijeniStilLinkova(boolean hrvatskiAktivan) {
        if (hrvatskiAktivan) {
            hrVeza.getStyleClass().remove(StiloviUtil.HIPERVEZA_NEAKTIVNA);
            hrVeza.getStyleClass().add(StiloviUtil.HIPERVEZA_AKTIVNA);

            enVeza.getStyleClass().remove(StiloviUtil.HIPERVEZA_AKTIVNA);
            enVeza.getStyleClass().add(StiloviUtil.HIPERVEZA_NEAKTIVNA);
        } else {
            hrVeza.getStyleClass().remove(StiloviUtil.HIPERVEZA_AKTIVNA);
            hrVeza.getStyleClass().add(StiloviUtil.HIPERVEZA_NEAKTIVNA);

            enVeza.getStyleClass().remove(StiloviUtil.HIPERVEZA_NEAKTIVNA);
            enVeza.getStyleClass().add(StiloviUtil.HIPERVEZA_AKTIVNA);
        }
    }

    private void osvjeziNaslovTekst() {
        naslovTekst.setText(trenutniJezik.equals("HR")
                ? "STUDENTSKI SUSTAV"
                : "STUDENT SYSTEM");
    }

    private void osvjeziNaslovProzora() {
        String naslov = trenutniJezik.equals("HR")
                ? "Studentski sustav"
                : "Student System";
        prozor.setTitle(naslov);
    }
}