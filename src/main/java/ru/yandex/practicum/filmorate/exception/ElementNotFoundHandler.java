package ru.yandex.practicum.filmorate.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@Slf4j
@ControllerAdvice
public class ElementNotFoundHandler {

    @ExceptionHandler(ElementNotFoundException.class)
    private ResponseEntity<Object> elementNotFoundHandler(ElementNotFoundException e) {
        log.warn(e.getMessage());
        return new ResponseEntity<>(e.getElement(), HttpStatus.NOT_FOUND);
    }
}
