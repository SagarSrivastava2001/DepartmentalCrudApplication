package com.example.DepartmentalCrudApplication.service;

public interface EmailSenderService {
    void sendSimpleEmail(String toEmail, String body, String subject);
}
