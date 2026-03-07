package projekt.servis;

import projekt.model.*;
import projekt.repozitorij.PredajaRepozitorij;
import projekt.upravitelj.Sesija;
import projekt.upravitelj.UpraviteljZapisima;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class PredajaServis {

    private final PredajaRepozitorij predajaRepozitorij = new PredajaRepozitorij();

    public Map<Integer, StatusPredaje> vratiStatusePredanihZadataka(Integer studentId) {
        return predajaRepozitorij.vratiStatusePredanihZadataka(studentId);
    }

    public List<PredajaZadatka> vratiPredajeZadatka(Integer zadatakId) {
        return predajaRepozitorij.vratiPredajeZadatka(zadatakId);
    }

    public Optional<PredajaZadatka> vratiPredajuStudentaZaZadatak(Integer zadatakId, Integer studentId) {
        return predajaRepozitorij.vratiPredajuStudentaZaZadatak(zadatakId, studentId);
    }

    public void predajRjesenje(Zadatak zadatak, Student student, String putanjaDatoteke) throws IOException {
        Path putanja = Paths.get(putanjaDatoteke);
        byte[] bajtovi = Files.readAllBytes(putanja);
        String nazivDatoteke = putanja.getFileName().toString();
        long velicina = bajtovi.length;
        String tipDatoteke = dohvatiTipDatoteke(nazivDatoteke);

        PredajaZadatka predaja = new PredajaZadatka(
                zadatak, student, nazivDatoteke, tipDatoteke, velicina, bajtovi);
        predajaRepozitorij.spremi(predaja);
        zapisi(ZapisAkcija.PREDANO_RJESENJE,
                zadatak.getNaziv() + " | " + student.getEmail() + " | " + nazivDatoteke);
    }

    public void ocijeniPredaju(PredajaZadatka predaja, Profesor profesor, Integer vrijednost, String komentar) {
        Ocjena ocjena = predaja.getOcjena();
        if (ocjena == null) {
            ocjena = new Ocjena();
        }
        ocjena.setProfesor(profesor);
        ocjena.setVrijednost(vrijednost);
        ocjena.setKomentar(komentar != null ? komentar : "");
        predaja.setOcjena(ocjena);
        predajaRepozitorij.azuriraj(predaja);
        zapisi(ZapisAkcija.OCJENJENO_RJESENJE,
                predaja.getZadatak().getNaziv() + " | " + predaja.getStudent().getEmail()
                        + " | Ocjena: " + vrijednost);
    }

    public void obrisiOcjenu(PredajaZadatka predaja) {
        predaja.setOcjena(null);
        predaja.setStatus(StatusPredaje.PREDANO);
        predajaRepozitorij.azuriraj(predaja);
        zapisi(ZapisAkcija.OBRISANA_OCJENA,
                predaja.getZadatak().getNaziv() + " | " + predaja.getStudent().getEmail());
    }

    private void zapisi(ZapisAkcija akcija, String detalji) {
        Korisnik korisnik = Sesija.getInstanca().getPrijavljeniKorisnik();
        String email = korisnik != null ? korisnik.getEmail() : "sustav";
        UpraviteljZapisima.getInstanca().dodajZapis(
                new Zapis(email, akcija, detalji, LocalDateTime.now()));
    }

    private String dohvatiTipDatoteke(String naziv) {
        int indeks = naziv.lastIndexOf('.');
        if (indeks > 0 && indeks < naziv.length() - 1) {
            return naziv.substring(indeks + 1).toUpperCase();
        }
        return "NEPOZNATO";
    }

    public Map<Integer, Double> vratiProsjecneOcjeneZaStudenta(Integer studentId) {
        return predajaRepozitorij.vratiProsjecneOcjeneZaStudenta(studentId);
    }

    public List<PredajaZadatka> vratiPredajeStudenta(Integer studentId) {
        return predajaRepozitorij.vratiPredajeStudenta(studentId);
    }
}
