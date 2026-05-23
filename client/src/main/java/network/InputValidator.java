package network;

import data.Difficulty;
import java.util.Scanner;

public class InputValidator {

    public static String readNonEmpty(Scanner sc, String prompt) {
        String value;
        while (true) {
            System.out.print(prompt);
            value = sc.nextLine().trim();
            if (!value.isEmpty()) return value;
            System.out.println("Ошибка: поле не может быть пустым!");
        }
    }

    public static double readDouble(Scanner sc, String prompt) {
        while (true) {
            System.out.print(prompt);
            try {
                return Double.parseDouble(sc.nextLine().trim());
            } catch (NumberFormatException e) {
                System.out.println("Ошибка: введите число!");
            }
        }
    }

    public static long readLong(Scanner sc, String prompt) {
        while (true) {
            System.out.print(prompt);
            try {
                return Long.parseLong(sc.nextLine().trim());
            } catch (NumberFormatException e) {
                System.out.println("Ошибка: введите целое число!");
            }
        }
    }

    public static float readFloat(Scanner sc, String prompt) {
        while (true) {
            System.out.print(prompt);
            try {
                return Float.parseFloat(sc.nextLine().trim());
            } catch (NumberFormatException e) {
                System.out.println("Ошибка: введите число!");
            }
        }
    }

    public static int readInt(Scanner sc, String prompt) {
        while (true) {
            System.out.print(prompt);
            String input = sc.nextLine().trim();
            if (input.isEmpty()) return 0;
            try {
                return Integer.parseInt(input);
            } catch (NumberFormatException e) {
                System.out.println("Ошибка: введите целое число!");
            }
        }
    }

    public static Difficulty readDifficulty(Scanner sc) {
        while (true) {
            System.out.print("Difficulty (EASY/MEDIUM/HARD/VERY_HARD/HOPELESS): ");
            String str = sc.nextLine().trim().toUpperCase();
            if (str.isEmpty()) {
                System.out.println("Ошибка: введите значение!");
                continue;
            }
            try {
                return Difficulty.valueOf(str);
            } catch (IllegalArgumentException e) {
                System.out.println("Ошибка: '" + str + "' — недопустимое значение!");
            }
        }
    }

    public static long parseId(String arg) {
        try {
            return Long.parseLong(arg.trim());
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("ID должен быть числом");
        }
    }
}