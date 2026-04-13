package ui;

import data.*;
import Exception.*;
import java.util.Scanner;
/**
 * Класс для считывания данных объекта LabWork из консоли или скрипта.
 * Обеспечивает валидацию ввода пользователем и повторный запрос при ошибках.
 * Поддерживает работу в интерактивном режиме и режиме выполнения скриптов.
 */
public class LabWorkReader {
    private Scanner scanner;
    private boolean isScriptMode = false;

    public LabWorkReader(Scanner scanner) { this.scanner = scanner; }

    public void setScanner(Scanner scanner) { this.scanner = scanner; }
    public Scanner getScanner() {return scanner; }
    public void setScriptMode(boolean scriptMode) { isScriptMode = scriptMode; }

    /**
     * Считывает данные новой лабораторной работы из стандартного потока ввода (консоли).
     * Запрашивает каждое поле отдельно, выводя подсказки пользователю.
     *
     * @return новый объект LabWork с введенными данными
     */
    public LabWork readLabWorkFromConsole() {
        System.out.print("Введите name: ");
        String name = readNonEmptyString();

        System.out.print("Введите x (> -279): ");
        double x = readDouble();
        while (x <= -279) {
            System.out.println("X должен быть > -279. Повторите: ");
            x = readDouble();
        }
        System.out.print("Введите y (> -240): ");
        long y = readLong();
        while (y <= -240) {
            System.out.println("Y должен быть > -240. Повторите: ");
            y = readLong();
        }
        Coordinates coords = new Coordinates(x, y);

        System.out.print("Введите minimalPoint (>0): ");
        float minP = (float) readDouble();
        while (minP <= 0) {
            System.out.println("minimalpoint > 0. Повторите: ");
            minP = (float) readDouble();
        }

        System.out.print("Введите personalQualitiesMaximum (>0): ");
        double pqMax = readDouble();
        while (pqMax <= 0) {
            System.out.println("personalQualitiesMaximum > 0. Повторите: ");
            pqMax = readDouble();
        }

        System.out.println("Доступные Difficulty: EASY, NORMAL, HARD, VERY_HARD, HOPELESS");
        System.out.print("Введите difficulty: ");
        Difficulty diff = readEnum(Difficulty.class);

        System.out.print("Введите название дисциплины: ");
        String discName = readNonEmptyString();

        Integer lh = null;
        boolean inputValid = false;
        while (!inputValid) {
            System.out.print("Введите lectureHours (или нажмите Enter для пропуска): ");
            String lhLine = scanner.nextLine().trim();
            if (lhLine.isEmpty()) {
                lh = null;
                inputValid = true;
            } else {
                try {
                    lh = Integer.parseInt(lhLine);
                    inputValid = true;
                } catch (NumberFormatException e) {
                    System.out.println("Ошибка: Введите целое число или оставьте поле пустым.");
                }
            }
        }
        Discipline disc = new Discipline(discName, lh);

        return new LabWork(name, coords, minP, pqMax, diff, disc);
    }

    public LabWork readLabWorkFromScript() {
        try {
            String name = scanner.nextLine();
            if (name == null || name.isEmpty()) throw new IllegalArgumentException("Name пуст");

            double x = Double.parseDouble(scanner.nextLine());
            long y = Long.parseLong(scanner.nextLine());
            Coordinates coords = new Coordinates(x, y);
            float minP = Float.parseFloat(scanner.nextLine());
            double pqMax = Double.parseDouble(scanner.nextLine());
            Difficulty diff = Difficulty.valueOf(scanner.nextLine().trim().toUpperCase());
            String discName = scanner.nextLine();
            String lhLine = scanner.nextLine();
            Integer lh = lhLine.trim().isEmpty() ? null : Integer.parseInt(lhLine.trim());
            Discipline disc = new Discipline(discName, lh);

            return new LabWork(name, coords, minP, pqMax, diff, disc);
        } catch (Exception e) {
            throw new InvalidInputException("Ошибка чтения из скрипта", e);
        }
    }
    /**
     * Считывает строку, гарантируя, что она не пустая.
     * Циклически запрашивает ввод, пока пользователь не введет значение.
     *
     * @return непустая строка
     */
    private String readNonEmptyString() {
        String s = scanner.nextLine();
        while (s == null || s.trim().isEmpty()) {
            System.out.print("Строка не может быь пустой. Повторите: ");
            s = scanner.nextLine();
        }
        return s.trim();
    }
    /**
     * Считывает число типа double с обработкой ошибок формата.
     * Повторяет запрос при вводе нечисловых значений.
     *
     * @return корректное значение double
     */
    private double readDouble() {
        while (true) {
            try {
                return Double.parseDouble(scanner.nextLine());
            } catch (NumberFormatException e) {
                System.out.print("Введите число: ");
            }
        }
    }
    /**
     * Считывает число типа long с обработкой ошибок формата.
     *
     * @return корректное значение long
     */
    private long readLong() {
        while (true) {
            try {
                return Long.parseLong(scanner.nextLine());
            } catch (NumberFormatException e) {
                System.out.print("Введите целое число: ");
            }
        }
    }
    /**
     * Считывает значение перечисления (Enum) по имени константы.
     * Выводит ошибку при вводе несуществующей константы.
     *
     * @param clazz класс перечисления
     * @param <T> тип перечисления
     * @return значение_enum
     */
    private <T extends Enum<T>> T readEnum(Class<T> clazz) {
        while (true) {
            try {
                return Enum.valueOf(clazz, scanner.nextLine().trim().toUpperCase());
            } catch (IllegalArgumentException e) {
                System.out.print("Неверное значение enum. Повторите: ");
            }
        }
    }
}

