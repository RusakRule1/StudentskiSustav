package projekt.pogled;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import projekt.izvjestaj.IzvjestajPodaci;
import projekt.model.Predmet;
import projekt.model.Profesor;
import projekt.model.Student;
import projekt.servis.IzvjestajServis;
import projekt.servis.PredmetServis;
import projekt.servis.UpisServis;
import projekt.upravitelj.Sesija;
import projekt.util.PorukaHelper;
import projekt.util.Stilovi;
import projekt.util.UITvornica;

import java.io.File;
import java.util.List;

import static projekt.util.UITvornica.*;

public class PregledUpisaPredmetaPogled extends OsnovniPogled {

    private final PredmetServis predmetServis;
    private final UpisServis upisServis;
    private final Profesor profesor;
    private final PorukaHelper poruke;

    private final Label naslov = labela().stil(Stilovi.PODNASLOV).build();
    private final ObservableList<Predmet> predmeti = FXCollections.observableArrayList();

    private final TableColumn<Predmet, String> predmetNazivKol = UITvornica.<Predmet, String>kolona("naziv").build();
    private final TableColumn<Predmet, String> predmetSifraKol = UITvornica.<Predmet, String>kolona("sifra").build();
    private final TableColumn<Predmet, Integer> predmetEctsKol = UITvornica.<Predmet, Integer>kolona("ectsBodovi").build();
    private final TableColumn<Predmet, String> predmetSemestarKol = UITvornica.<Predmet, String>kolona("semestar").build();
    private final TableColumn<Predmet, Integer> predmetGodinaKol = UITvornica.<Predmet, Integer>kolona("godinaIzvodenja").build();

    private final TableView<Predmet> tablicaPredmeta = UITvornica.<Predmet>tableView()
            .kolone(predmetNazivKol, predmetSifraKol, predmetEctsKol,
                    predmetSemestarKol, predmetGodinaKol)
            .stavke(predmeti)
            .constrained()
            .stil(Stilovi.TABLICA_VISINA_MALA)
            .build();

    private final Label naslovUpisani = labela().stil(Stilovi.PODNASLOV).build();
    private final ObservableList<Student> upisaniStudenti = FXCollections.observableArrayList();

    private final TableColumn<Student, String> upisaniImeKol = UITvornica.<Student, String>kolona(
            d -> new SimpleStringProperty(d.getValue().getIme())).build();
    private final TableColumn<Student, String> upisaniPrezimeKol = UITvornica.<Student, String>kolona(
            d -> new SimpleStringProperty(d.getValue().getPrezime())).build();
    private final TableColumn<Student, String> upisaniJmbagKol = UITvornica.<Student, String>kolona("jmbag").build();
    private final TableColumn<Student, Integer> upisaniGodinaKol = UITvornica.<Student, Integer>kolona("godinaStudija").build();

    private final TableView<Student> tablicaUpisanih = UITvornica.<Student>tableView()
            .kolone(upisaniImeKol, upisaniPrezimeKol, upisaniJmbagKol, upisaniGodinaKol)
            .stavke(upisaniStudenti)
            .constrained()
            .stil(Stilovi.TABLICA_VISINA_MALA)
            .build();

    private final Label naslovDostupni = labela().stil(Stilovi.PODNASLOV).build();
    private final ObservableList<Student> dostupniStudenti = FXCollections.observableArrayList();

    private final TableColumn<Student, String> dostupniImeKol = UITvornica.<Student, String>kolona(
            d -> new SimpleStringProperty(d.getValue().getIme())).build();
    private final TableColumn<Student, String> dostupniPrezimeKol = UITvornica.<Student, String>kolona(
            d -> new SimpleStringProperty(d.getValue().getPrezime())).build();
    private final TableColumn<Student, String> dostupniJmbagKol = UITvornica.<Student, String>kolona("jmbag").build();
    private final TableColumn<Student, Integer> dostupniGodinaKol = UITvornica.<Student, Integer>kolona("godinaStudija").build();

    private final TableView<Student> tablicaDostupnih = UITvornica.<Student>tableView()
            .kolone(dostupniImeKol, dostupniPrezimeKol, dostupniJmbagKol, dostupniGodinaKol)
            .stavke(dostupniStudenti)
            .constrained()
            .stil(Stilovi.TABLICA_VISINA_MALA)
            .build();

    private Button ispisGumb;
    private Button upisGumb;
    private Button izvjestajGumb;

    public PregledUpisaPredmetaPogled() {
        super();
        this.predmetServis = new PredmetServis();
        this.upisServis = new UpisServis();
        this.profesor = (Profesor) Sesija.getInstanca().getPrijavljeniKorisnik();
        this.poruke = PorukaHelper.kreiraj(prijevod);
        konfigurirajGumbe();
    }

    @Override
    protected VBox kreirajSadrzaj() {
        HBox transferBox = hbox(upisGumb, ispisGumb)
                .pozicija(Pos.CENTER)
                .stil(Stilovi.RAZMAK_GUMBI)
                .build();

        HBox izvjestajBox = hbox(izvjestajGumb)
                .pozicija(Pos.CENTER_RIGHT)
                .stil(Stilovi.RAZMAK_GUMBI)
                .build();

        VBox sadrzaj = vbox(
                naslov,
                tablicaPredmeta,
                izvjestajBox,
                naslovUpisani,
                tablicaUpisanih,
                transferBox,
                naslovDostupni,
                tablicaDostupnih,
                poruke.getKontejner()
        ).stil(Stilovi.RAZMAK_SREDNJI, Stilovi.PADDING_SREDNJI)
                .grow(Priority.ALWAYS)
                .build();

        VBox.setVgrow(tablicaPredmeta, Priority.ALWAYS);
        VBox.setVgrow(tablicaUpisanih, Priority.ALWAYS);
        VBox.setVgrow(tablicaDostupnih, Priority.ALWAYS);

        ucitajPredmete();
        postaviListenere();

        return sadrzaj;
    }

    private void konfigurirajGumbe() {
        ispisGumb = gumb(Stilovi.GUMB_CRVENI, this::ispisStudenta).onemogucen(true).build();
        upisGumb = gumb(Stilovi.GUMB_PLAVI, this::upisStudenta).onemogucen(true).build();
        izvjestajGumb = gumb(Stilovi.GUMB_ZUTI, this::generirajIzvjestaj).onemogucen(true).build();
    }

    private void postaviListenere() {
        tablicaPredmeta.getSelectionModel().selectedItemProperty().addListener(
                (obs, stari, novi) -> ucitajStudenteZaPredmet(novi)
        );
        tablicaUpisanih.getSelectionModel().selectedItemProperty().addListener(
                (obs, stari, novi) -> ispisGumb.setDisable(novi == null)
        );
        tablicaDostupnih.getSelectionModel().selectedItemProperty().addListener(
                (obs, stari, novi) -> upisGumb.setDisable(novi == null)
        );
    }

    private void ucitajPredmete() {
        try {
            List<Predmet> lista = predmetServis.pronadjiPredmeteProfesora(profesor.getId());
            predmeti.setAll(lista);
            if (predmeti.isEmpty()) {
                tablicaPredmeta.setPlaceholder(labela(prijevod.getPrijevod("nema_predmeta")).build());
            }
        } catch (Exception e) {
            System.err.println("Greška pri učitavanju predmeta: " + e.getMessage());
            poruke.prikaziGreskuSTimerom("greska_ucitavanje_predmeta");
        }
    }

    private void ucitajStudenteZaPredmet(Predmet predmet) {
        upisaniStudenti.clear();
        dostupniStudenti.clear();
        ispisGumb.setDisable(true);
        upisGumb.setDisable(true);
        izvjestajGumb.setDisable(true);

        if (predmet == null) {
            postavljiPlaceholderOdaberiPredmet();
            return;
        }

        try {
            upisaniStudenti.setAll(upisServis.pronadjiUpisaneStudente(predmet.getId()));
            dostupniStudenti.setAll(upisServis.pronadjiStudenteMoguceZaUpis(
                    predmet.getId(), predmet.getGodinaIzvodenja()));

            Predmet predmetSUpisi = predmetServis.vratiPredmetSUpisimaPoId(predmet.getId());
            naslovUpisani.setText(prijevod.getPrijevod("upis_upisani_studenti") +
                    " - " + predmetSUpisi.vratiSifraNaziv() +
                    " (" + predmetSUpisi.vratiBrojStudenata() + ")");
            
            azurirajPlaceholdereTablica();
            izvjestajGumb.setDisable(upisaniStudenti.isEmpty());
        } catch (Exception e) {
            System.err.println("Greška pri učitavanju studenata: " + e.getMessage());
            poruke.prikaziGreskuSTimerom("greska_ucitavanje_studenata");
        }
    }

    private void upisStudenta() {
        Student student = tablicaDostupnih.getSelectionModel().getSelectedItem();
        Predmet predmet = tablicaPredmeta.getSelectionModel().getSelectedItem();
        if (student == null || predmet == null) return;

        try {
            upisServis.upisi(student, predmet);
            ucitajStudenteZaPredmet(predmet);
            poruke.prikaziUspjehSTimerom("upis_uspjeh");
        } catch (Exception e) {
            System.err.println("Greška pri upisu studenta: " + e.getMessage());
            poruke.prikaziGreskuSTimerom("greska_upis");
        }
    }

    private void ispisStudenta() {
        Student student = tablicaUpisanih.getSelectionModel().getSelectedItem();
        Predmet predmet = tablicaPredmeta.getSelectionModel().getSelectedItem();
        if (student == null || predmet == null) return;

        try {
            upisServis.ispisi(student, predmet);
            ucitajStudenteZaPredmet(predmet);
            poruke.prikaziUspjehSTimerom("ispis_uspjeh");
        } catch (Exception e) {
            System.err.println("Greška pri ispisu studenta: " + e.getMessage());
            poruke.prikaziGreskuSTimerom("greska_ispis");
        }
    }

    private void generirajIzvjestaj() {
        Predmet predmet = tablicaPredmeta.getSelectionModel().getSelectedItem();
        if (predmet == null || upisaniStudenti.isEmpty()) return;

        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle(prijevod.getPrijevod("izvjestaj_odabir_datoteke"));
        fileChooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("PDF datoteke", "*.pdf"));
        fileChooser.setInitialFileName(predmet.getSifra() + "_izvjestaj.pdf");

        File odabrana = fileChooser.showSaveDialog(tablicaPredmeta.getScene().getWindow());
        if (odabrana == null) return;

        IzvjestajPodaci podaci = izgradiPodatke(predmet);
        String pdfPutanja = odabrana.getAbsolutePath();

        izvjestajGumb.setDisable(true);

        Task<Integer> zadatak = vratiZadatak(podaci, pdfPutanja);

        new Thread(zadatak, "izvjestaj-thread").start();
    }

    private Task<Integer> vratiZadatak(IzvjestajPodaci podaci, String pdfPutanja) {
        Task<Integer> zadatak = new Task<>() {
            @Override
            protected Integer call() {
                return IzvjestajServis.generirajIzvjestaj(podaci, pdfPutanja);
            }
        };

        zadatak.setOnSucceeded(e -> {
            izvjestajGumb.setDisable(upisaniStudenti.isEmpty());
            int exitCode = zadatak.getValue();
            switch (exitCode) {
                case IzvjestajPodaci.USPJEH -> poruke.prikaziUspjehSTimerom("izvjestaj_uspjeh");
                case IzvjestajPodaci.GRESKA_ARGUMENTI -> poruke.prikaziGreskuSTimerom("greska_izvjestaj_argumenti");
                case IzvjestajPodaci.GRESKA_CITANJE -> poruke.prikaziGreskuSTimerom("greska_izvjestaj_citanje");
                case IzvjestajPodaci.GRESKA_PARSIRANJE -> poruke.prikaziGreskuSTimerom("greska_izvjestaj_parsiranje");
                case IzvjestajPodaci.GRESKA_GENERIRANJE -> poruke.prikaziGreskuSTimerom("greska_izvjestaj_generiranje");
                default -> poruke.prikaziGreskuSTimerom("greska_izvjestaj_opcenita");
            }
        });

        zadatak.setOnFailed(e -> {
            izvjestajGumb.setDisable(upisaniStudenti.isEmpty());
            poruke.prikaziGreskuSTimerom("greska_izvjestaj_opcenita");
        });
        return zadatak;
    }

    private IzvjestajPodaci izgradiPodatke(Predmet predmet) {
        String profesorIme = profesor.getPunoImeSTitulom();

        String semestarTekst = predmet.getSemestar() != null
                ? prijevod.getPrijevod("semestar_" + predmet.getSemestar().name().toLowerCase())
                : "";

        IzvjestajPodaci.PredmetPodaci predmetPodaci = new IzvjestajPodaci.PredmetPodaci(
                predmet.getNaziv(),
                predmet.getSifra(),
                predmet.getEctsBodovi(),
                semestarTekst,
                predmet.getGodinaIzvodenja(),
                profesorIme
        );

        List<IzvjestajPodaci.StudentPodaci> studentPodaci = upisaniStudenti.stream()
                .map(s -> new IzvjestajPodaci.StudentPodaci(
                        s.getIme(), s.getPrezime(), s.getJmbag(), s.getGodinaStudija()))
                .toList();

        IzvjestajPodaci.Labele labele = new IzvjestajPodaci.Labele(
                prijevod.getPrijevod("izvjestaj_naslov"),
                prijevod.getPrijevod("predmet_sifra"),
                prijevod.getPrijevod("predmet_ects"),
                prijevod.getPrijevod("predmet_semestar"),
                prijevod.getPrijevod("predmet_godina"),
                prijevod.getPrijevod("predmet_profesor"),
                prijevod.getPrijevod("upis_upisani_studenti"),
                prijevod.getPrijevod("student_ime"),
                prijevod.getPrijevod("student_prezime"),
                prijevod.getPrijevod("student_jmbag"),
                prijevod.getPrijevod("student_godina_studija")
        );

        return new IzvjestajPodaci(predmetPodaci, studentPodaci, labele);
    }

    private void postavljiPlaceholderOdaberiPredmet() {
        tablicaUpisanih.setPlaceholder(labela(prijevod.getPrijevod("upis_odaberi_predmet")).build());
        tablicaDostupnih.setPlaceholder(labela(prijevod.getPrijevod("upis_odaberi_predmet")).build());
    }

    private void azurirajPlaceholdereTablica() {
        if (upisaniStudenti.isEmpty()) {
            tablicaUpisanih.setPlaceholder(labela(prijevod.getPrijevod("upis_nema_upisanih")).build());
        }
        if (dostupniStudenti.isEmpty()) {
            tablicaDostupnih.setPlaceholder(labela(prijevod.getPrijevod("upis_nema_dostupnih")).build());
        }
    }

    @Override
    public void priSakrivanju() {
        poruke.cleanup();
    }

    @Override
    protected void osvjeziPogledTekstove() {
        osvjeziNaslove();
        osvjeziKolonePredmeta();
        osvjeziKoloneStudenata();
        osvjeziGumbe();
        poruke.osvjeziPoruku();
    }

    private void osvjeziNaslove() {
        naslov.setText(prijevod.getPrijevod("upis_naslov"));
        naslovUpisani.setText(prijevod.getPrijevod("upis_upisani_studenti"));
        naslovDostupni.setText(prijevod.getPrijevod("upis_dostupni_studenti"));
    }

    private void osvjeziKolonePredmeta() {
        predmetNazivKol.setText(prijevod.getPrijevod("predmet_naziv"));
        predmetSifraKol.setText(prijevod.getPrijevod("predmet_sifra"));
        predmetEctsKol.setText(prijevod.getPrijevod("predmet_ects"));
        predmetSemestarKol.setText(prijevod.getPrijevod("predmet_semestar"));
        predmetGodinaKol.setText(prijevod.getPrijevod("predmet_godina"));
    }

    private void osvjeziKoloneStudenata() {
        upisaniImeKol.setText(prijevod.getPrijevod("student_ime"));
        upisaniPrezimeKol.setText(prijevod.getPrijevod("student_prezime"));
        upisaniJmbagKol.setText(prijevod.getPrijevod("student_jmbag"));
        upisaniGodinaKol.setText(prijevod.getPrijevod("student_godina_studija"));

        dostupniImeKol.setText(prijevod.getPrijevod("student_ime"));
        dostupniPrezimeKol.setText(prijevod.getPrijevod("student_prezime"));
        dostupniJmbagKol.setText(prijevod.getPrijevod("student_jmbag"));
        dostupniGodinaKol.setText(prijevod.getPrijevod("student_godina_studija"));
    }

    private void osvjeziGumbe() {
        ispisGumb.setText(prijevod.getPrijevod("ispis_gumb"));
        upisGumb.setText(prijevod.getPrijevod("upis_gumb"));
        izvjestajGumb.setText(prijevod.getPrijevod("izvjestaj_gumb"));
    }
}
