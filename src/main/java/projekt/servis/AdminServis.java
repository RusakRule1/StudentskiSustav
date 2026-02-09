package projekt.servis;

import projekt.baza.repozitorij.AdminRepozitorij;
import projekt.model.Admin;

public class AdminServis {

    private final AdminRepozitorij adminRepozitorij = new AdminRepozitorij();

    public void spremiAdmina(Admin noviAdmin) {
        adminRepozitorij.spremi(noviAdmin);
    }
}
