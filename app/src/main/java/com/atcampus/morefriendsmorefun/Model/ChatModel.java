package com.atcampus.morefriendsmorefun.Model;

public class ChatModel {

    private String chatMessage,receiver,sender,time;
    private boolean isSeen;

    public ChatModel(String chatMessage, String receiver, String sender, String time, boolean isSeen) {
        this.chatMessage = chatMessage;
        this.receiver = receiver;
        this.sender = sender;
        this.time = time;
        this.isSeen = isSeen;
    }

    public String getChatMessage() {
        return chatMessage;
    }

    public void setChatMessage(String chatMessage) {
        this.chatMessage = chatMessage;
    }

    public String getReceiver() {
        return receiver;
    }

    public void setReceiver(String receiver) {
        this.receiver = receiver;
    }

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public boolean isSeen() {
        return isSeen;
    }

    public void setSeen(boolean seen) {
        isSeen = seen;
    }
}
