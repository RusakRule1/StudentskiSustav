package projekt.pogled;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import projekt.upravitelj.UpraviteljPogleda;
import projekt.util.Stilovi;

public class AdminIzbornikPogled extends OsnovniPogled {

    private static final int RAZMAK_SADRZAJ = 30;
    private static final int PADDING_SADRZAJ = 40;
    private static final int SIRINA_GUMBA = 300;

    private final Text dodajKorisnikaPodnaslovTekst = new Text();
    private final Button dodajKorisnikaGumb = new Button();
    private final Text logoviPodnaslovTekst = new Text();
    private final Button logoviGumb = new Button();

    public AdminIzbornikPogled() {
        super();
    }

    @Override
    protected VBox kreirajSadrzaj() {
        VBox sadrzajBox = new VBox(RAZMAK_SADRZAJ);
        sadrzajBox.setPadding(new Insets(PADDING_SADRZAJ));
        sadrzajBox.setAlignment(Pos.TOP_LEFT);
        sadrzajBox.getStyleClass().add(Stilovi.POZADINA_SVIJETLA);

        VBox dodajKorisnikaSekcija = kreirajDodajKorisnikaSekciju();
        VBox logoviSekcija = kreirajLogoviSekciju();

        sadrzajBox.getChildren().addAll(
                dodajKorisnikaSekcija,
                logoviSekcija
        );

        return sadrzajBox;
    }

    private VBox kreirajDodajKorisnikaSekciju() {
        VBox sekcija = new VBox(10);
        sekcija.setAlignment(Pos.TOP_LEFT);

        konfigurirajDodajKorisnikaPodnaslov();
        konfigurirajDodajKorisnikaGumb();

        sekcija.getChildren().addAll(
                dodajKorisnikaPodnaslovTekst,
                dodajKorisnikaGumb
        );

        return sekcija;
    }

    private VBox kreirajLogoviSekciju() {
        VBox sekcija = new VBox(10);
        sekcija.setAlignment(Pos.TOP_LEFT);

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
        dodajKorisnikaGumb.getStyleClass().add(Stilovi.GUMB_PRIMARAN);
        dodajKorisnikaGumb.setPrefWidth(SIRINA_GUMBA);
        dodajKorisnikaGumb.setOnAction(e -> otvoriDodavanjeKorisnika());
    }

    private void konfigurirajLogoviPodnaslov() {
        logoviPodnaslovTekst.getStyleClass().add(Stilovi.PODNASLOV_TEKST);
    }

    private void konfigurirajLogoviGumb() {
        logoviGumb.getStyleClass().add(Stilovi.GUMB_PRIMARAN);
        logoviGumb.setPrefWidth(SIRINA_GUMBA);
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
