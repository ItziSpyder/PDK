package io.github.itzispyder.pdk.utils.network;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.net.Socket;
import java.util.UUID;
import java.util.concurrent.ConcurrentLinkedQueue;

public class Client extends ConnectionThread {

    private final Address serverAddress;
    private Address serverSideAddress;
    private final ConcurrentLinkedQueue<String> queuedMessages;
    private Socket socket;
    private final UUID id;

    public Client(Address connectingTo) {
        this.serverAddress = connectingTo;
        this.id = UUID.randomUUID();
        this.setName("[CLIENT:%s]".formatted(id));
        this.queuedMessages = new ConcurrentLinkedQueue<>();

        try {
            info("connecting to server %s ...", connectingTo);
            socket = new Socket(connectingTo.ip(), connectingTo.port());
            start();
            info("server connected! (%s)", connectingTo);
            new Thread(this::upload, "[CLIENT-UPLOADER:%s]".formatted(id)).start();
        }
        catch (Exception ex) {
            error("cannot connect to server %s: %s", connectingTo, ex.getMessage());
        }
    }

    @Override
    public void run() {
        try {
            DataInputStream dis = new DataInputStream(socket.getInputStream());

            while (!isInterrupted()) {
                onReceiveMessage(dis.readUTF());
            }

            dis.close();
        }
        catch (Exception ex) {
            error("cannot receive data from server: %s", serverAddress);
        }
    }

    public void upload() {
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

    public synchronized void onReceiveMessage(String message) {
        if (!Response.isResponsePattern(message)) {
            info("message received: %s", message);
            return;
        }

        try {
            Response res = Response.parse(message);
            if (res.getMethod() == Response.Method.TO_CLIENT && res.getType() == Response.Type.HANDSHAKE)
                this.handeShake(res);
        }
        catch (Exception ex) {
            error("Response parse failure: " + ex.getMessage());
        }
    }

    public synchronized void handeShake(Response res) {
        String serverSideIp = (String) res.getArgs()[0];
        String serverSidePort = (String) res.getArgs()[1];
        this.serverSideAddress = new Address(serverSideIp, Integer.parseInt(serverSidePort));
    }

    public synchronized void sendToServer(String str) {
        if (str != null)
            queuedMessages.add(str);
    }

    public synchronized void sendToServer(Response res) {
        sendToServer(res.toString());
    }

    public void disconnect() {
        sendToServer(new Response(Response.Method.TO_SERVER, Response.Type.DEAD_FISH, "nigger"));

        try {
            socket.close();
            socket = null;
        }
        catch (Exception ex) {
            error("server connection closed");
        }
        interrupt();
    }

    public UUID getUniqueId() {
        return id;
    }

    public Address getServerSideAddress() {
        return serverSideAddress;
    }
}
