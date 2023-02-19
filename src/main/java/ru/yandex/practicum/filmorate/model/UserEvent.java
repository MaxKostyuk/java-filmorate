package ru.yandex.practicum.filmorate.model;

import lombok.Data;

@Data
public class UserEvent {
    private int eventId;
    private int userId;
    private String eventType;
    private String operation;
    private int entityId;
    private long timestamp;
}
