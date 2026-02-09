package projekt.pogled;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import projekt.model.*;
import projekt.servis.*;
import projekt.util.Stilovi;
import projekt.util.Validacija;

import java.util.stream.Stream;

public class DodavanjeKorisnikaPogled extends OsnovniPogled {

    private static final int RAZMAK_SADRZAJ = 30;
    private static final int RAZMAK_POLJA = 10;
    private static final int RAZMAK_RADIO = 15;
    private static final int PADDING_SADRZAJ = 40;
    private static final int SIRINA_LABELE_GRESKE = 600;
    private static final int VISINA_PREDMETI_LISTE = 300;

    private final PredmetServis predmetServis;
    private final KorisnikServis korisnikServis;
    private final Validacija validator;
    private final StudentServis studentServis;
    private final ProfesorServis profesorServis;
    private final AdminServis adminServis;

    private final Label emailLabela = new Label();
    private final TextField emailPolje = new TextField();
    private final Label lozinkaLabela = new Label();
    private final PasswordField lozinkaPolje = new PasswordField();
    private final Label imeLabela = new Label();
    private final TextField imePolje = new TextField();
    private final Label prezimeLabela = new Label();
    private final TextField prezimePolje = new TextField();

    private final Label ulogaLabela = new Label();
    private final RadioButton studentRB = new RadioButton();
    private final RadioButton profesorRB = new RadioButton();
    private final RadioButton adminRB = new RadioButton();
    private final ToggleGroup ulogaGrupa = new ToggleGroup();

    private final VBox dodatniPodaciBox = new VBox(RAZMAK_POLJA);

    private final Label jmbagLabela = new Label();
    private final TextField jmbagPolje = new TextField();

    private final Label titulaLabela = new Label();
    private final TextField titulaPolje = new TextField();
    private final Label predmetiLabela = new Label();
    private final ListView<Predmet> predmetiLista = new ListView<>();

    private final Label ovlastiLabela = new Label();
    private final TextField ovlastiPolje = new TextField();

    private final Label porukaLabela = new Label();
    private String trenutnaPoruka = null;
    private final Button spremiGumb = new Button();

    public DodavanjeKorisnikaPogled() {
        super();
        this.predmetServis = new PredmetServis();
        this.validator = new Validacija();
        this.korisnikServis = new KorisnikServis();
        this.studentServis = new StudentServis();
        this.profesorServis = new ProfesorServis();
        this.adminServis = new AdminServis();
    }

    @Override
    protected VBox kreirajSadrzaj() {
        VBox sadrzajBox = new VBox(RAZMAK_SADRZAJ);
        sadrzajBox.setPadding(new Insets(PADDING_SADRZAJ));
        sadrzajBox.setAlignment(Pos.TOP_CENTER);
        sadrzajBox.getStyleClass().add(Stilovi.POZADINA_SVIJETLA);

        VBox osnovnaPoljaBox = kreirajOsnovnaPolja();
        VBox ulogaBox = kreirajUlogaSekciju();

        konfigurirajPorukaLabelu();
        konfigurirajSpremiGumb();

        sadrzajBox.getChildren().addAll(
                osnovnaPoljaBox,
                ulogaBox,
                dodatniPodaciBox,
                porukaLabela,
                spremiGumb
        );

        return kreirajScrollKontejner(sadrzajBox);
    }

    private VBox kreirajOsnovnaPolja() {
        VBox box = new VBox(RAZMAK_POLJA);
        konfigurirajOsnovnaPolja();
        box.getChildren().addAll(
                emailLabela, emailPolje,
                lozinkaLabela, lozinkaPolje,
                imeLabela, imePolje,
                prezimeLabela, prezimePolje
        );

        return box;
    }

    private VBox kreirajUlogaSekciju() {
        VBox box = new VBox(RAZMAK_POLJA);

        konfigurirajRadioGumbe();
        HBox radioBox = new HBox(RAZMAK_RADIO, studentRB, profesorRB, adminRB);

        box.getChildren().addAll(ulogaLabela, radioBox);

        postaviUlogaListener();

        return box;
    }

    private VBox kreirajStudentPolja() {
        VBox box = new VBox(RAZMAK_POLJA);
        box.getChildren().addAll(jmbagLabela, jmbagPolje);
        return box;
    }

    private VBox kreirajProfesorPolja() {
        VBox box = new VBox(RAZMAK_POLJA);
        konfigurirajPredmetiListu();
        box.getChildren().addAll(
                titulaLabela, titulaPolje,
                predmetiLabela, predmetiLista
        );
        return box;
    }

    private VBox kreirajAdminPolja() {
        VBox box = new VBox(RAZMAK_POLJA);
        box.getChildren().addAll(ovlastiLabela, ovlastiPolje);
        return box;
    }

    private VBox kreirajScrollKontejner(VBox sadrzaj) {
        ScrollPane scroll = new ScrollPane(sadrzaj);
        scroll.setFitToWidth(true);
        scroll.setFitToHeight(true);
        scroll.setPannable(true);
        VBox kontejner = new VBox(scroll);
        kontejner.setAlignment(Pos.CENTER);

        return kontejner;
    }

    private void konfigurirajOsnovnaPolja() {
        emailPolje.getStyleClass().add(Stilovi.POLJE_TEKSTA);
        lozinkaPolje.getStyleClass().add(Stilovi.POLJE_LOZINKE);
        imePolje.getStyleClass().add(Stilovi.POLJE_TEKSTA);
        prezimePolje.getStyleClass().add(Stilovi.POLJE_TEKSTA);
    }

    private void konfigurirajRadioGumbe() {
        studentRB.setToggleGroup(ulogaGrupa);
        profesorRB.setToggleGroup(ulogaGrupa);
        adminRB.setToggleGroup(ulogaGrupa);
    }

    private void konfigurirajPredmetiListu() {
        predmetiLista.getItems().clear();
        predmetiLista.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        predmetiLista.setPrefHeight(VISINA_PREDMETI_LISTE);
        predmetiLista.getItems().addAll(
                predmetServis.dohvatiDostupnePredmete()
        );
    }

    private void konfigurirajPorukaLabelu() {
        porukaLabela.getStyleClass().add(Stilovi.PORUKA_GRESKA);
        porukaLabela.setAlignment(Pos.CENTER);
        porukaLabela.setMinWidth(SIRINA_LABELE_GRESKE);
        porukaLabela.setVisible(false);
    }

    private void konfigurirajSpremiGumb() {
        spremiGumb.getStyleClass().add(Stilovi.GUMB_PRIMARAN);
        spremiGumb.setOnAction(e -> spremiKorisnika());
    }

    private void postaviUlogaListener() {
        ulogaGrupa.selectedToggleProperty().addListener((obs, stara, nova) ->
                azurirajDodatnaPoljaNaTemelјuUloge(nova)
        );
    }

    private void azurirajDodatnaPoljaNaTemelјuUloge(Toggle odabranaUloga) {
        dodatniPodaciBox.getChildren().clear();

        if (odabranaUloga == studentRB) {
            dodatniPodaciBox.getChildren().add(kreirajStudentPolja());
        } else if (odabranaUloga == profesorRB) {
            dodatniPodaciBox.getChildren().add(kreirajProfesorPolja());
        } else if (odabranaUloga == adminRB) {
            dodatniPodaciBox.getChildren().add(kreirajAdminPolja());
        }
    }

    private void spremiKorisnika() {
        pripremiZaValidaciju();

        if (!validirajOsnovnePodatke()) {
            return;
        }

        if (!validirajSpecificnePodatkePoUlozi()) {
            return;
        }

        spremiKorisnikaNaTemelјuUloge();
    }

    private void pripremiZaValidaciju() {
        sakrijPoruku();
        resetirajStilPolja();
    }

    private boolean validirajOsnovnePodatke() {
        String email = emailPolje.getText().trim();
        String emailGreska = validator.validirajEmailSaPorukom(email);
        if (emailGreska != null) {
            prikaziGreskuNaPolju(emailPolje, emailGreska);
            return false;
        }

        String lozinka = lozinkaPolje.getText();
        String lozinkaGreska = validator.validirajLozinkuSaPorukom(lozinka);
        if (lozinkaGreska != null) {
            prikaziGreskuNaPolju(lozinkaPolje, lozinkaGreska);
            return false;
        }

        String ime = imePolje.getText().trim();
        String imeGreska = validirajIme(ime);
        if (imeGreska != null) {
            prikaziGreskuNaPolju(imePolje, imeGreska);
            return false;
        }

        String prezime = prezimePolje.getText().trim();
        String prezimeGreska = validirajPrezime(prezime);
        if (prezimeGreska != null) {
            prikaziGreskuNaPolju(prezimePolje, prezimeGreska);
            return false;
        }

        return true;
    }

    private boolean validirajSpecificnePodatkePoUlozi() {
        if (studentRB.isSelected()) {
            return validirajStudentPodatke();
        } else if (profesorRB.isSelected()) {
            return validirajProfesorPodatke();
        } else if (adminRB.isSelected()) {
            return validirajAdminPodatke();
        }

        prikaziGresku("greska_uloga_obavezna");
        return false;
    }

    private boolean validirajStudentPodatke() {
        String jmbag = jmbagPolje.getText().trim();
        String jmbagGreska = validator.validirajJMBAGSaPorukom(jmbag);

        if (jmbagGreska != null) {
            prikaziGreskuNaPolju(jmbagPolje, jmbagGreska);
            return false;
        }
        return true;
    }

    private boolean validirajProfesorPodatke() {
        String titula = titulaPolje.getText().trim();

        if (titula.isEmpty()) {
            prikaziGreskuNaPolju(titulaPolje, "greska_titula_obavezna");
            return false;
        }
        if (predmetiLista.getSelectionModel().getSelectedItems().isEmpty()) {
            prikaziGresku("greska_predmeti_obavezni");
            return false;
        }
        return true;
    }

    private boolean validirajAdminPodatke() {
        String ovlasti = ovlastiPolje.getText().trim();

        if (ovlasti.isEmpty()) {
            prikaziGreskuNaPolju(ovlastiPolje, "greska_ovlasti_obavezne");
            return false;
        }
        return true;
    }

    private String validirajIme(String vrijednost) {
        if (vrijednost == null || vrijednost.isBlank()) {
            return "greska_ime_obavezno";
        }

        if (!vrijednost.matches("[A-ZČĆŽŠĐ][a-zčćžšđ]+")) {
            return "greska_ime_format";
        }

        return null;
    }

    private String validirajPrezime(String vrijednost) {
        if (vrijednost == null || vrijednost.isBlank()) {
            return "greska_prezime_obavezno";
        }

        if (!vrijednost.matches("[A-ZČĆŽŠĐ][a-zčćžšđ]+")) {
            return "greska_prezime_format";
        }

        return null;
    }

    private void spremiKorisnikaNaTemelјuUloge() {
        boolean uspjeh = false;
        if (studentRB.isSelected()) {
            uspjeh = spremiStudenta();
        } else if (profesorRB.isSelected()) {
            uspjeh = spremiProfesora();
        } else if (adminRB.isSelected()) {
            uspjeh = spremiAdmina();
        }
        if (uspjeh) {
            prikaziUspjehOvisnoOUlozi();
        }
    }

    private boolean spremiStudenta() {
        String email = emailPolje.getText().trim();
        String lozinka = lozinkaPolje.getText();
        String ime = imePolje.getText().trim();
        String prezime = prezimePolje.getText().trim();
        String jmbag = jmbagPolje.getText().trim();

        Korisnik korisnikSEmailom = korisnikServis.pronadiKorisnikaPoEmailu(email);
        if (korisnikSEmailom != null) {
            prikaziGresku("greska_email_vec_postoji");
            return false;
        }

        Student studentSJMBAGom = studentServis.pronadiStudentaPoJMBAGu(jmbag);
        if (studentSJMBAGom != null) {
            prikaziGresku("greska_jmbag_vec_postoji");
            return false;
        }
        try {
            Student student = new Student(email, lozinka, ime, prezime, jmbag);
            studentServis.spremiStudenta(student);
            return true;
        } catch (Exception e) {
            System.err.println("Greška pri spremanju studenta: " + e.getMessage());
            prikaziGresku("greska_spremanje_studenta");
            return false;
        }
    }

    private boolean spremiProfesora() {
        String email = emailPolje.getText().trim();
        String lozinka = lozinkaPolje.getText();
        String ime = imePolje.getText().trim();
        String prezime = prezimePolje.getText().trim();
        String titula = titulaPolje.getText().trim();
        var odabraniPredmeti = predmetiLista.getSelectionModel().getSelectedItems();

        Korisnik korisnikSEmailom = korisnikServis.pronadiKorisnikaPoEmailu(email);
        if (korisnikSEmailom != null) {
            prikaziGresku("greska_email_vec_postoji");
            return false;
        }

        try {
            Profesor profesor = new Profesor(email, lozinka, ime, prezime, titula);
            profesorServis.spremiProfesoraSPredmetima(profesor, odabraniPredmeti);
            osvjeziListuPredmeta();
            return true;
        } catch (Exception e) {
            System.err.println("Greška pri spremanju profesora: " + e.getMessage());
            prikaziGresku("greska_spremanje_profesora");
            return false;
        }
    }

    private void osvjeziListuPredmeta() {
        predmetiLista.getItems().clear();
        predmetiLista.getItems().setAll(predmetServis.dohvatiDostupnePredmete()
        );
    }

    private boolean spremiAdmina() {
        String email = emailPolje.getText().trim();
        String lozinka = lozinkaPolje.getText();
        String ime = imePolje.getText().trim();
        String prezime = prezimePolje.getText().trim();
        String ovlasti = ovlastiPolje.getText().trim();

        Korisnik korisnikSEmailom = korisnikServis.pronadiKorisnikaPoEmailu(email);
        if (korisnikSEmailom != null) {
            prikaziGresku("greska_email_vec_postoji");
            return false;
        }

        try {
            Admin admin = new Admin(email, lozinka, ime, prezime, ovlasti);
            adminServis.spremiAdmina(admin);
            return true;
        } catch (Exception e) {
            System.err.println("Greška pri spremanju admina: " + e.getMessage());
            prikaziGresku("greska_spremanje_admina");
            return false;
        }
    }

    private void prikaziGreskuNaPolju(Control polje, String kljucPoruke) {
        resetirajStilPolja(polje);

        if (polje instanceof PasswordField) {
            polje.getStyleClass().add(Stilovi.POLJE_LOZINKE_GRESKA);
        } else if (polje instanceof TextField) {
            polje.getStyleClass().add(Stilovi.POLJE_TEKSTA_GRESKA);
        }

        prikaziGresku(kljucPoruke);
        polje.requestFocus();
    }

    private void prikaziGresku(String kljucGreske) {
        trenutnaPoruka = kljucGreske;
        porukaLabela.setText(prijevod.getPrijevod(kljucGreske));
        porukaLabela.getStyleClass().remove(Stilovi.PORUKA_USPJESNO);
        porukaLabela.getStyleClass().add(Stilovi.PORUKA_GRESKA);
        porukaLabela.setVisible(true);
    }

    private void prikaziUspjehOvisnoOUlozi() {
        if (studentRB.isSelected()) {
            prikaziUspjeh("uspjeh_dodavanje_studenta");
        } else if (profesorRB.isSelected()) {
            prikaziUspjeh("uspjeh_dodavanje_profesora");
        } else if (adminRB.isSelected()) {
            prikaziUspjeh("uspjeh_dodavanje_admina");
        }
    }

    private void prikaziUspjeh(String kljucPoruke) {
        trenutnaPoruka = kljucPoruke;
        porukaLabela.setText(prijevod.getPrijevod(kljucPoruke));
        porukaLabela.getStyleClass().remove(Stilovi.PORUKA_GRESKA);
        porukaLabela.getStyleClass().add(Stilovi.PORUKA_USPJESNO);
        porukaLabela.setVisible(true);
        new java.util.Timer().schedule(
                new java.util.TimerTask() {
                    @Override
                    public void run() {
                        javafx.application.Platform.runLater(() -> sakrijPoruku());
                    }
                },
                3000
        );
        ocistiPolja();
    }

    private void sakrijPoruku() {
        porukaLabela.setText("");
        porukaLabela.setVisible(false);
        trenutnaPoruka = null;
    }

    private void resetirajStilPolja() {
        Stream.of(emailPolje, imePolje, prezimePolje, jmbagPolje, titulaPolje, ovlastiPolje)
                .forEach(this::resetirajStilPolja);

        resetirajStilPolja(lozinkaPolje);
    }

    private void resetirajStilPolja(Control polje) {
        if (polje == null) return;

        if (polje instanceof PasswordField) {
            polje.getStyleClass().removeAll(
                    Stilovi.POLJE_LOZINKE_GRESKA,
                    Stilovi.POLJE_LOZINKE_USPJESNO
            );
            polje.getStyleClass().add(Stilovi.POLJE_LOZINKE);
        } else if (polje instanceof TextField) {
            polje.getStyleClass().removeAll(
                    Stilovi.POLJE_TEKSTA_GRESKA,
                    Stilovi.POLJE_TEKSTA_USPJESNO
            );
            polje.getStyleClass().add(Stilovi.POLJE_TEKSTA);
        }
    }

    private void ocistiPolja() {
        emailPolje.clear();
        lozinkaPolje.clear();
        imePolje.clear();
        prezimePolje.clear();
        jmbagPolje.clear();
        titulaPolje.clear();
        ovlastiPolje.clear();
        predmetiLista.getSelectionModel().clearSelection();
        ulogaGrupa.selectToggle(null);
        dodatniPodaciBox.getChildren().clear();
    }

    @Override
    protected void osvjeziPogledTekstove() {
        osvjeziOsnovnaPolja();
        osvjeziUlogaSekciju();
        osvjeziSpecificnaPolja();
        osvjeziGumbe();
        osvjeziPorukuAkoPostoji();
    }

    private void osvjeziOsnovnaPolja() {
        emailLabela.setText(prijevod.getPrijevod("email_labela"));
        emailPolje.setPromptText(prijevod.getPrijevod("email_prompt"));

        lozinkaLabela.setText(prijevod.getPrijevod("lozinka_labela"));
        lozinkaPolje.setPromptText(prijevod.getPrijevod("lozinka_prompt"));

        imeLabela.setText(prijevod.getPrijevod("ime_labela"));
        imePolje.setPromptText(prijevod.getPrijevod("ime_prompt"));

        prezimeLabela.setText(prijevod.getPrijevod("prezime_labela"));
        prezimePolje.setPromptText(prijevod.getPrijevod("prezime_prompt"));
    }

    private void osvjeziUlogaSekciju() {
        ulogaLabela.setText(prijevod.getPrijevod("uloga_labela"));
        studentRB.setText(prijevod.getPrijevod("student_rb"));
        profesorRB.setText(prijevod.getPrijevod("profesor_rb"));
        adminRB.setText(prijevod.getPrijevod("admin_rb"));
    }

    private void osvjeziSpecificnaPolja() {
        jmbagLabela.setText(prijevod.getPrijevod("jmbag_labela"));
        jmbagPolje.setPromptText(prijevod.getPrijevod("jmbag_prompt"));
        titulaLabela.setText(prijevod.getPrijevod("titula_labela"));
        titulaPolje.setPromptText(prijevod.getPrijevod("titula_prompt"));
        predmetiLabela.setText(prijevod.getPrijevod("predmeti_labela"));
        ovlastiLabela.setText(prijevod.getPrijevod("ovlasti_labela"));
        ovlastiPolje.setPromptText(prijevod.getPrijevod("ovlasti_prompt"));
    }

    private void osvjeziGumbe() {
        spremiGumb.setText(prijevod.getPrijevod("dodaj_korisnika_gumb"));
    }

    private void osvjeziPorukuAkoPostoji() {
        if (trenutnaPoruka != null) {
            porukaLabela.setText(prijevod.getPrijevod(trenutnaPoruka));
        }
    }
}
