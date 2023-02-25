package ru.yandex.practicum.filmorate.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@AllArgsConstructor
@RequiredArgsConstructor
public class UserEvent {
    private int eventId;
    private final String eventType;
    private final String operation;
    private final int userId;
    private final int entityId;
    private long timestamp;

}
