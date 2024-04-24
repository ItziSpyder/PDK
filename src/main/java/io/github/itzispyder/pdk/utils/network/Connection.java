package io.github.itzispyder.pdk.utils.network;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.net.Socket;
import java.util.concurrent.ConcurrentLinkedQueue;

public class Connection extends ConnectionThread {

    private final ConcurrentLinkedQueue<String> queuedMessages;
    private final Socket socket;
    private final Server host;

    public Connection(Server host, Socket socket) {
        this.socket = socket;
        this.host = host;
        this.queuedMessages = new ConcurrentLinkedQueue<>();
        this.setName("[CONNECTION:%s]".formatted(getAddress()));
        start();
        new Thread(this::receive, "[CONNECTION-RECEIVER:%s]".formatted(getAddress())).start();
    }

    @Override
    public void run() {
        try {
            DataOutputStream dos = new DataOutputStream(socket.getOutputStream());
            while (!isInterrupted()) {
                if (!queuedMessages.isEmpty()) {
                    String msg = queuedMessages.poll();
                    info(msg);
                    dos.writeUTF(msg);
                    dos.flush();
                }
            }
            dos.close();
        }
        catch (Exception ex) {
            error("error transmitting message: %s", ex.getMessage());
        }
    }

    public void receive() {
        try {
            DataInputStream dis = new DataInputStream(socket.getInputStream());

            while (!isInterrupted()) {
                onReceiveMessage(dis.readUTF());
            }

            dis.close();
        }
        catch (Exception ex) {
            error("cannot receive data from client: %s", getAddress());
        }
        deadFish();
    }

    public synchronized void onReceiveMessage(String message) {
        if (!Response.isResponsePattern(message)) {
            host.info("(%s) message received: %s", getAddress(), message);
            return;
        }

        try {
            Response res = Response.parse(message);
            if (res.getMethod() == Response.Method.TO_SERVER && res.getType() == Response.Type.DEAD_FISH)
                this.deadFish();
        }
        catch (Exception ex) {
            host.error("Response parse failure: " + ex.getMessage());
        }
    }

    public synchronized void deadFish() {
        host.info("disconnecting %s ...", this.getAddress());
        host.removeConnection(getAddress());
    }

    public void disconnect() {
        info("disconnecting from %s ...", getAddress());
        queuedMessages.clear();
        try {
            socket.close();
        }
        catch (Exception ex) {
            error("connection closed");
        }
        interrupt();
    }

    public void sendMessage(String str) {
        if (str != null)
            queuedMessages.add(str);
    }

    public void sendMessage(Response res) {
        sendMessage(res.toString());
    }

    public Address getAddress() {
        return new Address(socket);
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof Connection conn))
            return false;
        return this.getAddress().equals(conn.getAddress());
    }
}
