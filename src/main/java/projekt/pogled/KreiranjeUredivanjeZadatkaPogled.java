package projekt.pogled;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import projekt.model.Predmet;
import projekt.model.Profesor;
import projekt.model.Zadatak;
import projekt.servis.PredmetServis;
import projekt.servis.ZadatakServis;
import projekt.upravitelj.Sesija;
import projekt.upravitelj.UpraviteljPogleda;
import projekt.util.PorukaHelper;
import projekt.util.Stilovi;
import projekt.util.UITvornica;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeParseException;
import java.util.List;

import static projekt.util.UITvornica.*;

public class KreiranjeUredivanjeZadatkaPogled extends OsnovniPogled {

    private final ZadatakServis zadatakServis;
    private final PredmetServis predmetServis;
    private final PorukaHelper poruke;
    private final Zadatak zadatakZaUredivanje;

    private final Label naslov = labela().stil(Stilovi.PODNASLOV).build();

    private final Label nazivLabela = labela().build();
    private final TextField nazivPolje = textField().stil(Stilovi.POLJE_TEKSTA).build();

    private final Label opisLabela = labela().build();
    private final TextArea opisPolje = textArea()
            .stil(Stilovi.POLJE_TEKSTA)
            .wrapText(true)
            .brojRedaka(4)
            .build();

    private final Label predmetLabela = labela().build();
    private final ObservableList<Predmet> predmeti = FXCollections.observableArrayList();
    private final ComboBox<Predmet> predmetCombo = UITvornica.<Predmet>comboBox()
            .stavke(predmeti)
            .stil(Stilovi.POLJE_SIRINA_COMBO)
            .build();

    private final Label rokPredajeLabela = labela().build();
    private final DatePicker rokPredajePicker = new DatePicker();
    private final TextField vrijemePolje = textField().stil(Stilovi.POLJE_TEKSTA).build();

    private Button spremiGumb;
    private Button odustaniGumb;

    public KreiranjeUredivanjeZadatkaPogled(Zadatak zadatakZaUredivanje) {
        super();
        this.zadatakZaUredivanje = zadatakZaUredivanje;
        this.zadatakServis = new ZadatakServis();
        this.predmetServis = new PredmetServis();
        this.poruke = PorukaHelper.kreiraj(prijevod);
        konfigurirajGumbe();
    }

    @Override
    protected VBox kreirajSadrzaj() {
        HBox rokBox = hbox(rokPredajePicker, vrijemePolje)
                .stil(Stilovi.RAZMAK_MALI)
                .build();

        HBox gumbBox = hbox(spremiGumb, odustaniGumb)
                .pozicija(Pos.CENTER)
                .stil(Stilovi.RAZMAK_KONTROLE)
                .build();

        VBox poljaBox = vbox(
                naslov,
                nazivLabela, nazivPolje,
                opisLabela, opisPolje,
                predmetLabela, predmetCombo,
                rokPredajeLabela, rokBox
        ).stil(Stilovi.RAZMAK_MALI).build();

        VBox sadrzaj = vbox(poljaBox, poruke.getKontejner(), gumbBox)
                .stil(Stilovi.GLAVNI_VBOX)
                .build();

        ucitajPredmete();

        if (jeUredivanje()) {
            popuniPolja();
        }

        return sadrzaj;
    }

    private void konfigurirajGumbe() {
        spremiGumb = gumb(Stilovi.GUMB_PLAVI, this::spremiZadatak).build();
        odustaniGumb = gumb(Stilovi.GUMB_ZELENI, UpraviteljPogleda::idiNatrag).build();
    }

    private void ucitajPredmete() {
        try {
            Profesor profesor = (Profesor) Sesija.getInstanca().getPrijavljeniKorisnik();
            List<Predmet> predmetiProfesora = predmetServis.pronadjiPredmeteProfesora(profesor.getId());
            predmeti.setAll(predmetiProfesora);
        } catch (Exception e) {
            System.err.println("Greška pri učitavanju predmeta: " + e.getMessage());
        }
    }

    private void popuniPolja() {
        nazivPolje.setText(zadatakZaUredivanje.getNaziv());
        opisPolje.setText(zadatakZaUredivanje.getOpis());

        if (zadatakZaUredivanje.getRokPredaje() != null) {
            rokPredajePicker.setValue(zadatakZaUredivanje.getRokPredaje().toLocalDate());
            vrijemePolje.setText(zadatakZaUredivanje.getRokPredaje().toLocalTime().toString());
        }

        postaviPredmetUCombo();
    }

    private void postaviPredmetUCombo() {
        try {
            Predmet predmet = zadatakZaUredivanje.getPredmet();
            if (predmet == null) return;

            Integer predmetId = predmet.getId();
            predmeti.stream()
                    .filter(p -> p.getId().equals(predmetId))
                    .findFirst()
                    .ifPresent(predmetCombo::setValue);
        } catch (Exception e) {
            System.err.println("Greška pri postavljanju predmeta u combo: " + e.getMessage());
        }
    }

    private void spremiZadatak() {
        if (!validirajUnos()) return;

        if (jeUredivanje()) {
            azurirajZadatak();
        } else {
            kreirajNoviZadatak();
        }
    }

    private boolean validirajUnos() {
        if (nazivPolje.getText().trim().isEmpty()) {
            poruke.prikaziGreskuSTimerom("greska_naziv_zadatka_obavezan");
            return false;
        }
        if (opisPolje.getText().trim().isEmpty()) {
            poruke.prikaziGreskuSTimerom("greska_opis_zadatka_obavezan");
            return false;
        }
        if (predmetCombo.getValue() == null) {
            poruke.prikaziGreskuSTimerom("greska_predmet_zadatka_obavezan");
            return false;
        }
        if (rokPredajePicker.getValue() == null) {
            poruke.prikaziGreskuSTimerom("greska_rok_predaje_obavezan");
            return false;
        }
        if (!jeValidnoVrijeme(vrijemePolje.getText())) {
            poruke.prikaziGreskuSTimerom("greska_vrijeme_predaje_nevalidno");
            return false;
        }
        if (!dohvatiRokPredaje().isAfter(LocalDateTime.now())) {
            poruke.prikaziGreskuSTimerom("greska_rok_predaje_u_proslosti");
            return false;
        }
        return true;
    }

    private boolean jeValidnoVrijeme(String tekst) {
        try {
            LocalTime.parse(tekst.trim());
            return true;
        } catch (DateTimeParseException e) {
            return false;
        }
    }

    private LocalDateTime dohvatiRokPredaje() {
        LocalDate datum = rokPredajePicker.getValue();
        LocalTime vrijeme = LocalTime.parse(vrijemePolje.getText().trim());
        return LocalDateTime.of(datum, vrijeme);
    }

    private void kreirajNoviZadatak() {
        try {
            zadatakServis.kreirajZadatak(
                    nazivPolje.getText().trim(),
                    opisPolje.getText().trim(),
                    predmetCombo.getValue(),
                    dohvatiRokPredaje()
            );
            ocistiFormu();
            poruke.prikaziUspjehSTimerom("zadatak_kreiran");
        } catch (Exception e) {
            System.err.println("Greška pri kreiranju zadatka: " + e.getMessage());
            poruke.prikaziGreskuSTimerom("greska_zadatak_nije_kreiran");
        }
    }

    private void azurirajZadatak() {
        try {
            zadatakZaUredivanje.setNaziv(nazivPolje.getText().trim());
            zadatakZaUredivanje.setOpis(opisPolje.getText().trim());
            zadatakZaUredivanje.setPredmet(predmetCombo.getValue());
            zadatakZaUredivanje.setRokPredaje(dohvatiRokPredaje());
            zadatakServis.azurirajZadatak(zadatakZaUredivanje);
            UpraviteljPogleda.idiNatrag();
        } catch (Exception e) {
            System.err.println("Greška pri ažuriranju zadatka: " + e.getMessage());
            poruke.prikaziGreskuSTimerom("greska_zadatak_nije_azuriran");
        }
    }

    private void ocistiFormu() {
        nazivPolje.clear();
        opisPolje.clear();
        predmetCombo.setValue(null);
        rokPredajePicker.setValue(null);
        vrijemePolje.clear();
    }

    private boolean jeUredivanje() {
        return zadatakZaUredivanje != null;
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
        String kljuc = jeUredivanje() ? "uredi_zadatak_naslov" : "kreiraj_zadatak_naslov";
        naslov.setText(prijevod.getPrijevod(kljuc));
    }

    private void osvjeziPolja() {
        nazivLabela.setText(prijevod.getPrijevod("zadatak_naziv_labela"));
        nazivPolje.setPromptText(prijevod.getPrijevod("zadatak_naziv_prompt"));

        opisLabela.setText(prijevod.getPrijevod("zadatak_opis_labela"));
        opisPolje.setPromptText(prijevod.getPrijevod("zadatak_opis_prompt"));

        predmetLabela.setText(prijevod.getPrijevod("zadatak_predmet_labela"));
        predmetCombo.setPromptText(prijevod.getPrijevod("zadatak_predmet_prompt"));

        rokPredajeLabela.setText(prijevod.getPrijevod("zadatak_rok_predaje_labela"));
        vrijemePolje.setPromptText(prijevod.getPrijevod("zadatak_vrijeme_prompt"));
    }

    private void osvjeziGumbe() {
        String kljuc = jeUredivanje() ? "spremi_izmjene_gumb" : "kreiraj_gumb";
        spremiGumb.setText(prijevod.getPrijevod(kljuc));
        odustaniGumb.setText(prijevod.getPrijevod("odustani_gumb"));
    }
}
