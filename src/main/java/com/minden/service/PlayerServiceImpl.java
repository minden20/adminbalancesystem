package com.minden.service;

import com.minden.dto.PlayerDto;
import com.minden.entity.Player;
import com.minden.exception.ValidationException;
import com.minden.repository.PlayerRepository;
import com.minden.util.Validator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Реалізація сервісу бізнес-операцій над гравцями.
 * Інкапсулює бізнес-логіку та повертає DTO замість Entity.
 */
public class PlayerServiceImpl implements PlayerService {

    private final PlayerRepository playerRepository;

    /**
     * Конструктор з ін'єкцією залежності (Dependency Injection).
     *
     * @param playerRepository репозиторій для доступу до даних гравців
     */
    public PlayerServiceImpl(PlayerRepository playerRepository) {
        this.playerRepository = playerRepository;
    }

    @Override
    public Optional<PlayerDto> findById(Integer id) {
        return playerRepository.findById(id).map(this::toDto);
    }

    @Override
    public List<PlayerDto> findAll() {
        return playerRepository.findAll().stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public void update(PlayerDto dto) throws ValidationException {
        if (dto.getId() == null) {
            throw new ValidationException("ID гравця не може бути порожнім.");
        }

        Validator.validateUsername(dto.getUsername());
        Validator.validateEmail(dto.getEmail());

        Player existing = playerRepository.findById(dto.getId())
                .orElseThrow(() -> new ValidationException(
                        "Гравця з ID " + dto.getId() + " не знайдено."));

        if (dto.getGold() != null && dto.getGold() < 0) {
            throw new ValidationException("Золото не може бути від'ємним.");
        }
        if (dto.getEnergy() != null && dto.getEnergy() < 0) {
            throw new ValidationException("Енергія не може бути від'ємною.");
        }

        existing.setUsername(dto.getUsername());
        existing.setEmail(dto.getEmail());
        existing.setX(dto.getX());
        existing.setY(dto.getY());
        existing.setGold(dto.getGold());
        existing.setEnergy(dto.getEnergy());
        existing.setCurrentDay(dto.getCurrentDay());

        playerRepository.update(existing);
    }

    @Override
    public void delete(Integer id) {
        playerRepository.delete(id);
    }

    /**
     * Конвертує Entity в DTO (без passwordHash).
     */
    private PlayerDto toDto(Player player) {
        return PlayerDto.builder()
                .id(player.getId())
                .username(player.getUsername())
                .email(player.getEmail())
                .x(player.getX())
                .y(player.getY())
                .gold(player.getGold())
                .energy(player.getEnergy())
                .currentDay(player.getCurrentDay())
                .build();
    }
}
