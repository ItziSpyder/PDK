package io.github.itzispyder.pdk.utils.network;

public class ConnectionThread extends Thread {

    protected void error(String str, Object... args) {
        System.err.println(getName() + " Error: " + str.formatted(args));
    }

    protected void info(String str, Object... args) {
        System.out.println(getName() + " Info: " + str.formatted(args));
    }
}
