show databases;
use quiz_app;
show tables;
CREATE TABLE Users (
    user_id INT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(100) NOT NULL,
    email VARCHAR(100) UNIQUE NOT NULL,
    password VARCHAR(255) NOT NULL,
    role ENUM('admin', 'user') DEFAULT 'user',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
CREATE TABLE Quizzes (
    quiz_id INT AUTO_INCREMENT PRIMARY KEY,
    quiz_name VARCHAR(100) NOT NULL,
    title TEXT NOT NULL,
    created_by INT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (created_by) REFERENCES Users(user_id) ON DELETE SET NULL
);
CREATE TABLE quiz_questions (
    question_id INT AUTO_INCREMENT PRIMARY KEY,
    quiz_id INT NOT NULL,
    text TEXT NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (quiz_id) REFERENCES Quizzes(quiz_id) ON DELETE CASCADE
);
CREATE TABLE quiz_options (
    option_id INT AUTO_INCREMENT PRIMARY KEY,
    question_id INT NOT NULL,
    option_text TEXT NOT NULL,
    is_correct BOOLEAN DEFAULT FALSE,
    FOREIGN KEY (question_id) REFERENCES quiz_questions(question_id) ON DELETE CASCADE
);
CREATE TABLE Results (
    result_id INT AUTO_INCREMENT PRIMARY KEY,      
    user_id INT NOT NULL,                           
    quiz_id INT NOT NULL,                           
    score INT NOT NULL,                             
    total_questions INT NOT NULL DEFAULT 0,                   
    attempted_questions INT NOT NULL,             
    completed_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES Users(user_id)
        ON DELETE CASCADE 
        ON UPDATE CASCADE,
    FOREIGN KEY (quiz_id) REFERENCES Quizzes(quiz_id) 
        ON DELETE CASCADE 
        ON UPDATE CASCADE,
    INDEX idx_user_id (user_id),                  
    INDEX idx_quiz_id (quiz_id)                     
);
select*from Users;
select*from Quizzes;
select*from quiz_questions;
select*from quiz_options;
select*from Results;


