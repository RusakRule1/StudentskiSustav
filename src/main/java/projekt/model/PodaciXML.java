package projekt.model;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;

import java.util.ArrayList;
import java.util.List;

@XmlRootElement(name = "predmetniMaterijali")
@XmlAccessorType(XmlAccessType.FIELD)
public class PodaciXML {

    @XmlElement(name = "predmet")
    private List<PredmetXML> predmeti;

    public PodaciXML() {
        this.predmeti = new ArrayList<>();
    }

    public List<PredmetXML> getPredmeti() {
        return predmeti;
    }

    public void setPredmeti(List<PredmetXML> predmeti) {
        this.predmeti = predmeti;
    }
}