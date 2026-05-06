package com.minden;

import javax.sql.DataSource;

import com.minden.entity.DatabaseConfig;

public class Main {
    public static void main(String[] args) {
        System.out.println("Запуск системи...");
        
        DataSource dataSource = DatabaseConfig.initialize();
        
        System.out.println("База даних готова до роботи!");
        
    }
}
