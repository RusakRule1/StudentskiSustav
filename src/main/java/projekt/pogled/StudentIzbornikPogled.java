package projekt.pogled;

import javafx.application.Platform;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import projekt.upravitelj.UpraviteljPogleda;
import projekt.util.Stilovi;

import java.util.List;

import static projekt.util.UITvornica.*;

public class StudentIzbornikPogled extends OsnovniPogled {

    private List<SekcijaInfo> sekcije;

    public StudentIzbornikPogled() {
        super();
        Platform.runLater(() -> prikaziInfoTraku(true));
    }

    @Override
    protected VBox kreirajSadrzaj() {
        sekcije = List.of(
                kreirajSekciju("dodaj_korisnika", this::otvoriDodavanjeKorisnika)
        );

        return vbox(sekcije.stream().map(SekcijaInfo::sekcija).toArray(Node[]::new)).stil(Stilovi.GLAVNI_VBOX).build();
    }

    private SekcijaInfo kreirajSekciju(String kljuc, Runnable akcija) {
        Text podnaslov = tekst().stil(Stilovi.PODNASLOV).build();

        Button gumbKomponenta = gumb(Stilovi.GUMB_PLAVI, akcija)
                .stil(Stilovi.GUMB_SIRINA_VELIKA)
                .build();

        VBox sekcija = vbox(podnaslov, gumbKomponenta)
                .stil(Stilovi.SEKCIJA)
                .build();

        return new SekcijaInfo(sekcija, podnaslov, gumbKomponenta, kljuc);
    }

    private void otvoriDodavanjeKorisnika() {
        UpraviteljPogleda.prikazi(new DodavanjeKorisnikaPogled());
    }

    @Override
    protected void osvjeziPogledTekstove() {
        sekcije.forEach(this::osvjeziSekciju);
    }

    private void osvjeziSekciju(SekcijaInfo sekcija) {
        sekcija.podnaslov().setText(prijevod.getPrijevod(sekcija.kljuc() + "_podnaslov"));
        sekcija.gumb().setText(prijevod.getPrijevod(sekcija.kljuc() + "_gumb"));
    }

    private record SekcijaInfo(VBox sekcija, Text podnaslov, Button gumb, String kljuc) {
    }
}