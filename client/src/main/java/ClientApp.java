import network.ClientNetworkService;
import commands.*;
import security.PasswordUtils;

import java.util.Scanner;

public class ClientApp {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        ClientNetworkService network = null;

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
                    String response = handleCommand(cmd, arg, network, scanner);
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

    private static String handleCommand(String cmd, String arg, ClientNetworkService network, Scanner scanner)
            throws Exception {

        switch (cmd.toLowerCase()) {
            case "help":
                return "Available commands: info, show, add, update, remove_by_id, clear, " +
                        "remove_first, sum_of_minimal_point, print_field_descending_difficulty, " +
                        "filter_less_than_discipline, add_if_max, exit";

            case "info":
                return network.sendCommand(new InfoCommand());

            case "show":
                return network.sendCommand(new ShowCommand());

            case "add":
                System.out.println("=== ДОБАВЛЕНИЕ LABWORK ===");

                String name = null;
                double x = 0;
                long y = 0;
                float minPoint = 0;
                double pqMax = 0;
                data.Difficulty difficulty = null;
                String discName = null;
                int lectureHours = 0;

                while (name == null || name.isEmpty()) {
                    System.out.print("Name: ");
                    name = scanner.nextLine().trim();
                    if (name.isEmpty()) System.out.println("Ошибка: имя не может быть пустым!");
                }

                while (true) {
                    System.out.print("X: ");
                    try {
                        x = Double.parseDouble(scanner.nextLine().trim());
                        break;
                    } catch (NumberFormatException e) {
                        System.out.println("Ошибка: введите число!");
                    }
                }

                while (true) {
                    System.out.print("Y: ");
                    try {
                        y = Long.parseLong(scanner.nextLine().trim());
                        break;
                    } catch (NumberFormatException e) {
                        System.out.println("Ошибка: введите целое число!");
                    }
                }

                while (true) {
                    System.out.print("Minimal point: ");
                    try {
                        minPoint = Float.parseFloat(scanner.nextLine().trim());
                        break;
                    } catch (NumberFormatException e) {
                        System.out.println("Ошибка: введите число!");
                    }
                }

                while (true) {
                    System.out.print("Personal qualities max: ");
                    try {
                        pqMax = Double.parseDouble(scanner.nextLine().trim());
                        break;
                    } catch (NumberFormatException e) {
                        System.out.println("Ошибка: введите число!");
                    }
                }

                while (difficulty == null) {
                    System.out.print("Difficulty (EASY/MEDIUM/HARD/VERY_HARD/HOPELESS): ");
                    String diffStr = scanner.nextLine().trim().toUpperCase();

                    if (diffStr.isEmpty()) {
                        System.out.println("Ошибка: введите Difficulty!");
                        continue;
                    }

                    try {
                        difficulty = data.Difficulty.valueOf(diffStr);
                    } catch (IllegalArgumentException e) {
                        System.out.println("Ошибка: '" + diffStr + "' - недопустимое значение!");
                    }
                }
                while (discName == null || discName.isEmpty()) {
                    System.out.print("Discipline name: ");
                    discName = scanner.nextLine().trim();
                    if (discName.isEmpty()) System.out.println("Ошибка: название дисциплины не может быть пустым!");
                }

                while (true) {
                    System.out.print("Lecture hours (нажмите Enter для пропуска): ");
                    String lectureHoursStr = scanner.nextLine().trim();
                    if (lectureHoursStr.isEmpty()) {
                        lectureHours = 0;
                        break;
                    }

                    try {
                        lectureHours = Integer.parseInt(lectureHoursStr);
                        break;
                    } catch (NumberFormatException e) {
                        System.out.println("Ошибка: введите целое число или оставьте пустым!");
                    }
                }

                var disc = new data.Discipline(discName, lectureHours);

                var lw = new data.LabWork(
                        name,
                        new data.Coordinates(x, y),
                        minPoint,
                        pqMax,
                        difficulty,
                        disc
                );

                return network.sendCommand(new AddCommand(lw));

            case "update":
                if (arg == null) return "Specify ID";

                long idUpdate = 0;
                try {
                    idUpdate = Long.parseLong(arg);
                } catch (NumberFormatException e) {
                    return "Ошибка: ID должен быть числом!";
                }

                System.out.println("=== ОБНОВЛЕНИЕ ЭЛЕМЕНТА ID=" + idUpdate + " ===");

                String newName = null;
                double newX = 0;
                long newY = 0;
                float newMinPoint = 0;
                double newPqMax = 0;
                data.Difficulty newDifficulty = null;
                String newDiscName = null;
                int newLectureHours = 0;

                while (newName == null || newName.isEmpty()) {
                    System.out.print("Name: ");
                    newName = scanner.nextLine().trim();
                    if (newName.isEmpty()) System.out.println("Ошибка: имя не может быть пустым!");
                }

                while (true) {
                    System.out.print("X: ");
                    try {
                        newX = Double.parseDouble(scanner.nextLine().trim());
                        break;
                    } catch (NumberFormatException e) {
                        System.out.println("Ошибка: введите число!");
                    }
                }

                while (true) {
                    System.out.print("Y: ");
                    try {
                        newY = Long.parseLong(scanner.nextLine().trim());
                        break;
                    } catch (NumberFormatException e) {
                        System.out.println("Ошибка: введите целое число!");
                    }
                }

                while (true) {
                    System.out.print("Minimal point: ");
                    try {
                        newMinPoint = Float.parseFloat(scanner.nextLine().trim());
                        break;
                    } catch (NumberFormatException e) {
                        System.out.println("Ошибка: введите число!");
                    }
                }

                while (true) {
                    System.out.print("Personal qualities max: ");
                    try {
                        newPqMax = Double.parseDouble(scanner.nextLine().trim());
                        break;
                    } catch (NumberFormatException e) {
                        System.out.println("Ошибка: введите число!");
                    }
                }

                while (newDifficulty == null) {
                    System.out.print("Difficulty (EASY/MEDIUM/HARD/VERY_HARD/HOPELESS): ");
                    String diffStr = scanner.nextLine().trim().toUpperCase();
                    if (diffStr.isEmpty()) {
                        System.out.println("Ошибка: введите Difficulty!");
                        continue;
                    }
                    try {
                        newDifficulty = data.Difficulty.valueOf(diffStr);
                    } catch (IllegalArgumentException e) {
                        System.out.println("Ошибка: '" + diffStr + "' — недопустимое значение!");
                    }
                }

                while (newDiscName == null || newDiscName.isEmpty()) {
                    System.out.print("Discipline name: ");
                    newDiscName = scanner.nextLine().trim();
                    if (newDiscName.isEmpty()) System.out.println("Ошибка: название не может быть пустым!");
                }

                boolean hoursEntered = false;
                while (!hoursEntered) {
                    System.out.print("Lecture hours (нажмите Enter для пропуска): ");
                    String hoursStr = scanner.nextLine().trim();

                    if (hoursStr.isEmpty()) {
                        newLectureHours = 0;
                        hoursEntered = true;
                    } else {
                        try {
                            newLectureHours = Integer.parseInt(hoursStr);
                            hoursEntered = true;
                        } catch (NumberFormatException e) {
                            System.out.println("Ошибка: введите целое число или нажмите Enter!");
                        }
                    }
                }

                var newDisc = new data.Discipline(newDiscName, newLectureHours);

                var lwUpdate = new data.LabWork(
                        newName,
                        new data.Coordinates(newX, newY),
                        newMinPoint,
                        newPqMax,
                        newDifficulty,
                        newDisc
                );

                return network.sendCommand(new UpdateCommand(idUpdate, lwUpdate));

            case "remove_by_id":
                if (arg == null) return "Specify ID";
                return network.sendCommand(new RemoveByIdCommand(Long.parseLong(arg)));

            case "clear":
                return network.sendCommand(new ClearCommand());

            case "remove_first":
                return network.sendCommand(new RemoveFirstCommand());

            case "sum_of_minimal_point":
                return network.sendCommand(new SumOfMinimalPointCommand());

            case "print_field_descending_difficulty":
                return network.sendCommand(new PrintFieldDescendingDifficultyCommand());

            case "filter_less_than_discipline":
                if (arg == null) return "Specify discipline name";
                return network.sendCommand(new FilterLessThanDisciplineCommand(arg));

            case "add_if_max":
                System.out.println("=== ДОБАВЛЕНИЕ ЕСЛИ МАКСИМУМ ===");

                String nameIfMax = null;
                double xIfMax = 0;
                long yIfMax = 0;
                float minPointIfMax = 0;
                double pqMaxIfMax = 0;
                data.Difficulty difficultyIfMax = null;
                String discNameIfMax = null;
                int lectureHoursIfMax = 0;

                while (nameIfMax == null || nameIfMax.isEmpty()) {
                    System.out.print("Name: ");
                    nameIfMax = scanner.nextLine().trim();
                    if (nameIfMax.isEmpty()) System.out.println("Ошибка: имя не может быть пустым!");
                }

                while (true) {
                    System.out.print("X: ");
                    try {
                        xIfMax = Double.parseDouble(scanner.nextLine().trim());
                        break;
                    } catch (NumberFormatException e) {
                        System.out.println("Ошибка: введите число!");
                    }
                }

                while (true) {
                    System.out.print("Y: ");
                    try {
                        yIfMax = Long.parseLong(scanner.nextLine().trim());
                        break;
                    } catch (NumberFormatException e) {
                        System.out.println("Ошибка: введите целое число!");
                    }
                }

                while (true) {
                    System.out.print("Minimal point: ");
                    try {
                        minPointIfMax = Float.parseFloat(scanner.nextLine().trim());
                        break;
                    } catch (NumberFormatException e) {
                        System.out.println("Ошибка: введите число!");
                    }
                }

                while (true) {
                    System.out.print("Personal qualities max: ");
                    try {
                        pqMaxIfMax = Double.parseDouble(scanner.nextLine().trim());
                        break;
                    } catch (NumberFormatException e) {
                        System.out.println("Ошибка: введите число!");
                    }
                }

                while (difficultyIfMax == null) {
                    System.out.print("Difficulty (EASY/MEDIUM/HARD): ");
                    String diffStr = scanner.nextLine().trim().toUpperCase();
                    if (diffStr.isEmpty()) {
                        System.out.println("Ошибка: введите EASY, MEDIUM или HARD!");
                        continue;
                    }
                    try {
                        difficultyIfMax = data.Difficulty.valueOf(diffStr);
                    } catch (IllegalArgumentException e) {
                        System.out.println("Ошибка: '" + diffStr + "' — недопустимое значение!");
                    }
                }

                while (discNameIfMax == null || discNameIfMax.isEmpty()) {
                    System.out.print("Discipline name: ");
                    discNameIfMax = scanner.nextLine().trim();
                    if (discNameIfMax.isEmpty()) System.out.println("Ошибка: название не может быть пустым!");
                }

                while (true) {
                    System.out.print("Lecture hours (нажмите Enter для 0): ");
                    String hoursStr = scanner.nextLine().trim();
                    if (hoursStr.isEmpty()) {
                        lectureHoursIfMax = 0;
                        break;
                    }
                    try {
                        lectureHoursIfMax = Integer.parseInt(hoursStr);
                        break;
                    } catch (NumberFormatException e) {
                        System.out.println("Ошибка: введите целое число или нажмите Enter!");
                    }
                }

                var discIfMax = new data.Discipline(discNameIfMax, lectureHoursIfMax);

                var lwIfMax = new data.LabWork(
                        nameIfMax,
                        new data.Coordinates(xIfMax, yIfMax),
                        minPointIfMax,
                        pqMaxIfMax,
                        difficultyIfMax,
                        discIfMax
                );

                return network.sendCommand(new AddIfMaxCommand(lwIfMax));

            default:
                return "Unknown command: " + cmd;
        }
    }
}