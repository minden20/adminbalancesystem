package com.minden.service;

import com.minden.dto.PlayerDto;
import com.minden.exception.ValidationException;
import java.util.List;
import java.util.Optional;

/**
 * Інтерфейс сервісу для бізнес-операцій над гравцями.
 * Забезпечує CRUD з валідацією та повертає DTO.
 */
public interface PlayerService {

    /**
     * Отримує гравця за ID.
     *
     * @param id ідентифікатор гравця
     * @return DTO гравця або порожній Optional
     */
    Optional<PlayerDto> findById(Integer id);

    /**
     * Отримує список усіх гравців.
     *
     * @return список DTO гравців
     */
    List<PlayerDto> findAll();

    /**
     * Оновлює дані гравця з валідацією.
     *
     * @param dto дані для оновлення
     * @throws ValidationException якщо дані невалідні
     */
    void update(PlayerDto dto) throws ValidationException;

    /**
     * Видаляє гравця за ID.
     *
     * @param id ідентифікатор гравця
     */
    void delete(Integer id);
}
