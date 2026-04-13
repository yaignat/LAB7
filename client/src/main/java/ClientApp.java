import network.ClientNetworkService;
import ui.LabWorkReader;
import commands.*;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

public class ClientApp {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        ClientNetworkService networkService = null;

        try {
            networkService = new ClientNetworkService("localhost", 5001);
            LabWorkReader reader = new LabWorkReader(scanner);

            System.out.println("Клиент запущен. Введите 'help' для списка команд.");

            while (true) {
                System.out.print("> ");
                String input = scanner.nextLine().trim();
                if (input.isEmpty()) continue;

                String[] parts = input.split("\\s+", 2);
                String commandType = parts[0];
                String argument = (parts.length > 1) ? parts[1] : null;

                try {
                    if (commandType.equalsIgnoreCase("execute_script")) {
                        if (argument == null) {
                            System.out.println("Ошибка: укажите путь к файлу скрипта.");
                            continue;
                        }
                        executeScript(argument, networkService, reader, scanner);
                        continue;
                    }

                    String response = handleCommand(commandType, argument, networkService, reader, scanner);
                    if (response != null) {
                        System.out.println("Ответ сервера:\n" + response);
                    }

                } catch (NumberFormatException e) {
                    System.out.println("Ошибка формата числа: " + e.getMessage());
                } catch (IllegalArgumentException e) {
                    System.out.println("Ошибка ввода: " + e.getMessage());
                } catch (Exception e) {
                    System.out.println("Ошибка выполнения: " + e.getMessage());
                }
            }

        } catch (Exception e) {
            System.out.println("Критическая ошибка подключения: " + e.getMessage());
        } finally {
            if (networkService != null) {
                try {
                    networkService.close();
                } catch (Exception ignored) {
                }
            }
            scanner.close();
        }
    }

    private static String handleCommand(String cmd, String arg, ClientNetworkService network, LabWorkReader reader, Scanner scanner) throws Exception {
        String response;

        switch (cmd.toLowerCase()) {
            case "help":
                return "Доступные команды:\n" +
                        "  info, show, clear, remove_first, remove_head\n" +
                        "  add, update <id>, remove_by_id <id>\n" +
                        "  add_if_max, filter_less_than_discipline <name>\n" +
                        "  sum_of_minimal_point, print_field_descending_difficulty\n" +
                        "  execute_script <file.txt>, exit\n";

            case "info":
                return network.sendCommand(new InfoCommand());
            case "show":
                return network.sendCommand(new ShowCommand());
            case "clear":
                return network.sendCommand(new ClearCommand());
            case "remove_first":
            case "remove_head":
                return network.sendCommand(new RemoveFirstCommand());
            case "sum_of_minimal_point":
                return network.sendCommand(new SumOfMinimalPointCommand());
            case "print_field_descending_difficulty":
                return network.sendCommand(new PrintFieldDescendingDifficultyCommand());

            case "add":
                System.out.println("Ввод данных для добавления:");
                return network.sendCommand(new AddCommand(reader.readLabWorkFromConsole()));

            case "add_if_max":
                System.out.println("Ввод данных для добавления (если макс):");
                return network.sendCommand(new AddIfMaxCommand(reader.readLabWorkFromConsole()));

            case "remove_by_id":
                if (arg == null) throw new IllegalArgumentException("Необходимо указать ID.");
                return network.sendCommand(new RemoveByIdCommand(Long.parseLong(arg)));

            case "update":
                if (arg == null) throw new IllegalArgumentException("Необходимо указать ID.");

                long idUpdate = Long.parseLong(arg);
                String showResponse = network.sendCommand(new ShowCommand());

                boolean exists = false;
                if (showResponse != null && !showResponse.equals("Коллекция пуста.")) {
                    String regex = "id=" + idUpdate + "[,\\}]";
                    if (showResponse.matches("(?s).*" + regex + ".*")) {
                        exists = true;
                    }
                }
                if (!exists) {
                    System.out.println("Ошибка: Элемент с ID=" + idUpdate + " не найден в коллекции.");
                    System.out.println("Введите 'show', чтобы увидеть список доступных ID.");
                    return "Обновление отменено: элемент не найден.";
                }
                System.out.println("Элемент найден. Ввод новых данных:");
                return network.sendCommand(new UpdateCommand(idUpdate, reader.readLabWorkFromConsole()));

            case "filter_less_than_discipline":
                if (arg == null) throw new IllegalArgumentException("Необходимо указать название дисциплины.");
                return network.sendCommand(new FilterLessThanDisciplineCommand(arg));

            case "save":
                return "Ошибка: команда save доступна только серверу.";

            case "exit":
                System.out.println("Завершение работы клиента.");
                System.exit(0);
                return null;

            default:
                return "Неизвестная команда: " + cmd + ". Введите 'help' для справки.";
        }
    }

    private static void executeScript(String fileName, ClientNetworkService network, LabWorkReader reader, Scanner consoleScanner) throws Exception {
        File file = new File(fileName);
        if (!file.exists()) {
            throw new FileNotFoundException("Файл не найден: " + fileName);
        }

        System.out.println("Выполнение скрипта из файла: " + fileName);

        try (Scanner fileScanner = new Scanner(file)) {
            reader.setScanner(fileScanner);

            while (fileScanner.hasNextLine()) {
                String line = fileScanner.nextLine().trim();
                if (line.isEmpty() || line.startsWith("#")) {
                    continue;
                }

                System.out.println("[Скрипт] " + line);

                String[] parts = line.split("\\s+", 2);
                String cmd = parts[0];
                String arg = (parts.length > 1) ? parts[1] : null;

                if (cmd.equalsIgnoreCase("exit")) {
                    break;
                }

                if (cmd.equalsIgnoreCase("execute_script")) {
                    System.out.println("Рекурсивный вызов скриптов запрещен.");
                    continue;
                }

                try {
                    String response = handleCommand(cmd, arg, network, reader, consoleScanner);
                    if (response != null) {
                        System.out.println("Ответ: " + response);
                    }
                } catch (Exception e) {
                    System.out.println("Ошибка в скрипте при выполнении команды '" + cmd + "': " + e.getMessage());
                }
            }
        } finally {
            reader.setScanner(consoleScanner);
        }
    }
}