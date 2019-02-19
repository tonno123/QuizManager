package fr.epita.quiz.services;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

import fr.epita.quiz.Launcher;
import fr.epita.quiz.datamodel.Answer;
import fr.epita.quiz.datamodel.Question;

public class adminMode {
	
	public static void handleAdminMode(Scanner scanner) {
		while (true) {
			System.out.println("-----------------------------------------");
			System.out.println("Administrator mode");
			System.out.println("1. Add question");
			System.out.println("2. Edit question");
			System.out.println("3. Search questions per topic");
			System.out.println("r. Return to main menu");
			
			String answer = scanner.nextLine();
			
			switch (answer) {
				case "1":
					handleAddQuestion(scanner);
					break;
				case "2":
					editQuestionMenu(scanner);
					break;
				case "3":
					searchQuestionsPerTopic(scanner);
					break;
				case "r":
					return;
			}
		}
	}
	
	private static void searchQuestionsPerTopic(Scanner scanner) {
		List<Question> question_list = new ArrayList<Question>();
		String topic;
		do {
			System.out.println("-----------------------------------------");
			System.out.println("Enter topic(s) below. If more than one, separate topics by comma.");
			topic = scanner.nextLine();

			QuestionJDBCDAO dao = new QuestionJDBCDAO();
			List<String> topic_list = Arrays.asList(topic.split(","));
			for (int i = 0; i < topic_list.size(); i++) {
				question_list.addAll(dao.search(Launcher.cleanInput(topic_list.get(i))));
			}
			if (question_list.size() == 0) {
				System.out.println("There are no questions available with this combination of topic and difficulty. Try again.");
			}
		} while (question_list.size() == 0);
		for (int ii = 0; ii < question_list.size(); ii++) {
			System.out.println(question_list.get(ii).getQuestion());
		}
		System.out.println("Press r to return.");
		if (scanner.nextLine() == "r") {
			return;
		}
	}
	
	private static void handleAddQuestion(Scanner scanner) {
		System.out.println("-----------------------------------------");
		System.out.println("Enter the question:");
		String question = scanner.nextLine();
		System.out.println("Enter question topic:");
		String topic = scanner.nextLine();
		System.out.println("Enter question difficulty between 1 and 3:");
		int difficulty = scanner.nextInt();
		scanner.nextLine();
		System.out.println("Is this a multiple choice question? (y/n)");
		boolean mc = false;
		String yn = scanner.nextLine();
		if (yn.charAt(0) == 'y') {
			mc = true;
		}
		QuestionJDBCDAO dao = new QuestionJDBCDAO();
		int question_id = dao.create(new Question(question, Launcher.cleanInput(topic), difficulty, 0, mc));
		System.out.println("Successfully created new question.");
		
		addAnswers(scanner, question_id, mc);
		return;
	}
	
	private static void addAnswers(Scanner scanner, int question_id, boolean mc) {
		if (mc) {
			boolean correct;
			String yn = "y";
			while (yn.charAt(0) == 'y') {
				System.out.println("-----------------------------------------");
				System.out.println("Enter a possible answer:");
				String answer = scanner.nextLine();
				System.out.println("Is this the correct answer to the question? (y/n)");
				String correctStr = scanner.nextLine();
				if (correctStr.charAt(0) == 'y') {
					correct = true;
				} else {
					correct = false;
				}
				AnswerJDBCDAO dao = new AnswerJDBCDAO();
				dao.create(new Answer(answer, question_id, correct));
				System.out.println("The answer was added. Would you like to add another possible answer to this question? (y/n)");
				yn = scanner.nextLine();
			}
		} else {
			System.out.println("Now enter the answer to the question:");
			String answer = scanner.nextLine();
			AnswerJDBCDAO dao = new AnswerJDBCDAO();
			dao.create(new Answer(answer, question_id, true));
		}
	}
	
	private static void editQuestionMenu(Scanner scanner) {
		while (true) {
			System.out.println("-----------------------------------------");
			System.out.println("List of all questions:");
			List<Question> question_list = showAllQuestions();
	
			System.out.println("Enter question number to edit question and its answer(s), or press '0' to return:");
			int id = scanner.nextInt();
			scanner.nextLine();
			if (id==0) {
				return;
			}
			Question question = findQuestionInList(question_list, id);
			editQuestion(scanner, question);
		}
	}
	
	private static void editQuestion(Scanner scanner, Question question) {
		while (true) {
			System.out.println("-----------------------------------------");
			System.out.println(question.toString());
			System.out.println("What would you like to do?");
			System.out.println("1. Edit question content");
			System.out.println("2. Change topic");
			System.out.println("3. Change difficulty");
			System.out.println("4. Change answer(s)");
			System.out.println("5. Delete question and answer(s)");
			System.out.println("r. Return");
			String answer = scanner.nextLine();
	
			switch (answer) {
			case "1":
				System.out.println("Enter new question:");
				question.setQuestion(scanner.nextLine());			
				break;
			case "2":
				System.out.println("Enter new topic:");
				question.setTopic(Launcher.cleanInput(scanner.nextLine()));
				break;
			case "3":
				System.out.println("Enter new difficulty value between 1 and 3:");
				question.setDifficulty(scanner.nextInt());
				scanner.nextLine();
				break;
			case "4":
				editAnswerMenu(scanner, question);
				break;
			case "5":
				System.out.println("Are you sure? The question and its answer(s) will be deleted. (y/n)");
				String yn = scanner.nextLine();
				if (yn.charAt(0) == 'y') {
					QuestionJDBCDAO dao = new QuestionJDBCDAO();
					dao.delete(question);
					AnswerJDBCDAO dao2 = new AnswerJDBCDAO();
					dao2.delete(question.getId());
					System.out.println("The question and answer(s) are deleted.");
					return;
				}
				return;
			case "r":
				return;
			}
			QuestionJDBCDAO dao = new QuestionJDBCDAO();
			dao.update(question);
		}
	}
	
	private static void editAnswerMenu(Scanner scanner, Question question) {
		while (true) {
			List<Answer> answer_list = Launcher.fetchAnswers(question.getId());
			if (answer_list.size() == 1) {
				editAnswer(scanner, answer_list.get(0));
				return;
			}
			System.out.println("-----------------------------------------");
			System.out.println("Select an answer to edit, or enter '0' to go back");
			for (int i = 0; i < answer_list.size(); i++) {
				Answer curr_a = answer_list.get(i);
				System.out.format("%d. %s\n", curr_a.getAnswerID(), curr_a.getAnswer());
			}
			int id = scanner.nextInt();
			scanner.nextLine();
			if (id == 0) {
				return;
			}
			Answer answer = findAnswerInList(answer_list, id);
			editAnswer(scanner, answer);
		}
	}
	
	private static void editAnswer(Scanner scanner, Answer answer) {
		System.out.println("-----------------------------------------");
		System.out.println("This is the current answer:");
		System.out.println(answer.getAnswer());
		System.out.println("Now enter the new answer, or press 'r' to go back.");
		String new_ans = scanner.nextLine();
		if (new_ans.equals("r")) {
			return;
		}
		answer.setAnswer(new_ans);
		AnswerJDBCDAO dao = new AnswerJDBCDAO();
		dao.update(answer);
	}
	//This function fetches all questions from the database and prints them in the console.
	private static List<Question> showAllQuestions() {
		QuestionJDBCDAO dao = new QuestionJDBCDAO();
		List<Question> question_list = dao.fetch();
		for (int i = 0; i < question_list.size(); i++) {
			Question curr_q = question_list.get(i);
			System.out.format("%d. %s\n", curr_q.getId(), curr_q.getQuestion());
		}
		return question_list;
	}
	
	//This function finds the correct question in a question list by comparing IDs.
	private static Question findQuestionInList(List<Question> list, int id) {
		int i = 0;
		Question curr_q = list.get(i);
		while (curr_q.getId() != id) {
			i++;
			curr_q = list.get(i);
		}
		return curr_q;
	}
	//This function finds the correct answer in an answer list by comparing IDs.
	private static Answer findAnswerInList(List<Answer> list, int id) {
		int i = 0;
		Answer curr_a = list.get(i);
		while (curr_a.getAnswerID() != id) {
			i++;
			curr_a = list.get(i);
		}
		return curr_a;
	}	
}