package repository;

import java.util.List;

public interface IRepository<T, Tid> {
    T add(T entity);
    T findById(Tid id);
    List<T> getAll();
    T update(T entity);
    void deleteById(Tid id);
}