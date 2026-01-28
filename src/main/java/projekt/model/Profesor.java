package projekt.model;

import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "profesor")
@PrimaryKeyJoinColumn(name = "korisnik_id")
@DiscriminatorValue("PROFESOR")
public class Profesor extends Korisnik {

    @Column(name = "titula", length = 50)
    private String titula;

    @OneToMany(mappedBy = "profesor", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Predmet> predmeti = new ArrayList<>();

    @OneToMany(mappedBy = "profesor", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Ocjena> ocjene = new ArrayList<>();

    public Profesor() {
    }

    public Profesor(String email, String lozinkaHash, String ime, String prezime, String titula) {
        super(email, lozinkaHash, ime, prezime, Uloga.PROFESOR);
        this.titula = titula;
    }

    public String getTitula() {
        return titula;
    }

    public void setTitula(String titula) {
        this.titula = titula;
    }

    public List<Predmet> getPredmeti() {
        return predmeti;
    }

    public void setPredmeti(List<Predmet> predmeti) {
        this.predmeti = predmeti;
    }

    public List<Ocjena> getOcjene() {
        return ocjene;
    }

    public void setOcjene(List<Ocjena> ocjene) {
        this.ocjene = ocjene;
    }

    public void dodajPredmet(Predmet predmet) {
        predmeti.add(predmet);
        predmet.setProfesor(this);
    }

    public void dodajOcjenu(Ocjena ocjena) {
        ocjene.add(ocjena);
        ocjena.setProfesor(this);
    }

    public String getPunoImeSTitulom() {
        return titula + " " + getIme() + " " + getPrezime();
    }
}