package fr.epita.quiz;

import java.util.List;
import java.util.Properties;
import java.util.Scanner;

import fr.epita.quiz.datamodel.Answer;
import fr.epita.quiz.services.AnswerJDBCDAO;
import fr.epita.quiz.services.adminMode;
import fr.epita.quiz.services.examMode;

public class Launcher {
	
	public static void main(String[]args) {
		Properties prop = System.getProperties();
		prop.setProperty("conf.location", "app.properties");
		Scanner scanner = new Scanner(System.in);
		while (true) {
			System.out.println("-----------------------------------------");
			System.out.println("Welcome to the Quiz Manager!");
			System.out.println("1. Administrator mode");
			System.out.println("2. Exam mode");
			System.out.println("q. Quit application");
			String answer = scanner.nextLine();
			
			switch (answer) {
				case "1":
					adminMode.handleAdminMode(scanner);
					break;
				case "2":
					examMode.handleExamMode(scanner);
					break;
				case "q":
					scanner.close();
					return;
			}
		}
	}
	
	public static List<Answer> fetchAnswers(int question_id) {
		AnswerJDBCDAO dao = new AnswerJDBCDAO();
		return dao.fetch(question_id);
	}
	
	public static String cleanInput(String input) {
		return input.replaceAll("[^a-zA-Z0-9]","").toLowerCase();
	}
}
