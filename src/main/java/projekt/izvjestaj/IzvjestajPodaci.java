package projekt.izvjestaj;

import java.util.List;

public class IzvjestajPodaci {

    public static final int USPJEH = 0;
    public static final int GRESKA_ARGUMENTI = 1;
    public static final int GRESKA_CITANJE = 2;
    public static final int GRESKA_PARSIRANJE = 3;
    public static final int GRESKA_GENERIRANJE = 4;

    private PredmetPodaci predmet;
    private List<StudentPodaci> studenti;
    private Labele labele;

    public IzvjestajPodaci() {
    }

    public IzvjestajPodaci(PredmetPodaci predmet, List<StudentPodaci> studenti, Labele labele) {
        this.predmet = predmet;
        this.studenti = studenti;
        this.labele = labele;
    }

    public PredmetPodaci getPredmet() {
        return predmet;
    }

    public void setPredmet(PredmetPodaci predmet) {
        this.predmet = predmet;
    }

    public List<StudentPodaci> getStudenti() {
        return studenti;
    }

    public void setStudenti(List<StudentPodaci> studenti) {
        this.studenti = studenti;
    }

    public Labele getLabele() {
        return labele;
    }

    public void setLabele(Labele labele) {
        this.labele = labele;
    }

    public static class PredmetPodaci {
        private String naziv;
        private String sifra;
        private Integer ectsBodovi;
        private String semestar;
        private Integer godinaIzvodenja;
        private String profesor;

        public PredmetPodaci() {
        }

        public PredmetPodaci(String naziv, String sifra, Integer ectsBodovi,
                             String semestar, Integer godinaIzvodenja, String profesor) {
            this.naziv = naziv;
            this.sifra = sifra;
            this.ectsBodovi = ectsBodovi;
            this.semestar = semestar;
            this.godinaIzvodenja = godinaIzvodenja;
            this.profesor = profesor;
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

        public String getSemestar() {
            return semestar;
        }

        public void setSemestar(String semestar) {
            this.semestar = semestar;
        }

        public Integer getGodinaIzvodenja() {
            return godinaIzvodenja;
        }

        public void setGodinaIzvodenja(Integer godinaIzvodenja) {
            this.godinaIzvodenja = godinaIzvodenja;
        }

        public String getProfesor() {
            return profesor;
        }

        public void setProfesor(String profesor) {
            this.profesor = profesor;
        }
    }

    public static class StudentPodaci {
        private String ime;
        private String prezime;
        private String jmbag;
        private Integer godinaStudija;

        public StudentPodaci() {
        }

        public StudentPodaci(String ime, String prezime, String jmbag, Integer godinaStudija) {
            this.ime = ime;
            this.prezime = prezime;
            this.jmbag = jmbag;
            this.godinaStudija = godinaStudija;
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

        public String getJmbag() {
            return jmbag;
        }

        public void setJmbag(String jmbag) {
            this.jmbag = jmbag;
        }

        public Integer getGodinaStudija() {
            return godinaStudija;
        }

        public void setGodinaStudija(Integer godinaStudija) {
            this.godinaStudija = godinaStudija;
        }
    }

    public static class Labele {
        private String naslov;
        private String sifra;
        private String ects;
        private String semestar;
        private String godinaIzvodenja;
        private String profesor;
        private String upisaniStudenti;
        private String kolonaIme;
        private String kolonaPrezime;
        private String kolonaJmbag;
        private String kolonaGodina;

        public Labele() {
        }

        public Labele(String naslov, String sifra, String ects, String semestar,
                      String godinaIzvodenja, String profesor, String upisaniStudenti,
                      String kolonaIme, String kolonaPrezime, String kolonaJmbag, String kolonaGodina) {
            this.naslov = naslov;
            this.sifra = sifra;
            this.ects = ects;
            this.semestar = semestar;
            this.godinaIzvodenja = godinaIzvodenja;
            this.profesor = profesor;
            this.upisaniStudenti = upisaniStudenti;
            this.kolonaIme = kolonaIme;
            this.kolonaPrezime = kolonaPrezime;
            this.kolonaJmbag = kolonaJmbag;
            this.kolonaGodina = kolonaGodina;
        }

        public String getNaslov() {
            return naslov;
        }

        public void setNaslov(String naslov) {
            this.naslov = naslov;
        }

        public String getSifra() {
            return sifra;
        }

        public void setSifra(String sifra) {
            this.sifra = sifra;
        }

        public String getEcts() {
            return ects;
        }

        public void setEcts(String ects) {
            this.ects = ects;
        }

        public String getSemestar() {
            return semestar;
        }

        public void setSemestar(String semestar) {
            this.semestar = semestar;
        }

        public String getGodinaIzvodenja() {
            return godinaIzvodenja;
        }

        public void setGodinaIzvodenja(String godinaIzvodenja) {
            this.godinaIzvodenja = godinaIzvodenja;
        }

        public String getProfesor() {
            return profesor;
        }

        public void setProfesor(String profesor) {
            this.profesor = profesor;
        }

        public String getUpisaniStudenti() {
            return upisaniStudenti;
        }

        public void setUpisaniStudenti(String upisaniStudenti) {
            this.upisaniStudenti = upisaniStudenti;
        }

        public String getKolonaIme() {
            return kolonaIme;
        }

        public void setKolonaIme(String kolonaIme) {
            this.kolonaIme = kolonaIme;
        }

        public String getKolonaPrezime() {
            return kolonaPrezime;
        }

        public void setKolonaPrezime(String kolonaPrezime) {
            this.kolonaPrezime = kolonaPrezime;
        }

        public String getKolonaJmbag() {
            return kolonaJmbag;
        }

        public void setKolonaJmbag(String kolonaJmbag) {
            this.kolonaJmbag = kolonaJmbag;
        }

        public String getKolonaGodina() {
            return kolonaGodina;
        }

        public void setKolonaGodina(String kolonaGodina) {
            this.kolonaGodina = kolonaGodina;
        }
    }
}
