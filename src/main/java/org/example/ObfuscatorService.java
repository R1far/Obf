package org.example;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ObfuscatorService {

    public void obfuscateFile(File file) {
        String code = FileUtils.readFile(file);

        // Если файл пустой или не прочитался
        if (code.isEmpty()) {
            return;
        }

        String oldFileName = file.getName().replace(".java", "");

        // Удаление комментов
        code = removeComments(code);

        // Удаление лишних пробелов и переносов
        code = compressCode(code);

        // Переименование переменных
        code = renameIdentifiers(code);

        // Переименование Класса и Файла
        String newClassName = "Obf" + oldFileName;

        // Заменяем имя класса в тексте кода
        code = code.replaceAll("\\b" + oldFileName + "\\b", newClassName);

        // Сохраняем код обратно в файл
        FileUtils.writeFile(file, code);

        // Переименовываем файл на диске
        FileUtils.renameFile(file, newClassName);
    }

    private String removeComments(String code) {
        return code.replaceAll("//.*|/\\*[\\s\\S]*?\\*/", "");
    }

    private String compressCode(String code) {
        return code.replaceAll("\\s+", " ").trim();
    }

    private String renameIdentifiers(String code) {
        NameGenerator nameGen = new NameGenerator();

        List<String> oldNames = new ArrayList<>();
        List<String> newNames = new ArrayList<>();

        Pattern pattern = Pattern.compile("\\b(int|String|boolean|double|long|float|char)\\s+([a-zA-Z_]\\w*)");
        Matcher matcher = pattern.matcher(code);

        while (matcher.find()) {
            String varName = matcher.group(2);

            if (!varName.equals("main") && !varName.equals("args") && !oldNames.contains(varName)) {
                oldNames.add(varName);
                newNames.add(nameGen.getNextName());
            }
        }

        for (int i = 0; i < oldNames.size(); i++) {
            String original = oldNames.get(i);
            String replacement = newNames.get(i);

            code = code.replaceAll("\\b" + original + "\\b", replacement);
        }

        return code;
    }
}