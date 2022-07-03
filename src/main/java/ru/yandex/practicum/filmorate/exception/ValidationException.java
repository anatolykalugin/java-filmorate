package ru.yandex.practicum.filmorate.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.BAD_REQUEST, reason = "Не прошло валидацию")
public class ValidationException extends RuntimeException {
    public ValidationException(String message) {
        super(message);
    }
}
