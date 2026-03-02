package projekt.pogled;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.util.StringConverter;
import projekt.model.Predmet;
import projekt.model.Profesor;
import projekt.model.Semestar;
import projekt.servis.PredmetServis;
import projekt.servis.ProfesorServis;
import projekt.upravitelj.UpraviteljPogleda;
import projekt.util.PorukaHelper;
import projekt.util.Stilovi;
import projekt.util.UITvornica;

import java.util.List;

import static projekt.util.UITvornica.*;

public class KreiranjeUredivanjePredmetaPogled extends OsnovniPogled {

    private final PredmetServis predmetServis;
    private final ProfesorServis profesorServis;
    private final PorukaHelper poruke;
    private final Predmet predmetZaUredivanje;

    private final Label naslov = labela().stil(Stilovi.PODNASLOV).build();

    private final Label nazivLabela = labela().build();
    private final TextField nazivPolje = textField().stil(Stilovi.POLJE_TEKSTA).build();

    private final Label sifraLabela = labela().build();
    private final TextField sifraPolje = textField().stil(Stilovi.POLJE_TEKSTA).build();

    private final Label ectsLabela = labela().build();
    private final TextField ectsPolje = textField().stil(Stilovi.POLJE_TEKSTA).build();

    private final Label semestarLabela = labela().build();
    private final RadioButton zimskiRB = new RadioButton();
    private final RadioButton ljetniRB = new RadioButton();
    private final ToggleGroup semestarGrupa = new ToggleGroup();

    private final Label godinaLabela = labela().build();
    private final TextField godinaPolje = textField().stil(Stilovi.POLJE_TEKSTA).build();

    private final Label profesorLabela = labela().build();
    private final ObservableList<Profesor> profesori = FXCollections.observableArrayList();
    private final ComboBox<Profesor> profesorCombo = UITvornica.<Profesor>comboBox()
            .stil(Stilovi.POLJE_SIRINA_COMBO)
            .build();

    private Button spremiGumb;
    private Button odustaniGumb;

    public KreiranjeUredivanjePredmetaPogled(Predmet predmetZaUredivanje) {
        super();
        this.predmetZaUredivanje = predmetZaUredivanje;
        this.predmetServis = new PredmetServis();
        this.profesorServis = new ProfesorServis();
        this.poruke = PorukaHelper.kreiraj(prijevod);
        konfigurirajGumbe();
        konfigurirajSemestarRadioGumbe();
        konfigurirajProfesorCombo();
    }

    @Override
    protected VBox kreirajSadrzaj() {
        HBox semestarBox = hbox(zimskiRB, ljetniRB)
                .stil(Stilovi.RAZMAK_RADIO)
                .build();

        HBox gumbBox = hbox(spremiGumb, odustaniGumb)
                .pozicija(Pos.CENTER)
                .stil(Stilovi.RAZMAK_KONTROLE)
                .build();

        VBox osnovnaPoljaBox = vbox(
                naslov,
                nazivLabela, nazivPolje,
                sifraLabela, sifraPolje,
                ectsLabela, ectsPolje,
                semestarLabela, semestarBox,
                godinaLabela, godinaPolje,
                profesorLabela, profesorCombo
        ).stil(Stilovi.RAZMAK_MALI).build();

        VBox sadrzaj = vbox(
                osnovnaPoljaBox,
                poruke.getKontejner(),
                gumbBox
        ).stil(Stilovi.GLAVNI_VBOX).build();

        ucitajProfesore();

        if (jeUredivanje()) {
            popuniPolja();
        }

        return sadrzaj;
    }

    private void konfigurirajGumbe() {
        spremiGumb = gumb(Stilovi.GUMB_PLAVI, this::spremiPredmet).build();
        odustaniGumb = gumb(Stilovi.GUMB_ZELENI, this::vratiSeNaPregled).build();
    }

    private void konfigurirajSemestarRadioGumbe() {
        zimskiRB.setToggleGroup(semestarGrupa);
        ljetniRB.setToggleGroup(semestarGrupa);
    }

    private void konfigurirajProfesorCombo() {
        profesorCombo.setItems(profesori);
        profesorCombo.setConverter(new StringConverter<>() {
            @Override
            public String toString(Profesor profesor) {
                return profesor == null ? "" : profesor.getPunoImeSTitulom();
            }

            @Override
            public Profesor fromString(String string) {
                return null;
            }
        });
    }

    private void ucitajProfesore() {
        try {
            List<Profesor> sviProfesori = profesorServis.vratiSve();
            profesori.setAll(sviProfesori);
        } catch (Exception e) {
            System.err.println("Greška pri učitavanju profesora: " + e.getMessage());
        }
    }

    private void popuniPolja() {
        nazivPolje.setText(predmetZaUredivanje.getNaziv());
        sifraPolje.setText(predmetZaUredivanje.getSifra());
        ectsPolje.setText(String.valueOf(predmetZaUredivanje.getEctsBodovi()));
        godinaPolje.setText(String.valueOf(predmetZaUredivanje.getGodinaIzvodenja()));
        postaviSemestar(predmetZaUredivanje.getSemestar());
        postaviProfesoraUCombo();
    }

    private void postaviSemestar(Semestar semestar) {
        if (semestar == Semestar.ZIMSKI) {
            zimskiRB.setSelected(true);
        } else if (semestar == Semestar.LJETNI) {
            ljetniRB.setSelected(true);
        }
    }

    private void postaviProfesoraUCombo() {
        try {
            Profesor predmetProfesor = predmetZaUredivanje.getProfesor();
            if (predmetProfesor == null) return;

            Integer profesorId = predmetProfesor.getId();
            profesori.stream()
                    .filter(p -> p.getId().equals(profesorId))
                    .findFirst()
                    .ifPresent(profesorCombo::setValue);
        } catch (Exception e) {
            System.err.println("Greška pri postavljanju profesora u combo: " + e.getMessage());
        }
    }

    private void spremiPredmet() {
        if (!validirajUnos()) return;

        if (jeUredivanje()) {
            azurirajPredmet();
        } else {
            kreirajNoviPredmet();
        }
    }

    private boolean validirajUnos() {
        if (nazivPolje.getText().trim().isEmpty()) {
            poruke.prikaziGreskuSTimerom("greska_naziv_predmeta_obavezan");
            return false;
        }
        if (sifraPolje.getText().trim().isEmpty()) {
            poruke.prikaziGreskuSTimerom("greska_sifra_predmeta_obavezna");
            return false;
        }
        if (!jeValidanEcts(ectsPolje.getText())) {
            poruke.prikaziGreskuSTimerom("greska_ects_predmeta_nevalidan");
            return false;
        }
        if (semestarGrupa.getSelectedToggle() == null) {
            poruke.prikaziGreskuSTimerom("greska_semestar_predmeta_obavezan");
            return false;
        }
        if (!jeValidanBroj(godinaPolje.getText())) {
            poruke.prikaziGreskuSTimerom("greska_godina_predmeta_nevalidna");
            return false;
        }
        return true;
    }

    private boolean jeValidanEcts(String tekst) {
        try {
            return Integer.parseInt(tekst.trim()) > 0;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    private boolean jeValidanBroj(String tekst) {
        try {
            Integer.parseInt(tekst.trim());
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    private Semestar dohvatiOdabraniSemestar() {
        if (zimskiRB.isSelected()) return Semestar.ZIMSKI;
        if (ljetniRB.isSelected()) return Semestar.LJETNI;
        return null;
    }

    private void kreirajNoviPredmet() {
        try {
            Predmet noviPredmet = new Predmet(
                    nazivPolje.getText().trim(),
                    sifraPolje.getText().trim(),
                    Integer.parseInt(ectsPolje.getText().trim()),
                    profesorCombo.getValue(),
                    dohvatiOdabraniSemestar(),
                    Integer.parseInt(godinaPolje.getText().trim())
            );
            predmetServis.spremiPredmet(noviPredmet);
            ocistiFormu();
            poruke.prikaziUspjehSTimerom("predmet_kreiran");
        } catch (Exception e) {
            System.err.println("Greška pri kreiranju predmeta: " + e.getMessage());
            poruke.prikaziGreskuSTimerom("greska_predmet_nije_kreiran");
        }
    }

    private void azurirajPredmet() {
        try {
            predmetZaUredivanje.setNaziv(nazivPolje.getText().trim());
            predmetZaUredivanje.setSifra(sifraPolje.getText().trim());
            predmetZaUredivanje.setEctsBodovi(Integer.parseInt(ectsPolje.getText().trim()));
            predmetZaUredivanje.setSemestar(dohvatiOdabraniSemestar());
            predmetZaUredivanje.setGodinaIzvodenja(Integer.parseInt(godinaPolje.getText().trim()));
            predmetZaUredivanje.setProfesor(profesorCombo.getValue());
            predmetServis.azurirajPredmet(predmetZaUredivanje);
            vratiSeNaPregled();
        } catch (Exception e) {
            System.err.println("Greška pri ažuriranju predmeta: " + e.getMessage());
            poruke.prikaziGreskuSTimerom("greska_predmet_nije_azuriran");
        }
    }

    private void ocistiFormu() {
        nazivPolje.clear();
        sifraPolje.clear();
        ectsPolje.clear();
        semestarGrupa.selectToggle(null);
        godinaPolje.clear();
        profesorCombo.setValue(null);
    }

    private void vratiSeNaPregled() {
        UpraviteljPogleda.idiNatrag();
    }

    private boolean jeUredivanje() {
        return predmetZaUredivanje != null;
    }

    @Override
    public void priSakrivanju() {
        poruke.cleanup();
    }

    @Override
    protected void osvjeziPogledTekstove() {
        osvjeziNaslov();
        osvjeziPolja();
        osvjeziGumbe();
        poruke.osvjeziPoruku();
    }

    private void osvjeziNaslov() {
        String kljuc = jeUredivanje() ? "uredi_predmet_naslov" : "kreiraj_predmet_naslov";
        naslov.setText(prijevod.getPrijevod(kljuc));
    }

    private void osvjeziPolja() {
        nazivLabela.setText(prijevod.getPrijevod("predmet_naziv_labela"));
        nazivPolje.setPromptText(prijevod.getPrijevod("predmet_naziv_prompt"));

        sifraLabela.setText(prijevod.getPrijevod("predmet_sifra_labela"));
        sifraPolje.setPromptText(prijevod.getPrijevod("predmet_sifra_prompt"));

        ectsLabela.setText(prijevod.getPrijevod("predmet_ects_labela"));
        ectsPolje.setPromptText(prijevod.getPrijevod("predmet_ects_prompt"));

        semestarLabela.setText(prijevod.getPrijevod("predmet_semestar_labela"));
        zimskiRB.setText(prijevod.getPrijevod("semestar_zimski"));
        ljetniRB.setText(prijevod.getPrijevod("semestar_ljetni"));

        godinaLabela.setText(prijevod.getPrijevod("predmet_godina_labela"));
        godinaPolje.setPromptText(prijevod.getPrijevod("predmet_godina_prompt"));

        profesorLabela.setText(prijevod.getPrijevod("predmet_profesor_labela"));
        profesorCombo.setPromptText(prijevod.getPrijevod("predmet_profesor_prompt"));
    }

    private void osvjeziGumbe() {
        String kljuc = jeUredivanje() ? "spremi_izmjene_gumb" : "kreiraj_gumb";
        spremiGumb.setText(prijevod.getPrijevod(kljuc));
        odustaniGumb.setText(prijevod.getPrijevod("odustani_gumb"));
    }
}
