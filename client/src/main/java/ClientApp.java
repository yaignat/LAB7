import commands.*;
import data.*;
import security.PasswordUtils;
import network.ClientNetworkService;
import network.InputValidator;

import java.io.IOException;
import java.util.Scanner;

public class ClientApp {
    private static ClientNetworkService network;
    private static Scanner scanner;

    public static void main(String[] args) {
        scanner = new Scanner(System.in);

        try {
            System.out.print("Login: ");
            String login = scanner.nextLine().trim();

            System.out.print("Password: ");
            String password = scanner.nextLine().trim();

            String passwordHash = PasswordUtils.hashPassword(password);

            network = new ClientNetworkService("localhost", 5001, login, passwordHash);

            System.out.println("Auth successful. Type 'help' for commands.\n");

            while (true) {
                System.out.print("> ");
                String input = scanner.nextLine().trim();
                if (input.isEmpty()) continue;

                String[] parts = input.split("\\s+", 2);
                String cmd = parts[0];
                String arg = parts.length > 1 ? parts[1] : null;

                if (cmd.equalsIgnoreCase("exit")) break;

                try {
                    String response = handleCommand(cmd, arg);
                    System.out.println(response);
                } catch (Exception e) {
                    System.out.println("Error: " + e.getMessage());
                }
            }

        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
            e.printStackTrace();
        } finally {
            if (network != null) {
                try { network.close(); } catch (Exception ignored) {}
            }
            scanner.close();
        }
    }

    private static String handleCommand(String cmd, String arg) {
        try {
            switch (cmd.toLowerCase()) {
                case "info":
                    return network.sendCommand(new InfoCommand());

                case "show":
                    return network.sendCommand(new ShowCommand());

                case "add":
                    return handleAdd();

                case "update":
                    if (arg == null) return "Укажите ID";
                    return handleUpdate(InputValidator.parseId(arg));

                case "remove_by_id":
                    if (arg == null) return "Укажите ID";
                    return network.sendCommand(new RemoveByIdCommand(InputValidator.parseId(arg)));

                case "clear":
                    return network.sendCommand(new ClearCommand());

                case "remove_first":
                    return network.sendCommand(new RemoveFirstCommand());

                case "sum_of_minimal_point":
                    return network.sendCommand(new SumOfMinimalPointCommand());

                case "print_field_descending_difficulty":
                    return network.sendCommand(new PrintFieldDescendingDifficultyCommand());

                case "filter_less_than_discipline":
                    if (arg == null) return "Укажите название дисциплины";
                    return network.sendCommand(new FilterLessThanDisciplineCommand(arg));

                case "add_if_max":
                    return handleAddIfMax();

                case "exit":
                    System.exit(0);
                    return "";

                case "help":
                    return "Доступные команды: register, info, show, add, update, remove_by_id, clear, remove_first, sum_of_minimal_point, print_field_descending_difficulty, filter_less_than_discipline, add_if_max, exit";
                case "register":
                    return handleRegister();
                default:
                    return "Неизвестная команда: " + cmd;
            }
        } catch (IOException | ClassNotFoundException | InterruptedException e) {
            return "Ошибка сети: " + e.getMessage();
        } catch (IllegalArgumentException e) {
            return "Ошибка ввода: " + e.getMessage();
        }
    }

    private static String handleAdd() throws IOException, ClassNotFoundException, InterruptedException {
        System.out.println("=== ДОБАВЛЕНИЕ ЭЛЕМЕНТА ===");

        String name = InputValidator.readNonEmpty(scanner, "Name: ");
        double x = InputValidator.readDouble(scanner, "X: ");
        long y = InputValidator.readLong(scanner, "Y: ");
        float minPoint = InputValidator.readFloat(scanner, "Minimal point: ");
        double pqMax = InputValidator.readDouble(scanner, "Personal qualities max: ");
        Difficulty difficulty = InputValidator.readDifficulty(scanner);
        String discName = InputValidator.readNonEmpty(scanner, "Discipline name: ");
        int lectureHours = InputValidator.readInt(scanner, "Lecture hours: ");

        var disc = new Discipline(discName, lectureHours);
        var lw = new LabWork(name, new Coordinates(x, y), minPoint, pqMax, difficulty, disc);

        return network.sendCommand(new AddCommand(lw));
    }

    private static String handleUpdate(long id) throws IOException, ClassNotFoundException, InterruptedException {
        System.out.println("=== ОБНОВЛЕНИЕ ЭЛЕМЕНТА ID=" + id + " ===");

        String name = InputValidator.readNonEmpty(scanner, "Name: ");
        double x = InputValidator.readDouble(scanner, "X: ");
        long y = InputValidator.readLong(scanner, "Y: ");
        float minPoint = InputValidator.readFloat(scanner, "Minimal point: ");
        double pqMax = InputValidator.readDouble(scanner, "Personal qualities max: ");
        Difficulty difficulty = InputValidator.readDifficulty(scanner);
        String discName = InputValidator.readNonEmpty(scanner, "Discipline name: ");
        int lectureHours = InputValidator.readInt(scanner, "Lecture hours: ");

        var disc = new Discipline(discName, lectureHours);
        var lw = new LabWork(name, new Coordinates(x, y), minPoint, pqMax, difficulty, disc);

        return network.sendCommand(new UpdateCommand(id, lw));
    }

    private static String handleAddIfMax() throws IOException, ClassNotFoundException, InterruptedException {
        System.out.println("=== ДОБАВЛЕНИЕ ЕСЛИ МАКСИМУМ ===");

        String name = InputValidator.readNonEmpty(scanner, "Name: ");
        double x = InputValidator.readDouble(scanner, "X: ");
        long y = InputValidator.readLong(scanner, "Y: ");
        float minPoint = InputValidator.readFloat(scanner, "Minimal point: ");
        double pqMax = InputValidator.readDouble(scanner, "Personal qualities max: ");
        Difficulty difficulty = InputValidator.readDifficulty(scanner);
        String discName = InputValidator.readNonEmpty(scanner, "Discipline name: ");
        int lectureHours = InputValidator.readInt(scanner, "Lecture hours: ");

        var disc = new Discipline(discName, lectureHours);
        var lw = new LabWork(name, new Coordinates(x, y), minPoint, pqMax, difficulty, disc);

        return network.sendCommand(new AddIfMaxCommand(lw));
    }
    private static String handleRegister() throws IOException, ClassNotFoundException, InterruptedException {
        System.out.println("=== РЕГИСТРАЦИЯ НОВОГО ПОЛЬЗОВАТЕЛЯ ===");

        String newLogin;
        while (true) {
            newLogin = InputValidator.readNonEmpty(scanner, "Придумайте логин: ");
            if (newLogin.length() < 3) {
                System.out.println("Ошибка: логин должен содержать минимум 3 символа!");
                continue;
            }
            if (newLogin.length() > 20) {
                System.out.println("Ошибка: логин должен содержать максимум 20 символов!");
                continue;
            }
            break;
        }

        String newPassword;
        String confirmPassword;
        while (true) {
            System.out.print("Придумайте пароль: ");
            newPassword = scanner.nextLine().trim();

            if (newPassword.isEmpty()) {
                System.out.println("Ошибка: пароль не может быть пустым!");
                continue;
            }
            if (newPassword.length() < 6) {
                System.out.println("Ошибка: пароль должен содержать минимум 6 символов!");
                continue;
            }

            System.out.print("Подтвердите пароль: ");
            confirmPassword = scanner.nextLine().trim();

            if (!newPassword.equals(confirmPassword)) {
                System.out.println("Ошибка: пароли не совпадают!");
                continue;
            }
            break;
        }

        String passwordHash = PasswordUtils.hashPassword(newPassword);

        return network.sendCommand(new RegisterCommand(newLogin, passwordHash));
    }
}