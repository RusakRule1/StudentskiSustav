package projekt.servis;

import projekt.model.Predmet;
import projekt.model.Student;
import projekt.repozitorij.UpisRepozitorij;

import java.util.List;

public class UpisServis {

    private final UpisRepozitorij upisRepozitorij = new UpisRepozitorij();

    public List<Student> pronadjiUpisaneStudente(Integer predmetId) {
        return upisRepozitorij.pronadjiUpisaneStudente(predmetId);
    }

    public List<Student> pronadjiStudenteMoguceZaUpis(Integer predmetId, Integer godinaIzvodenja) {
        return upisRepozitorij.pronadjiStudenteMoguceZaUpis(predmetId, godinaIzvodenja);
    }

    public void upisi(Student student, Predmet predmet) {
        upisRepozitorij.upisi(student, predmet);
    }

    public void ispisi(Student student, Predmet predmet) {
        upisRepozitorij.ispisi(student, predmet);
    }
}
