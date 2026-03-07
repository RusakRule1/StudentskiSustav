package projekt.repozitorij;

import jakarta.persistence.EntityManager;
import projekt.dao.OpciDAO;
import projekt.model.PredajaZadatka;
import projekt.model.StatusPredaje;
import projekt.upravitelj.UpraviteljBaze;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class PredajaRepozitorij {

    private final OpciDAO<PredajaZadatka> predajaDAO = new OpciDAO<>(PredajaZadatka.class);

    public Map<Integer, StatusPredaje> vratiStatusePredanihZadataka(Integer studentId) {
        List<Object[]> rezultati = predajaDAO.pronadjiListu(
                "SELECT p.zadatak.id, p.status FROM PredajaZadatka p WHERE p.student.id = :studentId",
                Object[].class, "studentId", studentId);
        Map<Integer, StatusPredaje> mapa = new HashMap<>();
        for (Object[] red : rezultati) {
            mapa.put((Integer) red[0], (StatusPredaje) red[1]);
        }
        return mapa;
    }

    public List<PredajaZadatka> vratiPredajeZadatka(Integer zadatakId) {
        return predajaDAO.pronadjiListu(
                "SELECT p FROM PredajaZadatka p " +
                        "JOIN FETCH p.student " +
                        "JOIN FETCH p.zadatak z " +
                        "JOIN FETCH z.predmet " +
                        "LEFT JOIN FETCH p.ocjena " +
                        "WHERE p.zadatak.id = :zadatakId " +
                        "ORDER BY p.datumPredaje DESC",
                PredajaZadatka.class, "zadatakId", zadatakId);
    }

    public void spremi(PredajaZadatka predaja) {
        predajaDAO.spremi(predaja);
    }

    public void azuriraj(PredajaZadatka predaja) {
        predajaDAO.azuriraj(predaja);
    }

    public Optional<PredajaZadatka> vratiPredajuStudentaZaZadatak(Integer zadatakId, Integer studentId) {
        EntityManager em = UpraviteljBaze.dohvatiEntityManager();
        try {
            return em.createQuery(
                            "SELECT p FROM PredajaZadatka p " +
                                    "LEFT JOIN FETCH p.ocjena o " +
                                    "LEFT JOIN FETCH o.profesor " +
                                    "JOIN FETCH p.zadatak z " +
                                    "JOIN FETCH z.predmet " +
                                    "WHERE p.zadatak.id = :zadatakId AND p.student.id = :studentId",
                            PredajaZadatka.class)
                    .setParameter("zadatakId", zadatakId)
                    .setParameter("studentId", studentId)
                    .getResultList()
                    .stream().findFirst();
        } finally {
            em.close();
        }
    }

    public Map<Integer, Double> vratiProsjecneOcjeneZaStudenta(Integer studentId) {
        List<Object[]> rezultati = predajaDAO.pronadjiListu(
                "SELECT z.predmet.id, AVG(o.vrijednost) " +
                        "FROM PredajaZadatka p " +
                        "JOIN p.zadatak z " +
                        "JOIN p.ocjena o " +
                        "WHERE p.student.id = :studentId " +
                        "GROUP BY z.predmet.id",
                Object[].class, "studentId", studentId);
        Map<Integer, Double> mapa = new HashMap<>();
        for (Object[] red : rezultati) {
            mapa.put((Integer) red[0], (Double) red[1]);
        }
        return mapa;
    }

    public List<PredajaZadatka> vratiPredajeStudenta(Integer studentId) {
        return predajaDAO.pronadjiListu(
                "SELECT p FROM PredajaZadatka p " +
                        "JOIN FETCH p.zadatak z " +
                        "JOIN FETCH z.predmet " +
                        "LEFT JOIN FETCH p.ocjena " +
                        "WHERE p.student.id = :studentId",
                PredajaZadatka.class, "studentId", studentId);
    }
}
