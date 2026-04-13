package network;

import commands.Command;
import command.CommandInvoker;
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

public class ServerNetworkService {
    private static final Logger logger = LoggerFactory.getLogger(ServerNetworkService.class);
    private final int port;
    private final CommandInvoker invoker;

    public ServerNetworkService(int port, CommandInvoker invoker) {
        this.port = port;
        this.invoker = invoker;
    }

    public void start() {
        try (DatagramChannel channel = DatagramChannel.open()) {
            channel.configureBlocking(false);
            channel.socket().bind(new InetSocketAddress(port));

            Selector selector = Selector.open();
            channel.register(selector, SelectionKey.OP_READ);

            logger.info("Сервер запущен на порту {}", port);
            logger.info("Ожидание подключений клиентов...");

            while (!Thread.currentThread().isInterrupted()) {
                selector.select();

                Set<SelectionKey> keys = selector.selectedKeys();
                Iterator<SelectionKey> iterator = keys.iterator();

                while (iterator.hasNext()) {
                    SelectionKey key = iterator.next();
                    iterator.remove();

                    if (key.isReadable()) {
                        handleRead(channel);
                    }
                }
            }
        } catch (IOException e) {
            logger.error("Ошибка сети: {}", e.getMessage());
            e.printStackTrace();
        } finally {
            logger.info("Сервер остановлен.");
        }
    }

    private void handleRead(DatagramChannel channel) throws IOException {
        ByteBuffer buffer = ByteBuffer.allocate(65535);
        buffer.clear();

        InetSocketAddress clientAddress = (InetSocketAddress) channel.receive(buffer);

        if (clientAddress != null) {
            buffer.flip();
            byte[] data = new byte[buffer.remaining()];
            buffer.get(data);

            try {
                Command command = deserialize(data);
                logger.info("Получена команда '{}' от {}", command.getType(), clientAddress);

                String result = invoker.execute(command);

                byte[] responseData = serialize(result);
                ByteBuffer responseBuffer = ByteBuffer.wrap(responseData);
                channel.send(responseBuffer, clientAddress);
                logger.debug("Ответ отправлен клиенту {}", clientAddress);

            } catch (ClassNotFoundException e) {
                logger.error("Ошибка десериализации: класс не найден", e);
                sendError(channel, clientAddress, "Ошибка: неизвестный тип команды");
            } catch (Exception e) {
                logger.error("Ошибка при выполнении команды: {}", e.getMessage());
                sendError(channel, clientAddress, "Ошибка сервера: " + e.getMessage());
            }
        }
    }

    private void sendError(DatagramChannel channel, InetSocketAddress address, String message) throws IOException {
        byte[] data = serialize(message);
        channel.send(ByteBuffer.wrap(data), address);
    }

    private Command deserialize(byte[] data) throws IOException, ClassNotFoundException {
        try (ByteArrayInputStream bis = new ByteArrayInputStream(data);
             ObjectInputStream ois = new ObjectInputStream(bis)) {
            return (Command) ois.readObject();
        }
    }

    private byte[] serialize(Object obj) throws IOException {
        try (ByteArrayOutputStream bos = new ByteArrayOutputStream();
             ObjectOutputStream oos = new ObjectOutputStream(bos)) {
            oos.writeObject(obj);
            oos.flush();
            return bos.toByteArray();
        }
    }
}