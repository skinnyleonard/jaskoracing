package online;

import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.JsonWriter;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.net.*;
import java.util.concurrent.ConcurrentLinkedQueue;

public class Client extends Thread{
    private DatagramSocket socket;
    private boolean end = false;
    private String stringServerIp ="255.255.255.255";
    private InetAddress ipServer;
    private int serverPort = 5555;
    private NetManager netManager;
    private long lastMessageTime = -1;
    public static ConcurrentLinkedQueue<Vector3> positionUpdates = new ConcurrentLinkedQueue<Vector3>();
    public Client(NetManager netManager){
        try{
            this.netManager = netManager;
            System.out.println("cliente iniciado perrito");
            this.ipServer = InetAddress.getByName(this.stringServerIp);
            socket = new DatagramSocket();
        } catch (SocketException | UnknownHostException e) {
//            throw new RuntimeException(e);
        }
    }
    int packetCount;
    long lastSecond =  System.currentTimeMillis();

    @Override
    public void run() {
        do {
            DatagramPacket packet = new DatagramPacket(new byte[1024], 1024);
//            System.out.println("esperando el mensaje");
            try{
                socket.receive(packet);
                packetCount++;
                long now  = System.currentTimeMillis();
                if(now - lastSecond >= 1000){
//                    System.out.println("paquetes recibidos en el ultimo segundo: " +packetCount);
                    packetCount = 0;
                    lastSecond = now;
                }
                procesarMensaje(packet);
                Thread.sleep(1);
            }catch (IOException e){
                e.printStackTrace();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        } while (!end);
    }
//    int packetCount = 0;
//    long lastSecond = System.currentTimeMillis();

    private void procesarMensaje(DatagramPacket packet) {
//        packetCount++;
//        long now = System.currentTimeMillis();

//        if(lastMessageTime != -1) {
//            long diff = now - lastMessageTime;
//            //System.out.println("tiempo en ms entre mensajes: "+diff+"ms");
//        }

//        lastMessageTime = now;
//        if(now - lastSecond >= 1000){
//            System.out.println("paquetes recibidos: "+packetCount);
//            packetCount = 0;
//            lastSecond = now;
//        }


//        processPosition(packet);

//        try {
//            ByteArrayInputStream bais =  new ByteArrayInputStream(packet.getData(), 0, packet.getLength());
//            DataInputStream dis = new DataInputStream(bais);
//
//            String message = dis.readUTF();
//
//            if(message.equals("updatePos")){
//                float newX  = dis.readFloat();
//                float newY  = dis.readFloat();
//                float newAngle = dis.readFloat();
//
//                netManager.updateSprites(new Vector3(newX, newY, newAngle));
//            }
//        }catch (IOException e){
//            e.printStackTrace();
//        }

        try {
            ByteArrayInputStream bais = new ByteArrayInputStream(packet.getData(), 0, packet.getLength());
            DataInputStream dis = new DataInputStream(bais);
            String type = dis.readUTF();

            switch (type) {
                case "updatePos":
                    float x = dis.readFloat();
                    float y = dis.readFloat();
                    float angle = dis.readFloat();
//                    netManager.updateSprites(new Vector3(x, y, angle));
                    positionUpdates.add(new Vector3(x, y, angle));
                    break;
            }
        } catch (IOException e){
            String mensaje = (new String(packet.getData(), 0, packet.getLength())).trim();
            System.out.println("mensaje: "+mensaje);
            String[] parts = mensaje.split(";");

            switch (parts[0]){
                case "connected":
                    this.netManager.connect(true, Integer.valueOf(parts[1]));
                    System.out.println("se conecto perfectamente");
                    this.ipServer = packet.getAddress();
                    break;
                case "updateOthersPos":
                    netManager.updateOtherPos(parts[1]);
//                netManager.updateSprites(parts[1]);
//                System.out.println("x:"+parts[1].split("%")[0]+
//                    " y:"+parts[1].split("%")[1]+
//                    " angle:"+parts[1].split("%")[2]);
                    break;
                case "newCar":
                    netManager.createSpritePlayer(parts[1]);
                    System.out.println("cree un nuevo autito!!!!!");
                    break;
                case "serverfull":
                    this.netManager.connect(false, -1);
                    System.out.println("el servidor esta lleno de personas");
                    break;
                case "alreadyConnected":
                    System.out.println("ya estas conectado");
                    break;
                case "waitingotheruser":
                    System.out.println("el contricante no se conecto todavia");
                    break;
                case "newmessage":
                    System.out.println(parts[1]);
                    break;
                case "contricantdisconnected":
                    System.out.println("el contricante se desconecto");
                    break;
                case "serverClosed":
                    netManager.connect(false, -1);
                    System.out.println("el servidor se apago");
                    break;
            }
        }
    }

    public void sendMessage(String message) {
        byte[] bytes = message.getBytes();
        DatagramPacket packet = new DatagramPacket(bytes, bytes.length, this.ipServer, this.serverPort);
        try {
            socket.send(packet);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void finish() {
        this.end = true;
        socket.close();
        this.interrupt();
    }

}
