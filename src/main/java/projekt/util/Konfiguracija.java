package projekt.util;

import org.ini4j.Ini;
import org.ini4j.IniPreferences;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.prefs.Preferences;

public class Konfiguracija {

    private static Konfiguracija instanca;
    private static final String NAZIV_APLIKACIJE = "StudentskiSustav";

    private static final String INI_DATOTEKA = Paths.get(System.getProperty("user.home"), ".studentski-sustav", "postavke.ini").toString();
    private static final String INI_SEKCIJA = "Podesavanja";

    private Preferences windowsRegistar;

    private String jezik;
    private int visinaProzora;
    private int sirinaProzora;
    private String zadnjiKorisnik;
    private boolean zapamtiMe;

    private static final String PODRAZUMIJEVANI_JEZIK = "HR";
    private static final int PODRAZUMIJEVANA_SIRINA = 800;
    private static final int PODRAZUMIJEVANA_VISINA = 600;

    private Konfiguracija() {
        kreirajDirektorijZaINI();
        ucitajIzINI();
        ucitajIzRegistra();
    }

    public static synchronized Konfiguracija getInstanca() {
        if (instanca == null) {
            instanca = new Konfiguracija();
        }
        return instanca;
    }

    private void kreirajDirektorijZaINI() {
        try {
            File iniDatoteka = new File(INI_DATOTEKA);
            File direktorij = iniDatoteka.getParentFile();

            if (direktorij != null && !direktorij.exists()) {
                boolean kreirano = direktorij.mkdirs();
                if (kreirano) {
                    System.out.println("Kreiran direktorij: " + direktorij.getAbsolutePath());
                } else {
                    System.err.println("Nije moguće kreirati direktorij: " + direktorij.getAbsolutePath());
                }
            }
        } catch (Exception e) {
            System.err.println("Greška pri kreiranju direktorija: " + e.getMessage());
        }
    }

    private void ucitajIzINI() {
        File iniDatoteka = new File(INI_DATOTEKA);
        try {
            if (iniDatoteka.exists()) {
                Ini ini = new Ini(iniDatoteka);
                IniPreferences prefs = new IniPreferences(ini);
                jezik = prefs.node(INI_SEKCIJA).get("jezik", PODRAZUMIJEVANI_JEZIK);
                sirinaProzora = prefs.node(INI_SEKCIJA).getInt("sirina", PODRAZUMIJEVANA_SIRINA);
                visinaProzora = prefs.node(INI_SEKCIJA).getInt("visina", PODRAZUMIJEVANA_VISINA);
            } else {
                postaviPodrazumijevaneINI();
                spremiUINI();
            }
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

    private void spremiUINI() {
        try {
            Ini ini = new Ini();
            ini.put(INI_SEKCIJA, "jezik", jezik);
            ini.put(INI_SEKCIJA, "sirina", String.valueOf(sirinaProzora));
            ini.put(INI_SEKCIJA, "visina", String.valueOf(visinaProzora));
            ini.store(new File(INI_DATOTEKA));
        } catch (IOException e) {
            System.err.println("Greška pri spremanju INI datoteke: " + e.getMessage());
        }
    }

    private void ucitajIzRegistra() {
        try {
            windowsRegistar = Preferences.userRoot().node(NAZIV_APLIKACIJE);
            zadnjiKorisnik = windowsRegistar.get("zadnjiKorisnik", "");
            zapamtiMe = windowsRegistar.getBoolean("zapamtiMe", false);
        } catch (Exception e) {
            System.err.println("Greška pri pristupu registru: " + e.getMessage());
            postaviPodrazumijevaneRegistar();
        }
    }

    private void spremiURegistar() {
        try {
            windowsRegistar.put("zadnjiKorisnik", zadnjiKorisnik);
            windowsRegistar.putBoolean("zapamtiMe", zapamtiMe);
            windowsRegistar.flush();
        } catch (Exception e) {
            System.err.println("Greška pri spremanju u registar: " + e.getMessage());
        }
    }

    private void postaviPodrazumijevaneRegistar() {
        zadnjiKorisnik = "";
        zapamtiMe = false;
    }

    public String getJezik() {
        return jezik;
    }

    public int getVisinaProzora() {
        return visinaProzora;
    }

    public int getSirinaProzora() {
        return sirinaProzora;
    }

    public String getZadnjiKorisnik() {
        return zadnjiKorisnik;
    }

    public boolean getZapamtiMe() {
        return zapamtiMe;
    }

    public void setJezik(String jezik) {
        this.jezik = jezik;
        spremiUINI();
    }

    public void setZadnjiKorisnik(String korisnik) {
        this.zadnjiKorisnik = korisnik;
        spremiURegistar();
    }

    public void setZapamtiMe(boolean zapamtiMe) {
        this.zapamtiMe = zapamtiMe;
        spremiURegistar();
    }
    
    public void spremiSvePostavke() {
        spremiUINI();
        spremiURegistar();
    }
}