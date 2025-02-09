package com.example.EchoLife.entities;

public class UserMessageDTO {
    private String content;

    public UserMessageDTO() {
    }

    public UserMessageDTO(String content) {
        this.content = content;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}
