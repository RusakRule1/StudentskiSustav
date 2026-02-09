package projekt.pogled;

import javafx.application.Platform;
import javafx.geometry.Insets;
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
import projekt.util.Stilovi;

public class LoginPogled extends OsnovniPogled {

    private final Text podnaslovTekst = new Text();
    private final Label emailLabela = new Label();
    private final TextField emailPolje = new TextField();
    private final Label lozinkaLabela = new Label();
    private final PasswordField lozinkaPolje = new PasswordField();
    private final CheckBox zapamtiMeCheck = new CheckBox();
    private final Button prijavaGumb = new Button();
    private final Label greskaLabela = new Label();
    private String trenutnaGreska = null;
    private final KorisnikServis korisnikServis;

    public LoginPogled() {
        super();
        korisnikServis = new KorisnikServis();
    }

    @Override
    protected VBox kreirajSadrzaj() {
        VBox sadrzajBox = new VBox(20);
        sadrzajBox.setAlignment(Pos.CENTER);
        sadrzajBox.setPadding(new Insets(40, 20, 20, 20));

        podnaslovTekst.getStyleClass().add(Stilovi.PODNASLOV_TEKST);
        GridPane forma = kreirajFormu();
        greskaLabela.getStyleClass().add(Stilovi.PORUKA_GRESKA);
        greskaLabela.setAlignment(Pos.CENTER);
        greskaLabela.setVisible(false);

        HBox gumbiBox = kreirajGumbe();
        sadrzajBox.getChildren().addAll(podnaslovTekst, forma, greskaLabela, gumbiBox);
        postaviLoginEventHandler();
        ucitajZapamcenePodatke();

        return sadrzajBox;
    }

    private GridPane kreirajFormu() {
        GridPane mreza = new GridPane();
        mreza.getStyleClass().addAll(Stilovi.MREZA_FORMA, Stilovi.KARTICA, Stilovi.KARTICA_SJENA);
        mreza.setVgap(15);
        mreza.setHgap(10);
        mreza.setAlignment(Pos.CENTER);

        emailLabela.getStyleClass().add(Stilovi.LABELA_PODEBLJANA);
        mreza.add(emailLabela, 0, 0);

        emailPolje.getStyleClass().add(Stilovi.POLJE_TEKSTA);
        emailPolje.setPrefWidth(250);
        mreza.add(emailPolje, 1, 0);

        lozinkaLabela.getStyleClass().add(Stilovi.LABELA_PODEBLJANA);
        mreza.add(lozinkaLabela, 0, 1);

        lozinkaPolje.getStyleClass().add(Stilovi.POLJE_LOZINKE);
        lozinkaPolje.setPrefWidth(250);
        mreza.add(lozinkaPolje, 1, 1);

        mreza.add(zapamtiMeCheck, 1, 2);
        return mreza;
    }

    private HBox kreirajGumbe() {
        HBox box = new HBox();
        box.setAlignment(Pos.CENTER);
        prijavaGumb.getStyleClass().add(Stilovi.GUMB_PRIMARAN);
        prijavaGumb.setPrefWidth(250);
        box.getChildren().add(prijavaGumb);
        return box;
    }

    @Override
    protected void osvjeziPogledTekstove() {
        podnaslovTekst.setText(prijevod.getPrijevod("login_podnaslov"));
        emailLabela.setText(prijevod.getPrijevod("email_labela"));
        emailPolje.setPromptText(prijevod.getPrijevod("email_prompt"));
        lozinkaLabela.setText(prijevod.getPrijevod("lozinka_labela"));
        lozinkaPolje.setPromptText(prijevod.getPrijevod("lozinka_prompt"));
        zapamtiMeCheck.setText(prijevod.getPrijevod("zapamti_me"));
        prijavaGumb.setText(prijevod.getPrijevod("prijavi_se_gumb"));
        if (trenutnaGreska != null) {
            greskaLabela.setText(prijevod.getPrijevod(trenutnaGreska));
        }
    }

    private void postaviLoginEventHandler() {
        prijavaGumb.setOnAction(e -> obradiPrijavu());
    }

    private void ucitajZapamcenePodatke() {
        String zadnjiKorisnik = konfig.getZadnjiKorisnik();
        if (!zadnjiKorisnik.isEmpty()) {
            emailPolje.setText(zadnjiKorisnik);
        }
        zapamtiMeCheck.setSelected(konfig.getZapamtiMe());
    }

    private void obradiPrijavu() {
        sakrijGresku();
        resetirajStilPolja();

        String email = emailPolje.getText().trim();
        String lozinka = lozinkaPolje.getText();

        String greskaEmail = validirajEmail(email);
        String greskaLozinka = validirajLozinku(lozinka);

        if (greskaEmail != null) {
            prikaziGreskuNaPolju(emailPolje, greskaEmail);
            return;
        }
        if (greskaLozinka != null) {
            prikaziGreskuNaPolju(lozinkaPolje, greskaLozinka);
            return;
        }

        prijavaGumb.getStyleClass().remove(Stilovi.GUMB_PRIMARAN);
        prijavaGumb.getStyleClass().add(Stilovi.GUMB_ONEMOGUCEN);
        prijavaGumb.setDisable(true);

        new Thread(() -> obradiPrijavuUBackgroundu(email, lozinka)).start();
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
                    Sesija.getInstanca().postaviPrijavljenogKorisnika(korisnik);
                    obradiZapamtiMe(email);
                    preusmjeriPoUlozi(korisnik.getUloga(), email);
                } else {
                    obradiNeuspjesnuPrijavu();
                }
            });
        } catch (Exception ex) {
            javafx.application.Platform.runLater(this::obradiGreskuPrijave);
        }
    }


    private void obradiNeuspjesnuPrijavu() {
        prikaziGresku("greska_neispravan_login");
        resetirajGumb();
        lozinkaPolje.setText("");
        lozinkaPolje.requestFocus();
    }

    private void obradiGreskuPrijave() {
        prikaziGresku("greska_opcenita");
        resetirajGumb();
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

    private void preusmjeriPoUlozi(Uloga uloga, String email) {
        UpraviteljZapisima.getInstance().dodajZapis(new Zapis(email, ZapisAkcija.PRIJAVA, "Uspješna prijava korisnika s ulogom: " + uloga.name()));
        UpraviteljPogleda.prikaziBezPovijesti(
                switch (uloga) {
                    case STUDENT -> new StudentIzbornikPogled();
                    case PROFESOR -> new ProfesorIzbornikPogled();
                    case ADMIN -> new AdminIzbornikPogled();
                }
        );
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

    private void prikaziGreskuNaPolju(Control polje, String poruka) {
        polje.getStyleClass().removeAll(
                Stilovi.POLJE_TEKSTA,
                Stilovi.POLJE_LOZINKE,
                Stilovi.POLJE_TEKSTA_USPJESNO,
                Stilovi.POLJE_LOZINKE_USPJESNO,
                Stilovi.POLJE_TEKSTA_GRESKA,
                Stilovi.POLJE_LOZINKE_GRESKA
        );

        if (polje instanceof PasswordField) {
            polje.getStyleClass().add(Stilovi.POLJE_LOZINKE_GRESKA);
        } else if (polje instanceof TextField) {
            polje.getStyleClass().add(Stilovi.POLJE_TEKSTA_GRESKA);
        }

        prikaziGresku(poruka);
    }

    private void resetirajStilPolja() {
        emailPolje.getStyleClass().remove(Stilovi.POLJE_TEKSTA_GRESKA);
        emailPolje.getStyleClass().remove(Stilovi.POLJE_TEKSTA_USPJESNO);
        emailPolje.getStyleClass().add(Stilovi.POLJE_TEKSTA);

        lozinkaPolje.getStyleClass().remove(Stilovi.POLJE_LOZINKE_GRESKA);
        lozinkaPolje.getStyleClass().remove(Stilovi.POLJE_LOZINKE_USPJESNO);
        lozinkaPolje.getStyleClass().add(Stilovi.POLJE_LOZINKE);
    }

    private void prikaziGresku(String greska) {
        trenutnaGreska = greska;
        String poruka = prijevod.getPrijevod(greska);
        greskaLabela.setText(poruka);
        greskaLabela.getStyleClass().remove(Stilovi.PORUKA_USPJESNO);
        greskaLabela.getStyleClass().add(Stilovi.PORUKA_GRESKA);
        greskaLabela.setVisible(true);
    }

    private void sakrijGresku() {
        greskaLabela.setText("");
        greskaLabela.setVisible(false);
    }

    private void resetirajGumb() {
        prijavaGumb.getStyleClass().remove(Stilovi.GUMB_ONEMOGUCEN);
        prijavaGumb.getStyleClass().add(Stilovi.GUMB_PRIMARAN);
        prijavaGumb.setDisable(false);
    }
}