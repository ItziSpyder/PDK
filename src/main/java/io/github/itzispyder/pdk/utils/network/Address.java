package io.github.itzispyder.pdk.utils.network;

import java.net.Socket;

public record Address(String ip, int port) {

    public Address(String ip, int port) {
        this.ip = "127.0.0.1".equals(ip) ? "localhost" : ip;
        this.port = port;
    }

    public Address(Socket socket) {
        this(socket.getInetAddress().getHostAddress(), socket.getPort());
    }

    @Override
    public String toString() {
        return ip + ":" + port;
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof Address ad))
            return false;
        return ad.ip.equals(this.ip) && ad.port == this.port;
    }
}
