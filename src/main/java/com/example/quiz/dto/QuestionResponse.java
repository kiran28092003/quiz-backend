package com.example.quiz.dto;

import java.util.List;

public class QuestionResponse {
    public String id;
    public String question;
    public List<String> options;

    public QuestionResponse() {}

    public QuestionResponse(String id, String question, List<String> options) {
        this.id = id; this.question = question; this.options = options;
    }
}
