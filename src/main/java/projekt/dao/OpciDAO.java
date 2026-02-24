package projekt.dao;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;
import jakarta.persistence.NoResultException;
import projekt.upravitelj.UpraviteljBaze;

import java.util.List;
import java.util.function.Consumer;

public class OpciDAO<T> implements IOpciDAO<T> {

    private final Class<T> entityClass;

    public OpciDAO(Class<T> entityClass) {
        this.entityClass = entityClass;
    }

    @Override
    public T pronadjiPoId(Integer id) {
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
            return em.createQuery("FROM " + entityClass.getName(), entityClass).getResultList();
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

    public void obrisiPoId(Integer id) {
        izvrsiTransakciju(em -> {
            T entity = em.find(entityClass, id);
            if (entity != null) {
                em.remove(entity);
            }
        });
    }

    public <R> R pronadjiJedan(String jpql, Class<R> klasa, String param, Object vrijednost) {
        EntityManager em = UpraviteljBaze.dohvatiEntityManager();
        try {
            return em.createQuery(jpql, klasa)
                    .setParameter(param, vrijednost)
                    .getSingleResult();
        } catch (NoResultException e) {
            return null;
        } finally {
            em.close();
        }
    }

    public <R> List<R> pronadjiListu(String jpql, Class<R> klasa) {
        EntityManager em = UpraviteljBaze.dohvatiEntityManager();
        try {
            return em.createQuery(jpql, klasa).getResultList();
        } finally {
            em.close();
        }
    }

    public <R> List<R> pronadjiListu(String jpql, Class<R> klasa, String param, Object vrijednost) {
        EntityManager em = UpraviteljBaze.dohvatiEntityManager();
        try {
            return em.createQuery(jpql, klasa)
                    .setParameter(param, vrijednost)
                    .getResultList();
        } finally {
            em.close();
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
            System.err.println("Greška pri transakciji: " + e.getMessage());
            throw e;
        } finally {
            em.close();
        }
    }
}
