package projekt.model;

import jakarta.xml.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@XmlRootElement(name = "predmet")
@XmlAccessorType(XmlAccessType.FIELD)
public class PredmetXML {

    @XmlAttribute(name = "id")
    private String id;

    @XmlElement(name = "naziv")
    private String naziv;

    @XmlElementWrapper(name = "materijali")
    @XmlElement(name = "materijal")
    private List<MaterijalXML> materijali;

    public PredmetXML() {
        this.materijali = new ArrayList<>();
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getNaziv() {
        return naziv;
    }

    public void setNaziv(String naziv) {
        this.naziv = naziv;
    }

    public List<MaterijalXML> getMaterijali() {
        return materijali;
    }

    public void setMaterijali(List<MaterijalXML> materijali) {
        this.materijali = materijali;
    }
}