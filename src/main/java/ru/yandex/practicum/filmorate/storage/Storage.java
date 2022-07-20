package ru.yandex.practicum.filmorate.storage;

import java.util.List;

public interface Storage<T> {
    List<T> showAll();
    T addNew(T t);
    T update(T t);
    T getById(long id);
    void deleteById(long id);
}
