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
import projekt.model.*;
import projekt.servis.MaterijalXMLServis;
import projekt.servis.PredmetServis;
import projekt.upravitelj.Sesija;
import projekt.util.Stilovi;

import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class PregledMaterijalaPogled extends OsnovniPogled {

    private static final int RAZMAK_SADRZAJ = 15;
    private static final int RAZMAK_UNOS = 10;
    private static final int RAZMAK_AKCIJE = 10;
    private static final int PADDING_SADRZAJ = 20;
    private static final int SIRINA_LABELE_PORUKE = 600;
    private static final int SIRINA_UNOS_NAZIV = 300;
    private static final int SIRINA_COMBO = 150;
    private static final int TRAJANJE_PORUKE_MS = 3000;

    private static final double SIRINA_NAZIV_PREDMETA = 0.45;
    private static final double SIRINA_SIFRA_PREDMETA = 0.2;
    private static final double SIRINA_ECTS_PREDMETA = 0.15;
    private static final double SIRINA_SEMESTAR_PREDMETA = 0.2;

    private static final double SIRINA_NAZIV_MATERIJALA = 0.65;
    private static final double SIRINA_TIP_MATERIJALA = 0.35;

    private final PredmetServis predmetServis;
    private final MaterijalXMLServis materijalServis;
    private final Profesor profesor;

    private final ObservableList<Predmet> predmetiProfesora = FXCollections.observableArrayList();
    private final ObservableList<MaterijalXML> materijaliPredmeta = FXCollections.observableArrayList();

    private final Label naslov = new Label();
    private final Label materijaliLabel = new Label();

    private final TableView<Predmet> tablicaPredmeta = new TableView<>();
    private final TableView<MaterijalXML> tablicaMaterijala = new TableView<>();

    private final TableColumn<Predmet, String> nazivPredmetaKolona = new TableColumn<>();
    private final TableColumn<Predmet, String> sifraPredmetaKolona = new TableColumn<>();
    private final TableColumn<Predmet, Integer> ectsPredmetaKolona = new TableColumn<>();
    private final TableColumn<Predmet, String> semestarPredmetaKolona = new TableColumn<>();

    private final TableColumn<MaterijalXML, String> nazivMaterijalKolona = new TableColumn<>();
    private final TableColumn<MaterijalXML, String> tipMaterijalKolona = new TableColumn<>();

    private final Label nazivLabel = new Label();
    private final Label tipLabel = new Label();
    private final TextField unosNaziva = new TextField();
    private final ComboBox<TipMaterijalaXML> tipComboBox = new ComboBox<>();

    private final Button spremiGumb = new Button();
    private final Button urediGumb = new Button();
    private final Button izbrisiGumb = new Button();

    private final HBox hboxPoruka = new HBox();
    private final Label porukaLabela = new Label();
    private String trenutnaPoruka = null;
    private Timer timerPoruke;

    private MaterijalXML trenutnoUredjivani = null;

    public PregledMaterijalaPogled() {
        super();
        this.predmetServis = new PredmetServis();
        this.materijalServis = new MaterijalXMLServis();

        Korisnik prijavljeniKorisnik = Sesija.getInstanca().getPrijavljeniKorisnik();
        if (prijavljeniKorisnik instanceof Profesor) {
            this.profesor = (Profesor) prijavljeniKorisnik;
        } else {
            throw new IllegalStateException("Samo profesori mogu pristupiti materijalima");
        }
    }

    @Override
    protected VBox kreirajSadrzaj() {
        VBox sadrzajBox = new VBox(RAZMAK_SADRZAJ);
        sadrzajBox.setPadding(new Insets(PADDING_SADRZAJ));
        sadrzajBox.getStyleClass().add(Stilovi.POZADINA_SVIJETLA);

        konfigurirajNaslov();
        konfigurirajTablicuPredmeta();
        konfigurirajTablicuMaterijala();

        HBox unosBox = kreirajUnosBox();
        konfigurirajPorukaLabelu();
        HBox akcijeBox = kreirajAkcijeBox();

        VBox.setVgrow(tablicaPredmeta, Priority.ALWAYS);
        VBox.setVgrow(tablicaMaterijala, Priority.ALWAYS);

        sadrzajBox.getChildren().addAll(
                naslov,
                tablicaPredmeta,
                materijaliLabel,
                tablicaMaterijala,
                unosBox,
                hboxPoruka,
                akcijeBox
        );

        ucitajPredmeteProfesora();
        konfigurirajTipComboBox();
        postaviListenere();

        return sadrzajBox;
    }

    private void konfigurirajNaslov() {
        naslov.getStyleClass().add(Stilovi.NASLOV_TEKST);
        materijaliLabel.getStyleClass().add(Stilovi.PODNASLOV_TEKST);
    }

    private void konfigurirajTablicuPredmeta() {
        tablicaPredmeta.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        nazivPredmetaKolona.setCellValueFactory(new PropertyValueFactory<>("naziv"));
        sifraPredmetaKolona.setCellValueFactory(new PropertyValueFactory<>("sifra"));
        ectsPredmetaKolona.setCellValueFactory(new PropertyValueFactory<>("ectsBodovi"));
        semestarPredmetaKolona.setCellValueFactory(new PropertyValueFactory<>("semestar"));

        nazivPredmetaKolona.prefWidthProperty().bind(tablicaPredmeta.widthProperty().multiply(SIRINA_NAZIV_PREDMETA));
        sifraPredmetaKolona.prefWidthProperty().bind(tablicaPredmeta.widthProperty().multiply(SIRINA_SIFRA_PREDMETA));
        ectsPredmetaKolona.prefWidthProperty().bind(tablicaPredmeta.widthProperty().multiply(SIRINA_ECTS_PREDMETA));
        semestarPredmetaKolona.prefWidthProperty().bind(tablicaPredmeta.widthProperty().multiply(SIRINA_SEMESTAR_PREDMETA));

        tablicaPredmeta.getColumns().addAll(
                nazivPredmetaKolona, sifraPredmetaKolona,
                ectsPredmetaKolona, semestarPredmetaKolona
        );
        tablicaPredmeta.setItems(predmetiProfesora);
    }

    private void konfigurirajTablicuMaterijala() {
        tablicaMaterijala.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        nazivMaterijalKolona.setCellValueFactory(new PropertyValueFactory<>("naziv"));
        tipMaterijalKolona.setCellValueFactory(cellData -> {
            TipMaterijalaXML tip = cellData.getValue().getTip();
            String prevedeniTip = tip != null ? prijevod.getPrijevod(tip.getKljucPrijevoda()) : "";
            return new javafx.beans.property.SimpleStringProperty(prevedeniTip);
        });

        nazivMaterijalKolona.prefWidthProperty().bind(tablicaMaterijala.widthProperty().multiply(SIRINA_NAZIV_MATERIJALA));
        tipMaterijalKolona.prefWidthProperty().bind(tablicaMaterijala.widthProperty().multiply(SIRINA_TIP_MATERIJALA));

        tablicaMaterijala.getColumns().addAll(nazivMaterijalKolona, tipMaterijalKolona);
        tablicaMaterijala.setItems(materijaliPredmeta);

        tablicaMaterijala.setRowFactory(tv -> {
            TableRow<MaterijalXML> row = new TableRow<>();
            row.setOnMouseClicked(event -> {
                if (event.getClickCount() == 2 && !row.isEmpty()) {
                    omoguciUredjivanje();
                }
            });
            return row;
        });
    }

    private HBox kreirajUnosBox() {
        HBox unosBox = new HBox(RAZMAK_UNOS);
        unosBox.setAlignment(Pos.CENTER_LEFT);

        unosNaziva.setPrefWidth(SIRINA_UNOS_NAZIV);
        tipComboBox.setPrefWidth(SIRINA_COMBO);

        unosNaziva.getStyleClass().add(Stilovi.POLJE_TEKSTA);

        unosBox.getChildren().addAll(
                nazivLabel, unosNaziva,
                tipLabel, tipComboBox
        );

        return unosBox;
    }

    private void konfigurirajTipComboBox() {
        tipComboBox.getItems().setAll(TipMaterijalaXML.values());
        postaviCellFactoryZaComboBox();
    }

    private void postaviCellFactoryZaComboBox() {
        tipComboBox.setCellFactory(lv -> new ListCell<TipMaterijalaXML>() {
            @Override
            protected void updateItem(TipMaterijalaXML tip, boolean empty) {
                super.updateItem(tip, empty);
                setText(empty || tip == null ? null : prijevod.getPrijevod(tip.getKljucPrijevoda()));
            }
        });

        tipComboBox.setButtonCell(new ListCell<TipMaterijalaXML>() {
            @Override
            protected void updateItem(TipMaterijalaXML tip, boolean empty) {
                super.updateItem(tip, empty);
                setText(empty || tip == null ? null : prijevod.getPrijevod(tip.getKljucPrijevoda()));
            }
        });
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

    private HBox kreirajAkcijeBox() {
        HBox akcijeBox = new HBox(RAZMAK_AKCIJE);
        akcijeBox.setAlignment(Pos.CENTER);

        spremiGumb.getStyleClass().add(Stilovi.GUMB_PRIMARAN);
        urediGumb.getStyleClass().add(Stilovi.GUMB_SEKUNDARAN);
        izbrisiGumb.getStyleClass().add(Stilovi.GUMB_OPASAN);

        urediGumb.setDisable(true);
        izbrisiGumb.setDisable(true);
        spremiGumb.setDisable(true);
        unosNaziva.setDisable(true);
        tipComboBox.setDisable(true);

        spremiGumb.setOnAction(e -> obradiSpremanje());
        urediGumb.setOnAction(e -> omoguciUredjivanje());
        izbrisiGumb.setOnAction(e -> obradiBrisanje());

        akcijeBox.getChildren().addAll(spremiGumb, urediGumb, izbrisiGumb);
        return akcijeBox;
    }

    private void postaviListenere() {
        postaviListenerZaTablicuPredmeta();
        postaviListenerZaTablicuMaterijala();
        postaviListenerZaUnosNaziva();
        postaviListenerZaTipComboBox();
    }

    private void postaviListenerZaTablicuPredmeta() {
        tablicaPredmeta.getSelectionModel().selectedItemProperty().addListener(
                (obs, stari, novi) -> {
                    ucitajMaterijalePredmeta(novi);

                    boolean predmetOdabran = novi != null;
                    unosNaziva.setDisable(!predmetOdabran);
                    tipComboBox.setDisable(!predmetOdabran);

                    if (!predmetOdabran) {
                        ocistiFormu();
                    }

                    azurirajStanjeSpremiGumba();
                }
        );
    }

    private void postaviListenerZaTablicuMaterijala() {
        tablicaMaterijala.getSelectionModel().selectedItemProperty().addListener(
                (obs, stari, novi) -> {
                    boolean imaOdabir = novi != null;
                    urediGumb.setDisable(!imaOdabir);
                    izbrisiGumb.setDisable(!imaOdabir);
                }
        );
    }

    private void postaviListenerZaUnosNaziva() {
        unosNaziva.textProperty().addListener((obs, stari, novi) ->
                azurirajStanjeSpremiGumba()
        );
    }

    private void postaviListenerZaTipComboBox() {
        tipComboBox.valueProperty().addListener((obs, stari, novi) ->
                azurirajStanjeSpremiGumba()
        );
    }

    private void azurirajStanjeSpremiGumba() {
        boolean imaUnos = !unosNaziva.getText().trim().isEmpty() && tipComboBox.getValue() != null;
        boolean predmetOdabran = tablicaPredmeta.getSelectionModel().getSelectedItem() != null;
        spremiGumb.setDisable(!imaUnos || !predmetOdabran);
    }

    private void ucitajPredmeteProfesora() {
        try {
            List<Predmet> predmeti = predmetServis.dohvatiPredmeteProfesora(profesor.getId());

            if (predmeti == null || predmeti.isEmpty()) {
                predmetiProfesora.clear();
                tablicaPredmeta.setPlaceholder(new Label(prijevod.getPrijevod("nema_predmeta")));
                return;
            }

            predmetiProfesora.setAll(predmeti);

        } catch (Exception e) {
            System.err.println("Greška pri učitavanju predmeta: " + e.getMessage());
            predmetiProfesora.clear();
            tablicaPredmeta.setPlaceholder(new Label(prijevod.getPrijevod("greska_ucitavanje_predmeta")));
        }
    }

    private void ucitajMaterijalePredmeta(Predmet predmet) {
        if (predmet == null) {
            materijaliPredmeta.clear();
            tablicaMaterijala.setPlaceholder(new Label(prijevod.getPrijevod("odaberite_predmet")));
            return;
        }

        try {
            List<MaterijalXML> materijali = materijalServis.dohvatiMaterijaleZaPredmet(predmet.getId());

            if (materijali == null || materijali.isEmpty()) {
                materijaliPredmeta.clear();
                tablicaMaterijala.setPlaceholder(new Label(prijevod.getPrijevod("nema_materijala")));
                return;
            }

            materijaliPredmeta.setAll(materijali);

        } catch (Exception e) {
            System.err.println("Greška pri učitavanju materijala: " + e.getMessage());
            materijaliPredmeta.clear();
            prikaziGresku("greska_ucitavanje_materijala");
        }
    }

    private void omoguciUredjivanje() {
        MaterijalXML odabrani = tablicaMaterijala.getSelectionModel().getSelectedItem();

        unosNaziva.setText(odabrani.getNaziv());
        tipComboBox.setValue(odabrani.getTip());
        trenutnoUredjivani = odabrani;

        unosNaziva.requestFocus();
    }

    private void obradiSpremanje() {
        Predmet odabraniPredmet = tablicaPredmeta.getSelectionModel().getSelectedItem();
        String naziv = unosNaziva.getText().trim();
        TipMaterijalaXML tip = tipComboBox.getValue();

        try {
            if (jeUredjivanje()) {
                azurirajMaterijal(odabraniPredmet, naziv, tip);
            } else {
                dodajNoviMaterijal(odabraniPredmet, naziv, tip);
            }

            ucitajMaterijalePredmeta(odabraniPredmet);
            ocistiFormu();

        } catch (Exception e) {
            System.err.println("Greška pri spremanju materijala: " + e.getMessage());
            prikaziGresku("greska_spremanje_materijala");
        }
    }

    private void dodajNoviMaterijal(Predmet predmet, String naziv, TipMaterijalaXML tip) {
        MaterijalXML noviMaterijal = new MaterijalXML(naziv, tip);
        boolean predmetPostojiUXml = materijalServis.predmetPostojiUXml(predmet.getId());

        boolean uspjeh;
        if (predmetPostojiUXml) {
            uspjeh = materijalServis.dodajMaterijalZaPostojeciPredmet(predmet.getId(), noviMaterijal);
        } else {
            uspjeh = materijalServis.dodajMaterijalZaPredmet(predmet.getId(), noviMaterijal, predmet.getNaziv());
        }

        if (!uspjeh) {
            prikaziGresku("greska_dodavanje_materijala");
        }
    }

    private void azurirajMaterijal(Predmet predmet, String naziv, TipMaterijalaXML tip) {
        MaterijalXML azurirani = new MaterijalXML(naziv, tip);
        azurirani.setId(trenutnoUredjivani.getId());

        boolean uspjeh = materijalServis.azurirajMaterijal(
                predmet.getId(),
                trenutnoUredjivani.getId(),
                azurirani
        );

        if (!uspjeh) {
            prikaziGresku("greska_azuriranje_materijala");
        }
    }

    private void obradiBrisanje() {
        MaterijalXML odabraniMaterijal = tablicaMaterijala.getSelectionModel().getSelectedItem();
        Predmet odabraniPredmet = tablicaPredmeta.getSelectionModel().getSelectedItem();

        try {
            boolean uspjeh = materijalServis.izbrisiMaterijal(
                    odabraniPredmet.getId(),
                    odabraniMaterijal.getId()
            );

            if (uspjeh) {
                ucitajMaterijalePredmeta(odabraniPredmet);

                if (jeUredjivanje() && trenutnoUredjivani.getId().equals(odabraniMaterijal.getId())) {
                    ocistiFormu();
                }
            } else {
                prikaziGresku("greska_brisanje_materijala");
            }

        } catch (Exception e) {
            System.err.println("Greška pri brisanju materijala: " + e.getMessage());
            prikaziGresku("greska_brisanje_materijala");
        }
    }

    private boolean jeUredjivanje() {
        return trenutnoUredjivani != null;
    }

    private void ocistiFormu() {
        unosNaziva.clear();
        tipComboBox.setValue(null);
        trenutnoUredjivani = null;
    }

    private void prikaziGresku(String kljucGreske) {
        trenutnaPoruka = kljucGreske;
        porukaLabela.setText(prijevod.getPrijevod(kljucGreske));

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
        osvjeziNaslove();
        osvjeziKolonePredmeta();
        osvjeziKoloneMaterijala();
        osvjeziFormu();
        osvjeziGumbe();
        osvjeziComboBox();
        osvjeziPlaceholdere();
        osvjeziPorukuAkoPostoji();
    }

    private void osvjeziNaslove() {
        naslov.setText(prijevod.getPrijevod("materijali_naslov"));
        materijaliLabel.setText(prijevod.getPrijevod("materijali_podnaslov"));
    }

    private void osvjeziKolonePredmeta() {
        nazivPredmetaKolona.setText(prijevod.getPrijevod("predmet_naziv"));
        sifraPredmetaKolona.setText(prijevod.getPrijevod("predmet_sifra"));
        ectsPredmetaKolona.setText(prijevod.getPrijevod("predmet_ects"));
        semestarPredmetaKolona.setText(prijevod.getPrijevod("predmet_semestar"));
    }

    private void osvjeziKoloneMaterijala() {
        nazivMaterijalKolona.setText(prijevod.getPrijevod("materijal_naziv"));
        tipMaterijalKolona.setText(prijevod.getPrijevod("materijal_tip"));
        tablicaMaterijala.refresh();
    }

    private void osvjeziFormu() {
        nazivLabel.setText(prijevod.getPrijevod("materijal_naziv_labela"));
        tipLabel.setText(prijevod.getPrijevod("materijal_tip_labela"));
        unosNaziva.setPromptText(prijevod.getPrijevod("materijal_naziv_prompt"));
        tipComboBox.setPromptText(prijevod.getPrijevod("materijal_tip_prompt"));
    }

    private void osvjeziGumbe() {
        if (jeUredjivanje()) {
            spremiGumb.setText(prijevod.getPrijevod("azuriraj_materijal_gumb"));
        } else {
            spremiGumb.setText(prijevod.getPrijevod("spremi_izmjene_gumb"));
        }

        urediGumb.setText(prijevod.getPrijevod("uredi_materijal_gumb"));
        izbrisiGumb.setText(prijevod.getPrijevod("izbrisi_materijal_gumb"));
    }

    private void osvjeziComboBox() {
        TipMaterijalaXML odabrani = tipComboBox.getValue();
        postaviCellFactoryZaComboBox();
        tipComboBox.setValue(odabrani);
    }

    private void osvjeziPlaceholdere() {
        tablicaPredmeta.setPlaceholder(new Label(prijevod.getPrijevod("nema_predmeta")));

        if (tablicaPredmeta.getSelectionModel().getSelectedItem() == null) {
            tablicaMaterijala.setPlaceholder(new Label(prijevod.getPrijevod("odaberite_predmet")));
        } else {
            tablicaMaterijala.setPlaceholder(new Label(prijevod.getPrijevod("nema_materijala")));
        }
    }

    private void osvjeziPorukuAkoPostoji() {
        if (trenutnaPoruka != null) {
            porukaLabela.setText(prijevod.getPrijevod(trenutnaPoruka));
        }
    }
}