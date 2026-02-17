package projekt.servis;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import projekt.model.TimJson;

import java.io.IOException;
import java.lang.reflect.Type;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class TimJsonServis {

    private static final String TIMOVI_DIR = "podaciProfesora";
    private static final String TIMOVI_FILE = "timovi.json";
    private static final Path TIMOVI_PATH = Paths.get(TIMOVI_DIR, TIMOVI_FILE);
    private static final Gson gson = new GsonBuilder().setPrettyPrinting().create();

    public TimJsonServis() {
        try {
            Files.createDirectories(Paths.get(TIMOVI_DIR));
            if (!Files.exists(TIMOVI_PATH)) {
                Files.write(TIMOVI_PATH, "[]".getBytes());
            }
        } catch (IOException e) {
            throw new RuntimeException("Greška pri inicijalizaciji TimJsonServis", e);
        }
    }

    public List<TimJson> dohvatiSveTimove() {
        try {
            String json = new String(Files.readAllBytes(TIMOVI_PATH));
            Type tipListe = new TypeToken<List<TimJson>>() {
            }.getType();
            List<TimJson> timovi = gson.fromJson(json, tipListe);
            return timovi != null ? timovi : new ArrayList<>();
        } catch (IOException e) {
            System.err.println("Greška pri čitanju timova: " + e.getMessage());
            return new ArrayList<>();
        }
    }

    public boolean spremiTim(TimJson tim) {
        try {
            List<TimJson> timovi = dohvatiSveTimove();
            timovi.add(tim);
            return spremiSveTimove(timovi);
        } catch (Exception e) {
            System.err.println("Greška pri spremanju tima: " + e.getMessage());
            return false;
        }
    }

    public boolean azurirajTim(TimJson azuriraniTim) {
        try {
            List<TimJson> timovi = dohvatiSveTimove();

            for (int i = 0; i < timovi.size(); i++) {
                if (timovi.get(i).getId().equals(azuriraniTim.getId())) {
                    timovi.set(i, azuriraniTim);
                    return spremiSveTimove(timovi);
                }
            }
            return false;
        } catch (Exception e) {
            System.err.println("Greška pri ažuriranju tima: " + e.getMessage());
            return false;
        }
    }

    public boolean obrisiTim(String timId) {
        try {
            List<TimJson> timovi = dohvatiSveTimove();
            boolean uklonjen = timovi.removeIf(tim -> tim.getId().equals(timId));
            if (uklonjen) {
                return spremiSveTimove(timovi);
            }
            return false;
        } catch (Exception e) {
            System.err.println("Greška pri brisanju tima: " + e.getMessage());
            return false;
        }
    }

    private boolean spremiSveTimove(List<TimJson> timovi) throws IOException {
        String json = gson.toJson(timovi);
        Files.write(TIMOVI_PATH, json.getBytes());
        return true;
    }
}