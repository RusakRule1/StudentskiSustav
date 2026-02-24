package projekt.upravitelj;

import org.ini4j.Ini;
import org.ini4j.IniPreferences;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.prefs.Preferences;

public class Konfiguracija {

    private static Konfiguracija instanca;
    private static final String NAZIV_APLIKACIJE = "StudentskiSustav";

    private static final Path DIREKTORIJ_KONFIGURACIJE = Paths.get(
            System.getProperty("user.home"),
            ".studentski-sustav"
    );
    private static final Path INI_PUTANJA = DIREKTORIJ_KONFIGURACIJE.resolve("postavke.ini");
    private static final String INI_SEKCIJA = "Podesavanja";

    private static final String PODRAZUMIJEVANI_JEZIK = "HR";
    private static final int PODRAZUMIJEVANA_SIRINA = 800;
    private static final int PODRAZUMIJEVANA_VISINA = 600;

    private final Preferences prefs;

    private String jezik;
    private int visinaProzora;
    private int sirinaProzora;
    private String zadnjiKorisnik;
    private boolean zapamtiMe;

    private Konfiguracija() {
        this.prefs = Preferences.userRoot().node(NAZIV_APLIKACIJE);
        inicijaliziraj();
    }

    public static synchronized Konfiguracija getInstanca() {
        if (instanca == null) {
            instanca = new Konfiguracija();
        }
        return instanca;
    }

    private void inicijaliziraj() {
        osiguraPostojanjeDirektorija();
        ucitajPostavke();
    }

    private void osiguraPostojanjeDirektorija() {
        try {
            if (!Files.exists(DIREKTORIJ_KONFIGURACIJE)) {
                Files.createDirectories(DIREKTORIJ_KONFIGURACIJE);
                System.out.println("Kreiran direktorij: " + DIREKTORIJ_KONFIGURACIJE);
            }
        } catch (IOException e) {
            System.err.println("Greška pri kreiranju direktorija: " + e.getMessage());
        }
    }

    private void ucitajPostavke() {
        ucitajIzINI();
        ucitajKorisnickePostavke();
    }

    private void ucitajIzINI() {
        File iniDatoteka = INI_PUTANJA.toFile();

        if (!iniDatoteka.exists()) {
            postaviPodrazumijevaneINI();
            spremiUINI();
            return;
        }

        try {
            Ini ini = new Ini(iniDatoteka);
            IniPreferences iniPrefs = new IniPreferences(ini);

            jezik = iniPrefs.node(INI_SEKCIJA).get("jezik", PODRAZUMIJEVANI_JEZIK);
            sirinaProzora = iniPrefs.node(INI_SEKCIJA).getInt("sirina", PODRAZUMIJEVANA_SIRINA);
            visinaProzora = iniPrefs.node(INI_SEKCIJA).getInt("visina", PODRAZUMIJEVANA_VISINA);
        } catch (IOException e) {
            System.err.println("Greška pri učitavanju INI datoteke: " + e.getMessage());
            postaviPodrazumijevaneINI();
        }
    }

    private void postaviPodrazumijevaneINI() {
        jezik = PODRAZUMIJEVANI_JEZIK;
        sirinaProzora = PODRAZUMIJEVANA_SIRINA;
        visinaProzora = PODRAZUMIJEVANA_VISINA;
    }

    private boolean spremiUINI() {
        try {
            Ini ini = new Ini();
            ini.put(INI_SEKCIJA, "jezik", jezik);
            ini.put(INI_SEKCIJA, "sirina", String.valueOf(sirinaProzora));
            ini.put(INI_SEKCIJA, "visina", String.valueOf(visinaProzora));
            ini.store(INI_PUTANJA.toFile());
            return true;
        } catch (IOException e) {
            System.err.println("Greška pri spremanju INI datoteke: " + e.getMessage());
            return false;
        }
    }

    private void ucitajKorisnickePostavke() {
        try {
            zadnjiKorisnik = prefs.get("zadnjiKorisnik", "");
            zapamtiMe = prefs.getBoolean("zapamtiMe", false);
        } catch (Exception e) {
            System.err.println("Greška pri učitavanju iz korisničkih postavki: " + e.getMessage());
            postaviPodrazumijevaneKorisnickePostavke();
        }
    }

    private boolean spremiKorisnickePostavke() {
        try {
            prefs.put("zadnjiKorisnik", zadnjiKorisnik);
            prefs.putBoolean("zapamtiMe", zapamtiMe);
            prefs.flush();
            return true;
        } catch (Exception e) {
            System.err.println("Greška pri spremanju u korisničke postavke: " + e.getMessage());
            return false;
        }
    }

    private void postaviPodrazumijevaneKorisnickePostavke() {
        zadnjiKorisnik = "";
        zapamtiMe = false;
    }

    public static String getPodrazumijevaniJezik() {
        return PODRAZUMIJEVANI_JEZIK;
    }

    public String getJezik() {
        return jezik;
    }

    public void setJezik(String jezik) {
        if (jezik == null || jezik.trim().isEmpty()) {
            throw new IllegalArgumentException("Jezik ne može biti null ili prazan");
        }
        this.jezik = jezik;
    }

    public int getVisinaProzora() {
        return visinaProzora;
    }

    public void setVisinaProzora(int visina) {
        if (visina > 0) {
            this.visinaProzora = visina;
        }
    }

    public int getSirinaProzora() {
        return sirinaProzora;
    }

    public void setSirinaProzora(int sirina) {
        if (sirina > 0) {
            this.sirinaProzora = sirina;
        }
    }

    public String getZadnjiKorisnik() {
        return zadnjiKorisnik;
    }

    public void setZadnjiKorisnik(String korisnik) {
        this.zadnjiKorisnik = korisnik != null ? korisnik : "";
    }

    public boolean getZapamtiMe() {
        return zapamtiMe;
    }

    public void setZapamtiMe(boolean zapamtiMe) {
        this.zapamtiMe = zapamtiMe;
    }

    public boolean spremiSvePostavke() {
        boolean iniUspjeh = spremiUINI();
        boolean korisnickePostavkeUspjeh = spremiKorisnickePostavke();
        return iniUspjeh && korisnickePostavkeUspjeh;
    }

}