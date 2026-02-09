package projekt.pogled;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import projekt.upravitelj.UpraviteljPogleda;
import projekt.util.Stilovi;

public class ProfesorIzbornikPogled extends OsnovniPogled {
    private static final int RAZMAK_SADRZAJ = 30;
    private static final int PADDING_SADRZAJ = 40;
    private static final int SIRINA_GUMBA = 300;

    private final Text timoviPodnaslovTekst = new Text();
    private final Button timoviGumb = new Button();
    private final Text materijaliPodnaslovTekst = new Text();
    private final Button materijaliGumb = new Button();

    public ProfesorIzbornikPogled() {
        super();
    }

    @Override
    protected VBox kreirajSadrzaj() {
        VBox sadrzajBox = new VBox(RAZMAK_SADRZAJ);
        sadrzajBox.setPadding(new Insets(PADDING_SADRZAJ));
        sadrzajBox.setAlignment(Pos.TOP_LEFT);
        sadrzajBox.getStyleClass().add(Stilovi.POZADINA_SVIJETLA);

        VBox timoviSekcija = kreirajTimoviSekciju();
        VBox materijaliSekcija = kreirajMaterijaliSekciju();

        sadrzajBox.getChildren().addAll(
                timoviSekcija,
                materijaliSekcija
        );

        return sadrzajBox;
    }

    private VBox kreirajTimoviSekciju() {
        VBox sekcija = new VBox(10);
        sekcija.setAlignment(Pos.TOP_LEFT);

        konfigurirajTimoviPodnaslov();
        konfigurirajTimoviGumb();

        sekcija.getChildren().addAll(
                timoviPodnaslovTekst,
                timoviGumb
        );

        return sekcija;
    }

    private VBox kreirajMaterijaliSekciju() {
        VBox sekcija = new VBox(10);
        sekcija.setAlignment(Pos.TOP_LEFT);

        konfigurirajMaterijaliPodnaslov();
        konfigurirajMaterijaliGumb();

        sekcija.getChildren().addAll(
                materijaliPodnaslovTekst,
                materijaliGumb
        );

        return sekcija;
    }

    private void konfigurirajTimoviPodnaslov() {
        timoviPodnaslovTekst.getStyleClass().add(Stilovi.PODNASLOV_TEKST);
    }

    private void konfigurirajTimoviGumb() {
        timoviGumb.getStyleClass().add(Stilovi.GUMB_PRIMARAN);
        timoviGumb.setPrefWidth(SIRINA_GUMBA);
        timoviGumb.setOnAction(e -> otvoriPregledTimova());
    }

    private void konfigurirajMaterijaliPodnaslov() {
        materijaliPodnaslovTekst.getStyleClass().add(Stilovi.PODNASLOV_TEKST);
    }

    private void konfigurirajMaterijaliGumb() {
        materijaliGumb.getStyleClass().add(Stilovi.GUMB_PRIMARAN);
        materijaliGumb.setPrefWidth(SIRINA_GUMBA);
        materijaliGumb.setOnAction(e -> otvoriPregledMaterijala());
    }

    private void otvoriPregledTimova() {
        UpraviteljPogleda.prikazi(new PregledTimovaPogled());
    }

    private void otvoriPregledMaterijala() {
        UpraviteljPogleda.prikazi(new PregledMaterijalaPogled());
    }

    @Override
    protected void osvjeziPogledTekstove() {
        osvjeziTimoviSekciju();
        osvjeziMaterijaliSekciju();
    }

    private void osvjeziTimoviSekciju() {
        timoviPodnaslovTekst.setText(
                prijevod.getPrijevod("timovi_podnaslov")
        );
        timoviGumb.setText(
                prijevod.getPrijevod("timovi_gumb")
        );
    }

    private void osvjeziMaterijaliSekciju() {
        materijaliPodnaslovTekst.setText(
                prijevod.getPrijevod("materijali_podnaslov")
        );
        materijaliGumb.setText(
                prijevod.getPrijevod("materijali_gumb")
        );
    }
}
