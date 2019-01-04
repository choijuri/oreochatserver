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
        System.out.println("message : " + nickname);

        chatLobby.addChatUser(chatUser);

        try {
            while (true) {
                String message = chatUser.read();
                System.out.println(chatUser.getNickname() + "님의 입력 : " + message);
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
                        String[] s = message.split(" ");
                        String whisperTo = s[1];
                        System.out.println("whisper to: " + whisperTo);
                        String msg = message.substring(message.indexOf(s[2]));
                        List<ChatUser> chatUsers = chatLobby.getUser(chatUser);
                        for (ChatUser cu : chatUsers) {
                            if(cu.getNickname().equals(whisperTo)){
                                cu.write("[" + chatUser.getNickname()+ "님의 귓속말]" + msg);

                            }
                        }

                    } else if (message.indexOf("/kick") == 0) {
                        if (chatUser.roomMaster() == true) {
                            String kickWho = message.substring(message.indexOf(" ") + 1);
                            List<ChatUser> chatUsers = chatLobby.getUser(chatUser);
                            for (ChatUser cu : chatUsers) {
                                if (cu.getNickname().equals(kickWho)) {
                                    cu.close();
                                }else if(chatUser.getNickname().equals(kickWho)){
                                    chatUser.write("자기자신은 강퇴시킬 수 없습니다.");
                                }else{
                                    chatUser.write(kickWho + " 해당하는 유저가 없습니다.");
                                }
                            }
                        } else {
                            chatUser.write("방장이 아닙니다.");
                        }
                    } else if(message.indexOf("/giveMaster") == 0){


                    } else if (message.indexOf("/change") == 0) {
                        String name = message.substring(message.indexOf(" ") + 1);
                        List<ChatUser> lists = chatLobby.getUser(chatUser);
                        for (ChatUser cu : lists) {
                            if (cu.getNickname().equals(chatUser.getNickname())) {
                                cu.setNickname(name);
                            }
                        }
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
}

