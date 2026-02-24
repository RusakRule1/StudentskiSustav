package projekt.pogled;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import projekt.model.Zapis;
import projekt.upravitelj.UpraviteljZapisima;
import projekt.util.Stilovi;
import projekt.util.UITvornica;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import static projekt.util.UITvornica.kolona;
import static projekt.util.UITvornica.vbox;

public class PregledZapisaPogled extends OsnovniPogled {

    private static final DateTimeFormatter FORMATER_DATUMA =
            DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm:ss");

    private final ObservableList<Zapis> podaciZapisa = FXCollections.observableArrayList();

    private final TableView<Zapis> tablicaZapisa = UITvornica.<Zapis>tableView()
            .stavke(podaciZapisa)
            .constrained()
            .build();

    public PregledZapisaPogled() {
        super();
        postaviTablicu();
    }

    @Override
    protected VBox kreirajSadrzaj() {
        VBox sadrzaj = vbox(tablicaZapisa)
                .stil(Stilovi.RAZMAK_SREDNJI, Stilovi.PADDING_SREDNJI)
                .build();

        VBox.setVgrow(tablicaZapisa, Priority.ALWAYS);

        ucitajLogove();
        return sadrzaj;
    }

    private void postaviTablicu() {
        TableColumn<Zapis, LocalDateTime> vrijemeKolona = kolona("vrijeme");
        vrijemeKolona.setPrefWidth(80);
        vrijemeKolona.setCellFactory(col -> new TableCell<>() {
            @Override
            protected void updateItem(LocalDateTime item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? null : item.format(FORMATER_DATUMA));
            }
        });

        TableColumn<Zapis, String> korisnikKolona = kolona("korisnik");
        korisnikKolona.setPrefWidth(150);

        TableColumn<Zapis, String> akcijaKolona = kolona("akcija");
        akcijaKolona.setPrefWidth(120);

        TableColumn<Zapis, String> detaljiKolona = kolona("detalji");

        tablicaZapisa.getColumns().addAll(vrijemeKolona, korisnikKolona, akcijaKolona, detaljiKolona);
    }

    private void ucitajLogove() {
        podaciZapisa.clear();
        podaciZapisa.addAll(UpraviteljZapisima.getInstanca().ucitajSveZapise());
    }

    @Override
    protected void osvjeziPogledTekstove() {
        tablicaZapisa.getColumns().get(0).setText(prijevod.getPrijevod("zapisi_vrijeme"));
        tablicaZapisa.getColumns().get(1).setText(prijevod.getPrijevod("zapisi_korisnika"));
        tablicaZapisa.getColumns().get(2).setText(prijevod.getPrijevod("zapisi_akcija"));
        tablicaZapisa.getColumns().get(3).setText(prijevod.getPrijevod("zapisi_detalji"));
    }
}
