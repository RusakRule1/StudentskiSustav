package projekt.servis;

import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.JAXBException;
import jakarta.xml.bind.Marshaller;
import jakarta.xml.bind.Unmarshaller;
import projekt.model.MaterijalXML;
import projekt.model.PodaciXML;
import projekt.model.PredmetXML;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class MaterijalXMLServis {

    private static final String MATERIJALI_DIR = "podaciProfesora";
    private static final String MATERIJALI_FILE = "materijali.xml";
    private static final Path MATERIJALI_PATH = Paths.get(MATERIJALI_DIR, MATERIJALI_FILE);

    private final JAXBContext jaxbContext;
    private PodaciXML podaciXML;

    public MaterijalXMLServis() {
        try {
            jaxbContext = JAXBContext.newInstance(PodaciXML.class);
            ucitajPodatke();
        } catch (JAXBException e) {
            throw new RuntimeException("Greška pri inicijalizaciji JAXB konteksta", e);
        }
    }

    public List<MaterijalXML> dohvatiMaterijaleZaPredmet(Integer predmetId) {
        if (predmetId == null || podaciXML == null) {
            return new ArrayList<>();
        }

        PredmetXML predmet = pronadjiPredmetXml(predmetId);
        if (predmet == null) {
            return new ArrayList<>();
        }

        return new ArrayList<>(predmet.getMaterijali());
    }

    public boolean dodajMaterijalZaPredmet(Integer predmetId, MaterijalXML materijal, String nazivPredmeta) {
        if (predmetId == null || materijal == null || nazivPredmeta == null || nazivPredmeta.trim().isEmpty()) {
            return false;
        }

        PredmetXML predmet = pronadjiPredmetXml(predmetId);
        if (predmet == null) {
            predmet = new PredmetXML();
            predmet.setId(predmetId.toString());
            predmet.setNaziv(nazivPredmeta);
            podaciXML.getPredmeti().add(predmet);
        }

        if (materijal.getId() == null || materijal.getId().isEmpty()) {
            materijal.setId(java.util.UUID.randomUUID().toString());
        }

        predmet.getMaterijali().add(materijal);
        spremiPodatke();
        return true;
    }

    public boolean dodajMaterijalZaPostojeciPredmet(Integer predmetId, MaterijalXML materijal) {
        if (predmetId == null || materijal == null) {
            return false;
        }

        PredmetXML predmet = pronadjiPredmetXml(predmetId);
        if (predmet == null) {
            return false;
        }

        if (materijal.getId() == null || materijal.getId().isEmpty()) {
            materijal.setId(java.util.UUID.randomUUID().toString());
        }

        predmet.getMaterijali().add(materijal);
        spremiPodatke();
        return true;
    }

    public boolean azurirajMaterijal(Integer predmetId, String materijalId, MaterijalXML azuriraniMaterijal) {
        if (predmetId == null || materijalId == null || azuriraniMaterijal == null) {
            return false;
        }

        PredmetXML predmet = pronadjiPredmetXml(predmetId);
        if (predmet == null) {
            return false;
        }

        for (MaterijalXML m : predmet.getMaterijali()) {
            if (m.getId().equals(materijalId)) {
                m.setNaziv(azuriraniMaterijal.getNaziv());
                m.setTip(azuriraniMaterijal.getTip());
                spremiPodatke();
                return true;
            }
        }

        return false;
    }

    public boolean izbrisiMaterijal(Integer predmetId, String materijalId) {
        if (predmetId == null || materijalId == null) {
            return false;
        }

        PredmetXML predmet = pronadjiPredmetXml(predmetId);
        if (predmet == null) {
            return false;
        }

        boolean uklonjen = predmet.getMaterijali().removeIf(m -> m.getId().equals(materijalId));

        if (uklonjen) {
            if (predmet.getMaterijali().isEmpty()) {
                podaciXML.getPredmeti().removeIf(p -> p.getId().equals(predmetId.toString()));
            }
            spremiPodatke();
        }

        return uklonjen;
    }

    public boolean predmetPostojiUXml(Integer predmetId) {
        if (predmetId == null) {
            return false;
        }
        return pronadjiPredmetXml(predmetId) != null;
    }

    private void ucitajPodatke() {
        try {
            File file = MATERIJALI_PATH.toFile();

            if (file.exists() && file.length() > 0) {
                Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();
                podaciXML = (PodaciXML) unmarshaller.unmarshal(file);
            } else {
                podaciXML = new PodaciXML();
                spremiPodatke();
            }

        } catch (Exception e) {
            System.err.println("Greška pri učitavanju XML podataka: " + e.getMessage());
            podaciXML = new PodaciXML();
        }
    }

    private void spremiPodatke() {
        try {
            File dir = new File(MATERIJALI_DIR);
            if (!dir.exists()) {
                dir.mkdirs();
            }

            Marshaller marshaller = jaxbContext.createMarshaller();
            marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
            marshaller.marshal(podaciXML, MATERIJALI_PATH.toFile());

        } catch (Exception e) {
            System.err.println("Greška pri spremanju XML podataka: " + e.getMessage());
        }
    }

    private PredmetXML pronadjiPredmetXml(Integer predmetId) {
        if (predmetId == null || podaciXML == null) return null;

        return podaciXML.getPredmeti().stream()
                .filter(p -> {
                    try {
                        return p.getId().equals(predmetId.toString());
                    } catch (NumberFormatException e) {
                        return false;
                    }
                })
                .findFirst()
                .orElse(null);
    }
}