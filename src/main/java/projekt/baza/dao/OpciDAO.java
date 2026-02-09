package projekt.baza.dao;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;
import projekt.upravitelj.UpraviteljBaze;

import java.util.List;
import java.util.function.Consumer;

public class OpciDAO<T> implements IOpciDAO<T> {

    private final Class<T> entityClass;

    public OpciDAO(Class<T> entityClass) {
        this.entityClass = entityClass;
    }

    @Override
    public T vratiPoID(Integer id) {
        EntityManager em = UpraviteljBaze.dohvatiEntityManager();
        try {
            return em.find(entityClass, id);
        } finally {
            em.close();
        }
    }

    @Override
    public List<T> vratiSve() {
        EntityManager em = UpraviteljBaze.dohvatiEntityManager();
        try {
            return em.createQuery("FROM " + entityClass.getSimpleName(), entityClass).getResultList();
        } finally {
            em.close();
        }
    }

    @Override
    public void spremi(T entity) {
        izvrsiTransakciju(em -> em.persist(entity));
    }

    @Override
    public void azuriraj(T entity) {
        izvrsiTransakciju(em -> em.merge(entity));
    }

    @Override
    public void obrisi(T entity) {
        izvrsiTransakciju(em -> em.remove(em.contains(entity) ? entity : em.merge(entity)));
    }

    @Override
    public void obrisiPoID(Integer id) {
        T entity = vratiPoID(id);
        if (entity != null) {
            obrisi(entity);
        }
    }

    private void izvrsiTransakciju(Consumer<EntityManager> action) {
        EntityManager em = UpraviteljBaze.dohvatiEntityManager();
        EntityTransaction transaction = em.getTransaction();

        try {
            transaction.begin();
            action.accept(em);
            transaction.commit();
        } catch (Exception e) {
            if (transaction.isActive()) {
                transaction.rollback();
            }
            throw e;
        } finally {
            em.close();
        }
    }
}
