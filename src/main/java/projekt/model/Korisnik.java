package projekt.model;

import jakarta.persistence.*;

import java.util.Objects;

@Entity
@Table(name = "korisnik")
@Inheritance(strategy = InheritanceType.JOINED)
@DiscriminatorColumn(name = "uloga", discriminatorType = DiscriminatorType.STRING)
public class Korisnik {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;

    @Column(name = "email", unique = true, nullable = false, length = 255)
    private String email;

    @Column(name = "lozinka_hash", nullable = false, length = 255)
    private String lozinkaHash;

    @Column(name = "ime", nullable = false, length = 100)
    private String ime;

    @Column(name = "prezime", nullable = false, length = 100)
    private String prezime;

    @Enumerated(EnumType.STRING)
    @Column(name = "uloga", nullable = false, length = 50, insertable = false, updatable = false)
    private Uloga uloga;

    public Korisnik() {
    }

    public Korisnik(String email, String lozinkaHash, String ime, String prezime, Uloga uloga) {
        this.email = email;
        this.lozinkaHash = lozinkaHash;
        this.ime = ime;
        this.prezime = prezime;
        this.uloga = uloga;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getLozinkaHash() {
        return lozinkaHash;
    }

    public void setLozinkaHash(String lozinkaHash) {
        this.lozinkaHash = lozinkaHash;
    }

    public String getIme() {
        return ime;
    }

    public void setIme(String ime) {
        this.ime = ime;
    }

    public String getPrezime() {
        return prezime;
    }

    public void setPrezime(String prezime) {
        this.prezime = prezime;
    }

    public Uloga getUloga() {
        return uloga;
    }

    public void setUloga(Uloga uloga) {
        this.uloga = uloga;
    }

    public String vratiPunoIme() {
        return ime + " " + prezime;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Korisnik korisnik = (Korisnik) o;
        return Objects.equals(id, korisnik.id) && Objects.equals(email, korisnik.email);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, email);
    }

    @Override
    public String toString() {
        return "Korisnik{" +
                "id=" + id +
                ", email='" + email + '\'' +
                ", ime='" + ime + '\'' +
                ", prezime='" + prezime + '\'' +
                ", uloga=" + uloga +
                '}';
    }
}
