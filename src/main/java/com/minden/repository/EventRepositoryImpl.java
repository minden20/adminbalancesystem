package com.minden.repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import com.minden.entity.ConnectionPool;
import com.minden.entity.Event;

public class EventRepositoryImpl implements EventRepository {
    private final ConnectionPool connectionPool;

    public EventRepositoryImpl(ConnectionPool connectionPool) {
        this.connectionPool = connectionPool;
    }

    @Override
    public Optional<Event> findById(Integer id) {
        String sql = "SELECT * FROM event WHERE id = ?";
        try (var connection = connectionPool.getConnection();
             var stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, id);
            var rs = stmt.executeQuery();
            if (rs.next()) {
                Event event = Event.builder()
                    .id(rs.getObject("id", Integer.class))
                    .name(rs.getString("name"))
                    .description(rs.getString("description"))
                    .minGoldPenalty(rs.getObject("min_gold_penalty", Integer.class))
                    .maxGoldPenalty(rs.getObject("max_gold_penalty", Integer.class))
                    .build();
                return Optional.of(event);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return Optional.empty();
    }

    @Override
    public void save(Event event) {
        String sql = "INSERT INTO event (name, description, min_gold_penalty, max_gold_penalty) VALUES (?, ?, ?, ?)";
        try (var connection = connectionPool.getConnection();
             var stmt = connection.prepareStatement(sql, java.sql.Statement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, event.getName());
            stmt.setString(2, event.getDescription());
            stmt.setObject(3, event.getMinGoldPenalty());
            stmt.setObject(4, event.getMaxGoldPenalty());
            stmt.executeUpdate();
            try (var generatedKeys = stmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    event.setId(generatedKeys.getInt(1));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void update(Event event) {
        String sql = "UPDATE event SET name = ?, description = ?, min_gold_penalty = ?, max_gold_penalty = ? WHERE id = ?";
        try (var connection = connectionPool.getConnection();
             var stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, event.getName());
            stmt.setString(2, event.getDescription());
            stmt.setObject(3, event.getMinGoldPenalty());
            stmt.setObject(4, event.getMaxGoldPenalty());
            stmt.setObject(5, event.getId());
            stmt.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void delete(Integer id) {
        String sql = "DELETE FROM event WHERE id = ?";
        try (var connection = connectionPool.getConnection();
             var stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, id);
            stmt.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public List<Event> findAll() {
        String sql = "SELECT * FROM event";
        List<Event> events = new ArrayList<>();
        try (var connection = connectionPool.getConnection();
             var stmt = connection.prepareStatement(sql)) {
            var rs = stmt.executeQuery();
            while (rs.next()) {
                Event event = Event.builder()
                    .id(rs.getObject("id", Integer.class))
                    .name(rs.getString("name"))
                    .description(rs.getString("description"))
                    .minGoldPenalty(rs.getObject("min_gold_penalty", Integer.class))
                    .maxGoldPenalty(rs.getObject("max_gold_penalty", Integer.class))
                    .build();
                    events.add(event);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return events;
    }
}
