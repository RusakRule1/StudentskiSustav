package projekt.pogled;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
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

import java.util.List;
import java.util.stream.Collectors;

public class KreiranjeUredivanjeTimaPogled extends OsnovniPogled {

    private static final int MIN_DULJINA_NAZIVA = 2;

    private final TimJsonServis timServis;
    private final StudentServis studentServis;
    private final PorukaHelper poruke = PorukaHelper.kreiraj(prijevod);

    private final TimJson timZaUredivanje;

    private final Label naslov = new Label();
    private final TextField nazivPolje = new TextField();

    private final Label labelPostavljeni = new Label();
    private final Label labelDostupni = new Label();

    private final TableView<StudentJson> tablicaPostavljeniStudenti = new TableView<>();
    private final TableView<StudentJson> tablicaDostupniStudenti = new TableView<>();
    private final ObservableList<StudentJson> postavljeniStudenti = FXCollections.observableArrayList();
    private final ObservableList<StudentJson> dostupniStudenti = FXCollections.observableArrayList();

    private final Button dodajGumb = new Button("↑");
    private final Button ukloniGumb = new Button("↓");
    private final Button dodajSveGumb = new Button("↑↑");
    private final Button ukloniSveGumb = new Button("↓↓");

    private final Button spremiGumb = new Button();
    private final Button odustaniGumb = new Button();


    public KreiranjeUredivanjeTimaPogled(TimJson timZaUredivanje) {
        super();
        this.timZaUredivanje = timZaUredivanje;
        this.timServis = new TimJsonServis();
        this.studentServis = new StudentServis();
    }

    @Override
    protected VBox kreirajSadrzaj() {
        VBox sadrzajBox = new VBox();
        sadrzajBox.setAlignment(Pos.TOP_CENTER);
        sadrzajBox.getStyleClass().addAll(
                Stilovi.RAZMAK_VELIKI,
                Stilovi.PADDING_VELIKI
        );

        konfigurirajNaslovKomponente();
        konfigurirajNazivPolje();
        VBox tabliceBox = kreirajTabliceSekciju();
        HBox kontroleBox = kreirajKontrole();

        sadrzajBox.getChildren().addAll(
                naslov,
                nazivPolje,
                tabliceBox,
                poruke.kontejner,
                kontroleBox
        );

        ucitajStudente();
        postaviListenere();

        return sadrzajBox;
    }

    private void konfigurirajNaslovKomponente() {
        naslov.getStyleClass().add(Stilovi.PODNASLOV);
    }

    private void konfigurirajNazivPolje() {
        nazivPolje.getStyleClass().add(Stilovi.POLJE_TEKSTA);
    }

    private VBox kreirajTabliceSekciju() {
        VBox glavniBox = new VBox();
        glavniBox.getStyleClass().addAll(
                Stilovi.RAZMAK_MALI
        );

        VBox postavljeniBox = kreirajPostavljeniSekciju();
        HBox gumbiBox = kreirajGumbeZaManipulaciju();
        VBox dostupniBox = kreirajDostupniSekciju();

        VBox.setVgrow(postavljeniBox, Priority.ALWAYS);
        VBox.setVgrow(dostupniBox, Priority.ALWAYS);

        glavniBox.getChildren().addAll(postavljeniBox, gumbiBox, dostupniBox);
        return glavniBox;
    }

    private VBox kreirajPostavljeniSekciju() {
        VBox box = new VBox();
        box.getStyleClass().addAll(
                Stilovi.RAZMAK_TABLICA_KONTEJNER
        );

        labelPostavljeni.getStyleClass().add(Stilovi.PODNASLOV);

        konfigurirajTablicu(tablicaPostavljeniStudenti, postavljeniStudenti);
        tablicaPostavljeniStudenti.getStyleClass().addAll(
                Stilovi.TABLICA_VISINA_MALA
        );

        box.getChildren().addAll(labelPostavljeni, tablicaPostavljeniStudenti);
        return box;
    }

    private VBox kreirajDostupniSekciju() {
        VBox box = new VBox();
        box.getStyleClass().addAll(
                Stilovi.RAZMAK_TABLICA_KONTEJNER
        );

        labelDostupni.getStyleClass().add(Stilovi.PODNASLOV);

        konfigurirajTablicu(tablicaDostupniStudenti, dostupniStudenti);
        tablicaDostupniStudenti.getStyleClass().addAll(
                Stilovi.TABLICA_VISINA_MALA
        );

        box.getChildren().addAll(labelDostupni, tablicaDostupniStudenti);
        return box;
    }

    private void konfigurirajTablicu(TableView<StudentJson> tablica,
                                     ObservableList<StudentJson> podaci) {
        tablica.getColumns().clear();

        TableColumn<StudentJson, String> imeKolona = new TableColumn<>();
        TableColumn<StudentJson, String> prezimeKolona = new TableColumn<>();
        TableColumn<StudentJson, String> jmbagKolona = new TableColumn<>();

        imeKolona.setCellValueFactory(new PropertyValueFactory<>("ime"));
        prezimeKolona.setCellValueFactory(new PropertyValueFactory<>("prezime"));
        jmbagKolona.setCellValueFactory(new PropertyValueFactory<>("jmbag"));

        imeKolona.getStyleClass().add(Stilovi.KOLONA_IME_STUDENTA);
        prezimeKolona.getStyleClass().add(Stilovi.KOLONA_PREZIME_STUDENTA);
        jmbagKolona.getStyleClass().add(Stilovi.KOLONA_JMBAG_STUDENTA);

        tablica.getColumns().addAll(imeKolona, prezimeKolona, jmbagKolona);
        tablica.setItems(podaci);
        tablica.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
    }

    private HBox kreirajGumbeZaManipulaciju() {
        HBox gumbiBox = new HBox();
        gumbiBox.getStyleClass().addAll(
                Stilovi.RAZMAK_GUMBI
        );
        gumbiBox.setAlignment(Pos.CENTER);

        konfigurirajDodajGumb();
        konfigurirajUkloniGumb();
        konfigurirajDodajSveGumb();
        konfigurirajUkloniSveGumb();

        gumbiBox.getChildren().addAll(dodajGumb, ukloniGumb, dodajSveGumb, ukloniSveGumb);
        return gumbiBox;
    }

    private void konfigurirajDodajGumb() {
        dodajGumb.getStyleClass().add(Stilovi.GUMB_PLAVI);
        dodajGumb.setOnAction(e -> dodajOdabranogStudenta());
        dodajGumb.setDisable(true);
    }

    private void konfigurirajUkloniGumb() {
        ukloniGumb.getStyleClass().add(Stilovi.GUMB_PLAVI);
        ukloniGumb.setOnAction(e -> ukloniOdabranogStudenta());
        ukloniGumb.setDisable(true);
    }

    private void konfigurirajDodajSveGumb() {
        dodajSveGumb.getStyleClass().add(Stilovi.GUMB_ZELENI);
        dodajSveGumb.setOnAction(e -> dodajSveStudente());
    }

    private void konfigurirajUkloniSveGumb() {
        ukloniSveGumb.getStyleClass().add(Stilovi.GUMB_ZELENI);
        ukloniSveGumb.setOnAction(e -> ukloniSveStudente());
    }

    private HBox kreirajKontrole() {
        HBox kontroleBox = new HBox();
        kontroleBox.getStyleClass().addAll(
                Stilovi.RAZMAK_KONTROLE
        );
        kontroleBox.setAlignment(Pos.CENTER);

        spremiGumb.getStyleClass().add(Stilovi.GUMB_PLAVI);
        spremiGumb.setOnAction(e -> spremiTim());

        odustaniGumb.getStyleClass().add(Stilovi.GUMB_ZELENI);
        odustaniGumb.setOnAction(e -> vratiSeNaPregled());

        kontroleBox.getChildren().addAll(spremiGumb, odustaniGumb);
        return kontroleBox;
    }

    private void postaviListenere() {
        postaviListenerZaDostupne();
        postaviListenerZaPostavljene();
    }

    private void postaviListenerZaDostupne() {
        tablicaDostupniStudenti.getSelectionModel().selectedItemProperty().addListener(
                (obs, stari, novi) -> dodajGumb.setDisable(novi == null)
        );
    }

    private void postaviListenerZaPostavljene() {
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
        List<Student> studentiHibernate = studentServis.dohvatiSveStudente();
        return konvertirajUStudentJson(studentiHibernate);
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
                .flatMap(tim -> tim.getClanovi().stream())
                .map(StudentJson::getJmbag)
                .toList();
    }

    private List<StudentJson> konvertirajUStudentJson(List<Student> studentiHibernate) {
        return studentiHibernate.stream()
                .map(student -> new StudentJson(
                        student.getJmbag(),
                        student.getIme(),
                        student.getPrezime()
                ))
                .collect(Collectors.toList());
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
        List<TimJson> sviTimovi = timServis.dohvatiSveTimove();

        return sviTimovi.stream()
                .anyMatch(t -> t.getNaziv().equalsIgnoreCase(naziv) &&
                        (timZaUredivanje == null || !t.getId().equals(timZaUredivanje.getId())));
    }

    private void kreirajNoviTim() {
        try {
            TimJson noviTim = new TimJson();
            noviTim.setNaziv(nazivPolje.getText().trim());
            noviTim.setClanovi(FXCollections.observableArrayList(postavljeniStudenti));

            boolean uspjeh = timServis.spremiTim(noviTim);

            if (uspjeh) {
                vratiSeNaPregled();
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
            timZaUredivanje.setClanovi(FXCollections.observableArrayList(postavljeniStudenti));

            boolean uspjeh = timServis.azurirajTim(timZaUredivanje);

            if (uspjeh) {
                vratiSeNaPregled();
            } else {
                poruke.prikaziGreskuSTimerom("greska_tim_nije_azuriran");
            }

        } catch (Exception e) {
            System.err.println("Greška pri ažuriranju tima: " + e.getMessage());
            poruke.prikaziGreskuSTimerom("greska_tim_nije_spremljen");
        }
    }

    private void vratiSeNaPregled() {
        UpraviteljPogleda.idiNatrag();
    }

    private boolean jeUredjivanje() {
        return timZaUredivanje != null;
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
        if (jeUredjivanje()) {
            naslov.setText(prijevod.getPrijevod("uredi_tim_naslov"));
        } else {
            naslov.setText(prijevod.getPrijevod("kreiraj_tim_naslov"));
        }
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
        TableColumn<StudentJson, ?> imeKolona = tablica.getColumns().get(0);
        TableColumn<StudentJson, ?> prezimeKolona = tablica.getColumns().get(1);
        TableColumn<StudentJson, ?> jmbagKolona = tablica.getColumns().get(2);

        imeKolona.setText(prijevod.getPrijevod("student_ime"));
        prezimeKolona.setText(prijevod.getPrijevod("student_prezime"));
        jmbagKolona.setText(prijevod.getPrijevod("student_jmbag"));
    }

    private void osvjeziPlaceholdere() {
        if (postavljeniStudenti.isEmpty()) {
            tablicaPostavljeniStudenti.setPlaceholder(
                    new Label(prijevod.getPrijevod("nema_postavljenih_studenata"))
            );
        }

        if (dostupniStudenti.isEmpty()) {
            tablicaDostupniStudenti.setPlaceholder(
                    new Label(prijevod.getPrijevod("nema_dostupnih_studenata"))
            );
        }
    }

    private void osvjeziGumbe() {
        if (jeUredjivanje()) {
            spremiGumb.setText(prijevod.getPrijevod("spremi_izmjene_gumb"));
        } else {
            spremiGumb.setText(prijevod.getPrijevod("kreiraj_gumb"));
        }

        odustaniGumb.setText(prijevod.getPrijevod("odustani_gumb"));
    }
}