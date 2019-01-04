package my.exam.chatserver;

import java.net.ServerSocket;
import java.net.Socket;

public class ChatServer {
    private int port;
    private ChatLobby chatHouse;

    public ChatServer(int port){
        this.port = port;
        chatHouse = new ChatLobby();
    }

    public void run(){
        ServerSocket serverSocket = null;
        try{
            serverSocket = new ServerSocket(port);
            while(true) {
                System.out.println("접속대기중");
                Socket socket = serverSocket.accept();
                System.out.println("접속 완료. 클라이언트의 ip 주소 : "  + socket.getInetAddress() );
                Thread t = new Thread(new ServerHandler(socket, chatHouse));
                t.start();
            }

        }catch (Exception ex){
            System.out.println("오류 발생.");
        }finally {
            try{ serverSocket.close(); }catch(Exception ignore){}
        }
    }
}


