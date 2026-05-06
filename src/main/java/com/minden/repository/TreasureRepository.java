package com.minden.repository;

import java.util.List;
import java.util.Optional;

import com.minden.entity.Treasure;

public interface TreasureRepository {
    Optional<Treasure> findById(Integer id);

    List<Treasure> findAll();

    List<Treasure> findByCoordinates(Integer x, Integer y);

    void save(Treasure treasure);

    void update(Treasure treasure);

    void delete(Integer id);
}
