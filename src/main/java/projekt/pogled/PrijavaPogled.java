package projekt.pogled;

import javafx.application.Platform;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import projekt.model.Korisnik;
import projekt.model.Uloga;
import projekt.model.Zapis;
import projekt.model.ZapisAkcija;
import projekt.servis.KorisnikServis;
import projekt.upravitelj.Sesija;
import projekt.upravitelj.UpraviteljPogleda;
import projekt.upravitelj.UpraviteljZapisima;
import projekt.util.Hash;
import projekt.util.PorukaHelper;
import projekt.util.Stilovi;

import static projekt.util.UITvornica.*;

public class PrijavaPogled extends OsnovniPogled {

    private final KorisnikServis korisnikServis;
    private final PorukaHelper poruke;

    private final Text podnaslovTekst = tekst().stil(Stilovi.NASLOV_POGLEDA).build();
    private final Label emailLabela = labela().stil(Stilovi.LABELA_PODEBLJANA).build();
    private final TextField emailPolje = textField().stil(Stilovi.POLJE_SIRINA_SREDNJA).build();
    private final Label lozinkaLabela = labela().stil(Stilovi.LABELA_PODEBLJANA).build();
    private final PasswordField lozinkaPolje = passwordField().stil(Stilovi.POLJE_SIRINA_SREDNJA).build();
    private final CheckBox zapamtiMeCheck = new CheckBox();
    private Button prijavaGumb;

    public PrijavaPogled() {
        super();
        this.korisnikServis = new KorisnikServis();
        this.poruke = PorukaHelper.kreiraj(prijevod);
        Platform.runLater(() -> prikaziInfoTraku(false));
    }

    @Override
    protected Pos dohvatiPoravnanjeSadrzaja() {
        return Pos.CENTER;
    }

    @Override
    protected VBox kreirajSadrzaj() {
        prijavaGumb = gumb(Stilovi.GUMB_PLAVI, this::obradiPrijavu)
                .stil(Stilovi.GUMB_SIRINA_SREDNJA)
                .build();

        VBox sadrzaj = vbox(podnaslovTekst, kreirajFormu(), poruke.kontejner, prijavaGumb)
                .stil(Stilovi.PRIJAVA_VBOX)
                .build();

        postaviEventHandlere();
        ucitajZapamcenePodatke();

        return sadrzaj;
    }

    private VBox kreirajFormu() {
        GridPane mreza = new GridPane();
        mreza.getStyleClass().add(Stilovi.MREZA_FORMA);

        mreza.add(emailLabela, 0, 0);
        mreza.add(emailPolje, 1, 0);
        mreza.add(lozinkaLabela, 0, 1);
        mreza.add(lozinkaPolje, 1, 1);
        mreza.add(zapamtiMeCheck, 1, 2);

        return vbox(mreza)
                .stil(Stilovi.KARTICA, Stilovi.KARTICA_SJENA)
                .build();
    }

    private void postaviEventHandlere() {
        lozinkaPolje.setOnAction(e -> obradiPrijavu());
    }

    private void ucitajZapamcenePodatke() {
        String zadnjiKorisnik = konfig.getZadnjiKorisnik();
        if (!zadnjiKorisnik.isEmpty()) {
            emailPolje.setText(zadnjiKorisnik);
            lozinkaPolje.requestFocus();
        }
        zapamtiMeCheck.setSelected(konfig.getZapamtiMe());
    }

    private void obradiPrijavu() {
        poruke.sakrijPoruku();

        String email = emailPolje.getText().trim();
        String lozinka = lozinkaPolje.getText();

        String greskaEmail = validirajEmail(email);
        String greskaLozinka = validirajLozinku(lozinka);

        if (greskaEmail != null) {
            poruke.prikaziGreskuSTimerom(greskaEmail);
            return;
        }
        if (greskaLozinka != null) {
            poruke.prikaziGreskuSTimerom(greskaLozinka);
            return;
        }

        onemoguciFunkcionalnost();
        new Thread(() -> obradiPrijavuUBackgroundu(email, lozinka)).start();
    }

    private String validirajEmail(String email) {
        if (email == null || email.isEmpty()) return "greska_email_obavezan";
        if (!email.matches("^[A-Za-z0-9+_.-]+@(.+)$")) return "greska_email_format";
        return null;
    }

    private String validirajLozinku(String lozinka) {
        if (lozinka == null || lozinka.isEmpty()) return "greska_lozinka_obavezna";
        return null;
    }

    private void obradiPrijavuUBackgroundu(String email, String lozinka) {
        try {
            Korisnik korisnik = korisnikServis.pronadjiKorisnikaPoEmailu(email);

            if (korisnik == null) {
                Platform.runLater(this::obradiNeuspjesnuPrijavu);
                return;
            }

            boolean uspjeh = Hash.provjeriLozinku(lozinka, korisnik.getLozinkaHash(), email);

            Platform.runLater(() -> {
                if (uspjeh) {
                    obradiUspjesnuPrijavu(korisnik, email);
                } else {
                    obradiNeuspjesnuPrijavu();
                }
            });
        } catch (Exception ex) {
            Platform.runLater(this::obradiGreskuPrijave);
        }
    }

    private void obradiUspjesnuPrijavu(Korisnik korisnik, String email) {
        Sesija.getInstanca().postaviPrijavljenogKorisnika(korisnik);
        obradiZapamtiMe(email);
        zatvoriSveProzore();
        zabiljeziPrijavu(korisnik.getEmail(), korisnik.getUloga());
        preusmjeriPoUlozi(korisnik.getUloga());
    }

    private void obradiNeuspjesnuPrijavu() {
        poruke.prikaziGreskuSTimerom("greska_neispravan_login");
        omoguciFunkcionalnost();
        lozinkaPolje.clear();
        lozinkaPolje.requestFocus();
    }

    private void obradiGreskuPrijave() {
        poruke.prikaziGreskuSTimerom("greska_opcenita");
        omoguciFunkcionalnost();
    }

    private void obradiZapamtiMe(String email) {
        if (zapamtiMeCheck.isSelected()) {
            konfig.setZadnjiKorisnik(email);
            konfig.setZapamtiMe(true);
        } else {
            konfig.setZadnjiKorisnik("");
            konfig.setZapamtiMe(false);
        }
    }

    private void zabiljeziPrijavu(String email, Uloga uloga) {
        UpraviteljZapisima.getInstanca().dodajZapis(
                new Zapis(email, ZapisAkcija.PRIJAVA,
                        "Uspješna prijava korisnika s ulogom: " + uloga.name())
        );
    }

    private void preusmjeriPoUlozi(Uloga uloga) {
        UpraviteljPogleda.prikaziBezPovijesti(
                switch (uloga) {
                    case STUDENT -> new StudentIzbornikPogled();
                    case PROFESOR -> new ProfesorIzbornikPogled();
                    case ADMIN -> new AdminIzbornikPogled();
                }
        );
    }

    private void onemoguciFunkcionalnost() {
        prijavaGumb.setDisable(true);
        prijavaGumb.getStyleClass().remove(Stilovi.GUMB_PLAVI);
        prijavaGumb.getStyleClass().add(Stilovi.GUMB_ONEMOGUCEN);
    }

    private void omoguciFunkcionalnost() {
        prijavaGumb.setDisable(false);
        prijavaGumb.getStyleClass().remove(Stilovi.GUMB_ONEMOGUCEN);
        prijavaGumb.getStyleClass().add(Stilovi.GUMB_PLAVI);
    }

    private void zatvoriSveProzore() {
        poruke.cleanup();
    }

    @Override
    protected void osvjeziPogledTekstove() {
        osvjeziLabele();
        osvjeziPolja();
        osvjeziGumbe();
        poruke.osvjeziPoruku();
    }

    private void osvjeziLabele() {
        podnaslovTekst.setText(prijevod.getPrijevod("login_podnaslov"));
        emailLabela.setText(prijevod.getPrijevod("email_labela"));
        lozinkaLabela.setText(prijevod.getPrijevod("lozinka_labela"));
        zapamtiMeCheck.setText(prijevod.getPrijevod("zapamti_me"));
    }

    private void osvjeziPolja() {
        emailPolje.setPromptText(prijevod.getPrijevod("email_prompt"));
        lozinkaPolje.setPromptText(prijevod.getPrijevod("lozinka_prompt"));
    }

    private void osvjeziGumbe() {
        prijavaGumb.setText(prijevod.getPrijevod("prijavi_se_gumb"));
    }
}
