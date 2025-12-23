package org.example;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;

public class FileUtils {

    public static String readFile(File file) {
        try {
            return Files.readString(file.toPath());
        } catch (IOException e) {
            System.err.println("Ошибка при чтении файла: " + e.getMessage());
            return "";
        }
    }

    public static void writeFile(File file, String content) {
        try {
            Files.writeString(file.toPath(), content, StandardOpenOption.TRUNCATE_EXISTING);
        } catch (IOException e) {
            System.err.println("Ошибка при записи файла: " + e.getMessage());
        }
    }

    public static void renameFile(File oldFile, String newNameWithoutExt) {
        try {
            Path source = oldFile.toPath();
            // Создаем путь к новому файлу в той же папке
            Path target = source.resolveSibling(newNameWithoutExt + ".java");

            Files.move(source, target);
            System.out.println("Файл физически переименован в: " + target.getFileName());
        } catch (IOException e) {
            System.err.println("Ошибка при переименовании файла: " + e.getMessage());
        }
    }
}