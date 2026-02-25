package projekt.repozitorij;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import projekt.model.TimJson;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class TimJsonRepozitorij {

    private static final Path TIMOVI_DIR = Paths.get(
            System.getProperty("user.home"), ".studentski-sustav", "podaci"
    );
    private static final Path TIMOVI_PATH = TIMOVI_DIR.resolve("timovi.json");
    private static final Gson gson = new GsonBuilder().setPrettyPrinting().create();

    public TimJsonRepozitorij() {
        try {
            Files.createDirectories(TIMOVI_DIR);
            if (!Files.exists(TIMOVI_PATH)) {
                Files.writeString(TIMOVI_PATH, "[]");
            }
        } catch (IOException e) {
            throw new RuntimeException("Greška pri inicijalizaciji TimJsonRepozitorij", e);
        }
    }

    public List<TimJson> ucitaj() {
        try {
            List<TimJson> timovi = gson.fromJson(
                    Files.readString(TIMOVI_PATH),
                    new TypeToken<List<TimJson>>() {
                    }.getType()
            );
            return timovi != null ? timovi : new ArrayList<>();
        } catch (IOException e) {
            System.err.println("Greška pri čitanju timova: " + e.getMessage());
            return new ArrayList<>();
        }
    }

    public void spremi(List<TimJson> timovi) {
        try {
            Files.createDirectories(TIMOVI_DIR);
            Files.writeString(TIMOVI_PATH, gson.toJson(timovi));
        } catch (IOException e) {
            throw new RuntimeException("Greška pri spremanju timova", e);
        }
    }
}
