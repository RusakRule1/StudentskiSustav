package projekt.pogled;

import javafx.application.Platform;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
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

public class LoginPogled extends OsnovniPogled {

    private final KorisnikServis korisnikServis;
    private final PorukaHelper poruke = PorukaHelper.kreiraj(prijevod);

    private final Text podnaslovTekst = new Text();
    private final Label emailLabela = new Label();
    private final TextField emailPolje = new TextField();
    private final Label lozinkaLabela = new Label();
    private final PasswordField lozinkaPolje = new PasswordField();
    private final CheckBox zapamtiMeCheck = new CheckBox();
    private final Button prijavaGumb = new Button();

    public LoginPogled() {
        super();
        this.korisnikServis = new KorisnikServis();
        Platform.runLater(this::sakrijInfoTraku);
    }

    @Override
    protected VBox kreirajSadrzaj() {
        VBox sadrzaj = kreirajGlavniSadrzaj(Pos.CENTER, Stilovi.INFO_TRAKA_SAKRIVENA);

        konfigurirajPodnaslov();
        GridPane forma = kreirajFormu();
        HBox gumbiBox = kreirajGumbe();

        sadrzaj.getChildren().addAll(podnaslovTekst, forma, poruke.kontejner, gumbiBox);

        postaviEventHandlere();
        ucitajZapamcenePodatke();

        return sadrzaj;
    }

    protected void sakrijInfoTraku() {
        if (korijen.getTop() instanceof VBox trakeKontejner) {
            trakeKontejner.getStyleClass().add(Stilovi.INFO_TRAKA_SAKRIVENA);
        }
    }

    private void konfigurirajPodnaslov() {
        podnaslovTekst.getStyleClass().add(Stilovi.NASLOV_POGLEDA);
    }

    private GridPane kreirajFormu() {
        GridPane mreza = new GridPane();
        mreza.getStyleClass().addAll(
                Stilovi.MREZA_FORMA,
                Stilovi.KARTICA,
                Stilovi.KARTICA_SJENA,
                Stilovi.RAZMAK_FORMA,
                Stilovi.RAZMAK_KOLONE,
                Stilovi.SREDINA
        );

        konfigurirajEmailPolje(mreza);
        konfigurirajLozinkaPolje(mreza);
        konfigurirajZapamtiMeCheck(mreza);

        return mreza;
    }

    private void konfigurirajEmailPolje(GridPane mreza) {
        emailLabela.getStyleClass().add(Stilovi.LABELA_PODEBLJANA);
        emailPolje.getStyleClass().add(Stilovi.POLJE_SIRINA_SREDNJA);
        mreza.add(emailLabela, 0, 0);
        mreza.add(emailPolje, 1, 0);
    }

    private void konfigurirajLozinkaPolje(GridPane mreza) {
        lozinkaLabela.getStyleClass().add(Stilovi.LABELA_PODEBLJANA);
        lozinkaPolje.getStyleClass().addAll(Stilovi.POLJE_SIRINA_SREDNJA);
        mreza.add(lozinkaLabela, 0, 1);
        mreza.add(lozinkaPolje, 1, 1);
    }

    private void konfigurirajZapamtiMeCheck(GridPane mreza) {
        mreza.add(zapamtiMeCheck, 1, 2);
    }

    private HBox kreirajGumbe() {
        HBox box = new HBox();
        box.setAlignment(Pos.CENTER);
        box.getStyleClass().add(Stilovi.RAZMAK_GUMBI);

        prijavaGumb.getStyleClass().addAll(
                Stilovi.GUMB_PLAVI,
                Stilovi.GUMB_SIRINA_SREDNJA
        );
        prijavaGumb.setOnAction(e -> obradiPrijavu());

        box.getChildren().add(prijavaGumb);
        return box;
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
            Korisnik korisnik = korisnikServis.pronadiKorisnikaPoEmailu(email);

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
            javafx.application.Platform.runLater(this::obradiGreskuPrijave);
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
        UpraviteljZapisima.getInstance().dodajZapis(
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