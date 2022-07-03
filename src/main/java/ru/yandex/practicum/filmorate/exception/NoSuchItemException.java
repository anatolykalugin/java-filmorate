package ru.yandex.practicum.filmorate.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.BAD_REQUEST, reason = "Нет объекта на обновление")
public class NoSuchItemException extends RuntimeException {
    public NoSuchItemException(String message) {
        super(message);
    }
}
