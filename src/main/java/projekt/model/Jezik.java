package projekt.model;

import java.util.stream.Stream;

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

    public static Jezik fromKod(String kod) {
        return Stream.of(values())
                .filter(j -> j.getKod().equals(kod))
                .findFirst()
                .orElse(HR);
    }
}
