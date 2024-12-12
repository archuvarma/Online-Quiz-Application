package com.quiz.models; 
import java.util.ArrayList;
import java.util.List;
public class Question {
    private String text; 
    private List<String> options; 
    private List<Boolean> correctAnswers; 
    public Question(String text) {
        this.text = text;
        this.options = new ArrayList<>();
        this.correctAnswers = new ArrayList<>();
    }
    public String getText() {
        return text;
    }
    public void setText(String text) {
        this.text = text;
    }
    public List<String> getOptions() {
        return options;
    }
    public void setOptions(List<String> options) {
        this.options = options;
    }
    public List<Boolean> getCorrectAnswers() {
        return correctAnswers;
    }
    public void setCorrectAnswers(List<Boolean> correctAnswers) {
        this.correctAnswers = correctAnswers;
    }
}