package projekt.servis;

import projekt.model.Student;
import projekt.repozitorij.StudentRepozitorij;

import java.util.List;

public class StudentServis {

    private final StudentRepozitorij studentRepozitorij = new StudentRepozitorij();

    public Student pronadjiStudentaPoJMBAGu(String jmbag) {
        return studentRepozitorij.pronadjiStudentaPoJMBAGu(jmbag);
    }

    public void spremiStudenta(Student noviStudent) {
        studentRepozitorij.spremi(noviStudent);
    }

    public List<Student> dohvatiSveStudente() {
        return studentRepozitorij.vratiSve();
    }
}
