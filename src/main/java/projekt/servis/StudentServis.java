package projekt.servis;

import projekt.model.Korisnik;
import projekt.model.Student;
import projekt.model.Zapis;
import projekt.model.ZapisAkcija;
import projekt.repozitorij.StudentRepozitorij;
import projekt.upravitelj.Sesija;
import projekt.upravitelj.UpraviteljZapisima;

import java.time.LocalDateTime;
import java.util.List;

public class StudentServis {

    private final StudentRepozitorij studentRepozitorij = new StudentRepozitorij();

    public Student pronadjiStudentaPoJMBAGu(String jmbag) {
        return studentRepozitorij.pronadjiStudentaPoJMBAGu(jmbag);
    }

    public void spremiStudenta(Student noviStudent) {
        studentRepozitorij.spremi(noviStudent);
        zapisi(ZapisAkcija.KREIRAN_KORISNIK,
                "Student: " + noviStudent.getEmail() + " | JMBAG: " + noviStudent.getJmbag());
    }

    public List<Student> dohvatiSveStudente() {
        return studentRepozitorij.vratiSve();
    }

    private void zapisi(ZapisAkcija akcija, String detalji) {
        Korisnik korisnik = Sesija.getInstanca().getPrijavljeniKorisnik();
        String email = korisnik != null ? korisnik.getEmail() : "sustav";
        UpraviteljZapisima.getInstanca().dodajZapis(
                new Zapis(email, akcija, detalji, LocalDateTime.now()));
    }
}
