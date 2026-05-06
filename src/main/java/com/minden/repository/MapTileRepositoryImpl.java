package com.minden.repository;

import java.util.Optional;
import java.util.ArrayList;
import java.util.List;

import com.minden.entity.ConnectionPool;
import com.minden.entity.MapTile;

public class MapTileRepositoryImpl implements MapTileRepository {
private final ConnectionPool connectionPool;

    public MapTileRepositoryImpl(ConnectionPool connectionPool) {
        this.connectionPool = connectionPool;
    }

    @Override
    public Optional<MapTile> findByCoordinates(Integer x, Integer y) {
        String sql = "SELECT * FROM map_tile WHERE x = ? AND y = ?";
        try (var connection = connectionPool.getConnection();
             var stmt = connection.prepareStatement(sql)) {
            stmt.setObject(1, x);
            stmt.setObject(2, y);
            var rs = stmt.executeQuery();
            if (rs.next()) {
                return Optional.of(MapTile.builder()
                        .x(rs.getInt("x"))
                        .y(rs.getInt("y"))
                        .terrainType(rs.getString("terrain_type"))
                        .build());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return Optional.empty();
    }

    @Override
    public void save(MapTile tile) {
        String sql = "INSERT INTO map_tile (x, y, terrain_type) VALUES (?, ?, ?)";
        try (var connection = connectionPool.getConnection();
             var stmt = connection.prepareStatement(sql)) {
            stmt.setObject(1, tile.getX());
            stmt.setObject(2, tile.getY());
            stmt.setString(3, tile.getTerrainType());
            stmt.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public List<MapTile> findAll() {
        List<MapTile> tiles = new ArrayList<>();
        String sql = "SELECT * FROM map_tile";
        try (var connection = connectionPool.getConnection();
             var stmt = connection.prepareStatement(sql)) {
            var rs = stmt.executeQuery();
            while (rs.next()) {
                tiles.add(MapTile.builder()
                        .x(rs.getInt("x"))
                        .y(rs.getInt("y"))
                        .terrainType(rs.getString("terrain_type"))
                        .build());
}
        } catch (Exception e) {
            e.printStackTrace();
        }
        return tiles;
    }

    @Override
    public void delete(Integer x, Integer y) {
        String sql = "DELETE FROM map_tile WHERE x = ? AND y = ?";
        try (var connection = connectionPool.getConnection();
             var stmt = connection.prepareStatement(sql)) {
            stmt.setObject(1, x);
            stmt.setObject(2, y);
            stmt.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}