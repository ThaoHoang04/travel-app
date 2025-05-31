package com.example.travelapp.Domain;


// Class để lưu trữ lịch sử chat như trong Python
public class ChatMessage {
    private String role;
    private String content;

    public ChatMessage(String role, String content) {
        this.role = role;
        this.content = content;
    }

    public String getRole() {
        return role;
    }

    public String getContent() {
        return content;
    }
}
