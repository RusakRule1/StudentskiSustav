package projekt.model;

import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "student")
@PrimaryKeyJoinColumn(name = "korisnik_id")
@DiscriminatorValue("STUDENT")
public class Student extends Korisnik {

    @Column(name = "jmbag", unique = true, nullable = false, length = 10)
    private String jmbag;

    @OneToMany(mappedBy = "student", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Upis> upisaniPredmeti = new ArrayList<>();

    @OneToMany(mappedBy = "student", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<PredajaZadatka> predaniZadaci = new ArrayList<>();

    public Student() {
    }

    public Student(String email, String lozinka, String ime, String prezime, String jmbag) {
        super(email, lozinka, ime, prezime, Uloga.STUDENT);
        this.jmbag = jmbag;
    }

    public String getJmbag() {
        return jmbag;
    }

    public void setJmbag(String jmbag) {
        this.jmbag = jmbag;
    }

    public List<Upis> getUpisaniPredmeti() {
        return upisaniPredmeti;
    }

    public void setUpisaniPredmeti(List<Upis> upisaniPredmeti) {
        this.upisaniPredmeti = upisaniPredmeti;
    }

    public List<PredajaZadatka> getPredaniZadaci() {
        return predaniZadaci;
    }

    public void setPredaniZadaci(List<PredajaZadatka> predaniZadaci) {
        this.predaniZadaci = predaniZadaci;
    }

    public void dodajUpis(Upis upisanPredmet) {
        upisaniPredmeti.add(upisanPredmet);
        upisanPredmet.setStudent(this);
    }

    public void dodajPredaju(PredajaZadatka predajaZadatka) {
        predaniZadaci.add(predajaZadatka);
        predajaZadatka.setStudent(this);
    }
}
