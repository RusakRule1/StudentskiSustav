package projekt.baza.repozitorij;

import projekt.baza.dao.OpciDAO;
import projekt.model.Admin;

public class AdminRepozitorij {
    private final OpciDAO<Admin> adminDAO;

    public AdminRepozitorij() {
        this.adminDAO = new OpciDAO<>(Admin.class);
    }

    public void spremi(Admin admin) {
        adminDAO.spremi(admin);
    }
}
