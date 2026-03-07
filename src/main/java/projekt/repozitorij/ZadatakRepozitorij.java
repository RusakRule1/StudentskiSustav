package projekt.repozitorij;

import projekt.dao.OpciDAO;
import projekt.model.Zadatak;

import java.util.List;

public class ZadatakRepozitorij {

    private final OpciDAO<Zadatak> zadatakDAO = new OpciDAO<>(Zadatak.class);

    public List<Zadatak> vratiZadatkeProfesora(Integer profesorId) {
        return zadatakDAO.pronadjiListu(
                "SELECT z FROM Zadatak z JOIN FETCH z.predmet p WHERE p.profesor.id = :profesorId ORDER BY z.datumObjave DESC",
                Zadatak.class, "profesorId", profesorId);
    }

    public List<Zadatak> vratiZadatkeStudenta(Integer studentId) {
        return zadatakDAO.pronadjiListu(
                "SELECT DISTINCT z FROM Zadatak z JOIN FETCH z.predmet p JOIN p.upisi u WHERE u.student.id = :studentId",
                Zadatak.class, "studentId", studentId);
    }

    public void spremi(Zadatak zadatak) {
        zadatakDAO.spremi(zadatak);
    }

    public void azuriraj(Zadatak zadatak) {
        zadatakDAO.azuriraj(zadatak);
    }

    public void obrisi(Zadatak zadatak) {
        zadatakDAO.obrisi(zadatak);
    }

    public Zadatak vratiZadatakSPredajamaPoId(Integer zadatakId) {
        return zadatakDAO.pronadjiJedan(
                "SELECT z FROM Zadatak z LEFT JOIN FETCH z.predaje WHERE z.id = :zadatakId",
                Zadatak.class, "zadatakId", zadatakId);
    }
}
