package com.example.quiz.dto;

public class AnswerDTO {
    private String questionId;
    private String selectedOption;

    public AnswerDTO() {}

    public String getQuestionId() { return questionId; }
    public void setQuestionId(String questionId) { this.questionId = questionId; }

    public String getSelectedOption() { return selectedOption; }
    public void setSelectedOption(String selectedOption) { this.selectedOption = selectedOption; }

    @Override
    public String toString() {
        return "{questionId:" + questionId + ", selectedOption:" + selectedOption + "}";
    }
}
