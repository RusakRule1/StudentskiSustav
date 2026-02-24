package projekt.servis;

import projekt.model.Admin;
import projekt.repozitorij.AdminRepozitorij;

public class AdminServis {

    private final AdminRepozitorij adminRepozitorij = new AdminRepozitorij();

    public void spremiAdmina(Admin noviAdmin) {
        adminRepozitorij.spremi(noviAdmin);
    }
}
