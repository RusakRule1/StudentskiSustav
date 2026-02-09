package projekt.pogled;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.layout.VBox;
import projekt.util.Stilovi;

public class PregledMaterijalaPogled extends OsnovniPogled {
    public PregledMaterijalaPogled() {
        super();
    }

    @Override
    protected VBox kreirajSadrzaj() {
        VBox sadrzajBox = new VBox(30);
        sadrzajBox.setPadding(new Insets(40));
        sadrzajBox.setAlignment(Pos.CENTER);
        sadrzajBox.getStyleClass().add(Stilovi.POZADINA_SVIJETLA);


        return sadrzajBox;
    }

    @Override
    protected void osvjeziPogledTekstove() {
    }
}
