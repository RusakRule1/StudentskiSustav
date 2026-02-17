package projekt.model;

import jakarta.xml.bind.annotation.XmlEnum;
import jakarta.xml.bind.annotation.XmlEnumValue;
import jakarta.xml.bind.annotation.XmlType;

@XmlType(name = "tipMaterijala")
@XmlEnum(String.class)
public enum TipMaterijalaXML {

    @XmlEnumValue("prezentacija")
    PREZENTACIJA("tip_prezentacija"),

    @XmlEnumValue("knjiga")
    KNJIGA("tip_knjiga"),

    @XmlEnumValue("video")
    VIDEO("tip_video"),

    @XmlEnumValue("zadaca")
    ZADACA("tip_zadaca"),

    @XmlEnumValue("test")
    TEST("tip_test"),

    @XmlEnumValue("primjer")
    PRIMJER("tip_primjer"),

    @XmlEnumValue("biljeska")
    BILJESKA("tip_biljeska"),

    @XmlEnumValue("link")
    LINK("tip_link");

    private final String kljucPrijevoda;

    TipMaterijalaXML(String kljucPrijevoda) {
        this.kljucPrijevoda = kljucPrijevoda;
    }

    public String getKljucPrijevoda() {
        return kljucPrijevoda;
    }
}