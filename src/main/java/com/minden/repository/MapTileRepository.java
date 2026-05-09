package com.minden.repository;

import java.util.List;
import java.util.Optional;

import com.minden.entity.MapTile;

public interface MapTileRepository {
    Optional<MapTile> findByCoordinates(Integer x, Integer y);

    void save(MapTile tile);

    List<MapTile> findAll();

    void delete(Integer x, Integer y);
    
    boolean hasTiles();
    void batchInsert(List<MapTile> tiles);
}
