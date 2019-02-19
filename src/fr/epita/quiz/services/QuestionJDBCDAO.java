package fr.epita.quiz.services;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import fr.epita.quiz.datamodel.Question;

public class QuestionJDBCDAO {
	
   private static final String INSERT_STATEMENT = "INSERT INTO QUESTIONS (ID, QUESTION, TOPIC, DIFFICULTY, MC) VALUES (DEFAULT, ?, ?, ?, ?)";
   private static final String FETCH_STATEMENT = "SELECT * FROM QUESTIONS";
   private static final String UPDATE_STATEMENT = "UPDATE QUESTIONS SET QUESTION=?, DIFFICULTY=?, TOPIC=? WHERE ID=?";
   private static final String DELETE_STATEMENT = "DELETE FROM QUESTIONS WHERE ID = ?";
   private static final String CREATE_TEST_STATEMENT = "SELECT * FROM QUESTIONS WHERE DIFFICULTY=? AND TOPIC=?";
   private static final String SEARCH_STATEMENT = "SELECT * FROM QUESTIONS WHERE TOPIC=?";

   public List<Question> search(String topic) {
	   List<Question> resultList = new ArrayList<Question>();
		try (Connection connection = getConnection();
				PreparedStatement preparedStatement = connection.prepareStatement(SEARCH_STATEMENT);
				) {

			preparedStatement.setString(1, topic);
			ResultSet rs = preparedStatement.executeQuery();
			while (rs.next()) {

				Question currentQuestion = new Question(rs.getString("QUESTION"), rs.getString("TOPIC"), rs.getInt("DIFFICULTY"), rs.getInt("ID"), rs.getBoolean("MC"));
				resultList.add(currentQuestion);
			}
			rs.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return resultList;
	}
   
   
   public List<Question> create_test(int difficulty, String topic) {
	   List<Question> resultList = new ArrayList<Question>();
		try (Connection connection = getConnection();
				PreparedStatement preparedStatement = connection.prepareStatement(CREATE_TEST_STATEMENT);
				) {

			preparedStatement.setInt(1, difficulty);
			preparedStatement.setString(2, topic);
			ResultSet rs = preparedStatement.executeQuery();
			while (rs.next()) {

				Question currentQuestion = new Question(rs.getString("QUESTION"), rs.getString("TOPIC"), rs.getInt("DIFFICULTY"), rs.getInt("ID"), rs.getBoolean("MC"));
				resultList.add(currentQuestion);
			}
			rs.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return resultList;
	}
   
   public List<Question> fetch() {
		List<Question> resultList = new ArrayList<Question>();
		try (Connection connection = getConnection();
				PreparedStatement preparedStatement = connection.prepareStatement(FETCH_STATEMENT);
				) {

		
			ResultSet rs = preparedStatement.executeQuery();
			while (rs.next()) {

				Question currentQuestion = new Question(rs.getString("QUESTION"), rs.getString("TOPIC"), rs.getInt("DIFFICULTY"), rs.getInt("ID"), rs.getBoolean("MC"));
				resultList.add(currentQuestion);
			}
			rs.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return resultList;
	}
	
	public int create(Question question) {
		int result = 0;
		try (Connection connection = getConnection();
				PreparedStatement insertStatement = connection.prepareStatement(INSERT_STATEMENT);) {
			
			insertStatement.setString(1, question.getQuestion());
			insertStatement.setString(2, question.getTopic());
			insertStatement.setInt(3, question.getDifficulty());
			insertStatement.setBoolean(4, question.getMC());
			
			insertStatement.execute();
			
			PreparedStatement preparedStatement = connection.prepareStatement("SELECT SCOPE_IDENTITY()");
			ResultSet rs = preparedStatement.executeQuery();
			if (rs.next()) {
				result = rs.getInt(1);
			}
			rs.close();
			
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return result;
	}

	public void update(Question question) {
		
		try (Connection connection = getConnection();
			PreparedStatement updateStatement = connection.prepareStatement(UPDATE_STATEMENT);){
			updateStatement.setString(1, question.getQuestion());
			updateStatement.setInt(2, question.getDifficulty());
			updateStatement.setString(3, question.getTopic());
			updateStatement.setInt(4, question.getId());
			updateStatement.execute();
		}catch (SQLException e) {
			e.printStackTrace();
		}
		
	}

	private Connection getConnection() throws SQLException {
		Configuration conf = Configuration.getInstance();
		String jdbcUrl = conf.getConfigurationValue("jdbc.url");
		String user = conf.getConfigurationValue("jdbc.user");
		String password = conf.getConfigurationValue("jdbc.password");
		Connection connection = DriverManager.getConnection(jdbcUrl, user, password);
		return connection;
	}

	public void delete(Question question) {
		
		try (Connection connection = getConnection();
			PreparedStatement deleteStatement = connection.prepareStatement(DELETE_STATEMENT)){
			deleteStatement.setInt(1, question.getId());
			deleteStatement.execute();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
}