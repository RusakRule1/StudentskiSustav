package projekt.pogled;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import projekt.model.Uloga;
import projekt.upravitelj.UpraviteljScena;
import projekt.util.StiloviUtil;

public class GlavniIzbornikPogled extends OsnovniPogled {

    private Uloga uloga;
    private String email;
    private Text dobrodosliTekst;
    private Label ulogaLabel;
    private Label emailLabel;
    private Button odjavaGumb;

    public GlavniIzbornikPogled(Uloga uloga, String email) {
        super();
        this.uloga = uloga;
        this.email = email;
    }

    @Override
    protected VBox kreirajSadrzaj() {
        VBox sadrzajBox = new VBox(30);
        sadrzajBox.setPadding(new Insets(40));
        sadrzajBox.setAlignment(Pos.CENTER);
        sadrzajBox.getStyleClass().add(StiloviUtil.POZADINA_SVIJETLA);

        dobrodosliTekst = new Text();
        dobrodosliTekst.getStyleClass().add(StiloviUtil.NASLOV_TEKST);

        ulogaLabel = new Label();
        ulogaLabel.getStyleClass().add(StiloviUtil.LABELA_INFORMACIJA);

        emailLabel = new Label();
        emailLabel.getStyleClass().add(StiloviUtil.LABELA_INFORMACIJA);

        odjavaGumb = new Button();
        odjavaGumb.getStyleClass().add(StiloviUtil.GUMB_OPASAN);
        odjavaGumb.setPrefWidth(200);

        odjavaGumb.setOnAction(e -> UpraviteljScena.pokreniLogin());

        if ("HR".equals(trenutniJezik)) {
            postaviHrvatski();
        } else {
            postaviEngleski();
        }
        
        sadrzajBox.getChildren().addAll(
                dobrodosliTekst,
                ulogaLabel,
                emailLabel,
                odjavaGumb
        );

        return sadrzajBox;
    }

    @Override
    protected void postaviHrvatski() {
        dobrodosliTekst.setText("DOBRODOŠLI!");
        ulogaLabel.setText("Uloga: " + prevediUloguHR(uloga));
        emailLabel.setText("Email: " + email);
        odjavaGumb.setText("ODJAVI SE");
    }

    @Override
    protected void postaviEngleski() {
        dobrodosliTekst.setText("WELCOME!");
        ulogaLabel.setText("Role: " + prevediUloguEN(uloga));
        emailLabel.setText("Email: " + email);
        odjavaGumb.setText("LOGOUT");
    }

    private String prevediUloguHR(Uloga uloga) {
        switch (uloga) {
            case STUDENT:
                return "Student";
            case PROFESOR:
                return "Profesor";
            case ADMIN:
                return "Administrator";
            default:
                return "";
        }
    }

    private String prevediUloguEN(Uloga uloga) {
        switch (uloga) {
            case STUDENT:
                return "Student";
            case PROFESOR:
                return "Professor";
            case ADMIN:
                return "Administrator";
            default:
                return "";
        }
    }
}