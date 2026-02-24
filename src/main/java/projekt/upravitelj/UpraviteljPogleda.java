package projekt.upravitelj;

import javafx.stage.Stage;
import projekt.pogled.OsnovniPogled;

import java.util.ArrayDeque;
import java.util.Deque;

public class UpraviteljPogleda {

    private static UpraviteljPogleda instanca;

    private final Stage glavniProzor;
    private final Deque<OsnovniPogled> povijest;
    private OsnovniPogled trenutniPogled;

    private UpraviteljPogleda(Stage prozor) {
        this.glavniProzor = prozor;
        this.povijest = new ArrayDeque<>();
    }

    public static synchronized void inicijaliziraj(Stage prozor) {
        if (prozor == null) {
            throw new IllegalArgumentException("Prozor ne može biti null");
        }
        instanca = new UpraviteljPogleda(prozor);
        inicijalizirajPracenjeVelicine(prozor);
    }

    private static UpraviteljPogleda getInstanca() {
        if (instanca == null) {
            throw new IllegalStateException(
                    "UpraviteljPogleda nije inicijaliziran. Pozovite inicijaliziraj() prvo."
            );
        }
        return instanca;
    }

    public static void prikazi(OsnovniPogled noviPogled) {
        getInstanca().prikaziPogled(noviPogled);
    }

    public static void prikaziBezPovijesti(OsnovniPogled noviPogled) {
        getInstanca().prikaziPogledBezPovijesti(noviPogled);
    }

    public static void idiNatrag() {
        getInstanca().vratiSeNatrag();
    }

    public static boolean imaPovijesti() {
        return getInstanca().postojiPovijest();
    }

    private void prikaziPogled(OsnovniPogled noviPogled) {
        if (noviPogled == null) {
            throw new IllegalArgumentException("Pogled ne može biti null");
        }
        if (trenutniPogled != null) {
            povijest.push(trenutniPogled);
        }
        postaviAktivniPogled(noviPogled);
    }

    private void prikaziPogledBezPovijesti(OsnovniPogled noviPogled) {
        if (noviPogled == null) {
            throw new IllegalArgumentException("Pogled ne može biti null");
        }
        povijest.clear();
        postaviAktivniPogled(noviPogled);
    }

    private void vratiSeNatrag() {
        if (povijest.isEmpty()) {
            System.out.println("Nema pogleda u povijesti");
            return;
        }
        OsnovniPogled prethodni = povijest.pop();
        postaviAktivniPogled(prethodni);
    }

    private boolean postojiPovijest() {
        return !povijest.isEmpty();
    }

    private void postaviAktivniPogled(OsnovniPogled pogled) {
        if (trenutniPogled != null) {
            trenutniPogled.priSakrivanju();
        }
        trenutniPogled = pogled;
        pogled.prikazi(glavniProzor);
        pogled.priPrikazivanju();
        glavniProzor.show();
    }

    private static void inicijalizirajPracenjeVelicine(Stage prozor) {
        Konfiguracija konfig = Konfiguracija.getInstanca();
        prozor.widthProperty().addListener((obs, old, newVal) ->
                konfig.setSirinaProzora(newVal.intValue()));
        prozor.heightProperty().addListener((obs, old, newVal) ->
                konfig.setVisinaProzora(newVal.intValue()));
    }
}
