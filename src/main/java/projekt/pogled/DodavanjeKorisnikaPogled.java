package projekt.pogled;

import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import projekt.model.*;
import projekt.servis.*;
import projekt.util.PorukaHelper;
import projekt.util.Stilovi;
import projekt.util.Validacija;

public class DodavanjeKorisnikaPogled extends OsnovniPogled {

    private final PredmetServis predmetServis;
    private final KorisnikServis korisnikServis;
    private final Validacija validator;
    private final StudentServis studentServis;
    private final ProfesorServis profesorServis;
    private final AdminServis adminServis;
    private final PorukaHelper poruke = PorukaHelper.kreiraj(prijevod);

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

    private final VBox dodatniPodaciBox = new VBox();

    private final Label jmbagLabela = new Label();
    private final TextField jmbagPolje = new TextField();

    private final Label titulaLabela = new Label();
    private final TextField titulaPolje = new TextField();
    private final Label predmetiLabela = new Label();
    private final ListView<Predmet> predmetiLista = new ListView<>();

    private final Label ovlastiLabela = new Label();
    private final TextField ovlastiPolje = new TextField();

    private Button spremiGumb;

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
        VBox sadrzaj = kreirajGlavniSadrzaj(Pos.TOP_CENTER);

        VBox osnovnaPoljaBox = kreirajOsnovnaPolja();
        VBox ulogaBox = kreirajUlogaSekciju();

        konfigurirajSpremiGumb();

        sadrzaj.getChildren().addAll(
                osnovnaPoljaBox,
                ulogaBox,
                dodatniPodaciBox,
                poruke.kontejner,
                spremiGumb
        );

        return kreirajScrollKontejner(sadrzaj);
    }

    private VBox kreirajOsnovnaPolja() {
        VBox box = new VBox();
        box.getStyleClass().add(Stilovi.RAZMAK_MALI);
        box.getChildren().addAll(
                emailLabela, emailPolje,
                lozinkaLabela, lozinkaPolje,
                imeLabela, imePolje,
                prezimeLabela, prezimePolje
        );

        return box;
    }

    private VBox kreirajUlogaSekciju() {
        VBox box = new VBox();
        box.getStyleClass().add(Stilovi.RAZMAK_MALI);

        konfigurirajRadioGumbe();

        HBox radioBox = new HBox();
        radioBox.getStyleClass().add(Stilovi.RAZMAK_RADIO);
        radioBox.getChildren().addAll(studentRB, profesorRB, adminRB);

        box.getChildren().addAll(ulogaLabela, radioBox);

        postaviUlogaListener();

        return box;
    }

    private void konfigurirajRadioGumbe() {
        studentRB.setToggleGroup(ulogaGrupa);
        profesorRB.setToggleGroup(ulogaGrupa);
        adminRB.setToggleGroup(ulogaGrupa);
    }

    private void postaviUlogaListener() {
        ulogaGrupa.selectedToggleProperty().addListener((obs, stara, nova) ->
                azurirajDodatnaPoljaNaTemelјuUloge(nova)
        );
    }

    private VBox kreirajStudentPolja() {
        VBox box = new VBox();
        box.getStyleClass().add(Stilovi.RAZMAK_MALI);
        box.getChildren().addAll(jmbagLabela, jmbagPolje);
        return box;
    }

    private VBox kreirajProfesorPolja() {
        VBox box = new VBox();
        box.getStyleClass().add(Stilovi.RAZMAK_MALI);

        konfigurirajPredmetiListu();

        box.getChildren().addAll(
                titulaLabela, titulaPolje,
                predmetiLabela, predmetiLista
        );
        return box;
    }

    private VBox kreirajAdminPolja() {
        VBox box = new VBox();
        box.getStyleClass().add(Stilovi.RAZMAK_MALI);
        box.getChildren().addAll(ovlastiLabela, ovlastiPolje);
        return box;
    }

    private VBox kreirajScrollKontejner(VBox sadrzaj) {
        ScrollPane scroll = new ScrollPane(sadrzaj);
        scroll.setFitToWidth(true);
        scroll.setFitToHeight(true);
        scroll.setPannable(true);
        scroll.getStyleClass().add(Stilovi.SCROLL_PANE_SADRZAJ);

        VBox kontejner = new VBox(scroll);
        kontejner.setAlignment(Pos.TOP_CENTER);
        kontejner.getStyleClass().add(Stilovi.KONTEJNER_SCROLL);
        VBox.setVgrow(kontejner, Priority.ALWAYS);

        return kontejner;
    }

    private void konfigurirajPredmetiListu() {
        predmetiLista.getItems().clear();
        predmetiLista.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        predmetiLista.getStyleClass().add(Stilovi.LISTA_VISINA);
        predmetiLista.getItems().addAll(predmetServis.dohvatiDostupnePredmete());
    }

    private void konfigurirajSpremiGumb() {
        spremiGumb = kreirajGumb(Stilovi.GUMB_PLAVI, this::spremiKorisnika);
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
        poruke.sakrijPoruku();

        if (!validirajOsnovnePodatke()) {
            return;
        }
        if (!validirajSpecificnePodatkePoUlozi()) {
            return;
        }

        spremiKorisnikaNaTemelјuUloge();
    }

    private boolean validirajOsnovnePodatke() {
        String email = emailPolje.getText().trim();
        String emailGreska = validator.validirajEmailSaPorukom(email);
        if (emailGreska != null) {
            poruke.prikaziGreskuSTimerom(emailGreska);
            return false;
        }

        String lozinka = lozinkaPolje.getText();
        String lozinkaGreska = validator.validirajLozinkuSaPorukom(lozinka);
        if (lozinkaGreska != null) {
            poruke.prikaziGreskuSTimerom(lozinkaGreska);
            return false;
        }

        String ime = imePolje.getText().trim();
        String imeGreska = validirajIme(ime);
        if (imeGreska != null) {
            poruke.prikaziGreskuSTimerom(imeGreska);
            return false;
        }

        String prezime = prezimePolje.getText().trim();
        String prezimeGreska = validirajPrezime(prezime);
        if (prezimeGreska != null) {
            poruke.prikaziGreskuSTimerom(prezimeGreska);
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

        poruke.prikaziGreskuSTimerom("greska_uloga_obavezna");
        return false;
    }

    private boolean validirajStudentPodatke() {
        String jmbag = jmbagPolje.getText().trim();
        String jmbagGreska = validator.validirajJMBAGSaPorukom(jmbag);

        if (jmbagGreska != null) {
            poruke.prikaziGreskuSTimerom(jmbagGreska);
            return false;
        }
        return true;
    }

    private boolean validirajProfesorPodatke() {
        String titula = titulaPolje.getText().trim();

        if (titula.isEmpty()) {
            poruke.prikaziGreskuSTimerom("greska_titula_obavezna");
            return false;
        }
        if (predmetiLista.getSelectionModel().getSelectedItems().isEmpty()) {
            poruke.prikaziGreskuSTimerom("greska_predmeti_obavezni");
            return false;
        }
        return true;
    }

    private boolean validirajAdminPodatke() {
        String ovlasti = ovlastiPolje.getText().trim();

        if (ovlasti.isEmpty()) {
            poruke.prikaziGreskuSTimerom("greska_ovlasti_obavezne");
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
            ocistiPolja();
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
            poruke.prikaziGreskuSTimerom("greska_email_vec_postoji");
            return false;
        }

        Student studentSJMBAGom = studentServis.pronadiStudentaPoJMBAGu(jmbag);
        if (studentSJMBAGom != null) {
            poruke.prikaziGreskuSTimerom("greska_jmbag_vec_postoji");
            return false;
        }
        try {
            Student student = new Student(email, lozinka, ime, prezime, jmbag);
            studentServis.spremiStudenta(student);
            return true;
        } catch (Exception e) {
            System.err.println("Greška pri spremanju studenta: " + e.getMessage());
            poruke.prikaziGreskuSTimerom("greska_spremanje_studenta");
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
            poruke.prikaziGreskuSTimerom("greska_email_vec_postoji");
            return false;
        }

        try {
            Profesor profesor = new Profesor(email, lozinka, ime, prezime, titula);
            profesorServis.spremiProfesoraSPredmetima(profesor, odabraniPredmeti);
            osvjeziListuPredmeta();
            return true;
        } catch (Exception e) {
            System.err.println("Greška pri spremanju profesora: " + e.getMessage());
            poruke.prikaziGreskuSTimerom("greska_spremanje_profesora");
            return false;
        }
    }

    private void osvjeziListuPredmeta() {
        predmetiLista.getItems().clear();
        predmetiLista.getItems().setAll(predmetServis.dohvatiDostupnePredmete());
    }

    private boolean spremiAdmina() {
        String email = emailPolje.getText().trim();
        String lozinka = lozinkaPolje.getText();
        String ime = imePolje.getText().trim();
        String prezime = prezimePolje.getText().trim();
        String ovlasti = ovlastiPolje.getText().trim();

        Korisnik korisnikSEmailom = korisnikServis.pronadiKorisnikaPoEmailu(email);
        if (korisnikSEmailom != null) {
            poruke.prikaziGreskuSTimerom("greska_email_vec_postoji");
            return false;
        }

        try {
            Admin admin = new Admin(email, lozinka, ime, prezime, ovlasti);
            adminServis.spremiAdmina(admin);
            return true;
        } catch (Exception e) {
            System.err.println("Greška pri spremanju admina: " + e.getMessage());
            poruke.prikaziGreskuSTimerom("greska_spremanje_admina");
            return false;
        }
    }

    private void prikaziUspjehOvisnoOUlozi() {
        if (studentRB.isSelected()) {
            poruke.prikaziUspjehSTimerom("uspjeh_dodavanje_studenta");
        } else if (profesorRB.isSelected()) {
            poruke.prikaziUspjehSTimerom("uspjeh_dodavanje_profesora");
        } else if (adminRB.isSelected()) {
            poruke.prikaziUspjehSTimerom("uspjeh_dodavanje_admina");
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
        poruke.osvjeziPoruku();
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
}