package projekt.pogled;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import projekt.kontroler.AuthKontroler;
import projekt.model.Uloga;
import projekt.upravitelj.UpraviteljScena;
import projekt.util.StiloviUtil;

public class LoginPogled extends OsnovniPogled {

    private AuthKontroler authKontroler;
    private Text podnaslovTekst;
    private Label emailLabela;
    private TextField emailPolje;
    private Label lozinkaLabela;
    private PasswordField lozinkaPolje;
    private CheckBox zapamtiMeCheck;
    private Button prijavaGumb;
    private Label greskaLabela;

    public LoginPogled() {
        super();
        this.authKontroler = new AuthKontroler();
    }

    @Override
    protected VBox kreirajSadrzaj() {
        VBox sadrzajBox = new VBox(20);
        sadrzajBox.setAlignment(Pos.CENTER);
        sadrzajBox.setPadding(new Insets(40, 20, 20, 20));

        podnaslovTekst = new Text();
        podnaslovTekst.getStyleClass().add(StiloviUtil.PODNASLOV_TEKST);

        GridPane forma = kreirajFormu();

        greskaLabela = new Label();
        greskaLabela.getStyleClass().add(StiloviUtil.PORUKA_GRESKA);
        greskaLabela.setAlignment(Pos.CENTER);
        greskaLabela.setVisible(false);

        HBox gumbiBox = kreirajGumbe();

        sadrzajBox.getChildren().addAll(podnaslovTekst, forma, greskaLabela, gumbiBox);

        if ("HR".equals(trenutniJezik)) {
            postaviHrvatski();
        } else {
            postaviEngleski();
        }

        postaviLoginEventHandler();
        ucitajZapamcenePodatke();
        return sadrzajBox;
    }

    private GridPane kreirajFormu() {
        GridPane mreza = new GridPane();
        mreza.getStyleClass().addAll(StiloviUtil.MREZA_FORMA, StiloviUtil.KARTICA, StiloviUtil.KARTICA_SJENA);
        mreza.setVgap(15);
        mreza.setHgap(10);
        mreza.setAlignment(Pos.CENTER);

        emailLabela = new Label();
        emailLabela.getStyleClass().add(StiloviUtil.LABELA_PODEBLJANA);
        mreza.add(emailLabela, 0, 0);

        emailPolje = new TextField();
        emailPolje.getStyleClass().add(StiloviUtil.POLJE_TEKSTA);
        emailPolje.setPrefWidth(250);
        mreza.add(emailPolje, 1, 0);

        lozinkaLabela = new Label();
        lozinkaLabela.getStyleClass().add(StiloviUtil.LABELA_PODEBLJANA);
        mreza.add(lozinkaLabela, 0, 1);

        lozinkaPolje = new PasswordField();
        lozinkaPolje.getStyleClass().add(StiloviUtil.POLJE_LOZINKE);
        lozinkaPolje.setPrefWidth(250);
        mreza.add(lozinkaPolje, 1, 1);

        zapamtiMeCheck = new CheckBox();
        mreza.add(zapamtiMeCheck, 1, 2);

        return mreza;
    }

    private HBox kreirajGumbe() {
        HBox box = new HBox();
        box.setAlignment(Pos.CENTER);

        prijavaGumb = new Button();
        prijavaGumb.getStyleClass().add(StiloviUtil.GUMB_PRIMARAN);
        prijavaGumb.setPrefWidth(250);

        box.getChildren().add(prijavaGumb);
        return box;
    }

    private void ucitajZapamcenePodatke() {
        String zadnjiKorisnik = konfig.getZadnjiKorisnik();
        if (!zadnjiKorisnik.isEmpty()) {
            emailPolje.setText(zadnjiKorisnik);
        }
        zapamtiMeCheck.setSelected(konfig.getZapamtiMe());
    }

    protected void postaviLoginEventHandler() {
        prijavaGumb.setOnAction(e -> obradiPrijavu());
    }

    private void obradiPrijavu() {
        sakrijGresku();
        resetirajStilPolja();

        String email = emailPolje.getText().trim();
        String lozinka = lozinkaPolje.getText();

        if (!validirajUnos(email, lozinka)) {
            return;
        }

        if (zapamtiMeCheck.isSelected()) {
            konfig.setZadnjiKorisnik(email);
            konfig.setZapamtiMe(true);
        } else {
            konfig.setZadnjiKorisnik("");
            konfig.setZapamtiMe(false);
        }

        prijavaGumb.getStyleClass().remove(StiloviUtil.GUMB_PRIMARAN);
        prijavaGumb.getStyleClass().add(StiloviUtil.GUMB_ONEMOGUCEN);
        prijavaGumb.setDisable(true);

        new Thread(() -> {
            try {
                Thread.sleep(300);

                Uloga uloga = authKontroler.prijaviKorisnika(email, lozinka);

                javafx.application.Platform.runLater(() -> {
                    if (uloga != null) {
                        UpraviteljScena.prikaziGlavniIzbornik(uloga, email);
                    } else {
                        String greskaTekst = trenutniJezik.equals("HR")
                                ? "Neispravni podaci za prijavu!"
                                : "Invalid login credentials!";
                        prikaziGresku(greskaTekst);
                        resetirajGumb();
                    }
                });

            } catch (Exception ex) {
                javafx.application.Platform.runLater(() -> {
                    String greskaTekst = trenutniJezik.equals("HR")
                            ? "Došlo je do greške: " + ex.getMessage()
                            : "An error occurred: " + ex.getMessage();
                    prikaziGresku(greskaTekst);
                    resetirajGumb();
                });
            }
        }).start();
    }


    private boolean validirajUnos(String email, String lozinka) {
        boolean validno = true;

        if (email.isEmpty()) {
            String poruka = trenutniJezik.equals("HR")
                    ? "Email je obavezan!"
                    : "Email is required!";
            prikaziGreskuNaPolju(emailPolje, poruka);
            validno = false;
        } else if (!email.contains("@")) {
            String poruka = trenutniJezik.equals("HR")
                    ? "Email mora sadržavati @"
                    : "Email must contain @";
            prikaziGreskuNaPolju(emailPolje, poruka);
            validno = false;
        } else if (!email.matches("^[A-Za-z0-9+_.-]+@(.+)$")) {
            String poruka = trenutniJezik.equals("HR")
                    ? "Neispravan format emaila"
                    : "Invalid email format";
            prikaziGreskuNaPolju(emailPolje, poruka);
            validno = false;
        }

        if (lozinka.isEmpty()) {
            String poruka = trenutniJezik.equals("HR")
                    ? "Lozinka je obavezna!"
                    : "Password is required!";
            prikaziGreskuNaPolju(lozinkaPolje, poruka);
            validno = false;
        }

        return validno;
    }

    private void prikaziGreskuNaPolju(Control polje, String poruka) {
        polje.getStyleClass().removeAll(
                StiloviUtil.POLJE_TEKSTA,
                StiloviUtil.POLJE_LOZINKE,
                StiloviUtil.POLJE_TEKSTA_USPJESNO,
                StiloviUtil.POLJE_LOZINKE_USPJESNO,
                StiloviUtil.POLJE_TEKSTA_GRESKA,
                StiloviUtil.POLJE_LOZINKE_GRESKA
        );

        if (polje instanceof PasswordField) {
            polje.getStyleClass().add(StiloviUtil.POLJE_LOZINKE_GRESKA);
        } else if (polje instanceof TextField) {
            polje.getStyleClass().add(StiloviUtil.POLJE_TEKSTA_GRESKA);
        }

        prikaziGresku(poruka);
    }

    private void resetirajStilPolja() {
        emailPolje.getStyleClass().remove(StiloviUtil.POLJE_TEKSTA_GRESKA);
        emailPolje.getStyleClass().remove(StiloviUtil.POLJE_TEKSTA_USPJESNO);
        emailPolje.getStyleClass().add(StiloviUtil.POLJE_TEKSTA);

        lozinkaPolje.getStyleClass().remove(StiloviUtil.POLJE_LOZINKE_GRESKA);
        lozinkaPolje.getStyleClass().remove(StiloviUtil.POLJE_LOZINKE_USPJESNO);
        lozinkaPolje.getStyleClass().add(StiloviUtil.POLJE_LOZINKE);
    }

    private void prikaziGresku(String poruka) {
        greskaLabela.setText(poruka);
        greskaLabela.getStyleClass().remove(StiloviUtil.PORUKA_USPJESNO);
        greskaLabela.getStyleClass().add(StiloviUtil.PORUKA_GRESKA);
        greskaLabela.setVisible(true);
    }

    private void sakrijGresku() {
        greskaLabela.setText("");
        greskaLabela.setVisible(false);
    }

    private void resetirajGumb() {
        prijavaGumb.getStyleClass().remove(StiloviUtil.GUMB_ONEMOGUCEN);
        prijavaGumb.getStyleClass().add(StiloviUtil.GUMB_PRIMARAN);
        prijavaGumb.setDisable(false);

        if (trenutniJezik.equals("HR")) {
            prijavaGumb.setText("PRIJAVI SE");
        } else {
            prijavaGumb.setText("LOGIN");
        }
    }

    @Override
    protected void postaviHrvatski() {
        podnaslovTekst.setText("Prijava u sustav");
        emailLabela.setText("Email:");
        emailPolje.setPromptText("primjer@gmail.com");
        lozinkaLabela.setText("Lozinka:");
        lozinkaPolje.setPromptText("Unesite lozinku");
        zapamtiMeCheck.setText("Zapamti me");
        prijavaGumb.setText("PRIJAVI SE");
    }

    @Override
    protected void postaviEngleski() {
        podnaslovTekst.setText("Login");
        emailLabela.setText("Email:");
        emailPolje.setPromptText("example@gmail.com");
        lozinkaLabela.setText("Password:");
        lozinkaPolje.setPromptText("Enter password");
        zapamtiMeCheck.setText("Remember me");
        prijavaGumb.setText("LOGIN");
    }
}