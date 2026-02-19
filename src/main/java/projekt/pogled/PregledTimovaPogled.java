package projekt.pogled;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import projekt.model.TimJson;
import projekt.servis.TimJsonServis;
import projekt.upravitelj.UpraviteljPogleda;
import projekt.util.PorukaHelper;
import projekt.util.Stilovi;

import java.util.List;

public class PregledTimovaPogled extends OsnovniPogled {

    private static final int RAZMAK_SADRZAJ = 20;
    private static final int RAZMAK_KONTROLE = 15;
    private static final int PADDING_SADRZAJ = 20;
    private static final int VISINA_TABLICE = 300;

    private static final double SIRINA_NAZIV_KOLONE = 0.2;
    private static final double SIRINA_BROJ_KOLONE = 0.2;
    private static final double SIRINA_CLANOVI_KOLONE = 0.6;

    private final TimJsonServis timServis;
    private final PorukaHelper poruke = PorukaHelper.kreiraj(prijevod);

    private final Label naslov = new Label();
    private final TableView<TimJson> tablicaTimova = new TableView<>();
    private final ObservableList<TimJson> podaciTimova = FXCollections.observableArrayList();

    private final TableColumn<TimJson, String> nazivKolona = new TableColumn<>();
    private final TableColumn<TimJson, String> brojKolona = new TableColumn<>();
    private final TableColumn<TimJson, String> clanoviKolona = new TableColumn<>();

    private final Button noviTimGumb = new Button();
    private final Button urediTimGumb = new Button();
    private final Button obrisiTimGumb = new Button();
    private final Button osvjeziGumb = new Button();

    public PregledTimovaPogled() {
        super();
        this.timServis = new TimJsonServis();
    }

    @Override
    protected VBox kreirajSadrzaj() {
        VBox sadrzajBox = new VBox(RAZMAK_SADRZAJ);
        sadrzajBox.setPadding(new Insets(PADDING_SADRZAJ));
        sadrzajBox.getStyleClass().add(Stilovi.POZADINA_SVIJETLA);

        konfigurirajNaslov();
        konfigurirajTablicu();
        HBox kontroleBox = kreirajKontrole();

        sadrzajBox.getChildren().addAll(
                naslov,
                tablicaTimova,
                poruke.kontejner,
                kontroleBox
        );

        ucitajTimove();
        postaviListenere();

        return sadrzajBox;
    }

    private void konfigurirajNaslov() {
        naslov.getStyleClass().add(Stilovi.NASLOV_TEKST);
    }

    private void konfigurirajTablicu() {
        tablicaTimova.getColumns().clear();

        konfigurirajKolone();
        postaviSirineKolona();

        tablicaTimova.setItems(podaciTimova);
        tablicaTimova.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        VBox.setVgrow(tablicaTimova, Priority.ALWAYS);
        tablicaTimova.setPrefHeight(VISINA_TABLICE);
    }

    private void konfigurirajKolone() {
        nazivKolona.setCellValueFactory(new PropertyValueFactory<>("naziv"));

        brojKolona.setCellValueFactory(cellData ->
                new SimpleStringProperty(
                        String.valueOf(cellData.getValue().getBrojClanova())
                )
        );

        clanoviKolona.setCellValueFactory(cellData ->
                new SimpleStringProperty(cellData.getValue().getClanoviFormatted())
        );

        tablicaTimova.getColumns().addAll(nazivKolona, brojKolona, clanoviKolona);
    }

    private void postaviSirineKolona() {
        nazivKolona.prefWidthProperty().bind(
                tablicaTimova.widthProperty().multiply(SIRINA_NAZIV_KOLONE)
        );
        brojKolona.prefWidthProperty().bind(
                tablicaTimova.widthProperty().multiply(SIRINA_BROJ_KOLONE)
        );
        clanoviKolona.prefWidthProperty().bind(
                tablicaTimova.widthProperty().multiply(SIRINA_CLANOVI_KOLONE)
        );
    }

    private HBox kreirajKontrole() {
        HBox kontroleBox = new HBox(RAZMAK_KONTROLE);
        kontroleBox.setAlignment(Pos.CENTER);

        konfigurirajNoviTimGumb();
        konfigurirajUrediTimGumb();
        konfigurirajObrisiTimGumb();
        konfigurirajOsvjeziGumb();

        kontroleBox.getChildren().addAll(
                noviTimGumb,
                urediTimGumb,
                obrisiTimGumb,
                osvjeziGumb
        );

        return kontroleBox;
    }

    private void konfigurirajNoviTimGumb() {
        noviTimGumb.getStyleClass().add(Stilovi.GUMB_PRIMARAN);
        noviTimGumb.setOnAction(e -> otvoriKreiranjeTima());
    }

    private void konfigurirajUrediTimGumb() {
        urediTimGumb.getStyleClass().add(Stilovi.GUMB_SEKUNDARAN);
        urediTimGumb.setOnAction(e -> otvoriUredivanjeTima());
        urediTimGumb.setDisable(true);
    }

    private void konfigurirajObrisiTimGumb() {
        obrisiTimGumb.getStyleClass().add(Stilovi.GUMB_OPASAN);
        obrisiTimGumb.setOnAction(e -> obrisiTim());
        obrisiTimGumb.setDisable(true);
    }

    private void konfigurirajOsvjeziGumb() {
        osvjeziGumb.getStyleClass().add(Stilovi.GUMB_SEKUNDARAN);
        osvjeziGumb.setOnAction(e -> ucitajTimove());
    }

    private void postaviListenere() {
        postaviListenerZaSelekciju();
    }

    private void postaviListenerZaSelekciju() {
        tablicaTimova.getSelectionModel().selectedItemProperty().addListener(
                (observable, stariTim, noviTim) -> azurirajOsjetljivostGumba(noviTim)
        );
    }

    private void azurirajOsjetljivostGumba(TimJson odabraniTim) {
        boolean imaSelekciju = odabraniTim != null;
        urediTimGumb.setDisable(!imaSelekciju);
        obrisiTimGumb.setDisable(!imaSelekciju);
    }

    private void ucitajTimove() {
        try {
            podaciTimova.clear();

            List<TimJson> timovi = timServis.dohvatiSveTimove();
            podaciTimova.addAll(timovi);

            azurirajPlaceholder();

        } catch (Exception e) {
            System.err.println("Greška pri učitavanju timova: " + e.getMessage());
            poruke.prikaziGreskuSTimerom("greska_ucitavanje_timova");
        }
    }

    private void azurirajPlaceholder() {
        if (podaciTimova.isEmpty()) {
            tablicaTimova.setPlaceholder(
                    new Label(prijevod.getPrijevod("nema_timova"))
            );
        }
    }

    private void otvoriKreiranjeTima() {
        UpraviteljPogleda.prikazi(new KreiranjeUredivanjeTimaPogled(null));
    }

    private void otvoriUredivanjeTima() {
        TimJson odabraniTim = tablicaTimova.getSelectionModel().getSelectedItem();
        UpraviteljPogleda.prikazi(new KreiranjeUredivanjeTimaPogled(odabraniTim));
    }

    private void obrisiTim() {
        TimJson odabraniTim = tablicaTimova.getSelectionModel().getSelectedItem();
        izvrsiBrisanjeTima(odabraniTim);
    }

    private void izvrsiBrisanjeTima(TimJson tim) {
        try {
            boolean uspjeh = timServis.obrisiTim(tim.getId());

            if (uspjeh) {
                ucitajTimove();
                poruke.prikaziUspjehSTimerom("tim_obrisan");
            } else {
                poruke.prikaziGreskuSTimerom("greska_tim_neobrisan");
            }

        } catch (Exception e) {
            System.err.println("Greška pri brisanju tima: " + e.getMessage());
            poruke.prikaziGreskuSTimerom("greska_tim_neobrisan");
        }
    }

    @Override
    protected void osvjeziPogledTekstove() {
        osvjeziNaslov();
        osvjeziTablicu();
        osvjeziGumbe();
        poruke.osvjeziPoruku();
    }

    private void osvjeziNaslov() {
        naslov.setText(prijevod.getPrijevod("tim_naslov"));
    }

    private void osvjeziTablicu() {
        nazivKolona.setText(prijevod.getPrijevod("tim_naziv"));
        brojKolona.setText(prijevod.getPrijevod("tim_broj_clanova"));
        clanoviKolona.setText(prijevod.getPrijevod("tim_clanovi"));

        azurirajPlaceholder();
    }

    private void osvjeziGumbe() {
        noviTimGumb.setText(prijevod.getPrijevod("novi_tim_gumb"));
        urediTimGumb.setText(prijevod.getPrijevod("uredi_tim_gumb"));
        obrisiTimGumb.setText(prijevod.getPrijevod("obrisi_tim_gumb"));
        osvjeziGumb.setText(prijevod.getPrijevod("osvjezi_gumb"));
    }
}