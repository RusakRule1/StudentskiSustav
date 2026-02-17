package projekt.model;

public enum Jezik {
    HR("HR"),
    EN("EN");

    private final String kod;

    Jezik(String kod) {
        this.kod = kod;
    }

    public String getKod() {
        return kod;
    }
}
