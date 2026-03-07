package projekt.model;

public enum ZapisAkcija {
    PRIJAVA("Prijava u sustav"),
    ODJAVA("Odjava iz sustava"),
    KREIRAN_KORISNIK("Kreiran novi korisnik"),
    OBJAVLJEN_ZADATAK("Objavljen zadatak"),
    IZMIJENJEN_ZADATAK("Izmijenjen zadatak"),
    OBRISAN_ZADATAK("Obrisan zadatak"),
    PREDANO_RJESENJE("Predano rješenje"),
    OCJENJENO_RJESENJE("Ocijenjeno rješenje"),
    OBRISANA_OCJENA("Obrisana ocjena");

    private final String opis;

    ZapisAkcija(String opis) {
        this.opis = opis;
    }

    public String getOpis() {
        return opis;
    }
}
