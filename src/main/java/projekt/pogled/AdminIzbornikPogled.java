package projekt.pogled;

import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import projekt.upravitelj.UpraviteljPogleda;
import projekt.util.Stilovi;

public class AdminIzbornikPogled extends OsnovniPogled {

    private final Text dodajKorisnikaPodnaslovTekst = new Text();
    private final Button dodajKorisnikaGumb = new Button();
    private final Text logoviPodnaslovTekst = new Text();
    private final Button logoviGumb = new Button();

    public AdminIzbornikPogled() {
        super();
    }

    @Override
    protected VBox kreirajSadrzaj() {
        VBox sadrzajBox = new VBox();
        sadrzajBox.setAlignment(Pos.TOP_LEFT);
        sadrzajBox.getStyleClass().addAll(
                Stilovi.POZADINA_SVIJETLA,
                Stilovi.RAZMAK_VELIKI,
                Stilovi.PADDING_VELIKI
        );

        VBox dodajKorisnikaSekcija = kreirajDodajKorisnikaSekciju();
        VBox logoviSekcija = kreirajLogoviSekciju();

        sadrzajBox.getChildren().addAll(
                dodajKorisnikaSekcija,
                logoviSekcija
        );

        return sadrzajBox;
    }

    private VBox kreirajDodajKorisnikaSekciju() {
        VBox sekcija = new VBox();
        sekcija.setAlignment(Pos.TOP_LEFT);
        sekcija.getStyleClass().add(Stilovi.RAZMAK_MALI);

        konfigurirajDodajKorisnikaPodnaslov();
        konfigurirajDodajKorisnikaGumb();

        sekcija.getChildren().addAll(
                dodajKorisnikaPodnaslovTekst,
                dodajKorisnikaGumb
        );

        return sekcija;
    }

    private VBox kreirajLogoviSekciju() {
        VBox sekcija = new VBox();
        sekcija.setAlignment(Pos.TOP_LEFT);
        sekcija.getStyleClass().add(Stilovi.RAZMAK_MALI);

        konfigurirajLogoviPodnaslov();
        konfigurirajLogoviGumb();

        sekcija.getChildren().addAll(
                logoviPodnaslovTekst,
                logoviGumb
        );

        return sekcija;
    }

    private void konfigurirajDodajKorisnikaPodnaslov() {
        dodajKorisnikaPodnaslovTekst.getStyleClass().add(Stilovi.PODNASLOV_TEKST);
    }

    private void konfigurirajDodajKorisnikaGumb() {
        dodajKorisnikaGumb.getStyleClass().addAll(
                Stilovi.GUMB_PRIMARAN,
                Stilovi.GUMB_SIRINA_VELIKA
        );
        dodajKorisnikaGumb.setOnAction(e -> otvoriDodavanjeKorisnika());
    }

    private void konfigurirajLogoviPodnaslov() {
        logoviPodnaslovTekst.getStyleClass().add(Stilovi.PODNASLOV_TEKST);
    }

    private void konfigurirajLogoviGumb() {
        logoviGumb.getStyleClass().addAll(
                Stilovi.GUMB_PRIMARAN,
                Stilovi.GUMB_SIRINA_VELIKA
        );
        logoviGumb.setOnAction(e -> otvoriPregledLogova());
    }

    private void otvoriDodavanjeKorisnika() {
        UpraviteljPogleda.prikazi(new DodavanjeKorisnikaPogled());
    }

    private void otvoriPregledLogova() {
        UpraviteljPogleda.prikazi(new PregledZapisaPogled());
    }

    @Override
    protected void osvjeziPogledTekstove() {
        osvjeziDodajKorisnikaSekciju();
        osvjeziLogoviSekciju();
    }

    private void osvjeziDodajKorisnikaSekciju() {
        dodajKorisnikaPodnaslovTekst.setText(
                prijevod.getPrijevod("dodaj_korisnika_podnaslov")
        );
        dodajKorisnikaGumb.setText(
                prijevod.getPrijevod("dodaj_korisnika_gumb")
        );
    }

    private void osvjeziLogoviSekciju() {
        logoviPodnaslovTekst.setText(
                prijevod.getPrijevod("pregledaj_logove_podnaslov")
        );
        logoviGumb.setText(
                prijevod.getPrijevod("pregledaj_logove_gumb")
        );
    }
}