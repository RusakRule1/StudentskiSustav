package projekt.model;

import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "zadatak")
public class Zadatak {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;

    @Column(name = "naziv", nullable = false, length = 255)
    private String naziv;

    @Column(name = "opis", nullable = false, columnDefinition = "TEXT")
    private String opis;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "predmet_id", nullable = false)
    private Predmet predmet;

    @Column(name = "rok_predaje", nullable = false)
    private LocalDateTime rokPredaje;

    @Column(name = "datum_objave", nullable = false)
    private LocalDateTime datumObjave;

    @OneToMany(mappedBy = "zadatak", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<PredajaZadatka> predaje = new ArrayList<>();

    public Zadatak() {
        this.datumObjave = LocalDateTime.now();
    }

    public Zadatak(String naziv, String opis, Predmet predmet, LocalDateTime rokPredaje) {
        this();
        this.naziv = naziv;
        this.opis = opis;
        this.predmet = predmet;
        this.rokPredaje = rokPredaje;
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

    public String getOpis() {
        return opis;
    }

    public void setOpis(String opis) {
        this.opis = opis;
    }

    public Predmet getPredmet() {
        return predmet;
    }

    public void setPredmet(Predmet predmet) {
        this.predmet = predmet;
    }

    public LocalDateTime getRokPredaje() {
        return rokPredaje;
    }

    public void setRokPredaje(LocalDateTime rokPredaje) {
        this.rokPredaje = rokPredaje;
    }

    public LocalDateTime getDatumObjave() {
        return datumObjave;
    }

    public void setDatumObjave(LocalDateTime datumObjave) {
        this.datumObjave = datumObjave;
    }

    public List<PredajaZadatka> getPredaje() {
        return predaje;
    }

    public void setPredaje(List<PredajaZadatka> predaje) {
        this.predaje = predaje;
    }

    public void dodajPredaju(PredajaZadatka predaja) {
        predaje.add(predaja);
        predaja.setZadatak(this);
    }

    public boolean jeLiKasnio() {
        return LocalDateTime.now().isAfter(rokPredaje);
    }

    public int vratiBrojPredanih() {
        return predaje.size();
    }
}