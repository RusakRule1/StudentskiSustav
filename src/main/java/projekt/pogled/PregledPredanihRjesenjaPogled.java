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
import projekt.model.PredajaZadatka;
import projekt.model.StatusPredaje;
import projekt.model.Zadatak;
import projekt.servis.PredajaServis;
import projekt.upravitelj.UpraviteljPogleda;
import projekt.util.PorukaHelper;
import projekt.util.Stilovi;
import projekt.util.UITvornica;

import java.time.format.DateTimeFormatter;

import static projekt.util.UITvornica.*;

public class PregledPredanihRjesenjaPogled extends OsnovniPogled {

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("dd.MM.yyyy. HH:mm");

    private final PredajaServis predajaServis;
    private final PorukaHelper poruke;
    private final Zadatak zadatak;

    private final Label naslov = labela().stil(Stilovi.PODNASLOV).build();
    private final Label opisZadatka = labela().stil(Stilovi.LABELA_INFORMACIJA).build();

    private final ObservableList<PredajaZadatka> predaje = FXCollections.observableArrayList();

    private final TableColumn<PredajaZadatka, String> studentKolona = UITvornica.<PredajaZadatka, String>kolona(
            data -> {
                var student = data.getValue().getStudent();
                return new SimpleStringProperty(student.vratiPunoIme());
            }
    ).build();
    private final TableColumn<PredajaZadatka, String> nazivDatotekeKolona = UITvornica.<PredajaZadatka, String>kolona(
            data -> new SimpleStringProperty(data.getValue().vratiNazivDatoteke())
    ).build();
    private final TableColumn<PredajaZadatka, String> tipDatotekeKolona =
            UITvornica.<PredajaZadatka, String>kolona("tipDatoteke").build();
    private final TableColumn<PredajaZadatka, String> velicinaDatotekeKolona = UITvornica.<PredajaZadatka, String>kolona(
            data -> new SimpleStringProperty(formatirajVelicinuDatoteke(data.getValue().getVelicinaDatoteke()))
    ).build();
    private final TableColumn<PredajaZadatka, String> datumPredajeKolona = UITvornica.<PredajaZadatka, String>kolona(
            data -> new SimpleStringProperty(
                    data.getValue().getDatumPredaje() != null
                            ? data.getValue().getDatumPredaje().format(FORMATTER)
                            : ""
            )
    ).build();
    private final TableColumn<PredajaZadatka, String> statusKolona = UITvornica.<PredajaZadatka, String>kolona(
            data -> new SimpleStringProperty(dohvatiStatusTekst(data.getValue().getStatus()))
    ).build();

    private final TableView<PredajaZadatka> tablicaPredaja = UITvornica.<PredajaZadatka>tableView()
            .kolone(studentKolona, nazivDatotekeKolona, tipDatotekeKolona,
                    velicinaDatotekeKolona, datumPredajeKolona, statusKolona)
            .stavke(predaje)
            .constrained()
            .stil(Stilovi.TABLICA_VISINA_VELIKA)
            .build();

    private Button ocijeniGumb;
    private Button obrisiOcjenuGumb;
    private Button osvjeziGumb;

    public PregledPredanihRjesenjaPogled(Zadatak zadatak) {
        super();
        this.zadatak = zadatak;
        this.predajaServis = new PredajaServis();
        this.poruke = PorukaHelper.kreiraj(prijevod);
        konfigurirajGumbe();
    }

    @Override
    protected VBox kreirajSadrzaj() {
        HBox kontroleBox = hbox(ocijeniGumb, obrisiOcjenuGumb, osvjeziGumb)
                .pozicija(Pos.CENTER)
                .stil(Stilovi.RAZMAK_KONTROLE)
                .build();

        VBox sadrzaj = vbox(naslov, opisZadatka, tablicaPredaja, poruke.getKontejner(), kontroleBox)
                .stil(Stilovi.RAZMAK_SREDNJI, Stilovi.PADDING_SREDNJI)
                .grow(Priority.ALWAYS)
                .build();

        VBox.setVgrow(tablicaPredaja, Priority.ALWAYS);

        ucitajPredaje();
        postaviListenere();

        return sadrzaj;
    }

    private void konfigurirajGumbe() {
        ocijeniGumb = gumb(Stilovi.GUMB_PLAVI, this::otvoriOcjenjivanje).onemogucen(true).build();
        obrisiOcjenuGumb = gumb(Stilovi.GUMB_CRVENI, this::obrisiOcjenu).onemogucen(true).build();
        osvjeziGumb = gumb(Stilovi.GUMB_ZELENI, this::ucitajPredaje).build();
    }

    private void postaviListenere() {
        tablicaPredaja.getSelectionModel().selectedItemProperty().addListener(
                (obs, stari, novi) -> azurirajGumbe(novi)
        );
    }

    private void azurirajGumbe(PredajaZadatka odabrana) {
        boolean nijeOdabrana = odabrana == null;
        boolean jeOcjenjeno = !nijeOdabrana && odabrana.jeLiOcjenjeno();
        ocijeniGumb.setDisable(nijeOdabrana);
        obrisiOcjenuGumb.setDisable(!jeOcjenjeno);
        ocijeniGumb.setText(jeOcjenjeno
                ? prijevod.getPrijevod("promjeni_ocjenu_gumb")
                : prijevod.getPrijevod("ocijeni_gumb"));
    }

    private void ucitajPredaje() {
        try {
            predaje.clear();
            predaje.addAll(predajaServis.vratiPredajeZadatka(zadatak.getId()));
            tablicaPredaja.getSelectionModel().clearSelection();
            azurirajGumbe(null);
            azurirajPlaceholder();
        } catch (Exception e) {
            System.err.println("Greška pri učitavanju predanih rješenja: " + e.getMessage());
            poruke.prikaziGreskuSTimerom("greska_ucitavanje_predaja");
        }
    }

    private void otvoriOcjenjivanje() {
        PredajaZadatka odabrana = tablicaPredaja.getSelectionModel().getSelectedItem();
        if (odabrana == null) return;
        UpraviteljPogleda.prikazi(new OcjenjivanjeRjesenjaPogled(odabrana));
    }

    private void obrisiOcjenu() {
        PredajaZadatka odabrana = tablicaPredaja.getSelectionModel().getSelectedItem();
        if (odabrana == null) return;
        try {
            predajaServis.obrisiOcjenu(odabrana);
            odabrana.setOcjena(null);
            odabrana.setStatus(StatusPredaje.PREDANO);
            tablicaPredaja.refresh();
            azurirajGumbe(odabrana);
            poruke.prikaziUspjehSTimerom("ocjena_obrisana");
        } catch (Exception e) {
            System.err.println("Greška pri brisanju ocjene: " + e.getMessage());
            poruke.prikaziGreskuSTimerom("greska_brisanje_ocjene");
        }
    }

    private void azurirajPlaceholder() {
        if (predaje.isEmpty()) {
            tablicaPredaja.setPlaceholder(labela(prijevod.getPrijevod("nema_predaja")).build());
        }
    }

    private String dohvatiStatusTekst(StatusPredaje status) {
        return switch (status) {
            case PREDANO -> prijevod.getPrijevod("status_predano");
            case OCJENJENO -> prijevod.getPrijevod("status_ocjenjeno");
        };
    }

    private static String formatirajVelicinuDatoteke(Long velicina) {
        if (velicina == null) return "";
        if (velicina < 1024) return velicina + " B";
        if (velicina < 1024 * 1024) return String.format("%.1f KB", velicina / 1024.0);
        return String.format("%.1f MB", velicina / (1024.0 * 1024.0));
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
        naslov.setText(prijevod.getPrijevod("predana_rjesenja_naslov"));
        opisZadatka.setText(zadatak.getNaziv() + " — " + zadatak.getPredmet().getNaziv());
    }

    private void osvjeziTablicu() {
        studentKolona.setText(prijevod.getPrijevod("predaja_student"));
        nazivDatotekeKolona.setText(prijevod.getPrijevod("predaja_naziv_datoteke"));
        tipDatotekeKolona.setText(prijevod.getPrijevod("predaja_tip_datoteke"));
        velicinaDatotekeKolona.setText(prijevod.getPrijevod("predaja_velicina"));
        datumPredajeKolona.setText(prijevod.getPrijevod("predaja_datum"));
        statusKolona.setText(prijevod.getPrijevod("predaja_status"));
        tablicaPredaja.refresh();
        azurirajPlaceholder();
    }

    private void osvjeziGumbe() {
        PredajaZadatka odabrana = tablicaPredaja.getSelectionModel().getSelectedItem();
        azurirajGumbe(odabrana);
        obrisiOcjenuGumb.setText(prijevod.getPrijevod("obrisi_ocjenu_gumb"));
        osvjeziGumb.setText(prijevod.getPrijevod("osvjezi_gumb"));
    }
}
