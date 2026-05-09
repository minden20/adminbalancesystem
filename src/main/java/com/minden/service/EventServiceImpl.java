package com.minden.service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import com.minden.dto.EventDto;
import com.minden.entity.Event;
import com.minden.repository.EventRepository;

public class EventServiceImpl implements EventService {
    private final EventRepository eventRepository;

    public EventServiceImpl(EventRepository eventRepository) {
        this.eventRepository = eventRepository;
    }

    @Override
    public List<EventDto> findAll() {
        return eventRepository.findAll().stream()
                .map(event -> new EventDto(
                        event.getId(),
                        event.getName(),
                        event.getDescription(),
                        event.getMinGoldPenalty(),
                        event.getMaxGoldPenalty()))
                .collect(Collectors.toList());
    }

    @Override
    public void createEvent(EventDto eventDto) {
        eventRepository.save(new Event(
                null,
                eventDto.getName(),
                eventDto.getDescription(),
                eventDto.getMinGoldPenalty(),
                eventDto.getMaxGoldPenalty()));
    }

    @Override
    public Optional<EventDto> getEventById(Integer id) {
        return eventRepository.findById(id).map(event -> new EventDto(
                event.getId(),
                event.getName(),
                event.getDescription(),
                event.getMinGoldPenalty(),
                event.getMaxGoldPenalty()));
    }

    @Override
    public void updateEvent(EventDto eventDto) {
        eventRepository.findById(eventDto.getId()).ifPresent(event -> {
            event.setName(eventDto.getName());
            event.setDescription(eventDto.getDescription());
            event.setMinGoldPenalty(eventDto.getMinGoldPenalty());
            event.setMaxGoldPenalty(eventDto.getMaxGoldPenalty());
            eventRepository.save(event);
        });
    }

    @Override
    public void deleteEvent(Integer id) {
        eventRepository.delete(id);
    }

    @Override
    public List<EventDto> getAllEvents() {
        return findAll();
    }
    
    
}