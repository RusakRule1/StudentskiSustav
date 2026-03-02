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
import projekt.model.Predmet;
import projekt.model.Profesor;
import projekt.servis.PredmetServis;
import projekt.upravitelj.UpraviteljPogleda;
import projekt.util.PorukaHelper;
import projekt.util.Stilovi;
import projekt.util.UITvornica;

import static projekt.util.UITvornica.*;

public class PregledPredmetaPogled extends OsnovniPogled {

    private final PredmetServis predmetServis;
    private final PorukaHelper poruke;

    private final Label naslov = labela().stil(Stilovi.PODNASLOV).build();

    private final ObservableList<Predmet> predmeti = FXCollections.observableArrayList();

    private final TableColumn<Predmet, String> nazivPredmetaKolona = UITvornica.<Predmet, String>kolona("naziv").build();
    private final TableColumn<Predmet, String> sifraPredmetaKolona = UITvornica.<Predmet, String>kolona("sifra").build();
    private final TableColumn<Predmet, Integer> ectsPredmetaKolona = UITvornica.<Predmet, Integer>kolona("ectsBodovi").build();
    private final TableColumn<Predmet, String> semestarPredmetaKolona = UITvornica.<Predmet, String>kolona("semestar").build();
    private final TableColumn<Predmet, Integer> godinaPredmetaKolona =
            UITvornica.<Predmet, Integer>kolona("godinaIzvodenja").build();
    private final TableColumn<Predmet, String> profesorPredmetaKolona =
            UITvornica.<Predmet, String>kolona(
                    data -> {
                        Profesor profesor = data.getValue().getProfesor();
                        return new SimpleStringProperty(
                                profesor == null ? "" : profesor.getPunoImeSTitulom()
                        );
                    }
            ).build();

    private final TableView<Predmet> tablicaPredmeta = UITvornica.<Predmet>tableView()
            .kolone(nazivPredmetaKolona, sifraPredmetaKolona, ectsPredmetaKolona,
                    semestarPredmetaKolona, godinaPredmetaKolona, profesorPredmetaKolona)
            .stavke(predmeti)
            .constrained()
            .stil(Stilovi.TABLICA_VISINA_MALA)
            .build();

    private Button noviPredmetGumb;
    private Button urediPredmetGumb;
    private Button obrisiPredmetGumb;
    private Button osvjeziGumb;

    public PregledPredmetaPogled() {
        super();
        this.predmetServis = new PredmetServis();
        this.poruke = PorukaHelper.kreiraj(prijevod);
        konfigurirajGumbe();
    }

    @Override
    protected VBox kreirajSadrzaj() {
        HBox kontroleBox = kreirajKontrole();

        VBox sadrzaj = vbox(naslov, tablicaPredmeta, poruke.getKontejner(), kontroleBox)
                .stil(Stilovi.RAZMAK_SREDNJI, Stilovi.PADDING_SREDNJI)
                .grow(Priority.ALWAYS)
                .build();

        VBox.setVgrow(tablicaPredmeta, Priority.ALWAYS);

        ucitajPredmete();
        postaviListenere();

        return sadrzaj;
    }

    private void konfigurirajGumbe() {
        noviPredmetGumb = gumb(Stilovi.GUMB_PLAVI, this::otvoriKreiranjePredmeta).build();
        urediPredmetGumb = gumb(Stilovi.GUMB_ZELENI, this::otvoriUredivanjePredmeta).onemogucen(true).build();
        obrisiPredmetGumb = gumb(Stilovi.GUMB_CRVENI, this::obrisiPredmet).onemogucen(true).build();
        osvjeziGumb = gumb(Stilovi.GUMB_ZELENI, this::ucitajPredmete).build();
    }

    private HBox kreirajKontrole() {
        return hbox(noviPredmetGumb, urediPredmetGumb, obrisiPredmetGumb, osvjeziGumb)
                .pozicija(Pos.CENTER)
                .stil(Stilovi.RAZMAK_KONTROLE)
                .build();
    }

    private void postaviListenere() {
        tablicaPredmeta.getSelectionModel().selectedItemProperty().addListener(
                (obs, stari, novi) -> azurirajOsjetljivostGumba(novi)
        );
    }

    private void azurirajOsjetljivostGumba(Predmet odabraniPredmet) {
        boolean imaSelekciju = odabraniPredmet != null;
        urediPredmetGumb.setDisable(!imaSelekciju);
        obrisiPredmetGumb.setDisable(!imaSelekciju);
    }

    private void ucitajPredmete() {
        try {
            predmeti.clear();
            predmeti.addAll(predmetServis.vratiSve());
            azurirajPlaceholder();
        } catch (Exception e) {
            System.err.println("Greška pri učitavanju predmeta: " + e.getMessage());
            poruke.prikaziGreskuSTimerom("greska_ucitavanje_predmeta");
        }
    }

    private void azurirajPlaceholder() {
        if (predmeti.isEmpty()) {
            tablicaPredmeta.setPlaceholder(labela(prijevod.getPrijevod("nema_predmeta")).build());
        }
    }

    private void otvoriKreiranjePredmeta() {
        UpraviteljPogleda.prikazi(new KreiranjeUredivanjePredmetaPogled(null));
    }

    private void otvoriUredivanjePredmeta() {
        Predmet odabraniPredmet = tablicaPredmeta.getSelectionModel().getSelectedItem();
        UpraviteljPogleda.prikazi(new KreiranjeUredivanjePredmetaPogled(odabraniPredmet));
    }

    private void obrisiPredmet() {
        izvrsiBrisanjePredmeta(tablicaPredmeta.getSelectionModel().getSelectedItem());
    }

    private void izvrsiBrisanjePredmeta(Predmet predmet) {
        try {
            predmetServis.obrisiPredmet(predmet);
            ucitajPredmete();
            poruke.prikaziUspjehSTimerom("predmet_obrisan");
        } catch (Exception e) {
            System.err.println("Greška pri brisanju predmeta: " + e.getMessage());
            poruke.prikaziGreskuSTimerom("greska_predmet_neobrisan");
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
        naslov.setText(prijevod.getPrijevod("predmet_naslov"));
    }

    private void osvjeziTablicu() {
        nazivPredmetaKolona.setText(prijevod.getPrijevod("predmet_naziv"));
        sifraPredmetaKolona.setText(prijevod.getPrijevod("predmet_sifra"));
        ectsPredmetaKolona.setText(prijevod.getPrijevod("predmet_ects"));
        semestarPredmetaKolona.setText(prijevod.getPrijevod("predmet_semestar"));
        godinaPredmetaKolona.setText(prijevod.getPrijevod("predmet_godina"));
        profesorPredmetaKolona.setText(prijevod.getPrijevod("predmet_profesor"));
        azurirajPlaceholder();
    }

    private void osvjeziGumbe() {
        noviPredmetGumb.setText(prijevod.getPrijevod("novi_predmet_gumb"));
        urediPredmetGumb.setText(prijevod.getPrijevod("uredi_predmet_gumb"));
        obrisiPredmetGumb.setText(prijevod.getPrijevod("obrisi_predmet_gumb"));
        osvjeziGumb.setText(prijevod.getPrijevod("osvjezi_gumb"));
    }
}
