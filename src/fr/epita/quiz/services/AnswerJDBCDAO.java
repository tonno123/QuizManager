package fr.epita.quiz.services;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import fr.epita.quiz.datamodel.Answer;
import fr.epita.quiz.datamodel.Question;

public class AnswerJDBCDAO {
	
		private static final String INSERT_STATEMENT = "INSERT INTO ANSWERS (ANSWER_ID, QUESTION_ID, ANSWER, CORRECT) VALUES (DEFAULT, ?, ?, ?)";
		private static final String FETCH_STATEMENT = "SELECT * FROM ANSWERS WHERE QUESTION_ID=?";
		private static final String UPDATE_STATEMENT = "UPDATE ANSWERS SET ANSWER=?, CORRECT=? WHERE ANSWER_ID=?";
		private static final String DELETE_STATEMENT = "DELETE FROM ANSWERS WHERE QUESTION_ID = ?";
		
		public void create(Answer answer) {
			
			try (Connection connection = getConnection();
					PreparedStatement insertStatement = connection.prepareStatement(INSERT_STATEMENT);) {
				
				insertStatement.setInt(1, answer.getQID());
				insertStatement.setString(2, answer.getAnswer());
				insertStatement.setBoolean(3, answer.getCorrect());
				insertStatement.execute();

			} catch (SQLException e) {
				e.printStackTrace();
			}

		}
		
		public List<Answer> fetch(int question_id) {
			List<Answer> resultList = new ArrayList<Answer>();
			try (Connection connection = getConnection();
					PreparedStatement preparedStatement = connection.prepareStatement(FETCH_STATEMENT);
					) {

				preparedStatement.setInt(1, question_id);
				ResultSet rs = preparedStatement.executeQuery();
				while (rs.next()) {

					Answer currentAnswer = new Answer(rs.getString("ANSWER"), rs.getInt("QUESTION_ID"), rs.getBoolean("CORRECT"), rs.getInt("ANSWER_ID"));
					resultList.add(currentAnswer);
				}
				rs.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
			return resultList;
		}
		
		public void update(Answer answer) {
			
			try (Connection connection = getConnection();
				PreparedStatement updateStatement = connection.prepareStatement(UPDATE_STATEMENT);){
				updateStatement.setString(1, answer.getAnswer());
				updateStatement.setBoolean(2, answer.getCorrect());
				updateStatement.setInt(3, answer.getAnswerID());
				updateStatement.execute();
			}catch (SQLException e) {
				e.printStackTrace();
			}
			
		}
		
		public void delete(int question_id) {
			
			try (Connection connection = getConnection();
				PreparedStatement deleteStatement = connection.prepareStatement(DELETE_STATEMENT)){
				deleteStatement.setInt(1, question_id);
				deleteStatement.execute();
			} catch (SQLException e) {
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
		
		
		
		
}
