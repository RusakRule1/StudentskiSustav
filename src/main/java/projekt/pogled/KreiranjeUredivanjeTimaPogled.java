package projekt.pogled;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import projekt.model.Student;
import projekt.model.StudentJson;
import projekt.model.TimJson;
import projekt.servis.StudentServis;
import projekt.servis.TimJsonServis;
import projekt.upravitelj.UpraviteljPogleda;
import projekt.util.PorukaHelper;
import projekt.util.Stilovi;
import projekt.util.UITvornica;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static projekt.util.UITvornica.*;

public class KreiranjeUredivanjeTimaPogled extends OsnovniPogled {

    private static final int MIN_DULJINA_NAZIVA = 2;

    private final TimJsonServis timServis;
    private final StudentServis studentServis;
    private final PorukaHelper poruke;

    private final TimJson timZaUredivanje;

    private final Label naslov = labela().stil(Stilovi.PODNASLOV).build();
    private final TextField nazivPolje = textField().stil(Stilovi.POLJE_TEKSTA).build();

    private final Label labelPostavljeni = labela().stil(Stilovi.PODNASLOV).build();
    private final Label labelDostupni = labela().stil(Stilovi.PODNASLOV).build();

    private Button dodajGumb;
    private Button ukloniGumb;
    private Button dodajSveGumb;
    private Button ukloniSveGumb;
    private Button spremiGumb;
    private Button odustaniGumb;

    private final ObservableList<StudentJson> postavljeniStudenti = FXCollections.observableArrayList();
    private final ObservableList<StudentJson> dostupniStudenti = FXCollections.observableArrayList();

    private final TableView<StudentJson> tablicaPostavljeniStudenti = UITvornica.<StudentJson>tableView()
            .kolone(
                    UITvornica.<StudentJson, String>kolona("ime", Stilovi.KOLONA_IME_STUDENTA).build(),
                    UITvornica.<StudentJson, String>kolona("prezime", Stilovi.KOLONA_PREZIME_STUDENTA).build(),
                    UITvornica.<StudentJson, String>kolona("jmbag", Stilovi.KOLONA_JMBAG_STUDENTA).build()
            )
            .stavke(postavljeniStudenti)
            .constrained()
            .stil(Stilovi.TABLICA_VISINA_MALA)
            .build();

    private final TableView<StudentJson> tablicaDostupniStudenti = UITvornica.<StudentJson>tableView()
            .kolone(
                    UITvornica.<StudentJson, String>kolona("ime", Stilovi.KOLONA_IME_STUDENTA).build(),
                    UITvornica.<StudentJson, String>kolona("prezime", Stilovi.KOLONA_PREZIME_STUDENTA).build(),
                    UITvornica.<StudentJson, String>kolona("jmbag", Stilovi.KOLONA_JMBAG_STUDENTA).build()
            )
            .stavke(dostupniStudenti)
            .constrained()
            .stil(Stilovi.TABLICA_VISINA_MALA)
            .build();

    public KreiranjeUredivanjeTimaPogled(TimJson timZaUredivanje) {
        super();
        this.timZaUredivanje = timZaUredivanje;
        this.timServis = new TimJsonServis();
        this.studentServis = new StudentServis();
        this.poruke = PorukaHelper.kreiraj(prijevod);

        konfigurirajGumbe();
    }

    @Override
    protected VBox kreirajSadrzaj() {
        VBox tabliceBox = kreirajTabliceSekciju();
        HBox kontroleBox = hbox(spremiGumb, odustaniGumb)
                .pozicija(Pos.CENTER)
                .stil(Stilovi.RAZMAK_KONTROLE)
                .build();

        VBox sadrzaj = vbox(naslov, nazivPolje, tabliceBox, poruke.getKontejner(), kontroleBox)
                .stil(Stilovi.GLAVNI_VBOX)
                .grow(Priority.ALWAYS)
                .build();

        VBox.setVgrow(tabliceBox, Priority.ALWAYS);

        ucitajStudente();
        postaviListenere();

        return sadrzaj;
    }

    private void konfigurirajGumbe() {
        dodajGumb = gumb(Stilovi.GUMB_PLAVI, this::dodajOdabranogStudenta)
                .tekst("↑").onemogucen(true).build();

        ukloniGumb = gumb(Stilovi.GUMB_PLAVI, this::ukloniOdabranogStudenta)
                .tekst("↓").onemogucen(true).build();

        dodajSveGumb = gumb(Stilovi.GUMB_ZELENI, this::dodajSveStudente)
                .tekst("↑↑").build();

        ukloniSveGumb = gumb(Stilovi.GUMB_ZELENI, this::ukloniSveStudente)
                .tekst("↓↓").build();

        spremiGumb = gumb(Stilovi.GUMB_PLAVI, this::spremiTim).build();
        odustaniGumb = gumb(Stilovi.GUMB_ZELENI, this::vratiSeNaPregled).build();
    }

    private VBox kreirajTabliceSekciju() {
        return vbox(kreirajPostavljeniSekciju(), kreirajGumbeZaManipulaciju(), kreirajDostupniSekciju())
                .stil(Stilovi.RAZMAK_MALI)
                .build();
    }

    private VBox kreirajPostavljeniSekciju() {
        VBox sekcija = vbox(labelPostavljeni, tablicaPostavljeniStudenti)
                .stil(Stilovi.RAZMAK_TABLICA_KONTEJNER)
                .grow(Priority.ALWAYS)
                .build();

        VBox.setVgrow(tablicaPostavljeniStudenti, Priority.ALWAYS);
        return sekcija;
    }

    private VBox kreirajDostupniSekciju() {
        VBox sekcija = vbox(labelDostupni, tablicaDostupniStudenti)
                .stil(Stilovi.RAZMAK_TABLICA_KONTEJNER)
                .grow(Priority.ALWAYS)
                .build();

        VBox.setVgrow(tablicaDostupniStudenti, Priority.ALWAYS);
        return sekcija;
    }

    private HBox kreirajGumbeZaManipulaciju() {
        return hbox(dodajGumb, ukloniGumb, dodajSveGumb, ukloniSveGumb)
                .pozicija(Pos.CENTER)
                .stil(Stilovi.RAZMAK_GUMBI)
                .build();
    }

    private void postaviListenere() {
        tablicaDostupniStudenti.getSelectionModel().selectedItemProperty().addListener(
                (obs, stari, novi) -> dodajGumb.setDisable(novi == null)
        );
        tablicaPostavljeniStudenti.getSelectionModel().selectedItemProperty().addListener(
                (obs, stari, novi) -> ukloniGumb.setDisable(novi == null)
        );
    }

    private void ucitajStudente() {
        try {
            List<StudentJson> sviStudentiJson = dohvatiSveStudenteKaoJson();
            List<TimJson> sviTimovi = timServis.dohvatiSveTimove();

            if (jeUredjivanje()) {
                ucitajZaUredjivanje(sviStudentiJson, sviTimovi);
            } else {
                ucitajZaKreiranje(sviStudentiJson, sviTimovi);
            }

            azurirajStanjeGumba();

        } catch (Exception e) {
            System.err.println("Greška pri učitavanju studenata: " + e.getMessage());
            poruke.prikaziGreskuSTimerom("greska_ucitavanje_studenata");
        }
    }

    private List<StudentJson> dohvatiSveStudenteKaoJson() {
        return konvertirajUStudentJson(studentServis.dohvatiSveStudente());
    }

    private void ucitajZaUredjivanje(List<StudentJson> sviStudenti, List<TimJson> sviTimovi) {
        nazivPolje.setText(timZaUredivanje.getNaziv());

        if (timZaUredivanje.getClanovi() != null) {
            postavljeniStudenti.setAll(timZaUredivanje.getClanovi());
        }

        List<String> postavljeniJmbagovi = dohvatiPostavljeneJmbagove();
        List<String> zauzetiJmbagovi = dohvatiZauzeteJmbagove(sviTimovi, timZaUredivanje.getId());

        dostupniStudenti.setAll(
                sviStudenti.stream()
                        .filter(s -> !postavljeniJmbagovi.contains(s.getJmbag()) &&
                                !zauzetiJmbagovi.contains(s.getJmbag()))
                        .collect(Collectors.toList())
        );
    }

    private void ucitajZaKreiranje(List<StudentJson> sviStudenti, List<TimJson> sviTimovi) {
        List<String> zauzetiJmbagovi = dohvatiZauzeteJmbagove(sviTimovi, null);

        dostupniStudenti.setAll(
                sviStudenti.stream()
                        .filter(s -> !zauzetiJmbagovi.contains(s.getJmbag()))
                        .collect(Collectors.toList())
        );

        postavljeniStudenti.clear();
    }

    private List<String> dohvatiPostavljeneJmbagove() {
        if (timZaUredivanje == null || timZaUredivanje.getClanovi() == null) {
            return List.of();
        }
        return timZaUredivanje.getClanovi().stream()
                .map(StudentJson::getJmbag)
                .toList();
    }

    private List<String> dohvatiZauzeteJmbagove(List<TimJson> sviTimovi, String idTrenutnog) {
        return sviTimovi.stream()
                .filter(tim -> !tim.getId().equals(idTrenutnog))
                .filter(tim -> tim.getClanovi() != null)
                .flatMap(tim -> tim.getClanovi().stream())
                .map(StudentJson::getJmbag)
                .toList();
    }

    private List<StudentJson> konvertirajUStudentJson(List<Student> studentiHibernate) {
        return studentiHibernate.stream()
                .map(s -> new StudentJson(s.getJmbag(), s.getIme(), s.getPrezime()))
                .toList();
    }

    private void dodajOdabranogStudenta() {
        StudentJson odabrani = tablicaDostupniStudenti.getSelectionModel().getSelectedItem();
        dostupniStudenti.remove(odabrani);
        postavljeniStudenti.add(odabrani);
        azurirajStanjeGumba();
    }

    private void ukloniOdabranogStudenta() {
        StudentJson odabrani = tablicaPostavljeniStudenti.getSelectionModel().getSelectedItem();
        postavljeniStudenti.remove(odabrani);
        dostupniStudenti.add(odabrani);
        azurirajStanjeGumba();
    }

    private void dodajSveStudente() {
        if (!dostupniStudenti.isEmpty()) {
            postavljeniStudenti.addAll(dostupniStudenti);
            dostupniStudenti.clear();
            azurirajStanjeGumba();
        }
    }

    private void ukloniSveStudente() {
        if (!postavljeniStudenti.isEmpty()) {
            dostupniStudenti.addAll(postavljeniStudenti);
            postavljeniStudenti.clear();
            azurirajStanjeGumba();
        }
    }

    private void azurirajStanjeGumba() {
        dodajSveGumb.setDisable(dostupniStudenti.isEmpty());
        ukloniSveGumb.setDisable(postavljeniStudenti.isEmpty());
        tablicaDostupniStudenti.getSelectionModel().clearSelection();
        tablicaPostavljeniStudenti.getSelectionModel().clearSelection();
    }

    private void spremiTim() {
        if (!validirajUnos()) {
            return;
        }
        if (jeUredjivanje()) {
            azurirajPostojeciTim();
        } else {
            kreirajNoviTim();
        }
    }

    private boolean validirajUnos() {
        String naziv = nazivPolje.getText().trim();

        if (naziv.isEmpty()) {
            poruke.prikaziGreskuSTimerom("greska_naziv_tima_obavezan");
            return false;
        }
        if (naziv.length() < MIN_DULJINA_NAZIVA) {
            poruke.prikaziGreskuSTimerom("greska_naziv_tima_prekratak");
            return false;
        }
        if (postojiNaziv(naziv)) {
            poruke.prikaziGreskuSTimerom("greska_naziv_tima_postoji");
            return false;
        }
        if (postavljeniStudenti.isEmpty()) {
            poruke.prikaziGreskuSTimerom("greska_tim_mora_imati_clanove");
            return false;
        }
        return true;
    }

    private boolean postojiNaziv(String naziv) {
        return timServis.dohvatiSveTimove().stream()
                .anyMatch(t -> t.getNaziv().equalsIgnoreCase(naziv) &&
                        (timZaUredivanje == null || !t.getId().equals(timZaUredivanje.getId())));
    }

    private void kreirajNoviTim() {
        try {
            TimJson noviTim = new TimJson();
            noviTim.setNaziv(nazivPolje.getText().trim());
            noviTim.setClanovi(new ArrayList<>(postavljeniStudenti));

            if (timServis.spremiTim(noviTim)) {
                ocistiFormuZaNoviTim();
                poruke.prikaziUspjehSTimerom("tim_kreiran");
            } else {
                poruke.prikaziGreskuSTimerom("greska_tim_nije_kreiran");
            }
        } catch (Exception e) {
            System.err.println("Greška pri kreiranju tima: " + e.getMessage());
            poruke.prikaziGreskuSTimerom("greska_tim_nije_spremljen");
        }
    }

    private void azurirajPostojeciTim() {
        try {
            timZaUredivanje.setNaziv(nazivPolje.getText().trim());
            timZaUredivanje.setClanovi(new ArrayList<>(postavljeniStudenti));

            if (timServis.azurirajTim(timZaUredivanje)) {
                vratiSeNaPregled();
            } else {
                poruke.prikaziGreskuSTimerom("greska_tim_nije_azuriran");
            }
        } catch (Exception e) {
            System.err.println("Greška pri ažuriranju tima: " + e.getMessage());
            poruke.prikaziGreskuSTimerom("greska_tim_nije_spremljen");
        }
    }

    private void ocistiFormuZaNoviTim() {
        nazivPolje.clear();
        ucitajStudente();
    }

    private void vratiSeNaPregled() {
        UpraviteljPogleda.idiNatrag();
    }

    private boolean jeUredjivanje() {
        return timZaUredivanje != null;
    }

    @Override
    public void priSakrivanju() {
        poruke.cleanup();
    }

    @Override
    protected void osvjeziPogledTekstove() {
        osvjeziNaslov();
        osvjeziPolja();
        osvjeziTablice();
        osvjeziGumbe();
        poruke.osvjeziPoruku();
    }

    private void osvjeziNaslov() {
        String kljuc = jeUredjivanje() ? "uredi_tim_naslov" : "kreiraj_tim_naslov";
        naslov.setText(prijevod.getPrijevod(kljuc));
    }

    private void osvjeziPolja() {
        nazivPolje.setPromptText(prijevod.getPrijevod("tim_naziv_prompt"));
    }

    private void osvjeziTablice() {
        labelPostavljeni.setText(prijevod.getPrijevod("postavljeni_clanovi"));
        labelDostupni.setText(prijevod.getPrijevod("dostupni_studenti"));

        osvjeziKoloneTablice(tablicaPostavljeniStudenti);
        osvjeziKoloneTablice(tablicaDostupniStudenti);

        osvjeziPlaceholdere();
    }

    private void osvjeziKoloneTablice(TableView<StudentJson> tablica) {
        tablica.getColumns().get(0).setText(prijevod.getPrijevod("student_ime"));
        tablica.getColumns().get(1).setText(prijevod.getPrijevod("student_prezime"));
        tablica.getColumns().get(2).setText(prijevod.getPrijevod("student_jmbag"));
    }

    private void osvjeziPlaceholdere() {
        if (postavljeniStudenti.isEmpty()) {
            tablicaPostavljeniStudenti.setPlaceholder(
                    labela(prijevod.getPrijevod("nema_postavljenih_studenata")).build()
            );
        }
        if (dostupniStudenti.isEmpty()) {
            tablicaDostupniStudenti.setPlaceholder(
                    labela(prijevod.getPrijevod("nema_dostupnih_studenata")).build()
            );
        }
    }

    private void osvjeziGumbe() {
        String kljuc = jeUredjivanje() ? "spremi_izmjene_gumb" : "kreiraj_gumb";
        spremiGumb.setText(prijevod.getPrijevod(kljuc));
        odustaniGumb.setText(prijevod.getPrijevod("odustani_gumb"));
    }
}
