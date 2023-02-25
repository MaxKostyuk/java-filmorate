package ru.yandex.practicum.filmorate.exception;

public class ElementNotFoundException extends RuntimeException {

    private final Object element;
    public ElementNotFoundException(String message, Object element) {
        super(message);
        this.element = element;
    }

    public ElementNotFoundException(String message, Object element, Throwable cause) {
        super(message, cause);
        this.element = element;
    }

    public Object getElement() {
        return element;
    }

}
