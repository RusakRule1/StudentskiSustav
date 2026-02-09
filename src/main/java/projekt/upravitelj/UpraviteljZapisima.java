package projekt.upravitelj;

import projekt.model.Zapis;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class UpraviteljZapisima {

    private static final String ZAPIS_DIR = "zapisi";
    private static final String ZAPIS_FILE = "zapisi.dat";
    private static final Path ZAPIS_PATH = Paths.get(ZAPIS_DIR, ZAPIS_FILE);

    private static UpraviteljZapisima instance;

    private UpraviteljZapisima() {
        try {
            if (!Files.exists(Paths.get(ZAPIS_DIR))) {
                Files.createDirectories(Paths.get(ZAPIS_DIR));
            }
        } catch (IOException e) {
            throw new RuntimeException("Greška pri kreiranju log direktorija", e);
        }
    }

    public static synchronized UpraviteljZapisima getInstance() {
        if (instance == null) {
            instance = new UpraviteljZapisima();
        }
        return instance;
    }

    public void dodajZapis(Zapis zapis) {
        try {
            boolean datotekaPostoji = Files.exists(ZAPIS_PATH);
            long velicina = datotekaPostoji ? Files.size(ZAPIS_PATH) : 0;
            boolean trebaHeader = velicina == 0;
            try (FileOutputStream fos = new FileOutputStream(ZAPIS_PATH.toFile(), true)) {
                ObjectOutputStream oos;
                if (trebaHeader) {
                    oos = new ObjectOutputStream(fos);
                } else {
                    oos = new AppendableObjectOutputStream(fos);
                }
                oos.writeObject(zapis);
            }
        } catch (IOException e) {
            System.err.println("Greška pri dodavanju zapisa: " + e.getMessage());
        }
    }

    public List<Zapis> ucitajSveZapise() {
        List<Zapis> zapisi = new ArrayList<>();

        if (!Files.exists(ZAPIS_PATH)) {
            return zapisi;
        }
        try (FileInputStream fis = new FileInputStream(ZAPIS_PATH.toFile());
             ObjectInputStream ois = new ObjectInputStream(fis)) {
            while (true) {
                try {
                    Zapis log = (Zapis) ois.readObject();
                    zapisi.add(log);
                } catch (EOFException e) {
                    break;
                }
            }
        } catch (IOException | ClassNotFoundException e) {
            System.err.println("Greška pri čitanju logova: " + e.getMessage());
        }
        return zapisi;
    }


    private static class AppendableObjectOutputStream extends ObjectOutputStream {

        public AppendableObjectOutputStream(OutputStream out) throws IOException {
            super(out);
        }

        @Override
        protected void writeStreamHeader() throws IOException {
            reset();
        }
    }
}