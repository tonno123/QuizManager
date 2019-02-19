package fr.epita.quiz.datamodel;

public class Answer {
	private String answer;
	private int answer_id;
	private int question_id;
	private boolean correct;
	
	public Answer(String answer) {
		this.answer = answer;
	}
	
	public Answer(String answer, int question_id, boolean correct) {
		this.answer = answer;
		this.question_id = question_id;
		this.correct = correct;
	}
	
	public Answer(String answer, int question_id, boolean correct, int answer_id) {
		this.answer = answer;
		this.question_id = question_id;
		this.correct = correct;
		this.answer_id = answer_id;
	}
	
	public void setAnswer(String answer) {
		this.answer = answer;
	}
	
	public String getAnswer() {
		return answer;
	}
	
	public boolean getCorrect() {
		return correct;
	}
	
	public int getQID() {
		return question_id;
	}
	
	public int getAnswerID() {
		return answer_id;
	}
}
