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
import projekt.model.Profesor;
import projekt.model.Zadatak;
import projekt.servis.PredmetServis;
import projekt.servis.ZadatakServis;
import projekt.upravitelj.Sesija;
import projekt.upravitelj.UpraviteljPogleda;
import projekt.util.PorukaHelper;
import projekt.util.Stilovi;
import projekt.util.UITvornica;

import java.time.format.DateTimeFormatter;

import static projekt.util.UITvornica.*;

public class PregledZadatakaPogled extends OsnovniPogled {

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("dd.MM.yyyy. HH:mm");

    private final ZadatakServis zadatakServis;
    private final PredmetServis predmetServis;
    private final PorukaHelper poruke;

    private final Label naslov = labela().stil(Stilovi.PODNASLOV).build();
    private final Label brojPredmetaLabela = labela().stil(Stilovi.LABELA_INFORMACIJA).build();
    private final Label predanihLabela = labela().stil(Stilovi.LABELA_INFORMACIJA).build();


    private final ObservableList<Zadatak> zadaci = FXCollections.observableArrayList();

    private final TableColumn<Zadatak, String> nazivKolona = UITvornica.<Zadatak, String>kolona("naziv").build();
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
    private final TableColumn<Zadatak, String> datumObjaveKolona = UITvornica.<Zadatak, String>kolona(
            data -> new SimpleStringProperty(
                    data.getValue().getDatumObjave() != null
                            ? data.getValue().getDatumObjave().format(FORMATTER)
                            : ""
            )
    ).build();

    private final TableView<Zadatak> tablicaZadataka = UITvornica.<Zadatak>tableView()
            .kolone(nazivKolona, predmetKolona, rokPredajeKolona, datumObjaveKolona)
            .stavke(zadaci)
            .constrained()
            .stil(Stilovi.TABLICA_VISINA_MALA)
            .build();

    private Button noviZadatakGumb;
    private Button urediZadatakGumb;
    private Button obrisiZadatakGumb;
    private Button pregledajPredaneGumb;
    private Button osvjeziGumb;

    public PregledZadatakaPogled() {
        super();
        this.zadatakServis = new ZadatakServis();
        this.predmetServis = new PredmetServis();
        this.poruke = PorukaHelper.kreiraj(prijevod);
        konfigurirajGumbe();
    }

    @Override
    protected VBox kreirajSadrzaj() {
        HBox kontroleBox = kreirajKontrole();

        VBox sadrzaj = vbox(naslov, brojPredmetaLabela, predanihLabela, tablicaZadataka, poruke.getKontejner(), kontroleBox)
                .stil(Stilovi.RAZMAK_SREDNJI, Stilovi.PADDING_SREDNJI)
                .grow(Priority.ALWAYS)
                .build();

        VBox.setVgrow(tablicaZadataka, Priority.ALWAYS);

        ucitajZadatke();
        postaviListenere();

        return sadrzaj;
    }

    private void konfigurirajGumbe() {
        noviZadatakGumb = gumb(Stilovi.GUMB_PLAVI, this::otvoriKreiranjeZadatka).build();
        urediZadatakGumb = gumb(Stilovi.GUMB_ZELENI, this::otvoriUredivanjeZadatka).onemogucen(true).build();
        obrisiZadatakGumb = gumb(Stilovi.GUMB_CRVENI, this::obrisiZadatak).onemogucen(true).build();
        pregledajPredaneGumb = gumb(Stilovi.GUMB_ZUTI, this::otvoriPregledPredanihRjesenja).onemogucen(true).build();
        osvjeziGumb = gumb(Stilovi.GUMB_ZELENI, this::ucitajZadatke).build();
    }

    private HBox kreirajKontrole() {
        return hbox(noviZadatakGumb, urediZadatakGumb, obrisiZadatakGumb, pregledajPredaneGumb, osvjeziGumb)
                .pozicija(Pos.CENTER)
                .stil(Stilovi.RAZMAK_KONTROLE)
                .build();
    }

    private void postaviListenere() {
        tablicaZadataka.getSelectionModel().selectedItemProperty().addListener(
                (obs, stari, novi) -> azurirajOsjetljivostGumba(novi)
        );
    }

    private void azurirajOsjetljivostGumba(Zadatak odabrani) {
        boolean imaSelekciju = odabrani != null;
        urediZadatakGumb.setDisable(!imaSelekciju);
        obrisiZadatakGumb.setDisable(!imaSelekciju);
        pregledajPredaneGumb.setDisable(!imaSelekciju);

        if (imaSelekciju) {
            Zadatak zadatakSPredajama = zadatakServis.vratiZadatakSPredajamaPoId(odabrani.getId());
            predanihLabela.setText("Predano: " + zadatakSPredajama.vratiBrojPredanih());
        } else {
            predanihLabela.setText("");
        }
    }

    private void ucitajZadatke() {
        try {
            Profesor profesor = (Profesor) Sesija.getInstanca().getPrijavljeniKorisnik();
            profesor.setPredmeti(predmetServis.pronadjiPredmeteProfesora(profesor.getId()));
            brojPredmetaLabela.setText("Broj predmeta: " + profesor.vratiBrojPredmeta());
            zadaci.clear();
            zadaci.addAll(zadatakServis.vratiZadatkeProfesora(profesor.getId()));
            azurirajPlaceholder();
        } catch (Exception e) {
            System.err.println("Greška pri učitavanju zadataka: " + e.getMessage());
            poruke.prikaziGreskuSTimerom("greska_ucitavanje_zadataka");
        }
    }

    private void azurirajPlaceholder() {
        if (zadaci.isEmpty()) {
            tablicaZadataka.setPlaceholder(labela(prijevod.getPrijevod("nema_zadataka")).build());
        }
    }

    private void otvoriKreiranjeZadatka() {
        UpraviteljPogleda.prikazi(new KreiranjeUredivanjeZadatkaPogled(null));
    }

    private void otvoriUredivanjeZadatka() {
        Zadatak odabrani = tablicaZadataka.getSelectionModel().getSelectedItem();
        UpraviteljPogleda.prikazi(new KreiranjeUredivanjeZadatkaPogled(odabrani));
    }

    private void obrisiZadatak() {
        Zadatak odabrani = tablicaZadataka.getSelectionModel().getSelectedItem();
        try {
            zadatakServis.obrisiZadatak(odabrani);
            ucitajZadatke();
            poruke.prikaziUspjehSTimerom("zadatak_obrisan");
        } catch (Exception e) {
            System.err.println("Greška pri brisanju zadatka: " + e.getMessage());
            poruke.prikaziGreskuSTimerom("greska_zadatak_neobrisan");
        }
    }

    private void otvoriPregledPredanihRjesenja() {
        Zadatak odabrani = tablicaZadataka.getSelectionModel().getSelectedItem();
        UpraviteljPogleda.prikazi(new PregledPredanihRjesenjaPogled(odabrani));
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
        naslov.setText(prijevod.getPrijevod("zadaci_naslov"));
    }

    private void osvjeziTablicu() {
        nazivKolona.setText(prijevod.getPrijevod("zadatak_naziv"));
        predmetKolona.setText(prijevod.getPrijevod("zadatak_predmet"));
        rokPredajeKolona.setText(prijevod.getPrijevod("zadatak_rok_predaje"));
        datumObjaveKolona.setText(prijevod.getPrijevod("zadatak_datum_objave"));
        azurirajPlaceholder();
    }

    private void osvjeziGumbe() {
        noviZadatakGumb.setText(prijevod.getPrijevod("novi_zadatak_gumb"));
        urediZadatakGumb.setText(prijevod.getPrijevod("uredi_zadatak_gumb"));
        obrisiZadatakGumb.setText(prijevod.getPrijevod("obrisi_zadatak_gumb"));
        pregledajPredaneGumb.setText(prijevod.getPrijevod("pregledaj_predane_gumb"));
        osvjeziGumb.setText(prijevod.getPrijevod("osvjezi_gumb"));
    }
}
