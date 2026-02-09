package projekt.model;

public enum ZapisAkcija {
    PRIJAVA("Prijava u sustav"),
    ODJAVA("Odjava iz sustava"),
    PREDAN_ZADATAK("Predan zadatak"),
    OBJAVLJEN_ZADATAK("Objavljen zadatak"),
    OCJENJEN_ZADATAK("Ocjenjen zadatak"),
    KREIRAN_KORISNIK("Kreiran novi korisnik"),
    IZMJENJEN_KORISNIK("Izmjenjen korisnik"),
    BRISANJE_KORISNIKA("Brisanje korisnika");
    
    private final String opis;

    ZapisAkcija(String opis) {
        this.opis = opis;
    }

    public String getOpis() {
        return opis;
    }
}
