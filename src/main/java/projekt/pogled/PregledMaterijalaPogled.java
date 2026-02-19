package projekt.pogled;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
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
import projekt.util.PorukaHelper;
import projekt.util.Stilovi;

import java.util.List;

public class PregledMaterijalaPogled extends OsnovniPogled {

    private final PredmetServis predmetServis;
    private final MaterijalXMLServis materijalServis;
    private final Profesor profesor;
    private final PorukaHelper poruke = PorukaHelper.kreiraj(prijevod);

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

    private MaterijalXML trenutnoUredjivani = null;

    public PregledMaterijalaPogled() {
        super();
        this.predmetServis = new PredmetServis();
        this.materijalServis = new MaterijalXMLServis();

        Korisnik prijavljeniKorisnik = Sesija.getInstanca().getPrijavljeniKorisnik();
        this.profesor = (Profesor) prijavljeniKorisnik;
    }

    @Override
    protected VBox kreirajSadrzaj() {
        VBox sadrzajBox = new VBox();
        sadrzajBox.getStyleClass().addAll(
                Stilovi.POZADINA_SVIJETLA,
                Stilovi.RAZMAK_SREDNJI,
                Stilovi.PADDING_SREDNJI
        );

        konfigurirajNaslov();
        konfigurirajTablicuPredmeta();
        konfigurirajTablicuMaterijala();

        HBox unosBox = kreirajUnosBox();
        HBox akcijeBox = kreirajAkcijeBox();

        VBox.setVgrow(tablicaPredmeta, Priority.ALWAYS);
        VBox.setVgrow(tablicaMaterijala, Priority.ALWAYS);

        sadrzajBox.getChildren().addAll(
                naslov,
                tablicaPredmeta,
                materijaliLabel,
                tablicaMaterijala,
                unosBox,
                poruke.kontejner,
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

        nazivPredmetaKolona.getStyleClass().add(Stilovi.KOLONA_NAZIV_PREDMETA);
        sifraPredmetaKolona.getStyleClass().add(Stilovi.KOLONA_SIFRA_PREDMETA);
        ectsPredmetaKolona.getStyleClass().add(Stilovi.KOLONA_ECTS_PREDMETA);
        semestarPredmetaKolona.getStyleClass().add(Stilovi.KOLONA_SEMESTAR_PREDMETA);

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

        nazivMaterijalKolona.getStyleClass().add(Stilovi.KOLONA_NAZIV_MATERIJALA);
        tipMaterijalKolona.getStyleClass().add(Stilovi.KOLONA_TIP_MATERIJALA);

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
        HBox unosBox = new HBox();
        unosBox.setAlignment(Pos.CENTER_LEFT);
        unosBox.getStyleClass().addAll(
                Stilovi.RAZMAK_UNOS
        );

        unosNaziva.getStyleClass().addAll(
                Stilovi.UNOS_NAZIV_SIRINA
        );
        tipComboBox.getStyleClass().addAll(
                Stilovi.POLJE_SIRINA_COMBO
        );

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

    private HBox kreirajAkcijeBox() {
        HBox akcijeBox = new HBox();
        unosNaziva.getStyleClass().addAll(
                Stilovi.RAZMAK_AKCIJE
        );
        akcijeBox.setAlignment(Pos.CENTER);
        akcijeBox.getStyleClass().add(Stilovi.RAZMAK_GUMBI);

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
            tablicaMaterijala.setPlaceholder(new Label(prijevod.getPrijevod("greska_ucitavanje_materijala")));
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
            poruke.prikaziGreskuSTimerom("greska_spremanje_materijala");
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
            poruke.prikaziGreskuSTimerom("greska_dodavanje_materijala");
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
            poruke.prikaziGreskuSTimerom("greska_azuriranje_materijala");
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
                poruke.prikaziGreskuSTimerom("greska_brisanje_materijala");
            }

        } catch (Exception e) {
            System.err.println("Greška pri brisanju materijala: " + e.getMessage());
            poruke.prikaziGreskuSTimerom("greska_brisanje_materijala");
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

    @Override
    protected void osvjeziPogledTekstove() {
        osvjeziNaslove();
        osvjeziKolonePredmeta();
        osvjeziKoloneMaterijala();
        osvjeziFormu();
        osvjeziGumbe();
        osvjeziComboBox();
        osvjeziPlaceholdere();
        poruke.osvjeziPoruku();
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
}