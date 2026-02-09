package projekt.pogled;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import projekt.model.Jezik;
import projekt.model.Korisnik;
import projekt.model.Zapis;
import projekt.model.ZapisAkcija;
import projekt.upravitelj.*;
import projekt.util.Stilovi;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Stream;

public abstract class OsnovniPogled {

    private static final String CSS_GLOBALNI = "/css/globalni.css";
    private static final String CSS_KOMPONENTE = "/css/komponente.css";
    private static final String SEPARATOR = "|";

    private static final Insets TOP_BAR_PADDING = new Insets(15, 20, 15, 20);
    private static final Insets INFO_BAR_PADDING = new Insets(5, 20, 5, 20);
    private static final int TOP_BAR_SPACING = 20;
    private static final int INFO_BAR_SPACING = 10;
    private static final int LANGUAGE_BOX_SPACING = 5;

    protected Stage prozor;
    protected Konfiguracija konfig;
    protected Prijevod prijevod;
    protected UpraviteljZapisima upraviteljZapisima;

    protected final Text naslovTekst = new Text();
    protected BorderPane korijen;
    protected final Hyperlink hrVeza = new Hyperlink(Jezik.HR.getKod());
    protected final Hyperlink enVeza = new Hyperlink(Jezik.EN.getKod());
    protected VBox glavniSadrzajKontejner;
    protected final Label prijavljenKorisnikLabel = new Label();
    protected final Button odjavaGumb = new Button();
    protected final Button natragGumb = new Button();

    public OsnovniPogled() {
        this.konfig = Konfiguracija.getInstanca();
        this.prijevod = Prijevod.getInstanca();
        this.upraviteljZapisima = UpraviteljZapisima.getInstance();
    }

    protected abstract VBox kreirajSadrzaj();

    protected abstract void osvjeziPogledTekstove();

    public void prikazi(Stage glavniProzor) {
        this.prozor = glavniProzor;
        kreirajGUI();
        postaviEventHandlere();
    }

    private void kreirajGUI() {
        inicijalizirajKorijen();
        dodajTrake();
        dodajSadrzaj();
        postaviScenu();
        osvjeziTekstove();
    }

    private void inicijalizirajKorijen() {
        korijen = new BorderPane();
        korijen.getStyleClass().add(Stilovi.POZADINA_GRADIJENT);
    }

    private void dodajTrake() {
        HBox gornjaTraka = kreirajGornjuTraku();
        HBox infoTraka = kreirajInfoTraku();
        korijen.setTop(new VBox(gornjaTraka, infoTraka));
    }

    private void dodajSadrzaj() {
        glavniSadrzajKontejner = kreirajSadrzaj();
        korijen.setCenter(glavniSadrzajKontejner);
    }

    private void postaviScenu() {
        Scene scena = new Scene(korijen, konfig.getSirinaProzora(), konfig.getVisinaProzora());
        scena.getStylesheets().addAll(ucitajStylesheets());
        prozor.setScene(scena);
        prozor.setResizable(false);
    }

    private List<String> ucitajStylesheets() {
        return List.of(
                getResourcePath(CSS_GLOBALNI),
                getResourcePath(CSS_KOMPONENTE)
        );
    }

    private String getResourcePath(String resource) {
        return Objects.requireNonNull(
                getClass().getResource(resource),
                "Stylesheet nije pronađen: " + resource
        ).toExternalForm();
    }

    private HBox kreirajGornjuTraku() {
        HBox traka = new HBox();
        traka.setPadding(TOP_BAR_PADDING);
        traka.setAlignment(Pos.CENTER_LEFT);
        traka.setSpacing(TOP_BAR_SPACING);
        traka.getStyleClass().add("gornja-traka");

        naslovTekst.getStyleClass().add(Stilovi.NASLOV_TEKST);

        traka.getChildren().addAll(naslovTekst, kreirajSpacer(), kreirajJezicniBox());
        return traka;
    }

    private HBox kreirajSpacer() {
        HBox spacer = new HBox();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        return spacer;
    }

    private HBox kreirajInfoTraku() {
        HBox infoTraka = new HBox(INFO_BAR_SPACING);
        infoTraka.setPadding(INFO_BAR_PADDING);
        infoTraka.setAlignment(Pos.CENTER_LEFT);

        konfigurirajNatragGumb();
        konfigurirajOdjavaGumb();

        prijavljenKorisnikLabel.getStyleClass().add(Stilovi.LABELA_INFORMACIJA);

        infoTraka.getChildren().addAll(
                natragGumb,
                kreirajSpacer(),
                prijavljenKorisnikLabel,
                odjavaGumb
        );
        return infoTraka;
    }

    private void konfigurirajNatragGumb() {
        natragGumb.getStyleClass().add(Stilovi.GUMB_SEKUNDARAN);
        natragGumb.setVisible(false);
        natragGumb.setOnAction(e -> UpraviteljPogleda.idiNatrag());
    }

    private void konfigurirajOdjavaGumb() {
        odjavaGumb.getStyleClass().add(Stilovi.GUMB_OPASAN);
        odjavaGumb.setOnAction(e -> obradiOdjavu());
    }

    private void obradiOdjavu() {
        UpraviteljZapisima.getInstance().dodajZapis(new Zapis(Sesija.getInstanca().getPrijavljeniKorisnik().getEmail(),
                ZapisAkcija.ODJAVA, "Uspješna odjava korisnika s ulogom: " + Sesija.getInstanca().getPrijavljeniKorisnik().getUloga()));
        Sesija.getInstanca().odjaviKorisnika();
        UpraviteljPogleda.prikaziBezPovijesti(new LoginPogled());
    }

    private HBox kreirajJezicniBox() {
        HBox jeziciBox = new HBox(LANGUAGE_BOX_SPACING);
        jeziciBox.setAlignment(Pos.CENTER_RIGHT);

        konfigurirajJezicneVeze();

        Label razdvajac = kreirajRazdvajac();

        jeziciBox.getChildren().addAll(hrVeza, razdvajac, enVeza);
        return jeziciBox;
    }

    private void konfigurirajJezicneVeze() {
        hrVeza.setFocusTraversable(false);
        enVeza.setFocusTraversable(false);
    }

    private Label kreirajRazdvajac() {
        Label razdvajac = new Label(SEPARATOR);
        razdvajac.getStyleClass().add(Stilovi.LABELA_PRIGUSENA);
        return razdvajac;
    }

    private void postaviEventHandlere() {
        hrVeza.setOnAction(e -> promijeniJezik(Jezik.HR));
        enVeza.setOnAction(e -> promijeniJezik(Jezik.EN));
    }

    private void promijeniJezik(Jezik jezik) {
        konfig.setJezik(jezik.getKod());
        osvjeziTekstove();
    }

    protected void osvjeziInfoKorisnika() {
        Optional.ofNullable(Sesija.getInstanca().getPrijavljeniKorisnik())
                .ifPresentOrElse(
                        this::prikaziInfoKorisnika,
                        this::sakrijInfoKorisnika
                );
    }

    private void prikaziInfoKorisnika(Korisnik korisnik) {
        String formatiraniTekst = String.format("%s: %s %s",
                prijevod.getPrijevod("prijavljeni_korisnik"),
                korisnik.getIme(),
                korisnik.getPrezime()
        );

        prijavljenKorisnikLabel.setText(formatiraniTekst);
        postaviVidljivostKorisnickeInfo(true);
    }

    private void sakrijInfoKorisnika() {
        postaviVidljivostKorisnickeInfo(false);
    }

    private void postaviVidljivostKorisnickeInfo(boolean vidljivo) {
        prijavljenKorisnikLabel.setVisible(vidljivo);
        odjavaGumb.setVisible(vidljivo);
    }

    private void promijeniStilLinkova(boolean hrvatskiAktivan) {
        resetirajStiloveVeza();

        Hyperlink aktivna = hrvatskiAktivan ? hrVeza : enVeza;
        Hyperlink neaktivna = hrvatskiAktivan ? enVeza : hrVeza;

        aktivna.getStyleClass().add(Stilovi.HIPERVEZA_AKTIVNA);
        neaktivna.getStyleClass().add(Stilovi.HIPERVEZA_NEAKTIVNA);
    }

    private void resetirajStiloveVeza() {
        Stream.of(hrVeza, enVeza).forEach(veza ->
                veza.getStyleClass().removeAll(
                        Stilovi.HIPERVEZA_AKTIVNA,
                        Stilovi.HIPERVEZA_NEAKTIVNA
                )
        );
    }

    private void osvjeziTekstove() {
        osvjeziNaslov();
        osvjeziGumbe();
        osvjeziJezicneStilove();
        osvjeziPogledTekstove();
        osvjeziInfoKorisnika();
    }

    private void osvjeziNaslov() {
        prozor.setTitle(prijevod.getPrijevod("naslov_prozor"));
        naslovTekst.setText(prijevod.getPrijevod("naslov_aplikacija"));
    }

    private void osvjeziGumbe() {
        natragGumb.setText(prijevod.getPrijevod("natrag_gumb"));
        natragGumb.setVisible(UpraviteljPogleda.imaPovijesti());
        odjavaGumb.setText(prijevod.getPrijevod("odjavi_se_gumb"));
    }

    private void osvjeziJezicneStilove() {
        boolean jeHrvatski = Jezik.HR.getKod().equals(konfig.getJezik());
        promijeniStilLinkova(jeHrvatski);
    }

    public Button getNatragGumb() {
        return natragGumb;
    }
}