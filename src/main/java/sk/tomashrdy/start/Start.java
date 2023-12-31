package sk.tomashrdy.start;

import sk.tomashrdy.GUI.Frame;
import sk.tomashrdy.dbCon.DatabaseConnection;
import sk.tomashrdy.entity.*;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class Start {
    //Pou�ijem na ulo�enie prihl�sen�ho pou��vatela
    private User user;
    private Frame frame;

    //Vr�ti udaje pou��vatela ktor�ho tu budem ma� ulo�eneho ako prihlaseneho
    public User getUser() {
        return user;
    }
    // Nastav�m si pou�iv�tela po prihl�sen�
    public void setUser(User user) {
        this.user = user;
    }
    //Kon�truktor
    public Start() {}
    private DatabaseConnection databaseConnection;
    Connection connection;
    //Met�da pre spustenie programu
    public void spusti(){
        databaseConnection = DatabaseConnection.getDB_con();
        connection = databaseConnection.getConnection();
        frame = new Frame(this);
    }

    public void deleteUserByEmail(String email){

        databaseConnection.executeUpdate("DELETE FROM users WHERE email = ?" ,
                email);
    }

    public ArrayList<Quiz> quizForTable(){
        String path = "D:\\Kurz\\Macrosoft\\Final_Projekt_02\\Kvizy";
        ArrayList<Quiz> quiz = new ArrayList<>();
        File directory = new File(path);
        if (directory.exists() && directory.isDirectory()){
            File[] files = directory.listFiles();
            if (files != null){
                for (File file : files){
                    Quiz newQuiz = new Quiz();
                    if (file.isFile() && file.getName().endsWith(".txt")){
                        ArrayList<String> lines = new ArrayList<>();
                        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
                            String line;
                            while ((line = reader.readLine()) != null) {
                                lines.add(line);
                            }
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        if (!lines.isEmpty()) {
                            String firstLine = lines.get(0);
                            String[] splitFirstLine = firstLine.split(";");
                            newQuiz.setName(splitFirstLine[0]);
                            newQuiz.setQuizCategory(QuizCategory.valueOf(splitFirstLine[1]));
                            newQuiz.setDifficulty(Integer.parseInt(splitFirstLine[2]));
                            quiz.add(newQuiz);
                        }
                    }
                }
            }
        }
        return quiz;
    }

    public Quiz createQuizByName(String name) {
        String path = "D:\\Kurz\\Macrosoft\\Final_Projekt_02\\Kvizy";
        File directory = new File(path);
        Quiz newQuiz = new Quiz();
        if (directory.exists() && directory.isDirectory()) {
            File[] files = directory.listFiles();
            if (files != null) {
                for (File file : files) {
                    if (file.isFile() && file.getName().endsWith(".txt")) {
                        ArrayList<String> lines = new ArrayList<>();
                        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
                            String line;
                            while ((line = reader.readLine()) != null) {
                                lines.add(line);
                            }
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        if (!lines.isEmpty() && lines.get(0).startsWith(name)) {
                            String firstLine = lines.get(0);
                            String[] splitFirstLine = firstLine.split(";");
                            newQuiz.setName(splitFirstLine[0]);
                            newQuiz.setQuizCategory(QuizCategory.valueOf(splitFirstLine[1]));
                            newQuiz.setDifficulty(Integer.parseInt(splitFirstLine[2]));
                            for (int i = 1; i < lines.size(); i++) {
                                String line = lines.get(i);
                                String[] splitLine = line.split(";");
                                String option = "";
                                boolean correct = false;
                                ArrayList<QuizOptions> options= new ArrayList<>();
                                QuizQuestion quizQuestion = new QuizQuestion(splitLine[0], options);
                                newQuiz.addQuestion(quizQuestion);
                                for (int x = 1 ; x < splitLine.length ; x+=2){
                                    int y = x+1;
                                        option = splitLine[x];
                                        if (splitLine[y].equals("1")){
                                            correct = true;
                                        }else { correct = false; }
                                        QuizOptions quizOption = new QuizOptions(null , false);
                                        quizOption.setTextOptions(option);
                                        quizOption.setIsCorrect(correct);
                                        quizQuestion.addOptions(quizOption);
                                }
                            }
                            break;
                        }
                    }
                }
            }
        }
        return newQuiz;
    }
    public void deleteQuiz(String quizNameForDelete){
        String path = "D:\\Kurz\\Macrosoft\\Final_Projekt_02\\Kvizy";
        File directory = new File(path);
        if (directory.exists() && directory.isDirectory()){
            File[] files = directory.listFiles();
            if (files != null){
                for (File file : files){
                    if (file.isFile() && file.getName().endsWith(".txt")){
                        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
                            String line;
                            line = reader.readLine();
                            if (line.startsWith(quizNameForDelete)){
                                reader.close();
                                String fileName = file.getName();
                                Path filePath = Paths.get(path, fileName);
                                Files.delete(filePath);
                            }
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        }
    }
    public void createNewQuiz(Quiz quiz){
        String path = "D:\\Kurz\\Macrosoft\\Final_Projekt_02\\Kvizy\\" + quiz.getNameForFile() + ".txt";
        try (FileWriter writer = new FileWriter(path , true)) {
           writer.write(quiz.getName() + ";" + quiz.getQuizCategory() + ";" + quiz.getDifficulty());
           ArrayList<QuizQuestion> quizQuestions = new ArrayList<>();
           quizQuestions = quiz.getQuestions();
           for (int i = 0 ; i < quizQuestions.size() ; i++){
               writer.write("\n" + quizQuestions.get(i).getTextQuestion());
               for (int j = 0 ; j < quizQuestions.get(i).getTextOptions().size() ; j++)
               writer.write(";" + quizQuestions.get(i).getTextOptions().get(j).getTextOptions() + ";" +
                       (quizQuestions.get(i).getTextOptions().get(j).isCorrect() ? "1" : "0"));
           }
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }
    public void updateScore(String userEmail, Integer scoreFromQuiz) {
        ResultSet resultSet = databaseConnection.executeQuery("SELECT score FROM users WHERE email = ?", userEmail);
        try {
            if (resultSet.next()) {
                int newScore = resultSet.getInt("score");
                newScore += scoreFromQuiz;
                databaseConnection.executeUpdate("UPDATE users SET score = ? WHERE email = ?", newScore, userEmail);
            } else {
                // Riadok s dan�m emailom nebol n�jden�
                throw new RuntimeException("Riadok s dan�m emailom nebol n�jden�.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    public int getScore(String email){
        ResultSet resultSet = databaseConnection.executeQuery("SELECT score FROM users WHERE email = ?", email);
        try {
            if (resultSet.next()) {
                int score = resultSet.getInt("score");
                return score;
            } else {
                // Riadok s dan�m emailom nebol n�jden�
                throw new RuntimeException("Riadok s dan�m emailom nebol n�jden�.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }
    public ArrayList<User> getAllUsers() {
        ArrayList<User> allUsers = new ArrayList<>();
        ResultSet resultSet;

        resultSet = databaseConnection.executeQuery("SELECT * FROM users");

        try {
            while (resultSet.next()) {
                String name = resultSet.getString("first_name");
                String lastName = resultSet.getString("last_name");
                String email = resultSet.getString("email");
                int score = resultSet.getInt("score");
                boolean isAdmin = resultSet.getBoolean("isAdmin");

                User user = new User(name, lastName, email , isAdmin , score);
                allUsers.add(user);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return allUsers;
    }
    //Metoda na kontrolu emailu a hesla
    public boolean loginControl(String email, String password) {
        // V query si vytvor�m dotaz na DB kolko riadkov obsahuje dan� email a heslo ( je potrebn� heslo posiela� u� za�ifrovan� )
        String query = "SELECT COUNT(*) FROM users WHERE email = ? AND password = ?";
        //�tandardne je loginSuccessful false zmen�m ho nesk�r ak sa bude zhodova� nejaky mail a heslo so zadan�mi
        boolean loginSuccessful = false;

        //Vytvorenie prepojenia a zadanie query , v�sledok sa ulo�� do resulSetu
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, email);
            statement.setString(2, password);

            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                int count = resultSet.getInt(1);
                //Do count sa mi ulo�ilo ��slo z prveho columu resultsetu , n�sledne ni��ie skontrolujem �i je count ve�� ako 0
                // ak je ve�� tak v databaze je email a heslo ktore som zadal do loginu -> loginsuccessful sa zmen� na true
                loginSuccessful = (count > 0);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return loginSuccessful;
    }
    //Metoda ktora mi bude vracia� celu zlo�ku u�ivatela podla emailu
    public User getUserByEmail(String email) {
        User user = null;
        //Dotaz na DB aby mi vitiahla first_name , last_name , email a to �i je admin u��vatel podla emailu ( vstupn� parameter email )
        String query = "SELECT first_name, last_name, email , isAdmin , score FROM users WHERE email = ?";

        try (PreparedStatement statement = connection.prepareStatement(query)) {
            //Prid�m si do query mail zo vstupu a hod�m ho na prv� ot�znik
            statement.setString(1, email);

            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                //Pouklad�m si do premenn�ch �daje ktore som si vy�iadal podla n�zvov stlpcov
                String name = resultSet.getString("first_name");
                String lastName = resultSet.getString("last_name");
                String userEmail = resultSet.getString("email");
                int score = resultSet.getInt("score");
                boolean isAdmin = resultSet.getBoolean("isAdmin");
                //Vytvor�m si Usera a nasetujem mu premenne
                user = new User(name, lastName, userEmail , isAdmin , score);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return user;
    }

    //Metoda pre ooverenie �i existuje email ( apr�klad pri registr�cii over�m �i email existuje ak ano neprid�m ho znova )
    public boolean emailExist(String email) {
        boolean mailExist = false;
        //Vytvor�m dotaz aby mi spo�ital po�et v�skytov kde email je rovnak� ako email ktor� som zadal na vstupe
        String query = "SELECT COUNT(*) FROM users WHERE email = ?";
        //Prid�m do statementu email zo vstupu ( v�ade sa sna��m o�etri� mail metodou .toLoweCase() aby som sa vyhol probl�mom pri porovn�vani )
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, email.toLowerCase());
            // Ak je po�et v�skitov ve�� ako 0 tak mi ulo�� do mailExist true
            try (ResultSet rs = statement.executeQuery()) {
                if (rs.next()) {
                    int count = rs.getInt(1);
                    mailExist = (count > 0);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return mailExist;
    }
    //Metoda ktor� mi prid� noveho u��vatela do datab�zy pomocou mojej met�dy executeUpdate
    public void userRegister(User user){
        databaseConnection.executeUpdate("INSERT INTO users (first_name, last_name, email, password, isadmin, score) VALUES (?, ?, ?, ?, ?, 0)" ,
                user.getName() , user.getLastName() , user.getEmail() , user.getPassword() , user.isAdmin());
    }
}
