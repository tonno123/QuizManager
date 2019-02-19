package fr.epita.quiz.services;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.Arrays;

import fr.epita.quiz.Launcher;
import fr.epita.quiz.datamodel.Answer;
import fr.epita.quiz.datamodel.Question;

public class examMode {
	
	public static void handleExamMode(Scanner scanner) {
		List<String> student_info = new ArrayList<String>();
		List<Question> question_list = new ArrayList<Question>();
		int difficulty;
		String topic;
		do {
			System.out.println("-----------------------------------------");
			System.out.println("Enter the desired quiz topics in a comma-separated list:");
			topic = scanner.nextLine();
			System.out.println("Enter desired quiz difficulty between 1 and 3:");
			difficulty = scanner.nextInt();
			scanner.nextLine();
			QuestionJDBCDAO dao = new QuestionJDBCDAO();
			List<String> topic_list = Arrays.asList(topic.split(","));
			for (int i = 0; i < topic_list.size(); i++) {
				question_list.addAll(dao.create_test(difficulty, Launcher.cleanInput(topic_list.get(i))));
			}
			if (question_list.size() == 0) {
				System.out.println("There are no questions available with this combination of topic and difficulty. Try again.");
			}
		} while (question_list.size() == 0);
		System.out.println("Would you like to export the quiz for printing or take it inside the application?");
		System.out.println("1. Export quiz");
		System.out.println("2. Take quiz in application");
		String answer = scanner.nextLine();
		if (answer.equals("1")) {
			exportQuiz(scanner, question_list, difficulty, topic);
		} else {
			System.out.println("Enter student ID:");
			student_info.add(scanner.nextLine());
			System.out.println("Enter student name:");
			student_info.add(scanner.nextLine());
			
			List<Answer> userAnswerList = playQuiz(scanner, question_list);
			try {
				evaluateQuiz(scanner, userAnswerList, question_list, student_info);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
 	}
	
	//This function exports the quiz to a text file, so the professor can print it and the student can take the test on paper.
	private static void exportQuiz(Scanner scanner, List<Question> question_list, int difficulty, String topic) {
		File file = new File("Quiz-" + topic + difficulty);
		try {
			file.createNewFile();
			FileWriter writer = new FileWriter(file);
			writer.write("Quiz topic: " + topic + " / difficulty level " + difficulty + "\n");
			writer.write("Name: \n");
			writer.write("Student number: \n");
			for (int i = 0; i < question_list.size(); i++) {
				writer.write("-----------------------------------------\n");
				Question curr_q = question_list.get(i);
				writer.write("Question " + (i + 1) + ": " + curr_q.getQuestion() + "\n");
				writer.write("\n");
				if (curr_q.getMC()) {
					List<Answer> dbAnswerList = Launcher.fetchAnswers(curr_q.getId());
					for (int ii = 0; ii < dbAnswerList.size(); ii++) {
						Answer curr_a = dbAnswerList.get(ii);
						writer.write((ii + 1) + ". " + curr_a.getAnswer() + "\n");
					}
				}
			}
			System.out.println("Quiz was exported to file: 'Quiz-" + topic + difficulty + "'");
			writer.flush();
			writer.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	//This function runs the quiz, and saves the student answers in a list for later evaluation.
	private static List<Answer> playQuiz(Scanner scanner, List<Question> question_list) {
		List<Answer> userAnswerList = new ArrayList<Answer>();
		for (int i = 0; i < question_list.size(); i++) {
			System.out.println("-----------------------------------------");
			System.out.println("Question " + (i + 1) + "/" + question_list.size());
			Question curr_q = question_list.get(i);
			System.out.println(curr_q.getQuestion());
			if (curr_q.getMC()) {
				List<Answer> dbAnswerList = Launcher.fetchAnswers(curr_q.getId());
				for (int ii = 0; ii < dbAnswerList.size(); ii++) {
					Answer curr_a = dbAnswerList.get(ii);
					System.out.format("%d. %s\n", ii, curr_a.getAnswer());
				}
				int userAnswer = scanner.nextInt();
				scanner.nextLine();
				userAnswerList.add(dbAnswerList.get(userAnswer));
			} else {
				userAnswerList.add(new Answer(scanner.nextLine()));
			}
		}
		return userAnswerList;
	}

	//This function is run after playing the quiz, and is run to correct the quiz and generate a correction file for the professor.
	private static void evaluateQuiz(Scanner scanner, List<Answer> userAnswerList, List<Question> question_list, List<String> student_info) throws IOException {
		File file = new File(student_info.get(0) + student_info.get(1));
		file.createNewFile();
		FileWriter writer = new FileWriter(file);
		int correctMCQ = 0;
		int totalMCQ = 0;
		writer.write("This is the exam of " + student_info.get(1) + ", with student number: " + student_info.get(0) + "\n");
		writer.write("\n");
		for (int i = 0; i < question_list.size(); i++) {
			writer.write("-----------------------------------------\n");
			Question curr_q = question_list.get(i);
			writer.write("Question " + (i + 1) + ": " + curr_q.getQuestion() + "\n");
			Answer curr_a = userAnswerList.get(i);
			writer.write("Student answer: " + curr_a.getAnswer() + "\n");
			if (curr_q.getMC()) {
				totalMCQ++;
				if (curr_a.getCorrect()) {
					correctMCQ++;
					writer.write("Correct!\n");
				} else {
					writer.write("Incorrect!\n");
				}
			} else {
				List<Answer> correctAnswerList = Launcher.fetchAnswers(curr_q.getId());
				Answer correctAnswer = correctAnswerList.get(0);
				writer.write("Correct answer: " + correctAnswer.getAnswer() + "\n");
			}
		}
		writer.write("-----------------------------------------\n");
		writer.write("Score on multiple choice questions: " + correctMCQ + "/" + totalMCQ + "\n");
		
		writer.flush();
		writer.close();
		
		System.out.println("Your score on the multiple choice questions is: " + correctMCQ + "/" + totalMCQ);
		System.out.println("A separate file is generated for your professor, for correcting the open questions.");
	}
}
