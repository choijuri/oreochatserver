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
                    chatUser.write("환영합니다. 다음 명령어들을 사용하여 채팅을 즐겨주세요 \n /create 방이름 -> 방 생성, /join 방아이디 -> 방 입장, /list -> 방목록");
                    System.out.println(chatUser.getNickname() + "님의 입력 : " + message);
                    if (message.indexOf("/create") == 0) {
                        String title = message.substring(message.indexOf(" ") + 1);
                        chatLobby.createRoom(chatUser, title, true);
                        inRoom = true;
                        if (chatUser.roomMaster() == true) {
                            chatUser.write("방장님 환영합니다. 방장은 다음의 기능을 사용 할 수 있습니다. \n /kick 유저 \n /whisper 유저이름, /change 바꿀이름");
                        }

                    } else if (message.indexOf("/join") == 0) {
                        String strRoomNum = message.substring(message.indexOf(" ") + 1);
                    }else if(message.indexOf("/join") == 0){
                        String strRoomNum = message.substring(message.indexOf(" ") +1);
                        System.out.println(strRoomNum);
                        int roomNum = Integer.parseInt(strRoomNum);
                        chatLobby.joinRoom(roomNum, chatUser);
                        inRoom = true;
                        List<ChatUser> chatUsers = chatLobby.getUser(chatUser);
                        for (ChatUser cu : chatUsers) {
                            cu.write(chatUser.getNickname() + "님이 입장하셨습니다.");
                        }
                        chatUser.write(chatUser.getNickname() + "님 환영합니다. 일반 유저는 다음의 기능을 사용 할 수 있습니다. \n /whisper 유저이름, /change 바꿀이름");
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
                    MasterOperation masterOperation = new MasterOperation(socket, chatLobby);
                    System.out.println(chatUser.getNickname() + "님의 입력 : " + message);
                    if (message.indexOf("/whisper") == 0) {
                    } else if (message.indexOf("/kick") == 0) {
                        masterOperation.kickUser(chatUser, message);
                    } else if(message.indexOf("/giveMaster") == 0){
                        masterOperation.giveMaster(chatUser, message);
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


