package ru.yandex.practicum.filmorate.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@ControllerAdvice
public class CustomExceptionHandlers {

    @ExceptionHandler(ElementNotFoundException.class)
    private ResponseEntity<Object> elementNotFoundHandler(ElementNotFoundException e) {
        log.warn(e.getMessage());
        return new ResponseEntity<>(e.getElement(), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler
    private ResponseEntity<String> illegalArgumentExceptionHandler(IllegalArgumentException e){
        log.warn("Invalid argument {}", e.getMessage());
        return new ResponseEntity<>("Invalid argument " + e.getMessage(), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler
    private ResponseEntity<Map<String, String>> methodArgumentNotValidHandler(MethodArgumentNotValidException e) {
        log.warn(e.getMessage());
        Map<String, String> errorMap = e.getBindingResult().getFieldErrors().stream()
                .collect(Collectors.toMap(FieldError::getField, FieldError::getDefaultMessage));
        return new ResponseEntity<>(errorMap, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    private void otherExceptionHandler(Throwable e) {
        log.warn(e.getMessage());
    }
}
