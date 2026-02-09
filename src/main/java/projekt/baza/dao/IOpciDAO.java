package projekt.baza.dao;

import java.util.List;

public interface IOpciDAO<T> {
    T vratiPoID(Integer id);

    List<T> vratiSve();

    void spremi(T entity);

    void azuriraj(T entity);

    void obrisi(T entity);

    void obrisiPoID(Integer id);
}