package projekt.dao;

import java.util.List;

public interface IOpciDAO<T> {
    T pronadjiPoId(Integer id);

    List<T> vratiSve();

    void spremi(T entity);

    void azuriraj(T entity);

    void obrisi(T entity);

    void obrisiPoId(Integer id);
}