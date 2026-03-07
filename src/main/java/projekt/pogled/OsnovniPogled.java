package projekt.pogled;

import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
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

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Stream;

import static projekt.util.UITvornica.*;

public abstract class OsnovniPogled {

    private static final String CSS_GLOBALNI = "/css/globalni.css";
    private static final String CSS_KOMPONENTE = "/css/komponente.css";
    private static final String SEPARATOR = "|";

    protected Stage prozor;
    protected Konfiguracija konfig;
    protected Prijevod prijevod;
    protected UpraviteljZapisima upraviteljZapisima;

    protected BorderPane korijen;
    protected HBox gornjaTraka;
    protected HBox infoTraka;
    protected final Text naslovAplikacijeTekst = tekst().stil(Stilovi.NASLOV_APLIKACIJE_TEKST).build();
    protected final Hyperlink hrVeza = new Hyperlink(Jezik.HR.getKod());
    protected final Hyperlink enVeza = new Hyperlink(Jezik.EN.getKod());
    protected final Label prijavljenKorisnikLabela = labela().stil(Stilovi.LABELA_INFORMACIJA).build();
    protected Button odjavaGumb;
    protected Button natragGumb;

    public OsnovniPogled() {
        this.konfig = Konfiguracija.getInstanca();
        this.prijevod = Prijevod.getInstanca();
        this.upraviteljZapisima = UpraviteljZapisima.getInstanca();
    }

    protected abstract VBox kreirajSadrzaj();

    protected abstract void osvjeziPogledTekstove();

    public void priPrikazivanju() {
    }

    public void priSakrivanju() {
    }

    protected Pos dohvatiPoravnanjeSadrzaja() {
        return Pos.TOP_LEFT;
    }

    public void prikazi(Stage glavniProzor) {
        this.prozor = glavniProzor;
        kreirajGUI();
        postaviEventHandlere();
    }

    private void kreirajGUI() {
        korijen = new BorderPane();

        kreirajGornjuTraku();
        kreirajInfoTraku();

        VBox centriranSadrzaj = vbox(kreirajSadrzaj())
                .pozicija(dohvatiPoravnanjeSadrzaja())
                .grow(Priority.ALWAYS)
                .build();

        VBox savSadrzaj = vbox(
                vbox(gornjaTraka, infoTraka).build(),
                centriranSadrzaj
        ).build();

        ScrollPane scroll = scrollPane(savSadrzaj)
                .fitSirinu(true)
                .fitVisinu(true)
                .pannable(true)
                .stil(Stilovi.SCROLL_PANE_SADRZAJ)
                .build();

        korijen.setCenter(scroll);

        postaviScenu();
        osvjeziTekstove();
    }

    private void postaviScenu() {
        Scene scena = new Scene(korijen);
        scena.getStylesheets().addAll(ucitajStylesheets());
        prozor.setScene(scena);
        prozor.setWidth(konfig.getSirinaProzora());
        prozor.setHeight(konfig.getVisinaProzora());
        prozor.setResizable(true);
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

    protected void prikaziInfoTraku(boolean prikazi) {
        infoTraka.setVisible(prikazi);
        infoTraka.setManaged(prikazi);
    }

    private void kreirajGornjuTraku() {
        gornjaTraka = hbox(naslovAplikacijeTekst, razmak(), kreirajJezicniBox())
                .stil(Stilovi.GORNJA_TRAKA)
                .build();
    }

    private void kreirajInfoTraku() {
        konfigurirajNatragGumb();
        konfigurirajOdjavaGumb();

        infoTraka = hbox(natragGumb, razmak(), prijavljenKorisnikLabela, odjavaGumb)
                .stil(Stilovi.INFO_TRAKA)
                .build();
    }

    private void konfigurirajNatragGumb() {
        natragGumb = gumb(Stilovi.GUMB_ZELENI, UpraviteljPogleda::idiNatrag)
                .stil(Stilovi.GUMB_SIRINA_MALA)
                .vidljivo(false)
                .build();
    }

    private void konfigurirajOdjavaGumb() {
        odjavaGumb = gumb(Stilovi.GUMB_CRVENI, this::obradiOdjavu)
                .stil(Stilovi.GUMB_SIRINA_MALA)
                .build();
    }

    private void obradiOdjavu() {
        Sesija sesija = Sesija.getInstanca();
        Korisnik korisnik = sesija.getPrijavljeniKorisnik();
        if (korisnik != null) {
            UpraviteljZapisima.getInstanca().dodajZapis(
                    new Zapis(korisnik.getEmail(), ZapisAkcija.ODJAVA, "", LocalDateTime.now()));
        }
        sesija.odjaviKorisnika();
        UpraviteljPogleda.prikaziBezPovijesti(new PrijavaPogled());
    }

    private HBox kreirajJezicniBox() {
        konfigurirajJezicneVeze();
        return hbox(hrVeza, labela(SEPARATOR).build(), enVeza)
                .stil(Stilovi.JEZICI)
                .build();
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

    private void osvjeziInfoKorisnika() {
        Optional.ofNullable(Sesija.getInstanca().getPrijavljeniKorisnik())
                .ifPresentOrElse(
                        this::prikaziInfoKorisnika,
                        this::sakrijInfoKorisnika
                );
    }

    private void prikaziInfoKorisnika(Korisnik korisnik) {
        try {
            prijavljenKorisnikLabela.setText(String.format("%s: %s %s",
                    prijevod.getPrijevod("prijavljeni_korisnik"),
                    korisnik.getIme(),
                    korisnik.getPrezime()
            ));
            postaviVidljivostKorisnickeInfo(true);
        } catch (RuntimeException e) {
            prijavljenKorisnikLabela.setText(prijevod.getPrijevod("greska_ucitavanja_korisnika"));
            postaviVidljivostKorisnickeInfo(false);
        }
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
        promijeniStilLinkova(Jezik.HR.getKod().equals(konfig.getJezik()));
    }
}
