package com.minden.repository;

import com.minden.entity.ConnectionPool;
import com.minden.entity.Treasure;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class TreasureRepositoryImpl implements TreasureRepository {
    private final ConnectionPool connectionPool;

    public TreasureRepositoryImpl(ConnectionPool connectionPool) {
        this.connectionPool = connectionPool;
    }

    @Override
    public void save(Treasure treasure) {
        String sql = "INSERT INTO treasure (x, y, min_gold, max_gold, is_collected) VALUES (?, ?, ?, ?, ?)";
        try (var connection = connectionPool.getConnection();
                var stmt = connection.prepareStatement(sql, java.sql.Statement.RETURN_GENERATED_KEYS)) {
            stmt.setObject(1, treasure.getX());
            stmt.setObject(2, treasure.getY());
            stmt.setObject(3, treasure.getMinGold());
            stmt.setObject(4, treasure.getMaxGold());
            stmt.setObject(5, treasure.getIsCollected());
            stmt.executeUpdate();

            try (var generatedKeys = stmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    treasure.setId(generatedKeys.getInt(1));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void update(Treasure treasure) {
        String sql = "UPDATE treasure SET x = ?, y = ?, min_gold = ?, max_gold = ?, is_collected = ? WHERE id = ?";
        try (var connection = connectionPool.getConnection();
                var stmt = connection.prepareStatement(sql)) {
            stmt.setObject(1, treasure.getX());
            stmt.setObject(2, treasure.getY());
            stmt.setObject(3, treasure.getMinGold());
            stmt.setObject(4, treasure.getMaxGold());
            stmt.setObject(5, treasure.getIsCollected());
            stmt.setObject(6, treasure.getId());
            stmt.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public List<Treasure> findByCoordinates(Integer x, Integer y) {
        List<Treasure> treasures = new ArrayList<>();
        String sql = "SELECT * FROM treasure WHERE x = ? AND y = ? AND is_collected = false";
        try (var connection = connectionPool.getConnection();
                var stmt = connection.prepareStatement(sql)) {
            stmt.setObject(1, x);
            stmt.setObject(2, y);
            var rs = stmt.executeQuery();
            while (rs.next()) {
                treasures.add(mapRowToTreasure(rs));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return treasures;
    }

    // Допоміжний метод, щоб не дублювати код створення об'єкта
    private Treasure mapRowToTreasure(java.sql.ResultSet rs) throws java.sql.SQLException {
        return Treasure.builder()
                .id(rs.getInt("id"))
                .x(rs.getInt("x"))
                .y(rs.getInt("y"))
                .minGold(rs.getInt("min_gold"))
                .maxGold(rs.getInt("max_gold"))
                .isCollected(rs.getBoolean("is_collected"))
                .build();
    }

    @Override
    public Optional<Treasure> findById(Integer id) {
        String sql = "SELECT * FROM treasure WHERE id = ?";
        try (var connection = connectionPool.getConnection();
                var stmt = connection.prepareStatement(sql)) {
            stmt.setObject(1, id);
            var rs = stmt.executeQuery();
            if (rs.next()) {
                return Optional.of(mapRowToTreasure(rs));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return Optional.empty();
    }

    @Override
    public List<Treasure> findAll() {
        List<Treasure> treasures = new ArrayList<>();
        String sql = "SELECT * FROM treasure";
        try (var connection = connectionPool.getConnection();
                var stmt = connection.prepareStatement(sql)) {
            var rs = stmt.executeQuery();
            while (rs.next()) {
                treasures.add(mapRowToTreasure(rs));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return treasures;
    }

    @Override
    public void delete(Integer id) {
        String sql = "DELETE FROM treasure WHERE id = ?";
        try (var connection = connectionPool.getConnection();
                var stmt = connection.prepareStatement(sql)) {
            stmt.setObject(1, id);
            stmt.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
