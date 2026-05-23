package network;

import commands.Command;

import java.io.*;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.DatagramChannel;

public class ClientNetworkService {
    private final DatagramChannel channel;
    private final InetSocketAddress serverAddress;
    private final String login;
    private final String passwordHash;

    public ClientNetworkService(String host, int port, String login, String passwordHash) throws IOException {
        this.channel = DatagramChannel.open();
        this.channel.configureBlocking(false);
        this.serverAddress = new InetSocketAddress(host, port);
        this.login = login;
        this.passwordHash = passwordHash;
    }

    public String sendCommand(Command command) throws IOException, ClassNotFoundException, InterruptedException {
        RequestWrapper wrapper = new RequestWrapper(login, passwordHash, command);
        byte[] data = serialize(wrapper);
        ByteBuffer buffer = ByteBuffer.wrap(data);
        channel.send(buffer, serverAddress);

        channel.configureBlocking(true);
        try {
            ByteBuffer responseBuffer = ByteBuffer.allocate(65535);
            responseBuffer.clear();

            InetSocketAddress sender = (InetSocketAddress) channel.receive(responseBuffer);

            if (sender != null) {
                responseBuffer.flip();
                byte[] respData = new byte[responseBuffer.remaining()];
                responseBuffer.get(respData);
                return (String) deserialize(respData);
            }
        } finally {
            channel.configureBlocking(false);
        }

        throw new IOException("Server timeout");
    }

    private byte[] serialize(Object obj) throws IOException {
        try (ByteArrayOutputStream bos = new ByteArrayOutputStream();
             ObjectOutputStream oos = new ObjectOutputStream(bos)) {
            oos.writeObject(obj);
            return bos.toByteArray();
        }
    }

    private Object deserialize(byte[] data) throws IOException, ClassNotFoundException {
        try (ByteArrayInputStream bis = new ByteArrayInputStream(data);
             ObjectInputStream ois = new ObjectInputStream(bis)) {
            return ois.readObject();
        }
    }

    public void close() throws IOException {
        channel.close();
    }
}