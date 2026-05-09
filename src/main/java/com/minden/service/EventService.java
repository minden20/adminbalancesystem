package com.minden.service;

import com.minden.dto.EventDto;

import java.util.List;
import java.util.Optional;

public interface EventService {
    Optional <EventDto> getEventById(Integer id);

    List<EventDto> getAllEvents();

    void updateEvent(EventDto eventDto);

    void deleteEvent(Integer id);

    void createEvent(EventDto eventDto);

    List<EventDto> findAll();
}
