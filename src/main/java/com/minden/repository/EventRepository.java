package com.minden.repository;

import java.util.List;
import java.util.Optional;

import com.minden.entity.Event;

public interface EventRepository {
    Optional<Event> findById(Integer id);

    void save(Event event);

    void update(Event event);

    void delete(Integer id);

    List<Event> findAll();
}
