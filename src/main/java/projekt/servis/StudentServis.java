package projekt.servis;

import projekt.baza.repozitorij.StudentRepozitorij;
import projekt.model.Student;

import java.util.List;

public class StudentServis {

    private final StudentRepozitorij studentRepozitorij = new StudentRepozitorij();

    public Student pronadiStudentaPoJMBAGu(String jmbag) {
        return studentRepozitorij.pronadiStudentaPoJMBAGu(jmbag);
    }

    public void spremiStudenta(Student noviStudent) {
        studentRepozitorij.spremi(noviStudent);
    }

    public List<Student> dohvatiSveStudente() {
        return studentRepozitorij.vratiSve();
    }
}
