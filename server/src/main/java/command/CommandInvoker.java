package command;

import commands.*;
import collection.CollectionManager;
import file.FileManager;
import data.LabWork;

public class CommandInvoker {
    private final CollectionManager collectionManager;
    private final FileManager fileManager;

    public CommandInvoker(CollectionManager collectionManager, FileManager fileManager) {
        this.collectionManager = collectionManager;
        this.fileManager = fileManager;
    }

    /**
     * Главный метод: принимает любую команду и выполняет её.
     */
    public String execute(Command command) {
        if (command == null) return "Ошибка: пустая команда";

        switch (command.getType()) {
            case "info":
                return collectionManager.getInfo();

            case "show":

                return collectionManager.showSortedByName();

            case "add":
                LabWork lwAdd = ((AddCommand) command).getLabWork();
                lwAdd.setId(generateId());
                lwAdd.setCreationDate(new java.util.Date());
                collectionManager.add(lwAdd);
                return "Элемент успешно добавлен";

            case "update":
                Long idUpdate = ((UpdateCommand) command).getId();
                LabWork lwUpdate = ((UpdateCommand) command).getLabWork();
                lwUpdate.setCreationDate(collectionManager.getCreationDateById(idUpdate));
                collectionManager.updateById(idUpdate, lwUpdate);
                return "Элемент обновлен";

            case "remove_by_id":
                Long idRemove = ((RemoveByIdCommand) command).getId();
                return collectionManager.removeById(idRemove) ? "Элемент удален" : "Элемент не найден";

            case "clear":
                collectionManager.clear();
                return "Коллекция очищена";

            case "save":
                try {
                    fileManager.saveToFile(collectionManager.getCollection());
                    return "Коллекция сохранена в файл";
                } catch (Exception e) {
                    return "Ошибка сохранения: " + e.getMessage();
                }

            case "remove_first":
                return collectionManager.removeFirst() ? "Первый элемент удален" : "Коллекция пуста";

            case "sum_of_minimal_point":
                return "Сумма minimalPoint: " + collectionManager.sumOfMinimalPoint();

            case "print_field_descending_difficulty":
                return collectionManager.printFieldDescendingDifficulty();

            case "filter_less_than_discipline":
                String discName = ((FilterLessThanDisciplineCommand) command).getDisciplineName();
                return collectionManager.filterLessThanDiscipline(discName);

            case "add_if_max":
                LabWork lwMax = ((AddIfMaxCommand) command).getLabWork();
                lwMax.setId(generateId());
                lwMax.setCreationDate(new java.util.Date());
                return collectionManager.addIfMax(lwMax) ? "Элемент добавлен (как максимальный)" : "Элемент не добавлен (не максимальный)";

            case "exit":
                return "Сервер получил команду exit (но продолжает работать, пока не выключат вручную)";

            default:
                return "Неизвестная команда: " + command.getType();
        }
    }

    private long generateId() {
        return System.currentTimeMillis() % 100000;
    }
}