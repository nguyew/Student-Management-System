package com.studentmanagement.dao;

import java.util.List;

public interface CrudRepository<T, ID> {
    boolean save(T entity);
    T findById(ID id);
    List<T> findAll();
    boolean update(T entity);
    boolean deleteById(ID id);
    boolean delete(T entity);
    boolean existsById(ID id);
    long count();
}
