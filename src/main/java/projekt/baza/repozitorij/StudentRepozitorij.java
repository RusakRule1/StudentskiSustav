package projekt.baza.repozitorij;

import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import jakarta.persistence.TypedQuery;
import projekt.baza.dao.OpciDAO;
import projekt.model.Student;
import projekt.upravitelj.UpraviteljBaze;

import java.util.List;

public class StudentRepozitorij {
    private final OpciDAO<Student> studentDAO;

    public StudentRepozitorij() {
        this.studentDAO = new OpciDAO<>(Student.class);
    }

    public void spremi(Student student) {
        studentDAO.spremi(student);
    }

    public List<Student> vratiSve() {
        return studentDAO.vratiSve();
    }

    public Student pronadiStudentaPoJMBAGu(String jmbag) {
        EntityManager em = UpraviteljBaze.dohvatiEntityManager();
        try {
            String jpql = "SELECT s FROM Student s WHERE s.jmbag = :jmbag";
            TypedQuery<Student> query = em.createQuery(jpql, Student.class);
            query.setParameter("jmbag", jmbag);
            return query.getSingleResult();
        } catch (NoResultException e) {
            return null;
        } finally {
            em.close();
        }
    }


}
