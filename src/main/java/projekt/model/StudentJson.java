package projekt.model;

public class StudentJson {
    private String jmbag;
    private String email;
    private String ime;
    private String prezime;

    public StudentJson() {
    }

    public StudentJson(String jmbag, String email, String ime, String prezime) {
        this.jmbag = jmbag;
        this.email = email;
        this.ime = ime;
        this.prezime = prezime;
    }

    public String getJmbag() {
        return jmbag;
    }

    public void setJmbag(String jmbag) {
        this.jmbag = jmbag;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
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

    public String getImePrezime() {
        return ime + " " + prezime;
    }

    @Override
    public String toString() {
        return getImePrezime() + " (" + email + ")";
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        StudentJson that = (StudentJson) obj;
        return jmbag.equals(that.jmbag);
    }

    @Override
    public int hashCode() {
        return jmbag.hashCode();
    }
}