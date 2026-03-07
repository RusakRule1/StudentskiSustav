package projekt.pogled;

import javafx.collections.FXCollections;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import projekt.adapter.JNIAdapter;
import projekt.model.Ocjena;
import projekt.model.PredajaZadatka;
import projekt.model.Profesor;
import projekt.servis.PredajaServis;
import projekt.upravitelj.Sesija;
import projekt.upravitelj.UpraviteljPogleda;
import projekt.util.PorukaHelper;
import projekt.util.Stilovi;
import projekt.util.UITvornica;

import java.io.FileOutputStream;
import java.time.format.DateTimeFormatter;

import static projekt.util.UITvornica.*;

public class OcjenjivanjeRjesenjaPogled extends OsnovniPogled {

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("dd.MM.yyyy. HH:mm");

    private final JNIAdapter jniAdapter = new JNIAdapter();
    private final PredajaServis predajaServis;
    private final PorukaHelper poruke;
    private final PredajaZadatka predaja;

    private final Label naslov = labela().stil(Stilovi.PODNASLOV).build();

    private final Label studentLabela = labela().stil(Stilovi.LABELA_INFORMACIJA).build();
    private final Label zadatakLabela = labela().stil(Stilovi.LABELA_INFORMACIJA).build();
    private final Label datotekeLabela = labela().stil(Stilovi.LABELA_INFORMACIJA).build();
    private final Label datumLabela = labela().stil(Stilovi.LABELA_INFORMACIJA).build();
    private final Label ocjenaInfoLabela = labela().stil(Stilovi.LABELA_INFORMACIJA).build();

    private final Label ocjenaNaslovLabela = labela().stil(Stilovi.LABELA_PODEBLJANA).build();
    private final ComboBox<Integer> ocjenaCombo = UITvornica.<Integer>comboBox()
            .stavke(FXCollections.observableArrayList(1, 2, 3, 4, 5))
            .stil(Stilovi.POLJE_SIRINA_COMBO)
            .build();

    private final Label komentarNaslovLabela = labela().stil(Stilovi.LABELA_PODEBLJANA).build();
    private final TextArea komentarPolje = textArea()
            .wrapText(true)
            .brojRedaka(4)
            .build();

    private Button preuzmiDatotekuGumb;
    private Button spremiGumb;
    private Button odustaniGumb;

    public OcjenjivanjeRjesenjaPogled(PredajaZadatka predaja) {
        super();
        this.predaja = predaja;
        this.predajaServis = new PredajaServis();
        this.poruke = PorukaHelper.kreiraj(prijevod);
        konfigurirajGumbe();
    }

    @Override
    protected VBox kreirajSadrzaj() {
        VBox infoBox = vbox(studentLabela, zadatakLabela, datotekeLabela, datumLabela, ocjenaInfoLabela)
                .stil(Stilovi.RAZMAK_MALI)
                .build();

        HBox ocjenaBox = hbox(ocjenaNaslovLabela, ocjenaCombo)
                .pozicija(Pos.CENTER_LEFT)
                .stil(Stilovi.RAZMAK_MALI)
                .build();

        VBox komentarBox = vbox(komentarNaslovLabela, komentarPolje)
                .stil(Stilovi.RAZMAK_MALI)
                .build();

        HBox gumbBox = hbox(preuzmiDatotekuGumb, spremiGumb, odustaniGumb)
                .pozicija(Pos.CENTER)
                .stil(Stilovi.RAZMAK_KONTROLE)
                .build();

        VBox sadrzaj = vbox(naslov, infoBox, ocjenaBox, komentarBox, poruke.getKontejner(), gumbBox)
                .stil(Stilovi.GLAVNI_VBOX)
                .build();

        popuniInfoPredaje();

        return sadrzaj;
    }

    private void konfigurirajGumbe() {
        preuzmiDatotekuGumb = gumb(Stilovi.GUMB_ZELENI, this::preuzmiDatoteku).build();
        spremiGumb = gumb(Stilovi.GUMB_PLAVI, this::spremiOcjenu).build();
        odustaniGumb = gumb(Stilovi.GUMB_CRVENI, UpraviteljPogleda::idiNatrag).build();
    }

    private void popuniInfoPredaje() {
        var student = predaja.getStudent();
        studentLabela.setText(student.vratiPunoIme() + " (" + student.getJmbag() + ")");
        zadatakLabela.setText(predaja.getZadatak().getNaziv()
                + " — " + predaja.getZadatak().getPredmet().getNaziv());
        datotekeLabela.setText(predaja.vratiNazivDatoteke()
                + "  (" + formatirajVelicinuDatoteke(predaja.getVelicinaDatoteke()) + ")");
        datumLabela.setText(predaja.getDatumPredaje() != null
                ? predaja.getDatumPredaje().format(FORMATTER)
                : "");
    }

    private void popuniPostojecuOcjenu() {
        Ocjena ocjena = predaja.getOcjena();
        if (ocjena != null) {
            ocjenaCombo.setValue(ocjena.getVrijednost());
            komentarPolje.setText(ocjena.getKomentar() != null ? ocjena.getKomentar() : "");
            String slovima = prijevod.getPrijevod(ocjena.vratiOcjenuSlovima());
            String prolaznost = ocjena.jeLiProlazna()
                    ? prijevod.getPrijevod("prolazna")
                    : prijevod.getPrijevod("neprolazna");
            ocjenaInfoLabela.setText(
                    prijevod.getPrijevod("trenutna_ocjena_labela") + slovima + " — " + prolaznost
            );
        }
    }

    private void preuzmiDatoteku() {
        String putanjaSpremanja = jniAdapter.otvoriDijalogSpremanja(predaja.vratiNazivDatoteke());
        if (putanjaSpremanja == null || putanjaSpremanja.isEmpty()) return;

        try (FileOutputStream izlaz = new FileOutputStream(putanjaSpremanja)) {
            izlaz.write(predaja.getPredanaDatoteka());
            poruke.prikaziUspjehSTimerom("datoteka_preuzeta");
        } catch (Exception e) {
            System.err.println("Greška pri preuzimanju datoteke: " + e.getMessage());
            poruke.prikaziGreskuSTimerom("greska_preuzimanje_datoteke");
        }
    }

    private void spremiOcjenu() {
        Integer odabranaOcjena = ocjenaCombo.getValue();
        if (odabranaOcjena == null) {
            poruke.prikaziGreskuSTimerom("greska_ocjena_nije_odabrana");
            return;
        }

        try {
            Profesor profesor = (Profesor) Sesija.getInstanca().getPrijavljeniKorisnik();
            String komentar = komentarPolje.getText().trim();
            predajaServis.ocijeniPredaju(predaja, profesor, odabranaOcjena, komentar);
            poruke.prikaziUspjehSTimerom("ocjena_spremljena");
            spremiGumb.setDisable(true);
        } catch (IllegalArgumentException e) {
            poruke.prikaziGreskuSTimerom("greska_nevazeca_ocjena");
        } catch (Exception e) {
            System.err.println("Greška pri ocjenjivanju: " + e.getMessage());
            poruke.prikaziGreskuSTimerom("greska_ocjenjivanje");
        }
    }

    private static String formatirajVelicinuDatoteke(Long velicina) {
        if (velicina == null) return "";
        if (velicina < 1024) return velicina + " B";
        if (velicina < 1024 * 1024) return String.format("%.1f KB", velicina / 1024.0);
        return String.format("%.1f MB", velicina / (1024.0 * 1024.0));
    }

    @Override
    public void priSakrivanju() {
        poruke.cleanup();
    }

    @Override
    protected void osvjeziPogledTekstove() {
        naslov.setText(prijevod.getPrijevod("ocjenjivanje_naslov"));
        ocjenaNaslovLabela.setText(prijevod.getPrijevod("ocjena_labela"));
        komentarNaslovLabela.setText(prijevod.getPrijevod("komentar_labela"));
        preuzmiDatotekuGumb.setText(prijevod.getPrijevod("preuzmi_datoteku_gumb"));
        spremiGumb.setText(prijevod.getPrijevod("spremi_ocjenu_gumb"));
        odustaniGumb.setText(prijevod.getPrijevod("odustani_gumb"));
        poruke.osvjeziPoruku();
        popuniPostojecuOcjenu();
    }
}
