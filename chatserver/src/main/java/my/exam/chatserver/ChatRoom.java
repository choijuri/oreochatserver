package my.exam.chatserver;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ChatRoom {
    private String title;
    private List<ChatUser> chatUsers;

    public ChatRoom(ChatUser chatUser, String title){
        this.title = title;
        chatUsers = Collections.synchronizedList(new ArrayList<>());
        chatUsers.add(chatUser);
    }

    public boolean existsUser(ChatUser chatUser){
        return chatUsers.contains(chatUser);
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public List<ChatUser> getChatUsers() {
        return chatUsers;
    }

    public void setChatUsers(List<ChatUser> chatUsers) {
        this.chatUsers = chatUsers;
    }

    public void addChatUser(ChatUser chatUser) {
        chatUsers.add(chatUser);
    }
    public void removeChatUser(ChatUser chatUser){
        chatUsers.remove(chatUser);
    }
}
