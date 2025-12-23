package org.example;

public class NameGenerator {
    private int counter = 0;

    public String getNextName() {
        StringBuilder name = new StringBuilder();
        int n = counter++;

        do {
            int remainder = n % 26;
            name.insert(0, (char) ('a' + remainder));
            n = (n / 26) - 1;
        } while (n >= 0);

        return name.toString();
    }
}