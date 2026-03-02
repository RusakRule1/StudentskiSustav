package projekt.repozitorij;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;
import projekt.dao.OpciDAO;
import projekt.model.Predmet;
import projekt.model.Student;
import projekt.model.Upis;
import projekt.upravitelj.UpraviteljBaze;

import java.util.List;

public class UpisRepozitorij {

    private final OpciDAO<Upis> upisDAO = new OpciDAO<>(Upis.class);

    public List<Student> pronadjiUpisaneStudente(Integer predmetId) {
        return upisDAO.pronadjiListu(
                "SELECT u.student FROM Upis u WHERE u.predmet.id = :predmetId",
                Student.class, "predmetId", predmetId);
    }

    public List<Student> pronadjiStudenteMoguceZaUpis(Integer predmetId, Integer godinaIzvodenja) {
        EntityManager em = UpraviteljBaze.dohvatiEntityManager();
        try {
            return em.createQuery(
                            "SELECT s FROM Student s " +
                                    "WHERE s.godinaStudija = :godina " +
                                    "AND s NOT IN (SELECT u.student FROM Upis u WHERE u.predmet.id = :predmetId)",
                            Student.class)
                    .setParameter("godina", godinaIzvodenja)
                    .setParameter("predmetId", predmetId)
                    .getResultList();
        } finally {
            em.close();
        }
    }

    public void upisi(Student student, Predmet predmet) {
        EntityManager em = UpraviteljBaze.dohvatiEntityManager();
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();
            Student managedStudent = em.getReference(Student.class, student.getId());
            Predmet managedPredmet = em.getReference(Predmet.class, predmet.getId());
            em.persist(new Upis(managedStudent, managedPredmet));
            tx.commit();
        } catch (Exception e) {
            if (tx.isActive()) tx.rollback();
            throw e;
        } finally {
            em.close();
        }
    }

    public void ispisi(Student student, Predmet predmet) {
        EntityManager em = UpraviteljBaze.dohvatiEntityManager();
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();
            em.createQuery(
                            "DELETE FROM Upis u WHERE u.student.id = :studentId AND u.predmet.id = :predmetId")
                    .setParameter("studentId", student.getId())
                    .setParameter("predmetId", predmet.getId())
                    .executeUpdate();
            tx.commit();
        } catch (Exception e) {
            if (tx.isActive()) tx.rollback();
            throw e;
        } finally {
            em.close();
        }
    }
}
