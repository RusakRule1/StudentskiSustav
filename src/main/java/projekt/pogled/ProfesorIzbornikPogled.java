package projekt.pogled;

import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import projekt.upravitelj.UpraviteljPogleda;
import projekt.util.Stilovi;

public class ProfesorIzbornikPogled extends OsnovniPogled {

    private SekcijaInfo timoviSekcija;
    private SekcijaInfo materijaliSekcija;

    public ProfesorIzbornikPogled() {
        super();
    }

    @Override
    protected VBox kreirajSadrzaj() {
        VBox sadrzaj = kreirajGlavniSadrzaj(Pos.TOP_LEFT);

        timoviSekcija = kreirajSekciju("timovi", this::otvoriPregledTimova);
        materijaliSekcija = kreirajSekciju("materijali", this::otvoriPregledMaterijala);

        sadrzaj.getChildren().addAll(
                timoviSekcija.sekcija(),
                materijaliSekcija.sekcija()
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

    private void otvoriPregledTimova() {
        UpraviteljPogleda.prikazi(new PregledTimovaPogled());
    }

    private void otvoriPregledMaterijala() {
        UpraviteljPogleda.prikazi(new PregledMaterijalaPogled());
    }


    @Override
    protected void osvjeziPogledTekstove() {
        osvjeziSekciju(timoviSekcija);
        osvjeziSekciju(materijaliSekcija);
    }

    private void osvjeziSekciju(SekcijaInfo sekcija) {
        sekcija.podnaslov().setText(prijevod.getPrijevod(sekcija.kljuc() + "_podnaslov"));
        sekcija.gumb().setText(prijevod.getPrijevod(sekcija.kljuc() + "_gumb"));
    }

    private record SekcijaInfo(VBox sekcija, Text podnaslov, Button gumb, String kljuc) {
    }
}
