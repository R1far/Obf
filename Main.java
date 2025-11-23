import java.io.*;
import java.nio.file.*;
import java.util.*;
import java.util.regex.*;

public class Main{

    public static void main(String[] args) {
        try {
            Scanner input = new Scanner(System.in);
            System.out.print("Введите путь к Java файлу: ");
            String path = input.nextLine().trim();

            File srcFile = new File(path);
            if (!srcFile.exists() || !srcFile.isFile()) {
                System.err.println("Ошибка: файл не существует - " + srcFile.getAbsolutePath());
                return;
            }

            processFile(srcFile);

        } catch (Exception e) {
            System.err.println("Критическая ошибка: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private static void processFile(File srcFile) throws IOException {
        try {
            String content = readFile(srcFile.toPath());
            StringHolder holder = saveStrings(content);
            content = holder.code;
            content = cleanComments(content);
            content = compress(content);
            String oldClass = getClassName(content);
            String newClass = generateName();
            content = changeClass(content, oldClass, newClass);
            content = scrambleVars(content);
            content = putBackStrings(content, holder.saved);
            Path outPath = srcFile.toPath().resolveSibling(newClass + ".java");
            writeFile(outPath, content);

            System.out.println("Успешно! Результат сохранен в: " + outPath);

        } catch (Exception ex) {
            throw new IOException("Ошибка обработки файла: " + ex.getMessage(), ex);
        }
    }

    private static String readFile(Path path) throws IOException {
        try {
            return Files.readString(path);
        } catch (IOException e) {
            throw new IOException("Не удалось прочитать файл: " + path, e);
        }
    }

    private static void writeFile(Path path, String data) throws IOException {
        try {
            Files.writeString(path, data);
        } catch (IOException e) {
            throw new IOException("Не удалось записать файл: " + path, e);
        }
    }

    private static String cleanComments(String src) {
        try {
            src = src.replaceAll("/\\*(?:.|[\\r\\n])*?\\*/", "");
            src = src.replaceAll("//.*", "");
            return src;
        } catch (Exception e) {
            System.err.println("Предупреждение: ошибка при удалении комментариев");
            return src;
        }
    }

    private static String compress(String src) {
        try {
            // Заменяем множественные пробелы на один
            src = src.replaceAll("\\s+", " ");
            // Удаляем пробелы вокруг операторов
            src = src.replaceAll("\\s*([{}();=,+\\-*/<>!])\\s*", "$1");
            return src.trim();
        } catch (Exception e) {
            System.err.println("Предупреждение: ошибка при сжатии кода");
            return src;
        }
    }

    private static String getClassName(String src) {
        try {
            Pattern pattern = Pattern.compile("class\\s+([A-Za-z_][A-Za-z0-9_]*)");
            Matcher match = pattern.matcher(src);

            if (match.find()) {
                return match.group(1);
            }
            throw new IllegalStateException("Класс не найден в коде");
        } catch (Exception e) {
            throw new RuntimeException("Ошибка поиска имени класса", e);
        }
    }

    private static String generateName() {
        Random rnd = new Random();
        String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
        StringBuilder name = new StringBuilder();
        name.append(chars.charAt(rnd.nextInt(chars.length())));

        for (int i = 0; i < 7; i++) {
            name.append(Integer.toHexString(rnd.nextInt(16)).toUpperCase());
        }
        return name.toString();
    }

    private static String changeClass(String src, String old, String fresh) {
        try {
            return src.replaceAll("\\b" + Pattern.quote(old) + "\\b", fresh);
        } catch (Exception e) {
            System.err.println("Предупреждение: ошибка при переименовании класса");
            return src;
        }
    }

    private static String scrambleVars(String src) {
        try {
            Set<String> vars = new LinkedHashSet<>();
            String types = "(?:int|double|float|long|String|char|boolean|byte|short|var)";
            Pattern pattern = Pattern.compile(types + "\\s+([a-zA-Z_][a-zA-Z0-9_]*)");
            Matcher match = pattern.matcher(src);

            while (match.find()) {
                vars.add(match.group(1));
            }

            Map<String, String> mapping = new HashMap<>();
            int counter = 0;

            for (String var : vars) {
                String newName = makeVarName(counter++);
                mapping.put(var, newName);
            }

            for (Map.Entry<String, String> entry : mapping.entrySet()) {
                src = src.replaceAll("\\b" + Pattern.quote(entry.getKey()) + "\\b", entry.getValue());
            }

            return src;
        } catch (Exception e) {
            System.err.println("Предупреждение: ошибка при обфускации переменных");
            return src;
        }
    }

    private static String makeVarName(int num) {
        if (num < 26) {
            return String.valueOf((char)('a' + num));
        } else if (num < 52) {
            return String.valueOf((char)('A' + (num - 26)));
        } else {
            return "v" + num;
        }
    }

    private static class StringHolder {
        String code;
        List<String> saved;

        StringHolder(String c, List<String> s) {
            this.code = c;
            this.saved = s;
        }
    }

    private static StringHolder saveStrings(String src) {
        try {
            List<String> list = new ArrayList<>();
            Pattern pattern = Pattern.compile("\"(\\\\.|[^\"\\\\])*\"|'(\\\\.|[^'\\\\])+'");
            Matcher match = pattern.matcher(src);
            StringBuffer result = new StringBuffer();
            int index = 0;

            while (match.find()) {
                list.add(match.group());
                match.appendReplacement(result, "___S" + index + "___");
                index++;
            }
            match.appendTail(result);

            return new StringHolder(result.toString(), list);
        } catch (Exception e) {
            System.err.println("Предупреждение: ошибка при сохранении строк");
            return new StringHolder(src, new ArrayList<>());
        }
    }

    private static String putBackStrings(String src, List<String> list) {
        try {
            for (int i = 0; i < list.size(); i++) {
                src = src.replace("___S" + i + "___", list.get(i));
            }
            return src;
        } catch (Exception e) {
            System.err.println("Предупреждение: ошибка при восстановлении строк");
            return src;
        }
    }
}