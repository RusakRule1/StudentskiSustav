package projekt.upravitelj;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;

public class UpraviteljBaze {
    private static EntityManagerFactory emf;

    public static EntityManager dohvatiEntityManager() {
        if (emf == null) {
            try {
                emf = Persistence.createEntityManagerFactory("studentski_sustav");
            } catch (Exception e) {
                e.printStackTrace();
                throw new RuntimeException("Ne mogu spojiti na bazu", e);
            }
        }
        return emf.createEntityManager();
    }

    public static void zatvori() {
        if (emf != null && emf.isOpen()) {
            emf.close();
        }
    }
}
