package projekt.model;

import jakarta.persistence.*;

@Entity
@Table(name = "admin")
@PrimaryKeyJoinColumn(name = "korisnik_id")
@DiscriminatorValue("ADMIN")
public class Admin extends Korisnik {

    @Column(name = "ovlasti", nullable = false, columnDefinition = "TEXT")
    private String ovlasti;

    public Admin() {
    }

    public Admin(String email, String lozinka, String ime, String prezime, String ovlasti) {
        super(email, lozinka, ime, prezime, Uloga.ADMIN);
        this.ovlasti = ovlasti;
    }

    public String getOvlasti() {
        return ovlasti;
    }

    public void setOvlasti(String ovlasti) {
        this.ovlasti = ovlasti;
    }
}