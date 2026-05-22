import collection.CollectionManager;
import command.CommandInvoker;
import database.DatabaseManager;
import network.ServerNetworkService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ServerApp {
    private static final Logger logger = LoggerFactory.getLogger(ServerApp.class);

    public static void main(String[] args) {
        logger.info("=== ЗАПУСК СЕРВЕРА ===");

        try {
            DatabaseManager dbManager = new DatabaseManager();
            logger.info("DatabaseManager initialized");

            var dbCollection = dbManager.loadAllLabWorks();
            logger.info("Loaded {} elements from database", dbCollection.size());

            CollectionManager collectionManager = new CollectionManager(dbCollection);

            CommandInvoker invoker = new CommandInvoker(collectionManager, dbManager);

            ServerNetworkService server = new ServerNetworkService(5001, invoker, dbManager);

            Runtime.getRuntime().addShutdownHook(new Thread(() -> {
                logger.info("Stopping server...");
                try { Thread.sleep(500); } catch (InterruptedException e) {}
                logger.info("Server stopped");
            }));

            logger.info("Server started on port 5001");
            server.start();

        } catch (Exception e) {
            logger.error("Critical server error: {}", e.getMessage(), e);
            System.exit(1);
        }
    }
}