package com.example.travelapp.Domain;

public class MailTo {
    public String to;
    public String subject;
    public String content;

    public MailTo(String to, String subject, String content) {
        this.to = to;
        this.subject = subject;
        this.content = content;
    }
}
