package projekt.model;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

public class TimJson {
    private String id;
    private String naziv;
    private List<StudentJson> clanovi = new ArrayList<>();

    public TimJson() {
        this.id = UUID.randomUUID().toString();
    }

    public TimJson(String naziv) {
        this();
        this.naziv = naziv;
    }

    public TimJson(String naziv, List<StudentJson> clanovi) {
        this();
        this.naziv = naziv;
        this.clanovi = clanovi != null ? clanovi : new ArrayList<>();
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getNaziv() {
        return naziv;
    }

    public void setNaziv(String naziv) {
        this.naziv = naziv;
    }

    public List<StudentJson> getClanovi() {
        return clanovi;
    }

    public void setClanovi(List<StudentJson> clanovi) {
        this.clanovi = clanovi;
    }

    public void dodajClana(StudentJson student) {
        if (!clanovi.contains(student)) {
            clanovi.add(student);
        }
    }

    public void ukloniClana(StudentJson student) {
        clanovi.remove(student);
    }

    public boolean sadrziStudenta(String jmbag) {
        return clanovi.stream().anyMatch(s -> s.getJmbag().equals(jmbag));
    }

    public int getBrojClanova() {
        return clanovi != null ? clanovi.size() : 0;
    }

    public String getClanoviFormatted() {
        if (clanovi == null || clanovi.isEmpty()) {
            return "-";
        }
        return clanovi.stream()
                .map(clan -> clan.getIme() + " " + clan.getPrezime())
                .collect(Collectors.joining(", "));
    }

    @Override
    public String toString() {
        return naziv + " (" + getBrojClanova() + " članova)";
    }
}