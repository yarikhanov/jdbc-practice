package org.example.repositoryInterface;

import java.util.List;

public interface GenericRepo <T, ID> {

    T getById(ID id);
    List<T> getAll();
    T save(T t);
    T update(T t);
    void deleteById(ID id);
}
