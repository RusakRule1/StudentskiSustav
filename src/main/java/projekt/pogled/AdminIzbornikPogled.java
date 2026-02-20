package projekt.pogled;

import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import projekt.upravitelj.UpraviteljPogleda;
import projekt.util.Stilovi;

public class AdminIzbornikPogled extends OsnovniPogled {

    private SekcijaInfo dodajKorisnikaSekcija;
    private SekcijaInfo logoviSekcija;

    public AdminIzbornikPogled() {
        super();
    }

    @Override
    protected VBox kreirajSadrzaj() {
        VBox sadrzaj = kreirajGlavniSadrzaj(Pos.TOP_LEFT);

        dodajKorisnikaSekcija = kreirajSekciju("dodaj_korisnika", this::otvoriDodavanjeKorisnika);
        logoviSekcija = kreirajSekciju("pregledaj_logove", this::otvoriPregledLogova);

        sadrzaj.getChildren().addAll(
                dodajKorisnikaSekcija.sekcija(),
                logoviSekcija.sekcija()
        );

        return sadrzaj;
    }

    private SekcijaInfo kreirajSekciju(String kljuc, Runnable akcija) {
        VBox sekcija = new VBox();
        sekcija.getStyleClass().add(Stilovi.SEKCIJA);

        Text podnaslov = new Text();
        podnaslov.getStyleClass().add(Stilovi.PODNASLOV);

        Button gumb = kreirajGumb(Stilovi.GUMB_PLAVI, akcija, Stilovi.GUMB_SIRINA_VELIKA);

        sekcija.getChildren().addAll(podnaslov, gumb);
        return new SekcijaInfo(sekcija, podnaslov, gumb, kljuc);
    }

    private void otvoriDodavanjeKorisnika() {
        UpraviteljPogleda.prikazi(new DodavanjeKorisnikaPogled());
    }

    private void otvoriPregledLogova() {
        UpraviteljPogleda.prikazi(new PregledZapisaPogled());
    }

    @Override
    protected void osvjeziPogledTekstove() {
        osvjeziSekciju(dodajKorisnikaSekcija);
        osvjeziSekciju(logoviSekcija);
    }

    private void osvjeziSekciju(SekcijaInfo sekcija) {
        sekcija.podnaslov().setText(prijevod.getPrijevod(sekcija.kljuc() + "_podnaslov"));
        sekcija.gumb().setText(prijevod.getPrijevod(sekcija.kljuc() + "_gumb"));
    }

    private record SekcijaInfo(VBox sekcija, Text podnaslov, Button gumb, String kljuc) {
    }
}