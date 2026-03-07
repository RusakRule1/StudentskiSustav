package projekt.servis;

import projekt.model.Predmet;
import projekt.repozitorij.PredmetRepozitorij;

import java.util.List;

public class PredmetServis {
    private final PredmetRepozitorij predmetRepozitorij = new PredmetRepozitorij();

    public List<Predmet> vratiSve() {
        return predmetRepozitorij.vratiSve();
    }

    public List<Predmet> pronadjiPredmeteBezProfesora() {
        return predmetRepozitorij.pronadjiPredmeteBezProfesora();
    }

    public List<Predmet> pronadjiPredmeteProfesora(Integer profesorId) {
        return predmetRepozitorij.pronadjiPredmeteProfesora(profesorId);
    }

    public void obrisiPredmet(Predmet predmet) {
        predmetRepozitorij.obrisi(predmet);
    }

    public void obrisiPredmetPoId(Integer id) {
        predmetRepozitorij.obrisiPoId(id);
    }

    public void spremiPredmet(Predmet predmet) {
        predmetRepozitorij.spremi(predmet);
    }

    public void azurirajPredmet(Predmet predmet) {
        predmetRepozitorij.azuriraj(predmet);
    }

    public List<Predmet> vratiPredmeteStudenta(Integer studentId) {
        return predmetRepozitorij.vratiPredmeteStudenta(studentId);
    }

    public Predmet vratiPredmetSUpisimaPoId(Integer predmetId) {
        return predmetRepozitorij.vratiPredmetSUpisimaPoId(predmetId);
    }
}
