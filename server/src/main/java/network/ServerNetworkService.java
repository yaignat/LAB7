package network;

import command.CommandInvoker;
import database.DatabaseManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.DatagramChannel;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.util.Iterator;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ServerNetworkService {
    private static final Logger logger = LoggerFactory.getLogger(ServerNetworkService.class);

    private final int port;
    private final CommandInvoker invoker;
    private final DatabaseManager dbManager;
    private final ExecutorService senderPool = Executors.newCachedThreadPool();

    public ServerNetworkService(int port, CommandInvoker invoker, DatabaseManager dbManager) {
        this.port = port;
        this.invoker = invoker;
        this.dbManager = dbManager;
    }

    public void start() {
        logger.info("Starting UDP server on port {}", port);

        try (DatagramChannel channel = DatagramChannel.open()) {
            channel.configureBlocking(false);
            channel.bind(new InetSocketAddress(port));

            Selector selector = Selector.open();
            channel.register(selector, SelectionKey.OP_READ);

            logger.info("Server ready");

            while (!Thread.currentThread().isInterrupted()) {
                selector.select();

                Set<SelectionKey> keys = selector.selectedKeys();
                Iterator<SelectionKey> iterator = keys.iterator();

                while (iterator.hasNext()) {
                    SelectionKey key = iterator.next();
                    iterator.remove();

                    if (key.isReadable()) {
                        new Thread(() -> handleRequest(channel)).start();
                    }
                }
            }
        } catch (Exception e) {
            logger.error("Server error: {}", e.getMessage(), e);
        } finally {
            senderPool.shutdown();
        }
    }

    private void handleRequest(DatagramChannel channel) {
        logger.info(">>> НАЧАЛО ОБРАБОТКИ ЗАПРОСА <<<");

        InetSocketAddress clientAddress = null;

        try {
            ByteBuffer buffer = ByteBuffer.allocate(65535);
            buffer.clear();

            logger.info("Ожидание получения пакета...");
            clientAddress = (InetSocketAddress) channel.receive(buffer);

            if (clientAddress == null) {
                logger.warn("Получен null адрес клиента!");
                return;
            }

            logger.info("Пакет получен от: {}", clientAddress.getAddress().getHostAddress());

            buffer.flip();
            byte[] data = new byte[buffer.remaining()];
            buffer.get(data);
            logger.info("Размер полученных данных: {} байт", data.length);

            RequestWrapper wrapper;
            try (ObjectInputStream ois = new ObjectInputStream(new ByteArrayInputStream(data))) {
                wrapper = (RequestWrapper) ois.readObject();
                logger.info("RequestWrapper десериализован успешно");
            }

            logger.info("Login из wrapper: '{}'", wrapper.getLogin());
            logger.info("PasswordHash из wrapper: '{}' (длина: {})",
                    wrapper.getPasswordHash(),
                    wrapper.getPasswordHash() != null ? wrapper.getPasswordHash().length() : 0);
            logger.info("Command type: '{}'", wrapper.getCommand() != null ? wrapper.getCommand().getType() : "NULL");

            int userId = dbManager.validateUser(wrapper.getLogin(), wrapper.getPasswordHash());
            logger.info("Результат авторизации - userId: {}", userId);

            if (userId == -1) {
                logger.warn("Auth failed для пользователя: '{}'", wrapper.getLogin());
                sendResponse(channel, clientAddress, "Auth failed: invalid login or password");
                return;
            }

            String result = invoker.execute(wrapper.getCommand(), userId, wrapper.getLogin());

            final DatagramChannel finalChannel = channel;
            final InetSocketAddress finalClientAddress = clientAddress;
            final String finalResult = result;

            senderPool.submit(() -> {
                try {
                    sendResponse(finalChannel, finalClientAddress, finalResult);
                    logger.debug("Ответ отправлен клиенту {}", finalClientAddress);
                } catch (IOException e) {
                    logger.error("Ошибка отправки ответа: {}", e.getMessage());
                }
            });

        } catch (ClassNotFoundException e) {
            logger.error("Ошибка десериализации: {}", e.getMessage(), e);
            if (clientAddress != null) {
                try { sendResponse(channel, clientAddress, "Invalid request format"); }
                catch (IOException ignored) {}
            }
        } catch (Exception e) {
            logger.error("КРИТИЧЕСКАЯ ОШИБКА обработки запроса: {}", e.getMessage(), e);
            e.printStackTrace(); // ← ВАЖНО: полный стектрейс
            if (clientAddress != null) {
                try { sendResponse(channel, clientAddress, "Internal server error"); }
                catch (IOException ignored) {}
            }
        }
    }

    private void sendResponse(DatagramChannel channel, InetSocketAddress address, String message)
            throws IOException {

        byte[] data;
        try (ByteArrayOutputStream bos = new ByteArrayOutputStream();
             ObjectOutputStream oos = new ObjectOutputStream(bos)) {
            oos.writeObject(message);
            data = bos.toByteArray();
        }

        ByteBuffer buffer = ByteBuffer.wrap(data);
        channel.send(buffer, address);
    }
}