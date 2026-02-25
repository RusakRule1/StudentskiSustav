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

import static projekt.util.UITvornica.vbox;

public class PregledZapisaPogled extends OsnovniPogled {

    private static final DateTimeFormatter FORMATER_DATUMA =
            DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm:ss");

    private final ObservableList<Zapis> podaciZapisa = FXCollections.observableArrayList();

    private final TableColumn<Zapis, LocalDateTime> vrijemeKolona = UITvornica.<Zapis, LocalDateTime>kolona("vrijeme")
            .prefSirina(160)
            .maxSirina(160)
            .tvornicaCelija(col -> new TableCell<>() {
                protected void updateItem(LocalDateTime item, boolean empty) {
                    super.updateItem(item, empty);
                    setText(empty || item == null ? null : item.format(FORMATER_DATUMA));
                }
            })
            .build();

    private final TableColumn<Zapis, String> korisnikKolona = UITvornica.<Zapis, String>kolona("korisnik")
            .prefSirina(200)
            .maxSirina(200)
            .build();

    private final TableColumn<Zapis, String> akcijaKolona = UITvornica.<Zapis, String>kolona("akcija")
            .prefSirina(160)
            .maxSirina(160)
            .build();

    private final TableColumn<Zapis, String> detaljiKolona = UITvornica.<Zapis, String>kolona("detalji")
            .build();

    private final TableView<Zapis> tablicaZapisa = UITvornica.<Zapis>tableView()
            .stavke(podaciZapisa)
            .constrained()
            .kolone(vrijemeKolona, korisnikKolona, akcijaKolona, detaljiKolona)
            .build();

    public PregledZapisaPogled() {
        super();
    }

    @Override
    protected VBox kreirajSadrzaj() {
        VBox sadrzaj = vbox(tablicaZapisa)
                .stil(Stilovi.RAZMAK_SREDNJI, Stilovi.PADDING_SREDNJI)
                .grow(Priority.ALWAYS)
                .build();

        VBox.setVgrow(tablicaZapisa, Priority.ALWAYS);

        ucitajLogove();
        return sadrzaj;
    }

    private void ucitajLogove() {
        podaciZapisa.setAll(UpraviteljZapisima.getInstanca().ucitajSveZapise());
    }

    @Override
    protected void osvjeziPogledTekstove() {
        vrijemeKolona.setText(prijevod.getPrijevod("zapisi_vrijeme"));
        korisnikKolona.setText(prijevod.getPrijevod("zapisi_korisnika"));
        akcijaKolona.setText(prijevod.getPrijevod("zapisi_akcija"));
        detaljiKolona.setText(prijevod.getPrijevod("zapisi_detalji"));
    }
}
