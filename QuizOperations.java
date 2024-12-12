package com.quiz.models;
import java.sql.*;
import java.util.ArrayList;
import java.util.Scanner;
import com.quiz.database.DatabaseConnection;
public class QuizOperations {
    public static void createQuiz() {
        Scanner scanner = new Scanner(System.in);
        System.out.print("Enter quiz name: ");
        String quizName = scanner.nextLine();
        System.out.print("Enter title name: ");
        String title = scanner.nextLine();
        String query = "INSERT INTO Quizzes (quiz_name,title) VALUES (?,?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, quizName);
            stmt.setString(2, title);
            stmt.executeUpdate();
            ResultSet rs = stmt.getGeneratedKeys();
            if (rs.next()) {
                int quizId = rs.getInt(1);
                System.out.println("Quiz created successfully with ID: " + quizId);
            }
        } catch (SQLException | ClassNotFoundException e) {
            System.out.println("Error creating quiz: " + e.getMessage());
        }
    }
    public static void viewAllQuizzes() {
        String query = "SELECT quiz_id, quiz_name,title FROM Quizzes";
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
    public static void addQuestionToQuiz() {
        Scanner scanner = new Scanner(System.in);
        System.out.print("Enter quiz ID to add question: ");
        int quizId = scanner.nextInt();
        scanner.nextLine();
        System.out.print("Enter question text: ");
        String questionText = scanner.nextLine();
        String query = "INSERT INTO quiz_questions (quiz_id, text) VALUES (?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setInt(1, quizId);
            stmt.setString(2, questionText);
            stmt.executeUpdate();
            ResultSet rs = stmt.getGeneratedKeys();
            if (rs.next()) {
                int questionId = rs.getInt(1);
                System.out.println("Question added successfully with ID: " + questionId);
                addOptionsToQuestion(questionId);
            }
        } catch (SQLException | ClassNotFoundException e) {
            System.out.println("Error adding question: " + e.getMessage());
        }
    }
    public static void addOptionsToQuestion(int questionId) {
        Scanner scanner = new Scanner(System.in);
        ArrayList<String> options = new ArrayList<>();
        int correctOptionIndex = -1;
        System.out.println("Enter options for the question (enter 0 to finish):");
        while (true) {
            System.out.print("Enter option: ");
            String option = scanner.nextLine();
            if (option.equals("0")) break;
            options.add(option);
        }
        if (options.isEmpty()) {
            System.out.println("No options added. Question will not be saved.");
            return;
        }
        for (int i = 0; i < options.size(); i++) {
            System.out.println((i + 1) + ". " + options.get(i));
        }
        while (true) {
            System.out.print("Enter the number of the correct option: ");
            try {
                correctOptionIndex = scanner.nextInt() - 1;
                if (correctOptionIndex >= 0 && correctOptionIndex < options.size()) {
                    break;
                } else {
                    System.out.println("Invalid choice. Try again.");
                }
            } catch (Exception e) {
                System.out.println("Please enter a valid number.");
                scanner.nextLine();
            }
        }

        String query = "INSERT INTO quiz_options (question_id, option_text, is_correct) VALUES (?, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            for (int i = 0; i < options.size(); i++) {
                stmt.setInt(1, questionId);
                stmt.setString(2, options.get(i));
                stmt.setBoolean(3, i == correctOptionIndex);
                stmt.addBatch();
            }
            stmt.executeBatch();
            System.out.println("Options added successfully.");
        } catch (SQLException | ClassNotFoundException e) {
            System.out.println("Error adding options: " + e.getMessage());
        }
    }
    public static void deleteQuiz() {
        Scanner scanner = new Scanner(System.in);
        System.out.print("Enter quiz ID to delete: ");
        int quizId = scanner.nextInt();
        scanner.nextLine();
        String query = "DELETE FROM Quizzes WHERE quiz_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, quizId);
            int rowsAffected = stmt.executeUpdate();
            if (rowsAffected > 0) {
                System.out.println("Quiz deleted successfully.");
            } else {
                System.out.println("Quiz not found with ID: " + quizId);
            }
        } catch (SQLException | ClassNotFoundException e) {
            System.out.println("Error deleting quiz: " + e.getMessage());
        }
    }
    public static void deleteQuestionFromQuiz() {
        Scanner scanner = new Scanner(System.in);
        System.out.print("Enter quiz ID to delete a question: ");
        int quizId = scanner.nextInt();
        scanner.nextLine();
        String fetchQuestionsQuery = "SELECT question_id, text FROM quiz_questions WHERE quiz_id = ?";
        String deleteOptionsQuery = "DELETE FROM quiz_options WHERE question_id = ?";
        String deleteQuestionQuery = "DELETE FROM quiz_questions WHERE question_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement fetchQuestionsStmt = conn.prepareStatement(fetchQuestionsQuery);
             PreparedStatement deleteOptionsStmt = conn.prepareStatement(deleteOptionsQuery);
             PreparedStatement deleteQuestionStmt = conn.prepareStatement(deleteQuestionQuery)) {
            fetchQuestionsStmt.setInt(1, quizId);
            ResultSet questionRs = fetchQuestionsStmt.executeQuery();
            ArrayList<Integer> questionIds = new ArrayList<>();
            ArrayList<String> questionTexts = new ArrayList<>();
            while (questionRs.next()) {
                questionIds.add(questionRs.getInt("question_id"));
                questionTexts.add(questionRs.getString("text"));
            }
            if (questionIds.isEmpty()) {
                System.out.println("No questions available in the selected quiz.");
                return;
            }
            System.out.println("Questions in the quiz:");
            for (int i = 0; i < questionTexts.size(); i++) {
                System.out.println((i + 1) + ". " + questionTexts.get(i));
            }
            System.out.print("Enter the question number to delete: ");
            int questionNumber = scanner.nextInt();
            scanner.nextLine();
            if (questionNumber < 1 || questionNumber > questionIds.size()) {
                System.out.println("Invalid question number. No question deleted.");
                return;
            }
            int questionIdToDelete = questionIds.get(questionNumber - 1);
            deleteOptionsStmt.setInt(1, questionIdToDelete);
            deleteOptionsStmt.executeUpdate();
            deleteQuestionStmt.setInt(1, questionIdToDelete);
            deleteQuestionStmt.executeUpdate();
            System.out.println("Question deleted successfully.");
        } catch (SQLException | ClassNotFoundException e) {
            System.out.println("Error deleting question: " + e.getMessage());
        }
    }
    public static void viewResults() {
        Scanner scanner = new Scanner(System.in);
        System.out.print("Enter quiz ID to view results: ");
        int quizId = scanner.nextInt();
        scanner.nextLine();
        String fetchResultsQuery = "SELECT u.username, r.score, r.total_questions, r.correct_answers, r.attempted_questions, r.completed_at " +
                                   "FROM Results r " +
                                   "JOIN Users u ON r.user_id = u.user_id " +
                                   "WHERE r.quiz_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(fetchResultsQuery)) {
            stmt.setInt(1, quizId);
            ResultSet rs = stmt.executeQuery();
            if (!rs.isBeforeFirst()) {
                System.out.println("No results found for the selected quiz.");
                return;
            }
            System.out.println("Results for Quiz ID " + quizId + ":");
            while (rs.next()) {
                String username = rs.getString("username");
                int score = rs.getInt("score");
                int totalQuestions = rs.getInt("total_questions");
                int correctAnswers = rs.getInt("correct_answers");
                int attemptedQuestions = rs.getInt("attempted_questions");
                Timestamp completedAt = rs.getTimestamp("completed_at");
                System.out.println("Username: " + username);
                System.out.println("Score: " + score);
                System.out.println("Total Questions: " + totalQuestions);
                System.out.println("Correct Answers: " + correctAnswers);
                System.out.println("Attempted Questions: " + attemptedQuestions);
                System.out.println("Completed At: " + completedAt);
            }
        } catch (SQLException | ClassNotFoundException e) {
            System.out.println("Error retrieving quiz results: " + e.getMessage());
        }
    }
}