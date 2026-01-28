package projekt.model;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "upis",
        uniqueConstraints = @UniqueConstraint(columnNames = {"student_id", "predmet_id"}))
public class Upis {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "student_id", nullable = false)
    private Student student;
 
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "predmet_id", nullable = false)
    private Predmet predmet;

    @Column(name = "datum_upisa", nullable = false)
    private LocalDateTime datumUpisa;

    @Column(name = "polozen", nullable = false)
    private Boolean polozen = false;

    public Upis() {
        this.datumUpisa = LocalDateTime.now();
    }

    public Upis(Student student, Predmet predmet) {
        this();
        this.student = student;
        this.predmet = predmet;
    }

    public Upis(Student student, Predmet predmet, Boolean polozen) {
        this(student, predmet);
        this.polozen = polozen;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Student getStudent() {
        return student;
    }

    public void setStudent(Student student) {
        this.student = student;
    }

    public Predmet getPredmet() {
        return predmet;
    }

    public void setPredmet(Predmet predmet) {
        this.predmet = predmet;
    }

    public LocalDateTime getDatumUpisa() {
        return datumUpisa;
    }

    public void setDatumUpisa(LocalDateTime datumUpisa) {
        this.datumUpisa = datumUpisa;
    }

    public Boolean getPolozen() {
        return polozen;
    }

    public void setPolozen(Boolean polozen) {
        this.polozen = polozen;
    }
}