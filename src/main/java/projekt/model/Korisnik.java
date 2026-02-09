package projekt.model;

import jakarta.persistence.*;
import projekt.util.AES;
import projekt.util.Hash;
import projekt.util.RSA;

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

    @Column(name = "sifrirano_ime", nullable = false, length = 500)
    private String sifriranoIme;

    @Column(name = "sifrirano_prezime", nullable = false, length = 500)
    private String sifriranoPrezime;

    @Enumerated(EnumType.STRING)
    @Column(name = "uloga", nullable = false, length = 50, insertable = false, updatable = false)
    private Uloga uloga;

    public Korisnik() {
    }

    public Korisnik(String email, String lozinka, String ime, String prezime, Uloga uloga) {
        this.email = email;
        this.lozinkaHash = Hash.hashirajLozinku(lozinka, email);
        this.sifriranoIme = AES.sifriraj(ime);
        this.sifriranoPrezime = RSA.getInstance().sifriraj(prezime);
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
        if (sifriranoIme == null || sifriranoIme.trim().isEmpty()) {
            return sifriranoIme;
        }
        return AES.desifriraj(sifriranoIme);
    }

    public void setIme(String ime) {
        this.sifriranoIme = ime;
    }

    public String getPrezime() {
        if (sifriranoPrezime == null || sifriranoPrezime.trim().isEmpty()) {
            return sifriranoPrezime;
        }
        return RSA.getInstance().desifriraj(sifriranoPrezime);
    }

    public void setPrezime(String prezime) {
        this.sifriranoPrezime = prezime;
    }

    public Uloga getUloga() {
        return uloga;
    }

    public void setUloga(Uloga uloga) {
        this.uloga = uloga;
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
                ", ime='" + getIme() + '\'' +
                ", prezime='" + getPrezime() + '\'' +
                ", uloga=" + uloga +
                '}';
    }
}
