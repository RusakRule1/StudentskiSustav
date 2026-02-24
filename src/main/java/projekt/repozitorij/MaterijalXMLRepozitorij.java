package projekt.repozitorij;

import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.JAXBException;
import jakarta.xml.bind.Marshaller;
import projekt.model.PodaciXML;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class MaterijalXMLRepozitorij {

    private static final Path MATERIJALI_DIR = Paths.get(
            System.getProperty("user.home"), ".studentski-sustav", "podaci"
    );
    private static final Path MATERIJALI_PATH = MATERIJALI_DIR.resolve("materijali.xml");

    private final JAXBContext jaxbContext;

    public MaterijalXMLRepozitorij() {
        try {
            jaxbContext = JAXBContext.newInstance(PodaciXML.class);
        } catch (JAXBException e) {
            throw new RuntimeException("Greška pri inicijalizaciji JAXB konteksta", e);
        }
    }

    public PodaciXML ucitaj() {
        try {
            File file = MATERIJALI_PATH.toFile();
            if (file.exists() && file.length() > 0) {
                return (PodaciXML) jaxbContext.createUnmarshaller().unmarshal(file);
            }
        } catch (Exception e) {
            System.err.println("Greška pri učitavanju XML podataka: " + e.getMessage());
        }
        return new PodaciXML();
    }

    public void spremi(PodaciXML podaci) {
        try {
            Files.createDirectories(MATERIJALI_DIR);
            Marshaller marshaller = jaxbContext.createMarshaller();
            marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
            marshaller.marshal(podaci, MATERIJALI_PATH.toFile());
        } catch (Exception e) {
            throw new RuntimeException("Greška pri spremanju XML podataka", e);
        }
    }
}
