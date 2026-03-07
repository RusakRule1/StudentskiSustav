package projekt.pogled;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import projekt.model.Predmet;
import projekt.model.Student;
import projekt.servis.PredajaServis;
import projekt.servis.PredmetServis;
import projekt.upravitelj.Sesija;
import projekt.util.PorukaHelper;
import projekt.util.Stilovi;
import projekt.util.UITvornica;

import java.util.List;
import java.util.Map;

import static projekt.util.UITvornica.*;

public class PregledPredmetaStudentPogled extends OsnovniPogled {

    private record PredmetRedak(Predmet predmet, Double prosjecnaOcjena) {
    }

    private final PredmetServis predmetServis;
    private final PredajaServis predajaServis;
    private final PorukaHelper poruke;

    private final Label naslov = labela().stil(Stilovi.PODNASLOV).build();
    private final Label filterNaslovLabela = labela().stil(Stilovi.LABELA_PODEBLJANA).build();
    private final TextField filterPolje = UITvornica.textField().build();
    private final Label ukupniProsjekLabela = labela().stil(Stilovi.LABELA_PODEBLJANA).build();

    private final ObservableList<PredmetRedak> svePredmeti = FXCollections.observableArrayList();
    private final FilteredList<PredmetRedak> filtrirani = new FilteredList<>(svePredmeti, p -> true);

    private final TableColumn<PredmetRedak, String> nazivKolona = UITvornica.<PredmetRedak, String>kolona(
            data -> new javafx.beans.property.SimpleStringProperty(data.getValue().predmet().getNaziv())
    ).build();

    private final TableColumn<PredmetRedak, String> sifraKolona = UITvornica.<PredmetRedak, String>kolona(
            data -> new javafx.beans.property.SimpleStringProperty(data.getValue().predmet().getSifra())
    ).build();

    private final TableColumn<PredmetRedak, String> profesorKolona = UITvornica.<PredmetRedak, String>kolona(
            data -> {
                var profesor = data.getValue().predmet().getProfesor();
                String ime = profesor != null
                        ? profesor.vratiPunoIme()
                        : "-";
                return new javafx.beans.property.SimpleStringProperty(ime);
            }
    ).build();

    private final TableColumn<PredmetRedak, String> ocjenaKolona = UITvornica.<PredmetRedak, String>kolona(
            data -> {
                Double ocjena = data.getValue().prosjecnaOcjena();
                String tekst = ocjena != null ? String.format("%.2f", ocjena) : "-";
                return new javafx.beans.property.SimpleStringProperty(tekst);
            }
    ).build();

    private final SortedList<PredmetRedak> sortirani = new SortedList<>(filtrirani);

    private final TableView<PredmetRedak> tablica = UITvornica.<PredmetRedak>tableView()
            .kolone(nazivKolona, sifraKolona, profesorKolona, ocjenaKolona)
            .stavke(sortirani)
            .constrained()
            .stil(Stilovi.TABLICA_VISINA_VELIKA)
            .build();

    public PregledPredmetaStudentPogled() {
        super();
        this.predmetServis = new PredmetServis();
        this.predajaServis = new PredajaServis();
        this.poruke = PorukaHelper.kreiraj(prijevod);
    }

    @Override
    protected VBox kreirajSadrzaj() {
        sortirani.comparatorProperty().bind(tablica.comparatorProperty());

        HBox filterBox = hbox(filterNaslovLabela, filterPolje)
                .pozicija(Pos.CENTER_LEFT)
                .stil(Stilovi.RAZMAK_MALI)
                .build();
        HBox.setHgrow(filterPolje, Priority.ALWAYS);

        VBox sadrzaj = vbox(naslov, filterBox, ukupniProsjekLabela, tablica, poruke.getKontejner())
                .stil(Stilovi.RAZMAK_SREDNJI, Stilovi.PADDING_SREDNJI)
                .grow(Priority.ALWAYS)
                .build();

        VBox.setVgrow(tablica, Priority.ALWAYS);

        postaviFilterListener();
        ucitajPredmete();

        return sadrzaj;
    }

    private void postaviFilterListener() {
        filterPolje.textProperty().addListener((obs, stari, novi) -> {
            String filter = novi.toLowerCase().trim();
            filtrirani.setPredicate(redak ->
                    filter.isEmpty() || redak.predmet().getNaziv().toLowerCase().contains(filter));
        });
    }

    private void ucitajPredmete() {
        try {
            Student student = (Student) Sesija.getInstanca().getPrijavljeniKorisnik();
            Integer studentId = student.getId();

            List<Predmet> predmeti = predmetServis.vratiPredmeteStudenta(studentId);
            Map<Integer, Double> ocjene = predajaServis.vratiProsjecneOcjeneZaStudenta(studentId);

            svePredmeti.clear();
            for (Predmet p : predmeti) {
                svePredmeti.add(new PredmetRedak(p, ocjene.get(p.getId())));
            }

            student.setPredaniZadaci(predajaServis.vratiPredajeStudenta(studentId));
            double ukupni = student.vratiProsjekOcjena();
            ukupniProsjekLabela.setText(ukupni > 0
                    ? String.format("Ukupni prosjek: %.2f", ukupni)
                    : "Ukupni prosjek: -");

            if (predmeti.isEmpty()) {
                tablica.setPlaceholder(labela(prijevod.getPrijevod("nema_predmeta")).build());
            }
        } catch (Exception e) {
            System.err.println("Greška pri učitavanju predmeta: " + e.getMessage());
            poruke.prikaziGreskuSTimerom("greska_ucitavanje_predmeta");
        }
    }

    @Override
    public void priSakrivanju() {
        poruke.cleanup();
    }

    @Override
    protected void osvjeziPogledTekstove() {
        naslov.setText(prijevod.getPrijevod("moji_predmeti_naslov"));
        filterNaslovLabela.setText(prijevod.getPrijevod("filter_labela"));
        nazivKolona.setText(prijevod.getPrijevod("predmet_naziv"));
        sifraKolona.setText(prijevod.getPrijevod("predmet_sifra"));
        profesorKolona.setText(prijevod.getPrijevod("predmet_profesor"));
        ocjenaKolona.setText(prijevod.getPrijevod("predmet_prosjecna_ocjena"));
        tablica.refresh();
        poruke.osvjeziPoruku();
    }
}
