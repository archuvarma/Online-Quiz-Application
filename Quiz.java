package com.quiz.models;
import java.util.ArrayList;
public class Quiz {
	    private int id;
	    private String name;
	    private ArrayList<Question> questions;
	    public Quiz(int id, String name) {
	        this.id = id;
	        this.name = name;
	        this.questions = new ArrayList<>();
	    }
	    public int getId() {
	        return id;
	    }
	    public String getName() {
	        return name;
	    }
	    public void addQuestion(Question question) {
	        questions.add(question);
	    }
	    public ArrayList<Question> getQuestions() {
	        return questions;
	    }
}