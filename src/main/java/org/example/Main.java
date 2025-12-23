package org.example;

import java.io.File;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        try {
            Scanner scanner = new Scanner(System.in);

            System.out.println("Введите ПОЛНЫЙ путь к файлу .java");
            String path = scanner.nextLine();

            // Удаляем кавычки, если путт в "..."
            path = path.replace("\"", "");

            File file = new File(path);

            if (!file.exists()) {
                System.out.println("Ошибка: Файл не найден по указанному пути.");
                return;
            }
            if (!file.isFile()) {
                System.out.println("Ошибка: Указанный путь — это папка.");
                return;
            }
            if (!file.getName().endsWith(".java")) {
                System.out.println("Ошибка: Файл должен иметь расширение .java");
                return;
            }

            System.out.println("Обработка файла: " + file.getName());

            ObfuscatorService service = new ObfuscatorService();
            service.obfuscateFile(file);

            System.out.println("Успешно! Обфускация завершена.");

        } catch (Exception e) {
            System.out.println("Произошла неожиданная ошибка в Main: " + e.getMessage());
            e.printStackTrace();
        }
    }
}