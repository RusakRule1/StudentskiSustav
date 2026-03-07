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
import projekt.model.StatusPredaje;
import projekt.model.Student;
import projekt.model.Zadatak;
import projekt.servis.PredajaServis;
import projekt.servis.ZadatakServis;
import projekt.upravitelj.Sesija;
import projekt.upravitelj.UpraviteljPogleda;
import projekt.util.PorukaHelper;
import projekt.util.Stilovi;
import projekt.util.UITvornica;

import java.time.format.DateTimeFormatter;
import java.util.Map;

import static projekt.util.UITvornica.*;

public class PregledZadatakaStudentPogled extends OsnovniPogled {

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("dd.MM.yyyy. HH:mm");

    private final ZadatakServis zadatakServis;
    private final PredajaServis predajaServis;
    private final PorukaHelper poruke;

    private final Label naslov = labela().stil(Stilovi.PODNASLOV).build();

    private final ObservableList<Zadatak> zadaci = FXCollections.observableArrayList();
    private Map<Integer, StatusPredaje> statusPredaje = Map.of();

    private final TableColumn<Zadatak, String> nazivKolona =
            UITvornica.<Zadatak, String>kolona("naziv").build();

    private final TableColumn<Zadatak, String> predmetKolona = UITvornica.<Zadatak, String>kolona(
            data -> new SimpleStringProperty(data.getValue().getPredmet().getNaziv())
    ).build();

    private final TableColumn<Zadatak, String> rokPredajeKolona = UITvornica.<Zadatak, String>kolona(
            data -> new SimpleStringProperty(
                    data.getValue().getRokPredaje() != null
                            ? data.getValue().getRokPredaje().format(FORMATTER)
                            : ""
            )
    ).build();

    private final TableColumn<Zadatak, String> statusKolona = UITvornica.<Zadatak, String>kolona(
            data -> new SimpleStringProperty(dohvatiStatusTekst(data.getValue()))
    ).build();

    private final TableView<Zadatak> tablicaZadataka = UITvornica.<Zadatak>tableView()
            .kolone(nazivKolona, predmetKolona, rokPredajeKolona, statusKolona)
            .stavke(zadaci)
            .constrained()
            .stil(Stilovi.TABLICA_VISINA_VELIKA)
            .build();

    private Button pregledajZadatakGumb;
    private Button osvjeziGumb;

    public PregledZadatakaStudentPogled() {
        super();
        this.zadatakServis = new ZadatakServis();
        this.predajaServis = new PredajaServis();
        this.poruke = PorukaHelper.kreiraj(prijevod);
        konfigurirajGumbe();
    }

    @Override
    protected VBox kreirajSadrzaj() {
        HBox kontroleBox = hbox(pregledajZadatakGumb, osvjeziGumb)
                .pozicija(Pos.CENTER)
                .stil(Stilovi.RAZMAK_KONTROLE)
                .build();

        VBox sadrzaj = vbox(naslov, tablicaZadataka, poruke.getKontejner(), kontroleBox)
                .stil(Stilovi.RAZMAK_SREDNJI, Stilovi.PADDING_SREDNJI)
                .grow(Priority.ALWAYS)
                .build();

        VBox.setVgrow(tablicaZadataka, Priority.ALWAYS);

        ucitajZadatke();
        postaviListenere();

        return sadrzaj;
    }

    private void konfigurirajGumbe() {
        pregledajZadatakGumb = gumb(Stilovi.GUMB_PLAVI, this::otvoriPregledZadatka).onemogucen(true).build();
        osvjeziGumb = gumb(Stilovi.GUMB_ZELENI, this::ucitajZadatke).build();
    }

    private void postaviListenere() {
        tablicaZadataka.getSelectionModel().selectedItemProperty().addListener(
                (obs, stari, novi) -> azurirajOsjetljivostGumba(novi)
        );
    }

    private void azurirajOsjetljivostGumba(Zadatak odabrani) {
        pregledajZadatakGumb.setDisable(odabrani == null);
    }

    private void ucitajZadatke() {
        try {
            Student student = (Student) Sesija.getInstanca().getPrijavljeniKorisnik();
            statusPredaje = predajaServis.vratiStatusePredanihZadataka(student.getId());
            zadaci.clear();
            zadaci.addAll(zadatakServis.vratiZadatkeStudenta(student.getId()));
            tablicaZadataka.refresh();
            azurirajPlaceholder();
        } catch (Exception e) {
            System.err.println("Greška pri učitavanju zadataka: " + e.getMessage());
            poruke.prikaziGreskuSTimerom("greska_ucitavanje_zadataka");
        }
    }

    private void azurirajPlaceholder() {
        if (zadaci.isEmpty()) {
            tablicaZadataka.setPlaceholder(labela(prijevod.getPrijevod("nema_dostupnih_zadataka")).build());
        }
    }

    private String dohvatiStatusTekst(Zadatak zadatak) {
        boolean rokIstekao = zadatak.jeLiKasnio();
        StatusPredaje status = statusPredaje.get(zadatak.getId());

        if (status == null) {
            return rokIstekao
                    ? prijevod.getPrijevod("status_isteklo")
                    : prijevod.getPrijevod("status_otvoreno");
        }
        if (status == StatusPredaje.OCJENJENO) {
            return prijevod.getPrijevod("status_ocjenjeno");
        }
        return rokIstekao
                ? prijevod.getPrijevod("status_nije_ocjenjeno")
                : prijevod.getPrijevod("status_predano");
    }

    private void otvoriPregledZadatka() {
        Zadatak odabrani = tablicaZadataka.getSelectionModel().getSelectedItem();
        if (odabrani == null) return;
        StatusPredaje status = statusPredaje.get(odabrani.getId());
        UpraviteljPogleda.prikazi(new PredajaRjesenjaPogled(odabrani, status));
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
        naslov.setText(prijevod.getPrijevod("zadaci_student_naslov"));
    }

    private void osvjeziTablicu() {
        nazivKolona.setText(prijevod.getPrijevod("zadatak_naziv"));
        predmetKolona.setText(prijevod.getPrijevod("zadatak_predmet"));
        rokPredajeKolona.setText(prijevod.getPrijevod("zadatak_rok_predaje"));
        statusKolona.setText(prijevod.getPrijevod("zadatak_status"));
        tablicaZadataka.refresh();
        azurirajPlaceholder();
    }

    private void osvjeziGumbe() {
        pregledajZadatakGumb.setText(prijevod.getPrijevod("pregledaj_zadatak_gumb"));
        osvjeziGumb.setText(prijevod.getPrijevod("osvjezi_gumb"));
    }
}
