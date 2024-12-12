package com.quiz.models;
import java.sql.*;
import java.util.ArrayList;
import java.util.Scanner;
import com.quiz.database.DatabaseConnection;
public class QuizTaking {
    public static void viewQuizzes() {
        String query = "SELECT quiz_id, quiz_name FROM Quizzes";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {

            if (!rs.isBeforeFirst()) {
                System.out.println("No quizzes available.");
                return;
            }
            System.out.println("Available Quizzes:");
            while (rs.next()) {
                int quizId = rs.getInt("quiz_id");
                String quizName = rs.getString("quiz_name");
                System.out.println("ID: " + quizId + ", Name: " + quizName);
            }
        } catch (SQLException | ClassNotFoundException e) {
            System.out.println("Error retrieving quizzes: " + e.getMessage());
        }
    }
    public static int[] takeQuiz(int quizId, int userId) {
        Scanner scanner = new Scanner(System.in);
        String fetchQuestionsQuery = "SELECT question_id, text FROM quiz_questions WHERE quiz_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(fetchQuestionsQuery)) {

            stmt.setInt(1, quizId);
            ResultSet rs = stmt.executeQuery();

            ArrayList<Question> questions = new ArrayList<>();
            while (rs.next()) {
                int questionId = rs.getInt("question_id");
                String questionText = rs.getString("text");

                String fetchOptionsQuery = "SELECT option_text, is_correct FROM quiz_options WHERE question_id = ?";
                try (PreparedStatement optionsStmt = conn.prepareStatement(fetchOptionsQuery)) {
                    optionsStmt.setInt(1, questionId);
                    ResultSet optionsRs = optionsStmt.executeQuery();

                    Question question = new Question(questionText);
                    while (optionsRs.next()) {
                        question.getOptions().add(optionsRs.getString("option_text"));
                        question.getCorrectAnswers().add(optionsRs.getBoolean("is_correct"));
                    }
                    questions.add(question);
                }
            }

            if (questions.isEmpty()) {
                System.out.println("No questions available for this quiz.");
                return null;
            }

            int score = 0;
            int totalQuestions = questions.size();

            for (Question question : questions) {
                System.out.println("Question: " + question.getText());
                for (int i = 0; i < question.getOptions().size(); i++) {
                    System.out.println((i + 1) + ". " + question.getOptions().get(i));
                }

                int userAnswer = -1;
                while (true) {
                    System.out.print("Enter the number of your answer: ");
                    try {
                        userAnswer = scanner.nextInt();
                        if (userAnswer >= 1 && userAnswer <= question.getOptions().size()) {
                            break;
                        } else {
                            System.out.println("Invalid choice. Try again.");
                        }
                    } catch (Exception e) {
                        System.out.println("Invalid input. Please enter a number.");
                        scanner.nextLine();
                    }
                }

                if (question.getCorrectAnswers().get(userAnswer - 1)) {
                    score++;
                    System.out.println("Correct!");
                } else {
                    System.out.println("Incorrect.");
                }
            }

            System.out.println("Quiz completed! Your score: " + score);
            return new int[]{score, totalQuestions};

        } catch (SQLException | ClassNotFoundException e) {
            System.out.println("Error retrieving quiz questions: " + e.getMessage());
        }
        return null;
    }

    public static void viewResults(int userId, int quizId, int score, int totalQuestions) {
        String query = "INSERT INTO Results (user_id, quiz_id, score, total_questions, attempted_questions) " +
                       "VALUES (?, ?, ?, ?, ?) " +
                       "ON DUPLICATE KEY UPDATE score = ?, total_questions = ?, attempted_questions = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, userId);
            stmt.setInt(2, quizId);
            stmt.setInt(3, score);
            stmt.setInt(4, totalQuestions);
            stmt.setInt(5, totalQuestions); 
            stmt.setInt(6, score); 
            stmt.setInt(7, totalQuestions);
            stmt.setInt(8, totalQuestions); 

            int rowsAffected = stmt.executeUpdate();
            if (rowsAffected > 0) {
                System.out.println("Result stored successfully.");
            } else {
                System.out.println("Failed to store the result.");
            }
        } catch (SQLException | ClassNotFoundException e) {
            System.err.println("Error storing result: " + e.getMessage());
        }
    }
}