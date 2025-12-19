package com.example.quiz.dto;

import java.util.List;

public class SubmitAnswersRequest {
    private String name;
    private String category;
    private String difficulty;
    private List<AnswerDTO> answers;

    public SubmitAnswersRequest() {}

    // getters + setters
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }

    public String getDifficulty() { return difficulty; }
    public void setDifficulty(String difficulty) { this.difficulty = difficulty; }

    public List<AnswerDTO> getAnswers() { return answers; }
    public void setAnswers(List<AnswerDTO> answers) { this.answers = answers; }

    @Override
    public String toString() {
        return "SubmitAnswersRequest{name='" + name + "', category='" + category + "', difficulty='" + difficulty + "', answers=" + answers + "}";
    }
}
