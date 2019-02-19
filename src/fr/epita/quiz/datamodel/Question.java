package fr.epita.quiz.datamodel;

public class Question {
	private String question;
	private String topic;
	private int difficulty;
	private int id;
	private boolean mc;

	public Question(String question, String topic, int difficulty, int id, boolean mc) {
		this.question = question;
		this.topic = topic;
		this.difficulty = difficulty;
		this.id = id;
		this.mc = mc;
	}
	
	public String toString() {
		return "Question=" + question + ", topic=" + topic + ", difficulty=" + difficulty;
	}
	
	public Question() {
	}
	
	public String getQuestion() {
		return question;
	}
	
	public void setQuestion(String question) {
		this.question = question;
	}
	
	public String getTopic() {
		return topic;
	}
	
	public void setTopic(String topic) {
		this.topic = topic;
	}
	
	public int getDifficulty() {
		return difficulty;
	}
	
	public void setDifficulty(Integer difficulty) {
		this.difficulty = difficulty;
	}
	
	public int getId() {
		return id;
	}
	
	public boolean getMC() {
		return mc;
	}
}