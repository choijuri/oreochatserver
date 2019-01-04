package my.exam.chatserver;

import java.net.Socket;
import java.util.List;

public class ServerHandler implements Runnable {
    private Socket socket;
    private ChatLobby chatLobby;
    private boolean inRoom;

    public ServerHandler(Socket socket, ChatLobby chatLobby) {
        this.socket = socket;
        this.chatLobby = chatLobby;
        inRoom = false;

    }

    @Override
    public void run() {
        ChatUser chatUser = new ChatUser(socket);
        String nickname = chatUser.read();
        chatUser.setNickname(nickname);
        System.out.println("nick : " + nickname);

        try {
            while (true) {
                String message = chatUser.read();
                System.out.println(chatUser.getNickname() + "님의 입력 : " + message);
                System.out.println(inRoom);

                if (!inRoom) { // 로비에 있을 경우
                    if (message.indexOf("/create") == 0) {
                        String title = message.substring(message.indexOf(" ") + 1);
                        chatLobby.createRoom(chatUser, title, true);
                        inRoom = true;
                    }else if(message.indexOf("/join") == 0){
                        String strRoomNum = message.substring(message.indexOf(" ") +1);
                        System.out.println(strRoomNum);
                        int roomNum = Integer.parseInt(strRoomNum);
                        chatLobby.joinRoom(roomNum, chatUser);
                        inRoom = true;
                    } else if (message.indexOf("/roomlist") == 0) {
                        List<ChatRoom> chatRooms = chatLobby.getChatRooms();
                        int i = 0;
                        for (ChatRoom cr : chatRooms) {
                            chatUser.write(i + " : " + cr.getTitle());
                            i++;
                        }

                    } else if (message.indexOf("/quit") == 0) {
                        chatLobby.exit(chatUser);
                    }
                } else { // 방안에 있을 경우
                    if (message.indexOf("/whisper") == 0) {
                    } else if (message.indexOf("/kick") == 0) {
                    } else if(message.indexOf("/giveMaster") == 0){
                    } else if(message.indexOf("/change")==0) {
                        change(chatUser, message);
                    }else {
                        List<ChatUser> chatUsers = chatLobby.getUser(chatUser);
                        for (ChatUser cu : chatUsers) {
                            cu.write(chatUser.getNickname() + " : " + message);
                        }
                    }
                }
            }
        } catch (Exception ex) {
            chatLobby.exit(chatUser);
        }
    }

    private void change(ChatUser chatUser, String message) {
        String name = message.substring(message.indexOf(" "));
            List<ChatUser> list = chatLobby.getUser(chatUser);
            for (ChatUser cu : list) {
                if (cu.getNickname().equals(chatUser.getNickname())) {
                    cu.setNickname(name);
                    cu.write("이름이 " + name + "으로 변경되었습니다.");
                }
            }
        }
    }


