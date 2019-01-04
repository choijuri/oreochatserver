package my.exam.chatserver;

import java.net.Socket;
import java.util.List;

public class MasterOperation {
    private ChatLobby chatLobby;
    private Socket socket;

    public MasterOperation(Socket socket, ChatLobby chatLobby) {
        this.socket = socket;
        this.chatLobby = chatLobby;
    }

    public void kickUser(ChatUser chatUser, String message) {
        if (chatUser.roomMaster() == true) {
            String kickWho = message.substring(message.indexOf(" ") + 1);
            List<ChatUser> chatUsers = chatLobby.getUser(chatUser);
            List<ChatUser> bannedUsers = chatLobby.getUser(chatUser);
            for (ChatUser cu : chatUsers) {
                if (chatUser.getNickname().equals(kickWho)) {
                    chatUser.write("자기자신은 강퇴할 수 없습니다.");
                    break;
                }
                if (cu.getNickname().equals(kickWho)) {
                    bannedUsers.add(cu);
                    cu.close();
                    chatUser.write(cu.getNickname() + "님을 강퇴했습니다.");
                    break;
                }
                if (bannedUsers.size() == 0) {
                    chatUser.write("강퇴대상이 없습니다.");
                }
            }
        } else {
            chatUser.write("방장이 아닙니다.");
        }
    }

    public void giveMaster(ChatUser chatUser, String message) {
        if (chatUser.roomMaster() == true) {
            String giveTo = message.substring(message.indexOf(" ") + 1);
            List<ChatUser> chatUsers = chatLobby.getUser(chatUser);
            if (chatUser.getNickname().equals(giveTo)) {
                chatUser.write("이미 방장입니다..");
            }
            for (ChatUser cu : chatUsers) {
                if (cu.getNickname().equals(giveTo)) {
                    cu.setMaster(true);
                    cu.write("방장은 다음의 기능을 사용 할 수 있습니다. \n /kick 유저");
                }

            }
        } else {
            chatUser.write("방장이 아닙니다.");
        }
    }
}

