package projekt.servis;

import projekt.model.MaterijalXML;
import projekt.model.PodaciXML;
import projekt.model.PredmetXML;
import projekt.repozitorij.MaterijalXMLRepozitorij;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class MaterijalXMLServis {

    private final MaterijalXMLRepozitorij repozitorij;
    private final PodaciXML podaciXML;

    public MaterijalXMLServis() {
        this.repozitorij = new MaterijalXMLRepozitorij();
        this.podaciXML = repozitorij.ucitaj();
    }

    public List<MaterijalXML> pronadjiMaterijaleZaPredmet(Integer predmetId) {
        if (predmetId == null) return new ArrayList<>();
        PredmetXML predmet = pronadjiPredmetXml(predmetId);
        return predmet == null ? new ArrayList<>() : new ArrayList<>(predmet.getMaterijali());
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

        dodajMaterijal(predmet, materijal);
        repozitorij.spremi(podaciXML);
        return true;
    }

    public boolean dodajMaterijalZaPostojeciPredmet(Integer predmetId, MaterijalXML materijal) {
        if (predmetId == null || materijal == null) return false;
        PredmetXML predmet = pronadjiPredmetXml(predmetId);
        if (predmet == null) return false;
        dodajMaterijal(predmet, materijal);
        repozitorij.spremi(podaciXML);
        return true;
    }

    public boolean azurirajMaterijal(Integer predmetId, String materijalId, MaterijalXML azuriraniMaterijal) {
        if (predmetId == null || materijalId == null || azuriraniMaterijal == null) return false;
        PredmetXML predmet = pronadjiPredmetXml(predmetId);
        if (predmet == null) return false;

        for (MaterijalXML m : predmet.getMaterijali()) {
            if (m.getId().equals(materijalId)) {
                m.setNaziv(azuriraniMaterijal.getNaziv());
                m.setTip(azuriraniMaterijal.getTip());
                repozitorij.spremi(podaciXML);
                return true;
            }
        }
        return false;
    }

    public boolean izbrisiMaterijal(Integer predmetId, String materijalId) {
        if (predmetId == null || materijalId == null) return false;
        PredmetXML predmet = pronadjiPredmetXml(predmetId);
        if (predmet == null) return false;

        boolean uklonjen = predmet.getMaterijali().removeIf(m -> m.getId().equals(materijalId));
        if (uklonjen) {
            if (predmet.getMaterijali().isEmpty()) {
                podaciXML.getPredmeti().removeIf(p -> p.getId().equals(predmetId.toString()));
            }
            repozitorij.spremi(podaciXML);
        }
        return uklonjen;
    }

    public boolean predmetPostojiUXml(Integer predmetId) {
        if (predmetId == null) return false;
        return pronadjiPredmetXml(predmetId) != null;
    }

    private void dodajMaterijal(PredmetXML predmet, MaterijalXML materijal) {
        if (materijal.getId() == null || materijal.getId().isEmpty()) {
            materijal.setId(UUID.randomUUID().toString());
        }
        predmet.getMaterijali().add(materijal);
    }

    private PredmetXML pronadjiPredmetXml(Integer predmetId) {
        return podaciXML.getPredmeti().stream()
                .filter(p -> p.getId().equals(predmetId.toString()))
                .findFirst()
                .orElse(null);
    }
}
