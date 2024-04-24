package io.github.itzispyder.pdk.utils.network;

import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ConcurrentLinkedQueue;

public class Server extends ConnectionThread {

    private final ConcurrentLinkedQueue<Connection> connections = new ConcurrentLinkedQueue<>();
    private ServerSocket serverSocket;
    private final Address address;

    public Server(Address address) {
        this.address = address;
        this.setName("[SERVER:%s]".formatted(address.port()));

        try {
            info("starting server...");
            this.serverSocket = new ServerSocket(address.port());
            start();
            info("server has started on %s:%s", serverSocket.getInetAddress().getHostAddress(), address.port());
        }
        catch (Exception ex) {
            error("error starting server: %s", ex.getMessage());
        }
    }

    @Override
    public void run() {
        while (!isInterrupted()) {
            try {
                Socket socket = serverSocket.accept();
                this.onClientConnect(socket);
            }
            catch (Exception ex) {
                error("cannot handle client connect event: %s", ex.getMessage());
            }
        }
    }

    private synchronized void onClientConnect(Socket socket) {
        Connection conn = new Connection(this, socket);
        connections.add(conn);
        Address connAddress = conn.getAddress();
        conn.sendMessage(new Response(Response.Method.TO_CLIENT, Response.Type.HANDSHAKE, connAddress.ip(), connAddress.port()));
    }

    public synchronized void broadcast(String str) {
        for (var conn : connections)
            conn.sendMessage(str);
    }

    public synchronized Connection getConnectionOfAddress(Address address) {
        for (var conn : connections)
            if (conn.getAddress().equals(address))
                return conn;
        return null;
    }

    public synchronized boolean removeConnection(Address address) {
        for (var conn : connections)
            if (conn.getAddress().equals(address))
                return connections.remove(conn);
        return false;
    }

    public void disconnect() {
        interrupt();
        info("stopping server...");

        for (var conn : connections)
            conn.disconnect();
        connections.clear();

        try {
            serverSocket.close();
        }
        catch (Exception ex) {
            error("server closed");
        }
    }

    public int connectionCount() {
        return connections.size();
    }

    public int getPort() {
        return address.port();
    }
}
