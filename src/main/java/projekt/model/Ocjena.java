package projekt.model;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "ocjena")
public class Ocjena {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;

    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "predaja_id", nullable = false, unique = true)
    private PredajaZadatka predaja;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "profesor_id", nullable = false)
    private Profesor profesor;

    @Column(name = "vrijednost", nullable = false)
    private Integer vrijednost;

    @Column(name = "komentar", nullable = false, columnDefinition = "TEXT")
    private String komentar;

    @Column(name = "datum_ocjenjivanja", nullable = false)
    private LocalDateTime datumOcjenjivanja;

    public Ocjena() {
        this.datumOcjenjivanja = LocalDateTime.now();
    }

    public Ocjena(PredajaZadatka predaja, Profesor profesor, Integer vrijednost, String komentar) {
        this();
        this.predaja = predaja;
        this.profesor = profesor;
        setVrijednost(vrijednost);
        this.komentar = komentar;
        if (predaja != null) {
            predaja.setStatus(StatusPredaje.OCJENJENO);
        }
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public PredajaZadatka getPredaja() {
        return predaja;
    }

    public void setPredaja(PredajaZadatka predaja) {
        this.predaja = predaja;
        if (predaja != null) {
            predaja.setStatus(StatusPredaje.OCJENJENO);
        }
    }

    public Profesor getProfesor() {
        return profesor;
    }

    public void setProfesor(Profesor profesor) {
        this.profesor = profesor;
    }

    public Integer getVrijednost() {
        return vrijednost;
    }

    public void setVrijednost(Integer vrijednost) {
        if (vrijednost < 1 || vrijednost > 5) {
            throw new IllegalArgumentException("Ocjena mora biti između 1 i 5");
        }
        this.vrijednost = vrijednost;
    }

    public String getKomentar() {
        return komentar;
    }

    public void setKomentar(String komentar) {
        this.komentar = komentar;
    }

    public LocalDateTime getDatumOcjenjivanja() {
        return datumOcjenjivanja;
    }

    public void setDatumOcjenjivanja(LocalDateTime datumOcjenjivanja) {
        this.datumOcjenjivanja = datumOcjenjivanja;
    }
}