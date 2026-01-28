package projekt.model;

import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "predmet")
public class Predmet {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;

    @Column(name = "naziv", nullable = false, length = 255)
    private String naziv;

    @Column(name = "sifra", nullable = false, length = 10)
    private String sifra;

    @Column(name = "ects_bodovi", nullable = false)
    private Integer ectsBodovi;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "profesor_id")
    private Profesor profesor;

    @Enumerated(EnumType.STRING)
    @Column(name = "semestar", nullable = false, length = 20)
    private Semestar semestar;

    @Column(name = "godina_izvodenja", nullable = false)
    private Integer godinaIzvodenja;

    @OneToMany(mappedBy = "predmet", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Upis> upisi = new ArrayList<>();

    @OneToMany(mappedBy = "predmet", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Zadatak> zadaci = new ArrayList<>();

    public Predmet() {
    }

    public Predmet(String naziv, String sifra, Integer ectsBodovi, Profesor profesor,
                   Semestar semestar, Integer godinaIzvodenja) {
        this.naziv = naziv;
        this.sifra = sifra;
        this.ectsBodovi = ectsBodovi;
        this.profesor = profesor;
        this.semestar = semestar;
        this.godinaIzvodenja = godinaIzvodenja;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getNaziv() {
        return naziv;
    }

    public void setNaziv(String naziv) {
        this.naziv = naziv;
    }

    public String getSifra() {
        return sifra;
    }

    public void setSifra(String sifra) {
        this.sifra = sifra;
    }

    public Integer getEctsBodovi() {
        return ectsBodovi;
    }

    public void setEctsBodovi(Integer ectsBodovi) {
        this.ectsBodovi = ectsBodovi;
    }

    public Profesor getProfesor() {
        return profesor;
    }

    public void setProfesor(Profesor profesor) {
        this.profesor = profesor;
    }

    public Semestar getSemestar() {
        return semestar;
    }

    public void setSemestar(Semestar semestar) {
        this.semestar = semestar;
    }

    public Integer getGodinaIzvodenja() {
        return godinaIzvodenja;
    }

    public void setGodinaIzvodenja(Integer godinaIzvodenja) {
        this.godinaIzvodenja = godinaIzvodenja;
    }

    public List<Upis> getUpisi() {
        return upisi;
    }

    public void setUpisi(List<Upis> upisi) {
        this.upisi = upisi;
    }

    public List<Zadatak> getZadaci() {
        return zadaci;
    }

    public void setZadaci(List<Zadatak> zadaci) {
        this.zadaci = zadaci;
    }

    public void dodajUpis(Upis upis) {
        upisi.add(upis);
        upis.setPredmet(this);
    }

    public void dodajZadatak(Zadatak zadatak) {
        zadaci.add(zadatak);
        zadatak.setPredmet(this);
    }
}