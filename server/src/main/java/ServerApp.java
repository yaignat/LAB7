import collection.CollectionManager;
import file.FileManager;
import command.CommandInvoker;
import network.ServerNetworkService;
import data.LabWork;

import java.io.File;
import java.util.LinkedList;
import java.util.List;

public class ServerApp {
    public static void main(String[] args) {
        System.out.println("Запуск сервера...");

        String fileName = System.getenv("LAB5_FILE");
        if (fileName == null) {
            System.err.println("Ошибка: Переменная окружения LAB5_FILE не установлена!");
            return;
        }

        try {
            File file = new File(fileName);
            FileManager fileManager = new FileManager(file);

            LinkedList<LabWork> collectionData;
            if (!file.exists() || file.length() == 0) {
                collectionData = new LinkedList<>();
                fileManager.saveToFile(collectionData);
                System.out.println("Файл не найден или пуст. Создан новый файл.");
            } else {
                collectionData = fileManager.readElementsFromFile();
                System.out.println("Файл успешно загружен.");
            }

            CollectionManager collectionManager = new CollectionManager(collectionData);
            System.out.println("Коллекция загружена. Элементов: " + collectionManager.getSize());

            CommandInvoker invoker = new CommandInvoker(collectionManager, fileManager);


            int port = 5001;
            ServerNetworkService networkService = new ServerNetworkService(port, invoker);
            if (!collectionData.isEmpty()) {
                long maxId = collectionData.stream()
                        .mapToLong(LabWork::getId)
                        .max()
                        .orElse(0);
                LabWork.setNextId(maxId + 1);
                System.out.println("Счетчик ID установлен на: " + (maxId + 1));
            } else {
                LabWork.setNextId(1);
            }

            System.out.println("Сервер слушает UDP порт " + port + "...");
            System.out.println("Нажмите Ctrl+C для остановки и сохранения данных.");

            Runtime.getRuntime().addShutdownHook(new Thread(() -> {
                System.out.println("\nОстановка сервера. Сохранение коллекции...");
                try {
                    fileManager.saveToFile(collectionManager.getCollection());
                    System.out.println("Данные сохранены.");
                } catch (Exception e) {
                    System.err.println("Ошибка сохранения: " + e.getMessage());
                }
            }));

            networkService.start();

        } catch (Exception e) {
            System.err.println("Критическая ошибка запуска: " + e.getMessage());
            e.printStackTrace();
        }
    }
}