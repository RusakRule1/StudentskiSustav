package projekt.util;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;

public class HibernateUtil {
    private static EntityManagerFactory emf;

    public static EntityManager dohvatiEntityManager() {
        if (emf == null) {
            try {
                emf = Persistence.createEntityManagerFactory("studentski_sustav");
                System.out.println("Hibernate spojen na bazu!");
            } catch (Exception e) {
                System.err.println("Greška pri spajanju na bazu: " + e.getMessage());
                e.printStackTrace();
                throw new RuntimeException("Ne mogu spojiti na bazu", e);
            }
        }
        return emf.createEntityManager();
    }

    public static void zatvori() {
        if (emf != null && emf.isOpen()) {
            emf.close();
            System.out.println("Hibernate zatvoren.");
        }
    }
}
