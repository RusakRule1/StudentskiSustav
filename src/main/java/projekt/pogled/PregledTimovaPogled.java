package projekt.pogled;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import projekt.model.TimJson;
import projekt.servis.TimJsonServis;
import projekt.upravitelj.UpraviteljPogleda;
import projekt.util.PorukaHelper;
import projekt.util.Stilovi;
import projekt.util.UITvornica;

import static projekt.util.UITvornica.*;

public class PregledTimovaPogled extends OsnovniPogled {

    private final TimJsonServis timServis;
    private final PorukaHelper poruke;

    private final Label naslov = labela().stil(Stilovi.PODNASLOV).build();

    private final TableColumn<TimJson, String> nazivKolona = UITvornica.<TimJson, String>kolona("naziv").build();
    private final TableColumn<TimJson, String> brojKolona = UITvornica.<TimJson, String>kolona(
            cellData -> new SimpleStringProperty(String.valueOf(cellData.getValue().getBrojClanova()))).build();

    private final TableColumn<TimJson, String> clanoviKolona = UITvornica.<TimJson, String>kolona(
            cellData -> new SimpleStringProperty(cellData.getValue().getClanoviFormatted())).build();

    private final ObservableList<TimJson> podaciTimova = FXCollections.observableArrayList();

    private final TableView<TimJson> tablicaTimova = UITvornica.<TimJson>tableView()
            .kolone(nazivKolona, brojKolona, clanoviKolona)
            .stavke(podaciTimova)
            .constrained()
            .stil(Stilovi.TABLICA_VISINA_VELIKA)
            .build();

    private Button noviTimGumb;
    private Button urediTimGumb;
    private Button obrisiTimGumb;
    private Button osvjeziGumb;

    public PregledTimovaPogled() {
        super();
        this.timServis = new TimJsonServis();
        this.poruke = PorukaHelper.kreiraj(prijevod);
        konfigurirajGumbe();
    }

    @Override
    protected VBox kreirajSadrzaj() {
        HBox kontroleBox = kreirajKontrole();

        VBox sadrzaj = vbox(naslov, tablicaTimova, poruke.getKontejner(), kontroleBox)
                .stil(Stilovi.RAZMAK_SREDNJI, Stilovi.PADDING_SREDNJI)
                .grow(Priority.ALWAYS)
                .build();

        VBox.setVgrow(tablicaTimova, Priority.ALWAYS);

        ucitajTimove();
        postaviListenere();

        return sadrzaj;
    }

    private void konfigurirajGumbe() {
        noviTimGumb = gumb(Stilovi.GUMB_PLAVI, this::otvoriKreiranjeTima).build();
        urediTimGumb = gumb(Stilovi.GUMB_ZELENI, this::otvoriUredivanjeTima).onemogucen(true).build();
        obrisiTimGumb = gumb(Stilovi.GUMB_CRVENI, this::obrisiTim).onemogucen(true).build();
        osvjeziGumb = gumb(Stilovi.GUMB_ZELENI, this::ucitajTimove).build();
    }

    private HBox kreirajKontrole() {
        return hbox(noviTimGumb, urediTimGumb, obrisiTimGumb, osvjeziGumb)
                .pozicija(Pos.CENTER)
                .stil(Stilovi.RAZMAK_KONTROLE)
                .build();
    }

    private void postaviListenere() {
        tablicaTimova.getSelectionModel().selectedItemProperty().addListener(
                (obs, stari, novi) -> azurirajOsjetljivostGumba(novi)
        );
    }

    private void azurirajOsjetljivostGumba(TimJson odabraniTim) {
        boolean imaSelekciju = odabraniTim != null;
        urediTimGumb.setDisable(!imaSelekciju);
        obrisiTimGumb.setDisable(!imaSelekciju);
    }

    private void ucitajTimove() {
        try {
            podaciTimova.clear();
            podaciTimova.addAll(timServis.dohvatiSveTimove());
            azurirajPlaceholder();
        } catch (Exception e) {
            System.err.println("Greška pri učitavanju timova: " + e.getMessage());
            poruke.prikaziGreskuSTimerom("greska_ucitavanje_timova");
        }
    }

    private void azurirajPlaceholder() {
        if (podaciTimova.isEmpty()) {
            tablicaTimova.setPlaceholder(labela(prijevod.getPrijevod("nema_timova")).build());
        }
    }

    private void otvoriKreiranjeTima() {
        UpraviteljPogleda.prikazi(new KreiranjeUredivanjeTimaPogled(null));
    }

    private void otvoriUredivanjeTima() {
        TimJson odabraniTim = tablicaTimova.getSelectionModel().getSelectedItem();
        UpraviteljPogleda.prikazi(new KreiranjeUredivanjeTimaPogled(odabraniTim));
    }

    private void obrisiTim() {
        izvrsiBrisanjeTima(tablicaTimova.getSelectionModel().getSelectedItem());
    }

    private void izvrsiBrisanjeTima(TimJson tim) {
        try {
            boolean uspjeh = timServis.obrisiTim(tim.getId());

            if (uspjeh) {
                ucitajTimove();
                poruke.prikaziUspjehSTimerom("tim_obrisan");
            } else {
                poruke.prikaziGreskuSTimerom("greska_tim_neobrisan");
            }

        } catch (Exception e) {
            System.err.println("Greška pri brisanju tima: " + e.getMessage());
            poruke.prikaziGreskuSTimerom("greska_tim_neobrisan");
        }
    }

    @Override
    public void priSakrivanju() {
        poruke.cleanup();
    }

    @Override
    protected void osvjeziPogledTekstove() {
        osvjeziNaslov();
        osvjeziTablicu();
        osvjeziGumbe();
        poruke.osvjeziPoruku();
    }

    private void osvjeziNaslov() {
        naslov.setText(prijevod.getPrijevod("tim_naslov"));
    }

    private void osvjeziTablicu() {
        nazivKolona.setText(prijevod.getPrijevod("tim_naziv"));
        brojKolona.setText(prijevod.getPrijevod("tim_broj_clanova"));
        clanoviKolona.setText(prijevod.getPrijevod("tim_clanovi"));
        azurirajPlaceholder();
    }

    private void osvjeziGumbe() {
        noviTimGumb.setText(prijevod.getPrijevod("novi_tim_gumb"));
        urediTimGumb.setText(prijevod.getPrijevod("uredi_tim_gumb"));
        obrisiTimGumb.setText(prijevod.getPrijevod("obrisi_tim_gumb"));
        osvjeziGumb.setText(prijevod.getPrijevod("osvjezi_gumb"));
    }
}
