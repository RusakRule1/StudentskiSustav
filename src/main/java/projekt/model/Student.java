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

    @Column(name = "godina_studija", nullable = false)
    private Integer godinaStudija;

    @OneToMany(mappedBy = "student", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Upis> upisaniPredmeti = new ArrayList<>();

    @OneToMany(mappedBy = "student", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<PredajaZadatka> predaniZadaci = new ArrayList<>();

    public Student() {
    }

    public Student(String email, String lozinka, String ime, String prezime, String jmbag, Integer godinaStudija) {
        super(email, lozinka, ime, prezime, Uloga.STUDENT);
        this.jmbag = jmbag;
        this.godinaStudija = godinaStudija;
    }

    public String getJmbag() {
        return jmbag;
    }

    public void setJmbag(String jmbag) {
        this.jmbag = jmbag;
    }

    public Integer getGodinaStudija() {
        return godinaStudija;
    }

    public void setGodinaStudija(Integer godinaStudija) {
        this.godinaStudija = godinaStudija;
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
