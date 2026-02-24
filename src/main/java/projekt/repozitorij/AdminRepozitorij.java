package projekt.repozitorij;

import projekt.dao.OpciDAO;
import projekt.model.Admin;

import java.util.List;

public class AdminRepozitorij {
    private final OpciDAO<Admin> adminDAO;

    public AdminRepozitorij() {
        this.adminDAO = new OpciDAO<>(Admin.class);
    }

    public void spremi(Admin admin) {
        adminDAO.spremi(admin);
    }

    public void azuriraj(Admin admin) {
        adminDAO.azuriraj(admin);
    }

    public void obrisi(Admin admin) {
        adminDAO.obrisi(admin);
    }

    public void obrisiPoId(Integer id) {
        adminDAO.obrisiPoId(id);
    }

    public Admin pronadjiPoId(Integer id) {
        return adminDAO.pronadjiPoId(id);
    }

    public List<Admin> vratiSve() {
        return adminDAO.vratiSve();
    }
}
