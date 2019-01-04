package my.exam.chatserver;

public class ServerMain {
    public static void main(String[] args){
        ChatServer chatServer = new ChatServer(9000);
        chatServer.run();
    }
}