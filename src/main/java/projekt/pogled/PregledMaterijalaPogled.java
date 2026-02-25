package projekt.pogled;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import projekt.model.MaterijalXML;
import projekt.model.Predmet;
import projekt.model.Profesor;
import projekt.model.TipMaterijalaXML;
import projekt.servis.MaterijalXMLServis;
import projekt.servis.PredmetServis;
import projekt.upravitelj.Sesija;
import projekt.util.PorukaHelper;
import projekt.util.Stilovi;
import projekt.util.UITvornica;

import java.util.List;

import static projekt.util.UITvornica.*;

public class PregledMaterijalaPogled extends OsnovniPogled {

    private final PredmetServis predmetServis;
    private final MaterijalXMLServis materijalServis;
    private final Profesor profesor;
    private final PorukaHelper poruke;

    private final ObservableList<Predmet> predmetiProfesora = FXCollections.observableArrayList();
    private final ObservableList<MaterijalXML> materijaliPredmeta = FXCollections.observableArrayList();

    private final Label naslov = labela().stil(Stilovi.PODNASLOV).build();
    private final Label materijaliLabel = labela().stil(Stilovi.PODNASLOV).build();

    private final TableColumn<Predmet, String> nazivPredmetaKolona = UITvornica.<Predmet, String>kolona("naziv", Stilovi.KOLONA_NAZIV_PREDMETA).build();
    private final TableColumn<Predmet, String> sifraPredmetaKolona = UITvornica.<Predmet, String>kolona("sifra", Stilovi.KOLONA_SIFRA_PREDMETA).build();
    private final TableColumn<Predmet, Integer> ectsPredmetaKolona = UITvornica.<Predmet, Integer>kolona("ectsBodovi", Stilovi.KOLONA_ECTS_PREDMETA).build();
    private final TableColumn<Predmet, String> semestarPredmetaKolona = UITvornica.<Predmet, String>kolona("semestar", Stilovi.KOLONA_SEMESTAR_PREDMETA).build();

    private final TableView<Predmet> tablicaPredmeta = UITvornica.<Predmet>tableView()
            .kolone(nazivPredmetaKolona, sifraPredmetaKolona, ectsPredmetaKolona, semestarPredmetaKolona)
            .stavke(predmetiProfesora)
            .constrained()
            .stil(Stilovi.TABLICA_VISINA_MALA)
            .build();

    private final TableColumn<MaterijalXML, String> nazivMaterijalKolona = UITvornica.<MaterijalXML, String>kolona("naziv").build();
    private final TableColumn<MaterijalXML, String> tipMaterijalKolona = UITvornica.<MaterijalXML, String>kolona(
            cellData -> {
                TipMaterijalaXML tip = cellData.getValue().getTip();
                String prevedeniTip = tip != null ? prijevod.getPrijevod(tip.getKljucPrijevoda()) : "";
                return new SimpleStringProperty(prevedeniTip);
            }
    ).build();

    private final TableView<MaterijalXML> tablicaMaterijala = UITvornica.<MaterijalXML>tableView()
            .kolone(nazivMaterijalKolona, tipMaterijalKolona)
            .stavke(materijaliPredmeta)
            .constrained()
            .stil(Stilovi.TABLICA_VISINA_MALA)
            .build();

    private final Label nazivLabel = labela().build();
    private final Label tipLabel = labela().build();
    private final TextField unosNaziva = textField()
            .stil(Stilovi.POLJE_SIRINA_VELIKA, Stilovi.POLJE_TEKSTA)
            .onemogucen(true)
            .build();
    private final ComboBox<TipMaterijalaXML> tipComboBox = UITvornica.<TipMaterijalaXML>comboBox()
            .stil(Stilovi.POLJE_SIRINA_COMBO)
            .onemogucen(true)
            .stavke(TipMaterijalaXML.values())
            .build();

    private Button spremiGumb;
    private Button urediGumb;
    private Button izbrisiGumb;

    private MaterijalXML trenutnoUredjivani = null;

    public PregledMaterijalaPogled() {
        super();
        this.predmetServis = new PredmetServis();
        this.materijalServis = new MaterijalXMLServis();
        this.profesor = (Profesor) Sesija.getInstanca().getPrijavljeniKorisnik();
        this.poruke = PorukaHelper.kreiraj(prijevod);
        konfigurirajGumbe();
        postaviCellFactoryZaComboBox();
        konfigurirajTablicuMaterijala();
    }

    @Override
    protected VBox kreirajSadrzaj() {
        HBox unosBox = kreirajUnosBox();
        HBox akcijeBox = kreirajAkcijeBox();

        VBox sadrzaj = vbox(naslov, tablicaPredmeta, materijaliLabel, tablicaMaterijala,
                unosBox, poruke.getKontejner(), akcijeBox)
                .stil(Stilovi.RAZMAK_SREDNJI, Stilovi.PADDING_SREDNJI)
                .grow(Priority.ALWAYS)
                .build();

        VBox.setVgrow(tablicaPredmeta, Priority.ALWAYS);
        VBox.setVgrow(tablicaMaterijala, Priority.ALWAYS);

        ucitajPredmeteProfesora();
        postaviListenere();

        return sadrzaj;
    }

    private void konfigurirajGumbe() {
        spremiGumb = gumb(Stilovi.GUMB_PLAVI, this::obradiSpremanje).onemogucen(true).build();
        urediGumb = gumb(Stilovi.GUMB_ZELENI, this::omoguciUredjivanje).onemogucen(true).build();
        izbrisiGumb = gumb(Stilovi.GUMB_CRVENI, this::obradiBrisanje).onemogucen(true).build();
    }

    private void konfigurirajTablicuMaterijala() {
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
        return hbox(nazivLabel, unosNaziva, tipLabel, tipComboBox)
                .pozicija(Pos.CENTER_LEFT)
                .stil(Stilovi.RAZMAK_UNOS)
                .build();
    }

    private HBox kreirajAkcijeBox() {
        return hbox(spremiGumb, urediGumb, izbrisiGumb)
                .pozicija(Pos.CENTER)
                .stil(Stilovi.RAZMAK_GUMBI)
                .build();
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
            List<Predmet> predmeti = predmetServis.pronadjiPredmeteProfesora(profesor.getId());

            if (predmeti == null || predmeti.isEmpty()) {
                predmetiProfesora.clear();
                tablicaPredmeta.setPlaceholder(labela(prijevod.getPrijevod("nema_predmeta")).build());
                return;
            }

            predmetiProfesora.setAll(predmeti);

        } catch (Exception e) {
            System.err.println("Greška pri učitavanju predmeta: " + e.getMessage());
            predmetiProfesora.clear();
            tablicaPredmeta.setPlaceholder(labela(prijevod.getPrijevod("greska_ucitavanje_predmeta")).build());
        }
    }

    private void ucitajMaterijalePredmeta(Predmet predmet) {
        if (predmet == null) {
            materijaliPredmeta.clear();
            tablicaMaterijala.setPlaceholder(labela(prijevod.getPrijevod("odaberite_predmet")).build());
            return;
        }

        try {
            List<MaterijalXML> materijali = materijalServis.pronadjiMaterijaleZaPredmet(predmet.getId());

            if (materijali == null || materijali.isEmpty()) {
                materijaliPredmeta.clear();
                tablicaMaterijala.setPlaceholder(labela(prijevod.getPrijevod("nema_materijala")).build());
                return;
            }

            materijaliPredmeta.setAll(materijali);

        } catch (Exception e) {
            System.err.println("Greška pri učitavanju materijala: " + e.getMessage());
            materijaliPredmeta.clear();
            tablicaMaterijala.setPlaceholder(labela(prijevod.getPrijevod("greska_ucitavanje_materijala")).build());
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
    public void priSakrivanju() {
        poruke.cleanup();
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
        String kljuc = jeUredjivanje() ? "azuriraj_materijal_gumb" : "dodaj_materijal_gumb";
        spremiGumb.setText(prijevod.getPrijevod(kljuc));
        urediGumb.setText(prijevod.getPrijevod("uredi_materijal_gumb"));
        izbrisiGumb.setText(prijevod.getPrijevod("izbrisi_materijal_gumb"));
    }

    private void osvjeziComboBox() {
        TipMaterijalaXML odabrani = tipComboBox.getValue();
        postaviCellFactoryZaComboBox();
        tipComboBox.setValue(odabrani);
    }

    private void osvjeziPlaceholdere() {
        tablicaPredmeta.setPlaceholder(labela(prijevod.getPrijevod("nema_predmeta")).build());

        if (tablicaPredmeta.getSelectionModel().getSelectedItem() == null) {
            tablicaMaterijala.setPlaceholder(labela(prijevod.getPrijevod("odaberite_predmet")).build());
        } else {
            tablicaMaterijala.setPlaceholder(labela(prijevod.getPrijevod("nema_materijala")).build());
        }
    }
}
