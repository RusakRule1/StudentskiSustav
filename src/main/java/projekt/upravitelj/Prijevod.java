package projekt.upravitelj;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import projekt.adapter.JNIAdapter;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;

public class Prijevod {

    private static Prijevod instanca;
    private final Map<String, Map<String, String>> prijevodi = new HashMap<>();
    private final JNIAdapter jniAdapter;
    private final Gson gson;

    private Prijevod() {
        this.jniAdapter = new JNIAdapter();
        this.gson = new Gson();
        ucitajPrijevode();
    }

    public static synchronized Prijevod getInstanca() {
        if (instanca == null) {
            instanca = new Prijevod();
        }
        return instanca;
    }

    private void ucitajPrijevode() {
        try {
            String jsonText = jniAdapter.ucitajSvePrijevode();

            if (jsonText == null || jsonText.trim().isEmpty()) {
                System.err.println("JNI adapter vratio prazan JSON");
                return;
            }
            Type type = new TypeToken<Map<String, Map<String, String>>>() {
            }.getType();
            Map<String, Map<String, String>> ucitaniPrijevodi = gson.fromJson(jsonText, type);

            if (ucitaniPrijevodi != null) {
                prijevodi.putAll(ucitaniPrijevodi);
                System.out.println("Prijevodi uspješno učitani iz JNI-a (" + prijevodi.size() + " ključeva)");
            } else {
                System.err.println("Greška pri parsiranju JSON-a");
            }
        } catch (Exception e) {
            System.err.println("Greška pri učitavanju prijevoda: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public String getPrijevod(String kljuc) {
        String aktivniJezik = Konfiguracija.getInstanca().getJezik();
        return getPrijevod(kljuc, aktivniJezik);
    }

    public String getPrijevod(String kljuc, String jezik) {
        if (kljuc == null || kljuc.trim().isEmpty()) return "";

        Map<String, String> mapZaKljuc = prijevodi.get(kljuc);
        if (mapZaKljuc == null) {
            System.err.println("Prijevod za ključ '" + kljuc + "' nije pronađen");
            return kljuc;
        }

        String prijevod = mapZaKljuc.get(jezik);
        if (prijevod != null) return prijevod;

        String defaultniJezik = Konfiguracija.getPodrazumijevaniJezik();
        if (!jezik.equals(defaultniJezik)) {
            prijevod = mapZaKljuc.get(defaultniJezik);
            if (prijevod != null) return prijevod;
        }

        return kljuc;
    }
}
