package projekt.pogled;

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

    protected Stage prozor;
    protected Konfiguracija konfig;
    protected Prijevod prijevod;
    protected UpraviteljZapisima upraviteljZapisima;

    protected BorderPane korijen;
    protected final Text naslovAplikacijeTekst = new Text();
    protected final Hyperlink hrVeza = new Hyperlink(Jezik.HR.getKod());
    protected final Hyperlink enVeza = new Hyperlink(Jezik.EN.getKod());
    protected VBox glavniSadrzajKontejner;
    protected final Label prijavljenKorisnikLabela = new Label();
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
        korijen = new BorderPane();
        dodajTrake();
        dodajSadrzaj();
        postaviScenu();
        osvjeziTekstove();
    }

    private void dodajTrake() {
        HBox gornjaTraka = kreirajGornjuTraku();
        HBox infoTraka = kreirajInfoTraku();

        VBox trakeKontejner = new VBox(gornjaTraka, infoTraka);
        trakeKontejner.getStyleClass().add(Stilovi.TRAKE_KONTEJNER);

        korijen.setTop(trakeKontejner);
    }

    private void dodajSadrzaj() {
        glavniSadrzajKontejner = kreirajSadrzaj();
        korijen.setCenter(glavniSadrzajKontejner);
        VBox.setVgrow(glavniSadrzajKontejner, Priority.ALWAYS);
    }

    protected VBox kreirajGlavniSadrzaj(Pos pozicija) {
        VBox kontejner = new VBox();
        kontejner.setAlignment(pozicija);
        kontejner.getStyleClass().addAll(
                Stilovi.RAZMAK_SREDNJI,
                Stilovi.PADDING_SREDNJI
        );
        return kontejner;
    }

    protected VBox kreirajGlavniSadrzaj(Pos pozicija, String... dodatniCss) {
        VBox kontejner = kreirajGlavniSadrzaj(pozicija);
        kontejner.getStyleClass().addAll(dodatniCss);
        return kontejner;
    }

    private void postaviScenu() {
        Scene scena = new Scene(korijen, konfig.getSirinaProzora(), konfig.getVisinaProzora());
        scena.getStylesheets().addAll(ucitajStylesheets());
        prozor.setScene(scena);
        prozor.setResizable(false);
    }

    private List<String> ucitajStylesheets() {
        return List.of(
                vratiPutanjuResursa(CSS_GLOBALNI),
                vratiPutanjuResursa(CSS_KOMPONENTE)
        );
    }

    private String vratiPutanjuResursa(String resurs) {
        return Objects.requireNonNull(
                getClass().getResource(resurs),
                "Stylesheet nije pronađen: " + resurs
        ).toExternalForm();
    }

    private HBox kreirajGornjuTraku() {
        HBox traka = new HBox();
        traka.getStyleClass().add(Stilovi.GORNJA_TRAKA);
        naslovAplikacijeTekst.getStyleClass().add(Stilovi.NASLOV_APLIKACIJE_TEKST);

        traka.getChildren().addAll(naslovAplikacijeTekst, kreirajRazmak(), kreirajJezicniBox());
        return traka;
    }

    private HBox kreirajRazmak() {
        HBox spacer = new HBox();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        return spacer;
    }

    private HBox kreirajInfoTraku() {
        HBox infoTraka = new HBox();
        infoTraka.getStyleClass().add(Stilovi.INFO_TRAKA);

        konfigurirajNatragGumb();
        konfigurirajOdjavaGumb();
        prijavljenKorisnikLabela.getStyleClass().add(Stilovi.LABELA_INFORMACIJA);

        infoTraka.getChildren().addAll(natragGumb, kreirajRazmak(), prijavljenKorisnikLabela, odjavaGumb);

        return infoTraka;
    }

    private void konfigurirajNatragGumb() {
        natragGumb.getStyleClass().add(Stilovi.GUMB_ZELENI);
        natragGumb.setVisible(false);
        natragGumb.setOnAction(e -> UpraviteljPogleda.idiNatrag());
    }

    private void konfigurirajOdjavaGumb() {
        odjavaGumb.getStyleClass().add(Stilovi.GUMB_CRVENI);
        odjavaGumb.setOnAction(e -> obradiOdjavu());
    }

    private void obradiOdjavu() {
        UpraviteljZapisima.getInstance().dodajZapis(new Zapis(
                Sesija.getInstanca().getPrijavljeniKorisnik().getEmail(),
                ZapisAkcija.ODJAVA,
                "Uspješna odjava korisnika s ulogom: " + Sesija.getInstanca().getPrijavljeniKorisnik().getUloga()
        ));
        Sesija.getInstanca().odjaviKorisnika();
        UpraviteljPogleda.prikaziBezPovijesti(new LoginPogled());
    }

    private HBox kreirajJezicniBox() {
        HBox jeziciBox = new HBox();
        jeziciBox.getStyleClass().add(Stilovi.JEZICI);
        konfigurirajJezicneVeze();
        Label separator = new Label(SEPARATOR);
        jeziciBox.getChildren().addAll(hrVeza, separator, enVeza);
        return jeziciBox;
    }

    private void konfigurirajJezicneVeze() {
        hrVeza.setFocusTraversable(false);
        enVeza.setFocusTraversable(false);
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

        prijavljenKorisnikLabela.setText(formatiraniTekst);
        postaviVidljivostKorisnickeInfo(true);
    }

    private void sakrijInfoKorisnika() {
        postaviVidljivostKorisnickeInfo(false);
    }

    private void postaviVidljivostKorisnickeInfo(boolean vidljivo) {
        prijavljenKorisnikLabela.setVisible(vidljivo);
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
        naslovAplikacijeTekst.setText(prijevod.getPrijevod("naslov_aplikacija"));
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

    protected Button kreirajGumb(String stil, Runnable akcija) {
        Button gumb = new Button();
        gumb.getStyleClass().add(stil);
        gumb.setOnAction(e -> akcija.run());
        return gumb;
    }

    protected Button kreirajGumb(String stil, Runnable akcija, String... dodatniStiloviGumba) {
        Button gumb = kreirajGumb(stil, akcija);
        gumb.getStyleClass().addAll(dodatniStiloviGumba);
        return gumb;
    }
}