package com.minden.service;

import com.minden.dto.TreasureDto;
import java.util.List;
import java.util.Optional;

public interface TreasureService {
    Optional<TreasureDto> getTreasureById(Integer id);
    List<TreasureDto> getAllTreasures();
    void updateTreasure(TreasureDto treasureDto);
    void deleteTreasure(Integer id);
    void createTreasure(TreasureDto treasureDto);
    List<TreasureDto> findAll();
}
