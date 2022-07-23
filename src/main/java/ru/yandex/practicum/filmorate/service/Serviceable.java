package ru.yandex.practicum.filmorate.service;

import java.util.List;

public interface Serviceable<T> {
    List<T> showAll();
    T addNew(T t);
    T update(T t);
    T getById(long id);
    void deleteById(long id);
}
