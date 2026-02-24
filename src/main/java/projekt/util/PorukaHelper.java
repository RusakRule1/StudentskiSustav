package projekt.util;

import javafx.application.Platform;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import projekt.upravitelj.Prijevod;

import java.util.Timer;
import java.util.TimerTask;

import static projekt.util.UITvornica.hbox;
import static projekt.util.UITvornica.labela;

public class PorukaHelper {

    private static final int TRAJANJE_PORUKE_MS = 3000;

    public final Label labela;
    public final HBox kontejner;
    private final Prijevod prijevod;

    private Timer timerPoruke;
    private String trenutnaPoruka;

    private PorukaHelper(Label labela, HBox kontejner, Prijevod prijevod) {
        this.labela = labela;
        this.kontejner = kontejner;
        this.prijevod = prijevod;
    }

    public static PorukaHelper kreiraj(Prijevod prijevod) {
        Label labela = kreirajLabelu();
        HBox kontejner = kreirajKontejner(labela);
        return new PorukaHelper(labela, kontejner, prijevod);
    }

    private static Label kreirajLabelu() {
        return labela()
                .stil(Stilovi.PORUKA_GRESKA)
                .pozicija(Pos.CENTER)
                .wrapText(true)
                .vidljivo(false)
                .build();
    }

    private static HBox kreirajKontejner(Label labela) {
        return hbox(labela)
                .pozicija(Pos.CENTER)
                .maxSirina(Double.MAX_VALUE)
                .fillVisinu(true)
                .childGrow(labela, Priority.ALWAYS)
                .vidljivo(false)
                .build();
    }

    public void prikaziGresku(String kljucGreske) {
        prikaziPoruku(kljucGreske, false, false);
    }

    public void prikaziUspjeh(String kljucUspjeha) {
        prikaziPoruku(kljucUspjeha, true, false);
    }

    public void prikaziGreskuSTimerom(String kljucGreske) {
        prikaziPoruku(kljucGreske, false, true);
    }

    public void prikaziUspjehSTimerom(String kljucUspjeha) {
        prikaziPoruku(kljucUspjeha, true, true);
    }

    private void prikaziPoruku(String kljucPoruke, boolean uspjeh, boolean automatskiSakrij) {
        trenutnaPoruka = kljucPoruke;
        labela.setText(prijevod.getPrijevod(kljucPoruke));

        labela.getStyleClass().removeAll(Stilovi.PORUKA_GRESKA, Stilovi.PORUKA_USPJESNO);
        labela.getStyleClass().add(uspjeh ? Stilovi.PORUKA_USPJESNO : Stilovi.PORUKA_GRESKA);

        labela.setVisible(true);
        kontejner.setVisible(true);

        if (automatskiSakrij) {
            pokreniTimer();
        }
    }

    public void sakrijPoruku() {
        labela.setText("");
        labela.setVisible(false);
        kontejner.setVisible(false);
        trenutnaPoruka = null;
    }

    public void osvjeziPoruku() {
        if (trenutnaPoruka != null) {
            labela.setText(prijevod.getPrijevod(trenutnaPoruka));
        }
    }

    public void cleanup() {
        if (timerPoruke != null) {
            timerPoruke.cancel();
            timerPoruke = null;
        }
    }

    private void pokreniTimer() {
        if (timerPoruke != null) {
            timerPoruke.cancel();
        }
        timerPoruke = new Timer(true);
        timerPoruke.schedule(new TimerTask() {
            @Override
            public void run() {
                Platform.runLater(PorukaHelper.this::sakrijPoruku);
            }
        }, TRAJANJE_PORUKE_MS);
    }
}