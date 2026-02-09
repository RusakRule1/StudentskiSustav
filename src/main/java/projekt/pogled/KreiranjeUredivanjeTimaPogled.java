package projekt.pogled;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
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
import projekt.util.Stilovi;

import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.stream.Collectors;

public class KreiranjeUredivanjeTimaPogled extends OsnovniPogled {

    private static final int RAZMAK_SADRZAJ = 20;
    private static final int RAZMAK_KONTROLE = 15;
    private static final int PADDING_SADRZAJ = 20;
    private static final int SIRINA_LABELE_PORUKE = 600;
    private static final int VISINA_TABLICE = 150;
    private static final int TRAJANJE_PORUKE_MS = 3000;

    private final TimJson timZaUredivanje;
    private final TimJsonServis timServis;
    private final StudentServis studentServis;

    private final Label naslov = new Label();
    private final TextField nazivPolje = new TextField();
    private final Label porukaGreske = new Label();

    private final Label labelPostavljeni = new Label();
    private final Label labelDostupni = new Label();

    private final TableView<StudentJson> tablicaPostavljeniStudenti = new TableView<>();
    private final TableView<StudentJson> tablicaDostupniStudenti = new TableView<>();


    private final ObservableList<StudentJson> postavljeniStudenti = FXCollections.observableArrayList();
    private final ObservableList<StudentJson> dostupniStudenti = FXCollections.observableArrayList();

    private final Button dodajGumb = new Button();
    private final Button ukloniGumb = new Button();
    private final Button dodajSveGumb = new Button();
    private final Button ukloniSveGumb = new Button();

    private final Button spremiGumb = new Button();
    private final Button odustaniGumb = new Button();

    private HBox hboxPoruka = new HBox();
    private final Label porukaLabela = new Label();
    private String trenutnaPoruka = null;
    private Timer timerPoruke;

    public KreiranjeUredivanjeTimaPogled(TimJson timZaUredivanje) {
        super();
        this.timZaUredivanje = timZaUredivanje;
        this.timServis = new TimJsonServis();
        this.studentServis = new StudentServis();
    }

    @Override
    protected VBox kreirajSadrzaj() {
        VBox sadrzajBox = new VBox(RAZMAK_SADRZAJ);
        sadrzajBox.setPadding(new Insets(PADDING_SADRZAJ));
        sadrzajBox.getStyleClass().add(Stilovi.POZADINA_SVIJETLA);

        konfigurirajNaslov();
        konfigurirajFormu();
        VBox tabliceBox = kreirajTabliceBox();
        konfigurirajPorukaLabelu();
        HBox kontroleBox = kreirajKontrole();

        sadrzajBox.getChildren().addAll(
                naslov,
                porukaGreske,
                nazivPolje,
                tabliceBox,
                hboxPoruka,
                kontroleBox
        );
        ucitajStudente();
        postaviListenere();

        return sadrzajBox;
    }

    private void konfigurirajNaslov() {
        naslov.getStyleClass().add(Stilovi.NASLOV_TEKST);
    }

    private void konfigurirajFormu() {
        porukaGreske.getStyleClass().add(Stilovi.PORUKA_GRESKA);
        porukaGreske.setVisible(false);
    }

    private VBox kreirajTabliceBox() {
        VBox glavniBox = new VBox(10);


        labelPostavljeni.getStyleClass().add(Stilovi.PODNASLOV_TEKST);
        konfigurirajTablicu(tablicaPostavljeniStudenti, postavljeniStudenti);

        VBox postavljeniBox = new VBox(5);
        postavljeniBox.getChildren().addAll(labelPostavljeni, tablicaPostavljeniStudenti);

        HBox gumbiBox = new HBox(10);
        gumbiBox.setAlignment(Pos.CENTER);
        konfigurirajGumbeZaManipulaciju();
        gumbiBox.getChildren().addAll(dodajGumb, ukloniGumb, dodajSveGumb, ukloniSveGumb);

        labelDostupni.getStyleClass().add(Stilovi.PODNASLOV_TEKST);
        konfigurirajTablicu(tablicaDostupniStudenti, dostupniStudenti);

        VBox dostupniBox = new VBox(5);
        dostupniBox.getChildren().addAll(labelDostupni, tablicaDostupniStudenti);

        VBox.setVgrow(postavljeniBox, Priority.ALWAYS);
        VBox.setVgrow(dostupniBox, Priority.ALWAYS);
        tablicaPostavljeniStudenti.setPrefHeight(VISINA_TABLICE);
        tablicaDostupniStudenti.setPrefHeight(VISINA_TABLICE);

        glavniBox.getChildren().addAll(postavljeniBox, gumbiBox, dostupniBox);
        return glavniBox;
    }

    private void konfigurirajTablicu(TableView<StudentJson> tablica, ObservableList<StudentJson> podaci) {
        tablica.getColumns().clear();

        TableColumn<StudentJson, String> imeKolona = new TableColumn<>();
        TableColumn<StudentJson, String> prezimeKolona = new TableColumn<>();
        TableColumn<StudentJson, String> jmbagKolona = new TableColumn<>();

        imeKolona.setCellValueFactory(new PropertyValueFactory<>("ime"));
        prezimeKolona.setCellValueFactory(new PropertyValueFactory<>("prezime"));
        jmbagKolona.setCellValueFactory(new PropertyValueFactory<>("jmbag"));

        imeKolona.prefWidthProperty().bind(tablica.widthProperty().multiply(0.3));
        prezimeKolona.prefWidthProperty().bind(tablica.widthProperty().multiply(0.3));
        jmbagKolona.prefWidthProperty().bind(tablica.widthProperty().multiply(0.4));

        tablica.getColumns().addAll(imeKolona, prezimeKolona, jmbagKolona);
        tablica.setItems(podaci);
        tablica.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
    }

    private void konfigurirajGumbeZaManipulaciju() {
        dodajGumb.getStyleClass().add(Stilovi.GUMB_PRIMARAN);
        dodajGumb.setOnAction(e -> dodajOdabranogStudenta());
        dodajGumb.setDisable(true);

        ukloniGumb.getStyleClass().add(Stilovi.GUMB_PRIMARAN);
        ukloniGumb.setOnAction(e -> ukloniOdabranogStudenta());
        ukloniGumb.setDisable(true);

        dodajSveGumb.getStyleClass().add(Stilovi.GUMB_PRIMARAN);
        dodajSveGumb.setOnAction(e -> dodajSveStudente());
        dodajSveGumb.setDisable(dostupniStudenti.isEmpty());

        ukloniSveGumb.getStyleClass().add(Stilovi.GUMB_PRIMARAN);
        ukloniSveGumb.setOnAction(e -> ukloniSveStudente());
        ukloniSveGumb.setDisable(postavljeniStudenti.isEmpty());
    }

    private HBox kreirajKontrole() {
        HBox kontroleBox = new HBox(RAZMAK_KONTROLE);
        kontroleBox.setAlignment(Pos.CENTER);

        spremiGumb.getStyleClass().add(Stilovi.GUMB_PRIMARAN);
        spremiGumb.setOnAction(e -> spremiTim());

        odustaniGumb.getStyleClass().add(Stilovi.GUMB_SEKUNDARAN);
        odustaniGumb.setOnAction(e -> vratiSeNaPregled());

        kontroleBox.getChildren().addAll(spremiGumb, odustaniGumb);
        return kontroleBox;
    }

    private void konfigurirajPorukaLabelu() {
        hboxPoruka.getChildren().clear();
        porukaLabela.getStyleClass().add(Stilovi.PORUKA_GRESKA);
        porukaLabela.setWrapText(true);
        porukaLabela.setMaxWidth(SIRINA_LABELE_PORUKE);
        hboxPoruka.setAlignment(Pos.CENTER);
        hboxPoruka.getChildren().add(porukaLabela);
        hboxPoruka.setVisible(false);
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
            List<Student> sviStudentiHibernate = studentServis.dohvatiSveStudente();
            List<StudentJson> sviStudentiJson = konvertirajUStudentJson(sviStudentiHibernate);

            List<TimJson> sviTimovi = timServis.dohvatiSveTimove();

            if (timZaUredivanje != null) {
                nazivPolje.setText(timZaUredivanje.getNaziv());
                if (timZaUredivanje.getClanovi() != null) {
                    postavljeniStudenti.setAll(timZaUredivanje.getClanovi());
                }

                List<String> postavljeniJmbagovi = new java.util.ArrayList<>();

                if (timZaUredivanje.getClanovi() != null) {
                    postavljeniJmbagovi.addAll(
                            timZaUredivanje.getClanovi().stream()
                                    .map(StudentJson::getJmbag)
                                    .toList()
                    );
                }

                List<String> zauzetiJmbagovi = sviTimovi.stream()
                        .filter(tim -> !tim.getId().equals(timZaUredivanje.getId()))
                        .flatMap(tim -> tim.getClanovi().stream())
                        .map(StudentJson::getJmbag)
                        .toList();

                dostupniStudenti.setAll(
                        sviStudentiJson.stream()
                                .filter(s -> !postavljeniJmbagovi.contains(s.getJmbag()) &&
                                        !zauzetiJmbagovi.contains(s.getJmbag()))
                                .collect(Collectors.toList())
                );
            } else {
                List<String> zauzetiJmbagovi = sviTimovi.stream()
                        .flatMap(tim -> tim.getClanovi().stream())
                        .map(StudentJson::getJmbag)
                        .toList();

                dostupniStudenti.setAll(
                        sviStudentiJson.stream()
                                .filter(s -> !zauzetiJmbagovi.contains(s.getJmbag()))
                                .collect(Collectors.toList())
                );
                postavljeniStudenti.clear();
            }

            dodajSveGumb.setDisable(dostupniStudenti.isEmpty());
            ukloniSveGumb.setDisable(postavljeniStudenti.isEmpty());

        } catch (Exception e) {
            System.err.println("Greška pri učitavanju studenata: " + e.getMessage());
            prikaziPoruku("greska_ucitavanje_studenata");
        }
    }

    private List<StudentJson> konvertirajUStudentJson(List<Student> studentiHibernate) {
        return studentiHibernate.stream()
                .map(student -> new StudentJson(
                        student.getJmbag(),
                        student.getEmail(),
                        student.getIme(),
                        student.getPrezime()
                ))
                .collect(Collectors.toList());
    }

    private void dodajOdabranogStudenta() {
        StudentJson odabrani = tablicaDostupniStudenti.getSelectionModel().getSelectedItem();
        if (odabrani != null) {
            dostupniStudenti.remove(odabrani);
            postavljeniStudenti.add(odabrani);
            azurirajStanjeGumba();
        }
    }

    private void ukloniOdabranogStudenta() {
        StudentJson odabrani = tablicaPostavljeniStudenti.getSelectionModel().getSelectedItem();
        if (odabrani != null) {
            postavljeniStudenti.remove(odabrani);
            dostupniStudenti.add(odabrani);
            azurirajStanjeGumba();
        }
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
        try {
            String naziv = nazivPolje.getText().trim();

            if (naziv.isEmpty()) {
                prikaziPoruku("greska_naziv_tima_obavezan");
                return;
            }

            if (naziv.length() < 2) {
                prikaziPoruku("greska_naziv_tima_prekratak");
                return;
            }

            List<TimJson> sviTimovi = timServis.dohvatiSveTimove();
            boolean postoji = sviTimovi.stream()
                    .anyMatch(t -> t.getNaziv().equalsIgnoreCase(naziv) &&
                            (timZaUredivanje == null || !t.getId().equals(timZaUredivanje.getId())));

            if (postoji) {
                prikaziPoruku("greska_naziv_tima_postoji");
                return;
            }

            if (postavljeniStudenti.isEmpty()) {
                prikaziPoruku("greska_tim_mora_imati_clanove");
                return;
            }

            if (timZaUredivanje == null) {
                TimJson noviTim = new TimJson();
                noviTim.setNaziv(naziv);
                noviTim.setClanovi(FXCollections.observableArrayList(postavljeniStudenti));

                boolean uspjeh = timServis.spremiTim(noviTim);
                if (uspjeh) {
                    vratiSeNaPregled();
                } else {
                    prikaziPoruku("greska_tim_nije_kreiran");
                }
            } else {
                timZaUredivanje.setNaziv(naziv);
                timZaUredivanje.setClanovi(FXCollections.observableArrayList(postavljeniStudenti));

                boolean uspjeh = timServis.azurirajTim(timZaUredivanje);
                if (uspjeh) {
                    vratiSeNaPregled();
                } else {
                    prikaziPoruku("greska_tim_nije_azuriran");
                }
            }

        } catch (Exception e) {
            System.err.println("Greška pri spremanju tima: " + e.getMessage());
            prikaziPoruku("greska_tim_nije_spremljen");
        }
    }

    private void vratiSeNaPregled() {
        UpraviteljPogleda.idiNatrag();
    }

    private void prikaziPoruku(String kljucPoruke) {
        trenutnaPoruka = kljucPoruke;
        porukaLabela.setText(prijevod.getPrijevod(kljucPoruke));
        porukaLabela.getStyleClass().removeAll(
                Stilovi.PORUKA_GRESKA,
                Stilovi.PORUKA_USPJESNO
        );
        porukaLabela.getStyleClass().add(Stilovi.PORUKA_GRESKA);

        hboxPoruka.setVisible(true);

        startTimerZaBrisanjePoruke();
    }

    private void sakrijPoruku() {
        porukaLabela.setText("");
        hboxPoruka.setVisible(false);
        trenutnaPoruka = null;
    }

    private void startTimerZaBrisanjePoruke() {
        if (timerPoruke != null) {
            timerPoruke.cancel();
        }

        timerPoruke = new Timer(true);
        timerPoruke.schedule(new TimerTask() {
            @Override
            public void run() {
                Platform.runLater(() -> sakrijPoruku());
            }
        }, TRAJANJE_PORUKE_MS);
    }

    @Override
    protected void osvjeziPogledTekstove() {
        osvjeziNaslov();
        osvjeziTablicu();
        osvjeziGumbe();
        osvjeziPorukuAkoPostoji();
    }

    private void osvjeziNaslov() {
        if (timZaUredivanje == null) {
            naslov.setText(prijevod.getPrijevod("kreiraj_tim_naslov"));
        } else {
            naslov.setText(prijevod.getPrijevod("uredi_tim_naslov"));
        }
        nazivPolje.setPromptText(prijevod.getPrijevod("tim_naziv_prompt"));
    }

    private void osvjeziTablicu() {
        labelPostavljeni.setText(prijevod.getPrijevod("postavljeni_clanovi"));
        labelDostupni.setText(prijevod.getPrijevod("dostupni_studenti"));

        TableColumn<StudentJson, ?> imeKolonaPostavljeni = tablicaPostavljeniStudenti.getColumns().get(0);
        TableColumn<StudentJson, ?> prezimeKolonaPostavljeni = tablicaPostavljeniStudenti.getColumns().get(1);
        TableColumn<StudentJson, ?> jmbagKolonaPostavljeni = tablicaPostavljeniStudenti.getColumns().get(2);

        TableColumn<StudentJson, ?> imeKolonaDostupni = tablicaDostupniStudenti.getColumns().get(0);
        TableColumn<StudentJson, ?> prezimeKolonaDostupni = tablicaDostupniStudenti.getColumns().get(1);
        TableColumn<StudentJson, ?> jmbagKolonaDostupni = tablicaDostupniStudenti.getColumns().get(2);

        imeKolonaPostavljeni.setText(prijevod.getPrijevod("student_ime"));
        prezimeKolonaPostavljeni.setText(prijevod.getPrijevod("student_prezime"));
        jmbagKolonaPostavljeni.setText(prijevod.getPrijevod("student_jmbag"));

        imeKolonaDostupni.setText(prijevod.getPrijevod("student_ime"));
        prezimeKolonaDostupni.setText(prijevod.getPrijevod("student_prezime"));
        jmbagKolonaDostupni.setText(prijevod.getPrijevod("student_jmbag"));

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
        if (timZaUredivanje == null) {
            spremiGumb.setText(prijevod.getPrijevod("kreiraj_gumb"));
        } else {
            spremiGumb.setText(prijevod.getPrijevod("spremi_izmjene_gumb"));
        }

        dodajGumb.setText("↑");
        ukloniGumb.setText("↓");
        dodajSveGumb.setText("↑↑");
        ukloniSveGumb.setText("↓↓");
        odustaniGumb.setText(prijevod.getPrijevod("odustani_gumb"));
    }

    private void osvjeziPorukuAkoPostoji() {
        if (trenutnaPoruka != null) {
            porukaLabela.setText(prijevod.getPrijevod(trenutnaPoruka));
        }
    }
}