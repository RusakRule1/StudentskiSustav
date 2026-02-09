package projekt.pogled;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import projekt.model.Zapis;
import projekt.upravitelj.UpraviteljZapisima;
import projekt.util.Stilovi;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class PregledZapisaPogled extends OsnovniPogled {

    private final TableView<Zapis> tablicaZapisa = new TableView<>();
    private final ObservableList<Zapis> podaciZapisa = FXCollections.observableArrayList();

    public PregledZapisaPogled() {
        super();
    }

    @Override
    protected VBox kreirajSadrzaj() {
        VBox sadrzajBox = new VBox(20);
        sadrzajBox.setPadding(new Insets(20));
        sadrzajBox.getStyleClass().add(Stilovi.POZADINA_SVIJETLA);

        postaviTablicu();
        VBox.setVgrow(tablicaZapisa, Priority.ALWAYS);
        tablicaZapisa.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        tablicaZapisa.setPrefHeight(500);

        sadrzajBox.getChildren().addAll(tablicaZapisa);

        ucitajLogove();
        return sadrzajBox;
    }

    private void postaviTablicu() {
        DateTimeFormatter formater = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm:ss");

        TableColumn<Zapis, LocalDateTime> vrijemeKolona = new TableColumn<>();
        vrijemeKolona.setCellValueFactory(new PropertyValueFactory<>("vrijeme"));
        vrijemeKolona.setPrefWidth(150);
        vrijemeKolona.setCellFactory(col -> new TableCell<>() {
            @Override
            protected void updateItem(LocalDateTime item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? null : item.format(formater));
            }
        });

        TableColumn<Zapis, String> korisnikKolona = new TableColumn<>();
        korisnikKolona.setCellValueFactory(new PropertyValueFactory<>("korisnik"));
        korisnikKolona.setPrefWidth(120);

        TableColumn<Zapis, String> akcijaKolona = new TableColumn<>();
        akcijaKolona.setCellValueFactory(new PropertyValueFactory<>("akcija"));
        akcijaKolona.setPrefWidth(120);

        TableColumn<Zapis, String> detaljiKolona = new TableColumn<>();
        detaljiKolona.setCellValueFactory(new PropertyValueFactory<>("detalji"));

        tablicaZapisa.getColumns().addAll(vrijemeKolona, korisnikKolona, akcijaKolona, detaljiKolona);
        tablicaZapisa.setItems(podaciZapisa);
    }

    private void ucitajLogove() {
        podaciZapisa.clear();
        List<Zapis> zapisi = UpraviteljZapisima.getInstance().ucitajSveZapise();
        podaciZapisa.addAll(zapisi);
    }

    @Override
    protected void osvjeziPogledTekstove() {
        tablicaZapisa.getColumns().get(0).setText(prijevod.getPrijevod("zapisi_vrijeme"));
        tablicaZapisa.getColumns().get(1).setText(prijevod.getPrijevod("zapisi_korisnika"));
        tablicaZapisa.getColumns().get(2).setText(prijevod.getPrijevod("zapisi_akcija"));
        tablicaZapisa.getColumns().get(3).setText(prijevod.getPrijevod("zapisi_detalji"));
    }
}