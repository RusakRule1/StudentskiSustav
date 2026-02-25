package projekt.util.graditelj;

import javafx.scene.control.PasswordField;

public class PasswordFieldGraditelj {

    private final PasswordField polje = new PasswordField();

    public PasswordFieldGraditelj() {
    }

    public PasswordFieldGraditelj stil(String... klase) {
        polje.getStyleClass().addAll(klase);
        return this;
    }

    public PasswordField build() {
        return polje;
    }
}
