package projekt.model;

import jakarta.xml.bind.annotation.*;

@XmlRootElement(name = "materijal")
@XmlAccessorType(XmlAccessType.FIELD)
public class MaterijalXML {

    @XmlAttribute(name = "id")
    private String id;

    @XmlElement(name = "naziv")
    private String naziv;

    @XmlElement(name = "tip")
    private TipMaterijalaXML tip;

    public MaterijalXML() {
        this.id = java.util.UUID.randomUUID().toString();
    }

    public MaterijalXML(String naziv, TipMaterijalaXML tip) {
        this();
        this.naziv = naziv;
        this.tip = tip;
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

    public TipMaterijalaXML getTip() {
        return tip;
    }

    public void setTip(TipMaterijalaXML tip) {
        this.tip = tip;
    }

    public String getTipPrikaz() {
        return tip.getKljucPrijevoda();
    }
}