package projekt.repozitorij;

import projekt.dao.OpciDAO;
import projekt.model.Student;

import java.util.List;

public class StudentRepozitorij {
    private final OpciDAO<Student> studentDAO;

    public StudentRepozitorij() {
        this.studentDAO = new OpciDAO<>(Student.class);
    }

    public List<Student> vratiSve() {
        return studentDAO.vratiSve();
    }

    public void spremi(Student student) {
        studentDAO.spremi(student);
    }

    public void azuriraj(Student student) {
        studentDAO.azuriraj(student);
    }

    public void obrisi(Student student) {
        studentDAO.obrisi(student);
    }

    public void obrisiPoId(Integer id) {
        studentDAO.obrisiPoId(id);
    }

    public Student pronadjiStudentaPoJMBAGu(String jmbag) {
        if (jmbag == null || jmbag.isBlank()) return null;
        return studentDAO.pronadjiJedan(
                "SELECT s FROM Student s WHERE s.jmbag = :jmbag",
                Student.class, "jmbag", jmbag);
    }
}
