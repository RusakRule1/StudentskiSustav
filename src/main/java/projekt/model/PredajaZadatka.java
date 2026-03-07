package projekt.model;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "predaja_zadatka", uniqueConstraints = @UniqueConstraint(columnNames = {"zadatak_id", "student_id"}))
public class PredajaZadatka {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "zadatak_id", nullable = false)
    private Zadatak zadatak;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "student_id", nullable = false)
    private Student student;

    @Column(name = "naziv_datoteke", nullable = false, length = 255)
    private String nazivDatoteke;

    @Column(name = "tip_datoteke", nullable = false, length = 50)
    private String tipDatoteke;

    @Column(name = "velicina_datoteke", nullable = false)
    private Long velicinaDatoteke;

    @Lob
    @Basic(fetch = FetchType.LAZY)
    @Column(name = "predana_datoteka", nullable = false)
    private byte[] predanaDatoteka;

    @Column(name = "datum_predaje", nullable = false)
    private LocalDateTime datumPredaje;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 50)
    private StatusPredaje status = StatusPredaje.PREDANO;

    @OneToOne(mappedBy = "predaja", cascade = CascadeType.ALL, orphanRemoval = true)
    private Ocjena ocjena;

    public PredajaZadatka() {
        this.datumPredaje = LocalDateTime.now();
    }

    public PredajaZadatka(Zadatak zadatak, Student student, String nazivDatoteke,
                          String tipDatoteke, Long velicinaDatoteke, byte[] predanaDatoteka) {
        this();
        this.zadatak = zadatak;
        this.student = student;
        this.nazivDatoteke = nazivDatoteke;
        this.tipDatoteke = tipDatoteke;
        this.velicinaDatoteke = velicinaDatoteke;
        this.predanaDatoteka = predanaDatoteka;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Zadatak getZadatak() {
        return zadatak;
    }

    public void setZadatak(Zadatak zadatak) {
        this.zadatak = zadatak;
    }

    public Student getStudent() {
        return student;
    }

    public void setStudent(Student student) {
        this.student = student;
    }

    public String vratiNazivDatoteke() {
        return nazivDatoteke;
    }

    public void setNazivDatoteke(String nazivDatoteke) {
        this.nazivDatoteke = nazivDatoteke;
    }

    public String getTipDatoteke() {
        return tipDatoteke;
    }

    public void setTipDatoteke(String tipDatoteke) {
        this.tipDatoteke = tipDatoteke;
    }

    public Long getVelicinaDatoteke() {
        return velicinaDatoteke;
    }

    public void setVelicinaDatoteke(Long velicinaDatoteke) {
        this.velicinaDatoteke = velicinaDatoteke;
    }

    public byte[] getPredanaDatoteka() {
        return predanaDatoteka;
    }

    public void setPredanaDatoteka(byte[] predanaDatoteka) {
        this.predanaDatoteka = predanaDatoteka;
    }

    public LocalDateTime getDatumPredaje() {
        return datumPredaje;
    }

    public void setDatumPredaje(LocalDateTime datumPredaje) {
        this.datumPredaje = datumPredaje;
    }

    public StatusPredaje getStatus() {
        return status;
    }

    public void setStatus(StatusPredaje status) {
        this.status = status;
    }

    public Ocjena getOcjena() {
        return ocjena;
    }

    public void setOcjena(Ocjena ocjena) {
        this.ocjena = ocjena;
        if (ocjena != null) {
            this.status = StatusPredaje.OCJENJENO;
            ocjena.setPredaja(this);
        }
    }

    public boolean jeLiOcjenjeno() {
        return status == StatusPredaje.OCJENJENO;
    }
}