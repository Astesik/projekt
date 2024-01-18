package net.javaguides.swing.database;

import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class DatabaseHandler {
    private static final String JDBC_URL = "jdbc:mysql://localhost:3306/pr";
    private static final String USERNAME = "root";
    private static final String PASSWORD = "";

    public static Connection connect() throws SQLException {
        return DriverManager.getConnection(JDBC_URL, USERNAME, PASSWORD);
    }

    public static int checkLogin(String username, String password) {
        try (Connection connection = connect()) {
            String query = "SELECT id FROM users WHERE username = ? AND password = ?";
            try (PreparedStatement statement = connection.prepareStatement(query)) {
                statement.setString(1, username);
                statement.setString(2, password);
                try (ResultSet resultSet = statement.executeQuery()) {
                    if (resultSet.next()) {
                        return resultSet.getInt("id");
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    public static String authenticateUser(String username, String password) {
        try (Connection connection = connect()) {
            String query = "SELECT role FROM users WHERE username = ? AND password = ?";
            try (PreparedStatement statement = connection.prepareStatement(query)) {
                statement.setString(1, username);
                statement.setString(2, password);
                try (ResultSet resultSet = statement.executeQuery()) {
                    if (resultSet.next()) {
                        return resultSet.getString("role");
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return "";
    }

	public static void sendLogs(String message) {
		try (Connection connection = connect()) {
			String query = "INSERT INTO logs (message) VALUES (?)";
			try (PreparedStatement statement = connection.prepareStatement(query)) {
				statement.setString(1, message);
				statement.executeUpdate();
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
    public static void sendStudentGrades(int studentId, PrintWriter output) {
        try (Connection connection = connect()) {
            String query = "SELECT GROUP_CONCAT(CASE WHEN subjects.subjectName = 'Fizyka' THEN IFNULL(grades.grade, 'Brak oceny') END) AS Fizyka, GROUP_CONCAT(CASE WHEN subjects.subjectName = 'Matematyka' THEN IFNULL(grades.grade, 'Brak oceny') END) AS Matematyka, GROUP_CONCAT(CASE WHEN subjects.subjectName = 'Polski' THEN IFNULL(grades.grade, 'Brak oceny') END) AS Polski, GROUP_CONCAT(CASE WHEN subjects.subjectName = 'Chemia' THEN IFNULL(grades.grade, 'Brak oceny') END) AS Chemia, GROUP_CONCAT(CASE WHEN subjects.subjectName = 'Biologia' THEN IFNULL(grades.grade, 'Brak oceny') END) AS Biologia, GROUP_CONCAT(CASE WHEN subjects.subjectName = 'Geografia' THEN IFNULL(grades.grade, 'Brak oceny') END) AS Geografia, GROUP_CONCAT(CASE WHEN subjects.subjectName = 'Historia' THEN IFNULL(grades.grade, 'Brak oceny') END) AS Historia, GROUP_CONCAT(CASE WHEN subjects.subjectName = 'Informatyka' THEN IFNULL(grades.grade, 'Brak oceny') END) AS Informatyka, GROUP_CONCAT(CASE WHEN subjects.subjectName = 'WOS' THEN IFNULL(grades.grade, 'Brak oceny') END) AS WOS, GROUP_CONCAT(CASE WHEN subjects.subjectName = 'Angielski' THEN IFNULL(grades.grade, 'Brak oceny') END) AS Angielski, GROUP_CONCAT(CASE WHEN subjects.subjectName = 'WF' THEN IFNULL(grades.grade, 'Brak oceny') END) AS WF FROM students INNER JOIN users ON users.id = students.userID INNER JOIN classes ON classes.classID = students.classID LEFT JOIN subjects ON 1 LEFT JOIN grades ON grades.studentID = students.studentID AND subjects.subjectID = grades.subjectID WHERE students.userID = ?  -- Dodany warunek dla konkretnego studenta GROUP BY students.studentID, users.name, users.surname;";
            try (PreparedStatement statement = connection.prepareStatement(query)) {
                statement.setInt(1, studentId);
                try (ResultSet resultSet = statement.executeQuery()) {
                    // Usuwamy resultSet.next() przed pętlą while
                    //System.out.println("Działam! 2"+ resultSet.next());
                    //System.out.println(resultSet.getString("subjectName") + " " + resultSet.getString("grade"));
                    
                    while (resultSet.next()) {
                        String subject1 = resultSet.getString("Fizyka");
                        String subject2 = resultSet.getString("Matematyka");
                        String subject3 = resultSet.getString("Polski");
                        String subject4 = resultSet.getString("Chemia");
                        String subject5 = resultSet.getString("Biologia");
                        String subject6 = resultSet.getString("Geografia");
                        String subject7 = resultSet.getString("Historia");
                        String subject8 = resultSet.getString("Informatyka");
                        String subject9 = resultSet.getString("WOS");
                        String subject10 = resultSet.getString("Angielski");
                        String subject11 = resultSet.getString("WF");
                        output.println(subject1);
                        output.println(subject2);
                        output.println(subject3);
                        output.println(subject4);
                        output.println(subject5);
                        output.println(subject6);
                        output.println(subject7);
                        output.println(subject8);
                        output.println(subject9);
                        output.println(subject10);
                        output.println(subject11);
                    }
                }
            }
            output.println("END");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    public static void addGrade(int studentId, String subjectName, int grade, PrintWriter output) {
        try (Connection connection = connect()) {
            String query = "INSERT INTO grades (studentID, subjectID, grade) VALUES (?, (SELECT subjectID FROM subjects WHERE subjectName = ?), ?);";
            try (PreparedStatement statement = connection.prepareStatement(query)) {
                statement.setInt(1, studentId);
                statement.setString(2, subjectName);
                statement.setInt(3, grade);
                int rowsAffected = statement.executeUpdate();

                if (rowsAffected > 0) {
                    output.println("Ocena została dodana pomyślnie.");
                } else {
                	output.println("Nie udało się dodać oceny.");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    public static void deleteGrade(int studentId, String subjectName, int grade, PrintWriter output) {
        try (Connection connection = connect()) {
            String query = "DELETE FROM grades WHERE studentID = ? AND subjectID = (SELECT subjectID FROM subjects WHERE subjectName = ?) AND grade = ? LIMIT 1;";
            try (PreparedStatement statement = connection.prepareStatement(query)) {
                statement.setInt(1, studentId);
                statement.setString(2, subjectName);
                statement.setInt(3, grade);
                int rowsAffected = statement.executeUpdate();

                if (rowsAffected > 0) {
                    output.println("Ocena została usunieta pomyślnie.");
                } else {
                	output.println("Nie udało się usunąć oceny.");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    public static void addStudent(String login,String password,String name, String surname, PrintWriter output) {
        try (Connection connection = connect()) {
            String query = "INSERT INTO users (username,password,role,name,surname) VALUES (?,?,'student',?, ?)";
            try (PreparedStatement statement = connection.prepareStatement(query)) {
                statement.setString(1, login);
                statement.setString(2, password);
                statement.setString(3, name);
                statement.setString(4, surname);
                int rowsAffected = statement.executeUpdate();

                if (rowsAffected > 0) {
                    output.println("Student został dodany pomyślnie.");
                } else {
                	output.println("Nie udało się dodać studenta.");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    public static void deleteStudent(String id, PrintWriter output) {
        try (Connection connection = connect()) {
            String query = "DELETE FROM users WHERE id = ? AND role = 'student';";
            try (PreparedStatement statement = connection.prepareStatement(query)) {
                statement.setInt(1, Integer.parseInt(id));
                int rowsAffected = statement.executeUpdate();

                if (rowsAffected > 0) {
                    output.println("Student został usunięty pomyślnie.");
                } else {
                	output.println("Nie udało się usunąć studenta.");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    public static void linkStudent(String id, String klasa, PrintWriter output) {
        try (Connection connection = connect()) {
            String query = "UPDATE students SET classID = ( SELECT classID FROM classes WHERE className = ? ) WHERE userID = ?;";
            try (PreparedStatement statement = connection.prepareStatement(query)) {
            	statement.setString(1, klasa);
                statement.setInt(2, Integer.parseInt(id));
                
                int rowsAffected = statement.executeUpdate();

                if (rowsAffected > 0) {
                    output.println("Student został przypisany pomyślnie.");
                } else {
                	output.println("Nie udało się przypisać studenta.");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    public static void addClass(String className, PrintWriter output) {
        try (Connection connection = connect()) {
            String query = "INSERT INTO classes (className) VALUES (?)";
            try (PreparedStatement statement = connection.prepareStatement(query)) {
                statement.setString(1, className);
                int rowsAffected = statement.executeUpdate();

                if (rowsAffected > 0) {
                    output.println("Klasa została dodana pomyślnie.");
                } else {
                	output.println("Nie udało się dodać klasy.");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    public static void deleteClass(String className, PrintWriter output) {
        try (Connection connection = connect()) {
            String query = "DELETE FROM classes WHERE className = ?";
            try (PreparedStatement statement = connection.prepareStatement(query)) {
            	statement.setString(1, className);
                int rowsAffected = statement.executeUpdate();

                if (rowsAffected > 0) {
                    output.println("Klasa została usunięta pomyślnie.");
                } else {
                	output.println("Nie udało się usunąć klasy.");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    public static void sendClasses(PrintWriter output) {
        try (Connection connection = connect()) {
            String query = "SELECT * FROM classes";
            try (PreparedStatement statement = connection.prepareStatement(query)) {
                try (ResultSet resultSet = statement.executeQuery()) {
                    // Usuwamy resultSet.next() przed pętlą while
                    //System.out.println("Działam! 2"+ resultSet.next());
                    //System.out.println(resultSet.getString("subjectName") + " " + resultSet.getString("grade"));
                    
                    while (resultSet.next()) {
                        String subjectName = resultSet.getString("ClassID");
                        String grade = resultSet.getString("ClassName");
                        output.println(subjectName);
                        output.println(grade);
                    }
                }
            }
            output.println("END");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    public static void sendStudents(String classId, PrintWriter output) {
        try (Connection connection = connect()) {
            String query = "SELECT students.studentID, users.name, users.surname, GROUP_CONCAT(CASE WHEN subjects.subjectName = 'Fizyka' THEN IFNULL(grades.grade, 'Brak oceny') END) AS Fizyka, GROUP_CONCAT(CASE WHEN subjects.subjectName = 'Matematyka' THEN IFNULL(grades.grade, 'Brak oceny') END) AS Matematyka, GROUP_CONCAT(CASE WHEN subjects.subjectName = 'Polski' THEN IFNULL(grades.grade, 'Brak oceny') END) AS Polski, GROUP_CONCAT(CASE WHEN subjects.subjectName = 'Chemia' THEN IFNULL(grades.grade, 'Brak oceny') END) AS Chemia, GROUP_CONCAT(CASE WHEN subjects.subjectName = 'Biologia' THEN IFNULL(grades.grade, 'Brak oceny') END) AS Biologia, GROUP_CONCAT(CASE WHEN subjects.subjectName = 'Geografia' THEN IFNULL(grades.grade, 'Brak oceny') END) AS Geografia, GROUP_CONCAT(CASE WHEN subjects.subjectName = 'Historia' THEN IFNULL(grades.grade, 'Brak oceny') END) AS Historia, GROUP_CONCAT(CASE WHEN subjects.subjectName = 'Informatyka' THEN IFNULL(grades.grade, 'Brak oceny') END) AS Informatyka, GROUP_CONCAT(CASE WHEN subjects.subjectName = 'WOS' THEN IFNULL(grades.grade, 'Brak oceny') END) AS WOS, GROUP_CONCAT(CASE WHEN subjects.subjectName = 'Angielski' THEN IFNULL(grades.grade, 'Brak oceny') END) AS Angielski, GROUP_CONCAT(CASE WHEN subjects.subjectName = 'WF' THEN IFNULL(grades.grade, 'Brak oceny') END) AS WF FROM students INNER JOIN users ON users.id = students.userID INNER JOIN classes ON classes.classID = students.classID LEFT JOIN subjects ON 1 LEFT JOIN grades ON grades.studentID = students.studentID AND subjects.subjectID = grades.subjectID WHERE classes.className = ? GROUP BY students.studentID, users.name, users.surname;";
            try (PreparedStatement statement = connection.prepareStatement(query)) {
            	statement.setString(1, classId);
                try (ResultSet resultSet = statement.executeQuery()) {
                    // Usuwamy resultSet.next() przed pętlą while
                    //System.out.println("Działam! 2"+ resultSet.next());
                    //System.out.println(resultSet.getString("subjectName") + " " + resultSet.getString("grade"));
                    
                    while (resultSet.next()) {
                        String studentID = resultSet.getString("studentID");
                        String name = resultSet.getString("name") + " " + resultSet.getString("surname");
                        String fizyka = resultSet.getString("Fizyka");
                        String matematyka = resultSet.getString("Matematyka");
                        String polski = resultSet.getString("Polski");
                        String chemia = resultSet.getString("Chemia");
                        String biologia = resultSet.getString("Biologia");
                        String geografia = resultSet.getString("Geografia");
                        String historia = resultSet.getString("Historia");
                        String informatyka = resultSet.getString("Informatyka");
                        String wos = resultSet.getString("WOS");
                        String angielski = resultSet.getString("Angielski");
                        String wf = resultSet.getString("WF");
                        output.println(studentID);
                        output.println(name);
                        output.println(fizyka);
                        output.println(matematyka);
                        output.println(polski);
                        output.println(chemia);
                        output.println(biologia);
                        output.println(geografia);
                        output.println(historia);
                        output.println(informatyka);
                        output.println(wos);
                        output.println(angielski);
                        output.println(wf);
                    }
                }
            }
            output.println("END");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    public static void sendStudentsAdmin(PrintWriter output) {
        try (Connection connection = connect()) {
            String query = "SELECT users.id,users.username,users.password,users.name,users.surname, IFNULL(classes.className, \"Brak klasy\") AS className FROM students INNER JOIN users ON students.userID = users.id LEFT JOIN classes ON students.classID = classes.classID;";
            try (PreparedStatement statement = connection.prepareStatement(query)) {
                try (ResultSet resultSet = statement.executeQuery()) {
                    // Usuwamy resultSet.next() przed pętlą while
                    //System.out.println("Działam! 2"+ resultSet.next());
                    //System.out.println(resultSet.getString("subjectName") + " " + resultSet.getString("grade"));
                    
                    while (resultSet.next()) {
                        String studentID = resultSet.getString("id");
                        String username = resultSet.getString("username");
                        String password = resultSet.getString("password");
                        String name = resultSet.getString("name") + " " + resultSet.getString("surname");
                        String className = resultSet.getString("className");
                        output.println(studentID);
                        output.println(username);
                        output.println(password);
                        output.println(name);
                        output.println(className);
                    }
                }
            }
            output.println("END");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}