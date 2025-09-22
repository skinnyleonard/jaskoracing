package online;

import java.net.InetAddress;

public class User {
    private String id;
    private String username;
    private InetAddress ip;
    private int port;

    public User(String username, InetAddress ip, int port) {
        this.username = username;
        this.ip = ip;
        this.port = port;
        this.id = ip.toString()+":"+port;
    }

    public String getUsername() {
        return username;
    }

    public InetAddress getIp() {
        return ip;
    }

    public int getPort() {
        return port;
    }
}
