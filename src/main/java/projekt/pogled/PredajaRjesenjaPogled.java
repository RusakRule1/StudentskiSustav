package projekt.pogled;

import javafx.concurrent.Task;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.util.StringConverter;
import projekt.adapter.JNIAdapter;
import projekt.model.*;
import projekt.servis.PredajaServis;
import projekt.upravitelj.Sesija;
import projekt.upravitelj.UpraviteljPogleda;
import projekt.util.PorukaHelper;
import projekt.util.Stilovi;
import projekt.util.UITvornica;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import static projekt.util.UITvornica.*;

public class PredajaRjesenjaPogled extends OsnovniPogled {

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("dd.MM.yyyy. HH:mm");
    private static final String URL_PREDLOSKA = "http://speedtest.tele2.net/10MB.zip";

    private final PredajaServis predajaServis;
    private final JNIAdapter jniAdapter;
    private final PorukaHelper poruke;
    private final Zadatak zadatak;
    private final StatusPredaje statusPredaje;
    private final PredajaZadatka predaja; // samo ako je OCJENJENO

    private String odabranaPutanja = null;
    private Task<Void> trenutniZadatakPreuzimanja = null;

    // Info
    private final Label naslov = labela().stil(Stilovi.PODNASLOV).build();
    private final Label nazivZadatkaLabela = labela().stil(Stilovi.LABELA_PODEBLJANA).build();
    private final Label predmetLabela = labela().stil(Stilovi.LABELA_INFORMACIJA).build();
    private final Label rokPredajeLabela = labela().stil(Stilovi.LABELA_INFORMACIJA).build();
    private final Label opisNaslovLabela = labela().stil(Stilovi.LABELA_PODEBLJANA).build();
    private final TextArea opisPolje = textArea().wrapText(true).brojRedaka(4).onemogucen(true).build();

    // Predložak
    private final Label predlozakNaslovLabela = labela().stil(Stilovi.LABELA_PODEBLJANA).build();
    private final Label brzinaLabela = labela().build();
    private final ComboBox<Long> brzinaCombo = UITvornica.<Long>comboBox()
            .stavke(0L, 1024L * 1000, 1024L * 250, 1024L * 10)
            .stil(Stilovi.POLJE_SIRINA_COMBO).build();
    private final ProgressBar trakaPreuzimanja = new ProgressBar(0);
    private final Label postotakLabela = labela().stil(Stilovi.LABELA_INFORMACIJA).build();
    private Button preuzmiPredlozakGumb;
    private Button prekiniPreuzimanjeGumb;

    // Predaja (samo kad je otvoreno)
    private final Label odabranaDatotekaLabela = labela().stil(Stilovi.LABELA_PRIGUSENA).build();
    private Button odaberiDatotekuGumb;
    private Button predajGumb;

    // Statusna poruka (predano/isteklo)
    private final Label statusPorukaLabela = labela().stil(Stilovi.LABELA_INFORMACIJA).build();

    // Ocjena (samo kad je OCJENJENO)
    private final Label ocjenaNaslovLabela = labela().stil(Stilovi.LABELA_PODEBLJANA).build();
    private final Label ocjenaVrijednostLabela = labela().stil(Stilovi.PODNASLOV).build();
    private final Label komentarNaslovLabela = labela().stil(Stilovi.LABELA_PODEBLJANA).build();
    private final TextArea komentarPolje = textArea().wrapText(true).brojRedaka(4).onemogucen(true).build();
    private final Label ocjenioLabela = labela().stil(Stilovi.LABELA_PRIGUSENA).build();

    private Button odustaniGumb;

    public PredajaRjesenjaPogled(Zadatak zadatak, StatusPredaje statusPredaje) {
        super();
        this.zadatak = zadatak;
        this.statusPredaje = statusPredaje;
        this.predajaServis = new PredajaServis();
        this.jniAdapter = new JNIAdapter();
        this.poruke = PorukaHelper.kreiraj(prijevod);

        if (statusPredaje == StatusPredaje.OCJENJENO) {
            Student student = (Student) Sesija.getInstanca().getPrijavljeniKorisnik();
            this.predaja = predajaServis
                    .vratiPredajuStudentaZaZadatak(zadatak.getId(), student.getId())
                    .orElse(null);
        } else {
            this.predaja = null;
        }

        konfigurirajGumbe();
        konfigurirajBrzinaCombo();
        konfigurirajTrakuPreuzimanja();
    }

    @Override
    protected VBox kreirajSadrzaj() {
        boolean rokIstekao = LocalDateTime.now().isAfter(zadatak.getRokPredaje());
        boolean mozePredat = statusPredaje == null && !rokIstekao;
        boolean pokaziStatusPoruku = statusPredaje == StatusPredaje.PREDANO || (statusPredaje == null && rokIstekao);
        boolean pokaziOcjenu = statusPredaje == StatusPredaje.OCJENJENO;

        HBox brzinaSelekcija = hbox(brzinaLabela, brzinaCombo)
                .pozicija(Pos.CENTER_LEFT).stil(Stilovi.RAZMAK_MALI).build();
        HBox predlozakGumbi = hbox(preuzmiPredlozakGumb, prekiniPreuzimanjeGumb)
                .pozicija(Pos.CENTER_LEFT).stil(Stilovi.RAZMAK_MALI).build();
        HBox napredakBox = hbox(trakaPreuzimanja, postotakLabela)
                .pozicija(Pos.CENTER_LEFT).stil(Stilovi.RAZMAK_MALI).build();
        HBox.setHgrow(trakaPreuzimanja, Priority.ALWAYS);
        VBox predlozakBox = vbox(predlozakNaslovLabela, brzinaSelekcija, predlozakGumbi, napredakBox)
                .stil(Stilovi.RAZMAK_MALI).build();

        VBox predajaBox = vbox(odaberiDatotekuGumb, odabranaDatotekaLabela, predajGumb)
                .stil(Stilovi.RAZMAK_MALI).build();
        postaviVidljivost(predajaBox, mozePredat);

        postaviVidljivost(statusPorukaLabela, pokaziStatusPoruku);

        VBox ocjenaBox = vbox(ocjenaNaslovLabela, ocjenaVrijednostLabela,
                komentarNaslovLabela, komentarPolje, ocjenioLabela)
                .stil(Stilovi.RAZMAK_MALI).build();
        postaviVidljivost(ocjenaBox, pokaziOcjenu);

        HBox gumbBox = hbox(odustaniGumb).pozicija(Pos.CENTER).stil(Stilovi.RAZMAK_KONTROLE).build();

        VBox infoBox = vbox(nazivZadatkaLabela, predmetLabela, rokPredajeLabela, opisNaslovLabela, opisPolje)
                .stil(Stilovi.RAZMAK_MALI).build();

        VBox sadrzaj = vbox(naslov, infoBox, predlozakBox, predajaBox,
                statusPorukaLabela, ocjenaBox, poruke.getKontejner(), gumbBox)
                .stil(Stilovi.GLAVNI_VBOX).build();

        popuniInfoZadatka();
        if (pokaziOcjenu && predaja != null) popuniOcjenu();

        return sadrzaj;
    }

    private void postaviVidljivost(Node cvor, boolean vidljivo) {
        cvor.setVisible(vidljivo);
        cvor.setManaged(vidljivo);
    }

    private void konfigurirajGumbe() {
        preuzmiPredlozakGumb = gumb(Stilovi.GUMB_ZELENI, this::preuzmiPredlozak).build();
        prekiniPreuzimanjeGumb = gumb(Stilovi.GUMB_CRVENI, this::prekiniPreuzimanje).onemogucen(true).build();
        odaberiDatotekuGumb = gumb(Stilovi.GUMB_ZELENI, this::odaberiDatoteku).build();
        predajGumb = gumb(Stilovi.GUMB_PLAVI, this::predajRjesenje).onemogucen(true).build();
        odustaniGumb = gumb(Stilovi.GUMB_CRVENI, UpraviteljPogleda::idiNatrag).build();
    }

    private void konfigurirajBrzinaCombo() {
        brzinaCombo.setConverter(kreirajBrzinaConverter());
        brzinaCombo.getSelectionModel().selectFirst();
    }

    private StringConverter<Long> kreirajBrzinaConverter() {
        return new StringConverter<>() {
            @Override
            public String toString(Long brzina) {
                if (brzina == null || brzina == 0) return prijevod.getPrijevod("brzina_neogranicena");
                return (brzina / 1024) + " KB/s";
            }

            @Override
            public Long fromString(String s) {
                return null;
            }
        };
    }

    private void konfigurirajTrakuPreuzimanja() {
        trakaPreuzimanja.setMaxWidth(Double.MAX_VALUE);
        postotakLabela.setText("0%");
    }

    private void popuniInfoZadatka() {
        nazivZadatkaLabela.setText(zadatak.getNaziv());
        predmetLabela.setText(zadatak.getPredmet().getNaziv());
        rokPredajeLabela.setText(zadatak.getRokPredaje() != null ? zadatak.getRokPredaje().format(FORMATTER) : "");
        opisPolje.setText(zadatak.getOpis());
    }

    private void popuniOcjenu() {
        Ocjena ocjena = predaja.getOcjena();
        ocjenaVrijednostLabela.setText(String.valueOf(ocjena.getVrijednost()));
        komentarPolje.setText(ocjena.getKomentar());
        Profesor profesor = ocjena.getProfesor();
        String datum = ocjena.getDatumOcjenjivanja() != null ? ocjena.getDatumOcjenjivanja().format(FORMATTER) : "";
        ocjenioLabela.setText(profesor.vratiPunoIme() + " · " + datum);
    }

    private void preuzmiPredlozak() {
        String putanjaSpremanja = jniAdapter.otvoriDijalogSpremanja("predlozak_zadatka.zip");
        if (putanjaSpremanja == null || putanjaSpremanja.isEmpty()) return;

        long odabranaBrzina = brzinaCombo.getValue() != null ? brzinaCombo.getValue() : 0L;
        Task<Void> zadatakPreuzimanja = kreirajZadatakPreuzimanja(putanjaSpremanja, odabranaBrzina);

        trakaPreuzimanja.progressProperty().bind(zadatakPreuzimanja.progressProperty());
        zadatakPreuzimanja.progressProperty().addListener((obs, stari, novi) -> {
            double v = novi.doubleValue();
            postotakLabela.setText(v < 0 ? "..." : (int) (v * 100) + "%");
        });

        zadatakPreuzimanja.setOnSucceeded(e -> {
            poruke.prikaziUspjehSTimerom("predlozak_preuzet");
            zavrsiPreuzimanje(1.0, "100%");
        });
        zadatakPreuzimanja.setOnCancelled(e -> zavrsiPreuzimanje(0.0, "0%"));
        zadatakPreuzimanja.setOnFailed(e -> {
            System.err.println("Greška: " + zadatakPreuzimanja.getException().getMessage());
            poruke.prikaziGreskuSTimerom("greska_preuzimanje_predloska");
            zavrsiPreuzimanje(0.0, "0%");
        });

        preuzmiPredlozakGumb.setDisable(true);
        brzinaCombo.setDisable(true);
        prekiniPreuzimanjeGumb.setDisable(false);
        trenutniZadatakPreuzimanja = zadatakPreuzimanja;

        Thread nit = new Thread(zadatakPreuzimanja);
        nit.setDaemon(true);
        nit.start();
    }

    private void zavrsiPreuzimanje(double progres, String tekst) {
        trakaPreuzimanja.progressProperty().unbind();
        trakaPreuzimanja.setProgress(progres);
        postotakLabela.setText(tekst);
        preuzmiPredlozakGumb.setDisable(false);
        brzinaCombo.setDisable(false);
        prekiniPreuzimanjeGumb.setDisable(true);
    }

    private void prekiniPreuzimanje() {
        if (trenutniZadatakPreuzimanja != null && trenutniZadatakPreuzimanja.isRunning()) {
            trenutniZadatakPreuzimanja.cancel();
        }
    }

    private Task<Void> kreirajZadatakPreuzimanja(String putanjaSpremanja, long brzinaBytesPoSekundi) {
        return new Task<>() {
            @Override
            protected Void call() throws Exception {
                HttpURLConnection veza = (HttpURLConnection) new URL(URL_PREDLOSKA).openConnection();
                veza.setConnectTimeout(10_000);
                veza.setReadTimeout(30_000);
                long ukupnoByteova = veza.getContentLengthLong();
                try (InputStream ulaz = veza.getInputStream();
                     FileOutputStream izlaz = new FileOutputStream(putanjaSpremanja)) {
                    byte[] buffer = new byte[8192];
                    long preuzeto = 0;
                    long vrijemePocetka = System.currentTimeMillis();
                    int procitano;
                    while ((procitano = ulaz.read(buffer)) != -1) {
                        if (isCancelled()) break;
                        izlaz.write(buffer, 0, procitano);
                        preuzeto += procitano;
                        if (ukupnoByteova > 0) updateProgress(preuzeto, ukupnoByteova);
                        if (brzinaBytesPoSekundi > 0) {
                            long spavajMs = (preuzeto * 1000L) / brzinaBytesPoSekundi - (System.currentTimeMillis() - vrijemePocetka);
                            if (spavajMs > 0) try {
                                Thread.sleep(spavajMs);
                            } catch (InterruptedException e) {
                                Thread.currentThread().interrupt();
                                break;
                            }
                        }
                    }
                } finally {
                    veza.disconnect();
                }
                return null;
            }
        };
    }

    private void odaberiDatoteku() {
        String putanja = jniAdapter.otvoriDijalogOdabira();
        if (putanja == null || putanja.isEmpty()) return;
        odabranaPutanja = putanja;
        try {
            String naziv = Paths.get(odabranaPutanja).getFileName().toString();
            long velicina = Files.size(Paths.get(odabranaPutanja));
            odabranaDatotekaLabela.setText(naziv + "  (" + formatVelicinuDatoteke(velicina) + ")");
        } catch (IOException e) {
            odabranaDatotekaLabela.setText(odabranaPutanja);
        }
        predajGumb.setDisable(false);
    }

    private void predajRjesenje() {
        if (odabranaPutanja == null || odabranaPutanja.isEmpty()) {
            poruke.prikaziGreskuSTimerom("greska_odabir_datoteke");
            return;
        }
        try {
            Student student = (Student) Sesija.getInstanca().getPrijavljeniKorisnik();
            predajaServis.predajRjesenje(zadatak, student, odabranaPutanja);
            poruke.prikaziUspjehSTimerom("rjesenje_predano");
            predajGumb.setDisable(true);
            odaberiDatotekuGumb.setDisable(true);
        } catch (IOException e) {
            poruke.prikaziGreskuSTimerom("greska_citanje_datoteke");
        } catch (Exception e) {
            poruke.prikaziGreskuSTimerom("greska_predaja_rjesenja");
        }
    }

    private static String formatVelicinuDatoteke(long velicina) {
        if (velicina < 1024) return velicina + " B";
        if (velicina < 1024 * 1024) return String.format("%.1f KB", velicina / 1024.0);
        return String.format("%.1f MB", velicina / (1024.0 * 1024.0));
    }

    @Override
    public void priSakrivanju() {
        if (trenutniZadatakPreuzimanja != null && trenutniZadatakPreuzimanja.isRunning())
            trenutniZadatakPreuzimanja.cancel();
        poruke.cleanup();
    }

    @Override
    protected void osvjeziPogledTekstove() {
        naslov.setText(prijevod.getPrijevod("predaja_naslov"));
        opisNaslovLabela.setText(prijevod.getPrijevod("zadatak_opis_labela"));
        predlozakNaslovLabela.setText(prijevod.getPrijevod("predlozak_naslov"));
        brzinaLabela.setText(prijevod.getPrijevod("brzina_preuzimanja_labela"));
        preuzmiPredlozakGumb.setText(prijevod.getPrijevod("preuzmi_predlozak_gumb"));
        prekiniPreuzimanjeGumb.setText(prijevod.getPrijevod("prekini_preuzimanje_gumb"));
        odaberiDatotekuGumb.setText(prijevod.getPrijevod("odaberi_datoteku_gumb"));
        predajGumb.setText(prijevod.getPrijevod("predaj_rjesenje_gumb"));
        odustaniGumb.setText(prijevod.getPrijevod("odustani_gumb"));
        ocjenaNaslovLabela.setText(prijevod.getPrijevod("ocjena_labela"));
        komentarNaslovLabela.setText(prijevod.getPrijevod("komentar_labela"));

        if (statusPredaje == StatusPredaje.PREDANO) {
            statusPorukaLabela.setText(prijevod.getPrijevod("rjesenje_ceka_ocjenu"));
        } else if (statusPredaje == null && LocalDateTime.now().isAfter(zadatak.getRokPredaje())) {
            statusPorukaLabela.setText(prijevod.getPrijevod("rok_je_istekao"));
        }

        if (odabranaPutanja == null) odabranaDatotekaLabela.setText(prijevod.getPrijevod("nema_odabrane_datoteke"));

        Long odabranaBrzina = brzinaCombo.getValue();
        brzinaCombo.setValue(null);
        brzinaCombo.setValue(odabranaBrzina);

        poruke.osvjeziPoruku();
    }
}
