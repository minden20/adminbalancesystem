package com.minden;

import javax.sql.DataSource;

import com.minden.entity.DatabaseConfig;

public class Main {
    public static void main(String[] args) {
        System.out.println("Запуск системи...");
        
        // Ініціалізуємо базу та запускаємо міграції Flyway
        DataSource dataSource = DatabaseConfig.initialize();
        
        System.out.println("База даних готова до роботи!");
        
        // Тут пізніше буде запуск вашого сервера або логіки
    }
}
