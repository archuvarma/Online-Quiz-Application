package com.quiz.test;
import com.quiz.models.QuizOperations;
import com.quiz.models.QuizTaking;
import com.quiz.models.UserAuthentication;
import java.util.Scanner;
public class QuizApp {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        System.out.println("Welcome to the Online Quiz Application!");
        System.out.print("Enter your role (admin/user): ");
        String role = scanner.nextLine().toLowerCase();
        if (role.equals("admin")) {
            adminMenu();
        } else if (role.equals("user")) {
            userMenu();
        } else {
            System.out.println("Invalid role. Exiting...");
        }
    }
    private static void adminMenu() {
        Scanner scanner = new Scanner(System.in);
        while (true) {
            System.out.println("\n--- Admin Menu ---");
            System.out.println("1. Create Quiz");
            System.out.println("2. View All Quizzes");
            System.out.println("3. Add Question to Quiz");
            System.out.println("4. Add Option to Question");
            System.out.println("5. Delete Question from Quiz");
            System.out.println("6. Delete Quiz");
            System.out.println("0. Exit");
            System.out.print("Choose an option: ");
            int choice = scanner.nextInt();
            scanner.nextLine();  
            try {
                switch (choice) {
                    case 1:
                        QuizOperations.createQuiz();
                        break;

                    case 2:
                        QuizOperations.viewAllQuizzes();
                        break;

                    case 3:
                        QuizOperations.addQuestionToQuiz();
                        break;

                    case 4:
                        System.out.print("Enter question ID to add options to: ");
                        int questionId = scanner.nextInt();
                        scanner.nextLine();  
                        QuizOperations.addOptionsToQuestion(questionId);
                        break;

                    case 5:
                        QuizOperations.deleteQuestionFromQuiz();
                        break;

                    case 6:
                        QuizOperations.deleteQuiz();
                        break;

                    case 0:
                        System.out.println("Exiting admin menu...");
                        return;

                    default:
                        System.out.println("Invalid option. Try again.");
                }
            } catch (Exception e) {
                System.out.println("Error: " + e.getMessage());
            }
        }
    }
    private static void userMenu() {
        Scanner scanner = new Scanner(System.in);

        while (true) {
            System.out.println("\n--- User Menu ---");
            System.out.println("1. Register");
            System.out.println("2. Login");
            System.out.println("0. Exit");
            System.out.print("Choose an option: ");
            int choice = scanner.nextInt();
            scanner.nextLine();
            try {
                switch (choice) {
                    case 1:
                        UserAuthentication.register();
                        break;

                    case 2:
                        int userId = UserAuthentication.login();
                        if (userId != -1) {
                            userQuizMenu(userId);
                        }
                        break;

                    case 0:
                        System.out.println("Exiting user menu...");
                        return;

                    default:
                        System.out.println("Invalid option. Try again.");
                }
            } catch (Exception e) {
                System.out.println("Error: " + e.getMessage());
            }
        }
    }
    private static void userQuizMenu(int userId) {
        Scanner scanner = new Scanner(System.in);
        while (true) {
            System.out.println("\n--- Quiz Menu ---");
            System.out.println("1. View Available Quizzes");
            System.out.println("2. Take a Quiz");
            System.out.println("3. View Your Results");
            System.out.println("0. Logout");
            System.out.print("Choose an option: ");
            int choice = scanner.nextInt();
            scanner.nextLine();  
            try {
                switch (choice) {
                    case 1:
                        QuizTaking.viewQuizzes();
                        break;
                    case 2:
                        System.out.print("Enter quiz ID to take: ");
                        int quizId = scanner.nextInt();
                        scanner.nextLine(); 
                        int[] quizResults = QuizTaking.takeQuiz(quizId, userId);
                        if (quizResults != null) {
                            int score = quizResults[0];
                            int totalQuestions = quizResults[1];
                            QuizTaking.viewResults(userId, quizId, score, totalQuestions);
                        } else {
                            System.out.println("Error occurred during quiz.");
                        }
                        break;
                    case 3:
                        System.out.println("Viewing your results...");
                        break;
                    case 0:
                        System.out.println("Logging out...");
                        return;
                    default:
                        System.out.println("Invalid option. Try again.");
                }
            } catch (Exception e) {
                System.out.println("Error: " + e.getMessage());
            }
        }
    }
}               