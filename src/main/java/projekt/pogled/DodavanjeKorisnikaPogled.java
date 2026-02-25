package projekt.pogled;

import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import projekt.model.Admin;
import projekt.model.Predmet;
import projekt.model.Profesor;
import projekt.model.Student;
import projekt.servis.*;
import projekt.util.PorukaHelper;
import projekt.util.Stilovi;
import projekt.util.UITvornica;
import projekt.util.Validacija;

import static projekt.util.UITvornica.*;

public class DodavanjeKorisnikaPogled extends OsnovniPogled {

    private final PredmetServis predmetServis;
    private final KorisnikServis korisnikServis;
    private final StudentServis studentServis;
    private final ProfesorServis profesorServis;
    private final AdminServis adminServis;
    private final PorukaHelper poruke = PorukaHelper.kreiraj(prijevod);

    private final Label emailLabela = labela().build();
    private final TextField emailPolje = textField().build();
    private final Label lozinkaLabela = labela().build();
    private final PasswordField lozinkaPolje = passwordField().build();
    private final Label imeLabela = labela().build();
    private final TextField imePolje = textField().build();
    private final Label prezimeLabela = labela().build();
    private final TextField prezimePolje = textField().build();

    private final Label ulogaLabela = labela().build();
    private final RadioButton studentRB = new RadioButton();
    private final RadioButton profesorRB = new RadioButton();
    private final RadioButton adminRB = new RadioButton();
    private final ToggleGroup ulogaGrupa = new ToggleGroup();

    private final VBox dodatniPodaciBox = vbox().build();

    private final Label jmbagLabela = labela().build();
    private final TextField jmbagPolje = textField().build();

    private final Label titulaLabela = labela().build();
    private final TextField titulaPolje = textField().build();
    private final Label predmetiLabela = labela().build();
    private final ListView<Predmet> predmetiLista = UITvornica.<Predmet>listView()
            .nacinOdabira(SelectionMode.MULTIPLE)
            .stil(Stilovi.LISTA_VISINA)
            .build();

    private final Label ovlastiLabela = labela().build();
    private final TextField ovlastiPolje = textField().build();

    private Button spremiGumb;

    public DodavanjeKorisnikaPogled() {
        super();
        this.predmetServis = new PredmetServis();
        this.korisnikServis = new KorisnikServis();
        this.studentServis = new StudentServis();
        this.profesorServis = new ProfesorServis();
        this.adminServis = new AdminServis();
    }

    @Override
    protected VBox kreirajSadrzaj() {
        VBox osnovnaPoljaBox = kreirajOsnovnaPolja();
        VBox ulogaBox = kreirajUlogaSekciju();
        konfigurirajSpremiGumb();

        HBox gumbBox = hbox(spremiGumb).pozicija(Pos.CENTER).build();

        return vbox(
                osnovnaPoljaBox,
                ulogaBox,
                dodatniPodaciBox,
                poruke.getKontejner(),
                gumbBox
        ).stil(Stilovi.GLAVNI_VBOX).build();
    }

    private VBox kreirajOsnovnaPolja() {
        return vbox(
                emailLabela, emailPolje,
                lozinkaLabela, lozinkaPolje,
                imeLabela, imePolje,
                prezimeLabela, prezimePolje
        ).stil(Stilovi.RAZMAK_MALI).build();
    }

    private VBox kreirajUlogaSekciju() {
        konfigurirajRadioGumbe();

        HBox radioBox = hbox(studentRB, profesorRB, adminRB)
                .stil(Stilovi.RAZMAK_RADIO)
                .build();

        VBox box = vbox(ulogaLabela, radioBox)
                .stil(Stilovi.RAZMAK_MALI)
                .build();

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
                azurirajDodatnaPoljaNaTemeljuUloge(nova)
        );
    }

    private VBox kreirajStudentPolja() {
        return vbox(jmbagLabela, jmbagPolje)
                .stil(Stilovi.RAZMAK_MALI)
                .build();
    }

    private VBox kreirajProfesorPolja() {
        popuniPredmetiListu();
        return vbox(titulaLabela, titulaPolje, predmetiLabela, predmetiLista)
                .stil(Stilovi.RAZMAK_MALI)
                .build();
    }

    private VBox kreirajAdminPolja() {
        return vbox(ovlastiLabela, ovlastiPolje)
                .stil(Stilovi.RAZMAK_MALI)
                .build();
    }

    private void popuniPredmetiListu() {
        predmetiLista.getItems().setAll(predmetServis.pronadjiPredmeteBezProfesora());
    }

    private void konfigurirajSpremiGumb() {
        spremiGumb = gumb(Stilovi.GUMB_PLAVI, this::spremiKorisnika).build();
    }

    private void azurirajDodatnaPoljaNaTemeljuUloge(Toggle odabranaUloga) {
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

        spremiKorisnikaNaTemeljuUloge();
    }

    private boolean validirajOsnovnePodatke() {
        String email = emailPolje.getText().trim();
        String emailGreska = Validacija.validirajEmail(email);
        if (emailGreska != null) {
            poruke.prikaziGreskuSTimerom(emailGreska);
            return false;
        }

        String lozinka = lozinkaPolje.getText();
        String lozinkaGreska = Validacija.validirajLozinku(lozinka);
        if (lozinkaGreska != null) {
            poruke.prikaziGreskuSTimerom(lozinkaGreska);
            return false;
        }

        String ime = imePolje.getText().trim();
        String imeGreska = Validacija.validirajIme(ime);
        if (imeGreska != null) {
            poruke.prikaziGreskuSTimerom(imeGreska);
            return false;
        }

        String prezime = prezimePolje.getText().trim();
        String prezimeGreska = Validacija.validirajPrezime(prezime);
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
        String jmbagGreska = Validacija.validirajJMBAG(jmbag);

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

    private void spremiKorisnikaNaTemeljuUloge() {
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

        if (korisnikServis.pronadjiKorisnikaPoEmailu(email) != null) {
            poruke.prikaziGreskuSTimerom("greska_email_vec_postoji");
            return false;
        }
        if (studentServis.pronadjiStudentaPoJMBAGu(jmbag) != null) {
            poruke.prikaziGreskuSTimerom("greska_jmbag_vec_postoji");
            return false;
        }
        try {
            studentServis.spremiStudenta(new Student(email, lozinka, ime, prezime, jmbag));
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

        if (korisnikServis.pronadjiKorisnikaPoEmailu(email) != null) {
            poruke.prikaziGreskuSTimerom("greska_email_vec_postoji");
            return false;
        }
        try {
            profesorServis.spremiProfesoraSPredmetima(
                    new Profesor(email, lozinka, ime, prezime, titula),
                    odabraniPredmeti
            );
            osvjeziListuPredmeta();
            return true;
        } catch (Exception e) {
            System.err.println("Greška pri spremanju profesora: " + e.getMessage());
            poruke.prikaziGreskuSTimerom("greska_spremanje_profesora");
            return false;
        }
    }

    private void osvjeziListuPredmeta() {
        predmetiLista.getItems().setAll(predmetServis.pronadjiPredmeteBezProfesora());
    }

    private boolean spremiAdmina() {
        String email = emailPolje.getText().trim();
        String lozinka = lozinkaPolje.getText();
        String ime = imePolje.getText().trim();
        String prezime = prezimePolje.getText().trim();
        String ovlasti = ovlastiPolje.getText().trim();

        if (korisnikServis.pronadjiKorisnikaPoEmailu(email) != null) {
            poruke.prikaziGreskuSTimerom("greska_email_vec_postoji");
            return false;
        }
        try {
            adminServis.spremiAdmina(new Admin(email, lozinka, ime, prezime, ovlasti));
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
    public void priSakrivanju() {
        poruke.cleanup();
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
