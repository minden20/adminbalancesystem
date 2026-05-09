package com.minden.service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import com.minden.dto.TreasureDto;
import com.minden.entity.Treasure;
import com.minden.repository.TreasureRepository;

public class TreasureServiceImpl implements TreasureService {
    private final TreasureRepository treasureRepository;

    public TreasureServiceImpl(TreasureRepository treasureRepository) {
        this.treasureRepository = treasureRepository;
    }

    @Override
    public List<TreasureDto> findAll() {
        return treasureRepository.findAll().stream()
                .map(t -> new TreasureDto(
                        t.getId(),
                        t.getX(),
                        t.getY(),
                        t.getMinGold(),
                        t.getMaxGold(),
                        t.getIsCollected()))
                .collect(Collectors.toList());
    }

    @Override
    public void createTreasure(TreasureDto dto) {
        treasureRepository.save(new Treasure(
                null,
                dto.getX(),
                dto.getY(),
                dto.getMinGold(),
                dto.getMaxGold(),
                dto.getIsCollected()));
    }

    @Override
    public Optional<TreasureDto> getTreasureById(Integer id) {
        return treasureRepository.findById(id).map(t -> new TreasureDto(
                t.getId(),
                t.getX(),
                t.getY(),
                t.getMinGold(),
                t.getMaxGold(),
                t.getIsCollected()));
    }

    @Override
    public void updateTreasure(TreasureDto dto) {
        treasureRepository.findById(dto.getId()).ifPresent(t -> {
            t.setX(dto.getX());
            t.setY(dto.getY());
            t.setMinGold(dto.getMinGold());
            t.setMaxGold(dto.getMaxGold());
            t.setIsCollected(dto.getIsCollected());
            treasureRepository.update(t);
        });
    }

    @Override
    public void deleteTreasure(Integer id) {
        treasureRepository.delete(id);
    }

    @Override
    public List<TreasureDto> getAllTreasures() {
        return findAll();
    }
}
