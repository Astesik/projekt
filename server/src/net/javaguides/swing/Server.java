package net.javaguides.swing;

import net.javaguides.swing.database.DatabaseHandler;

import java.awt.BorderLayout;
import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

import javax.swing.*;



public class Server {
    private ServerSocket serverSocket;
    private JFrame logFrame;
    private JTextArea logTextArea;

    public static void main(String[] args) {
        Server server = new Server();
        server.initializeLogFrame();
        server.startServer();
    }
    
    LocalTime currentTime = LocalTime.now();

    // Zdefiniuj format, np. "HH:mm:ss"
    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm:ss");
    
    private void initializeLogFrame() {
        logFrame = new JFrame("Log Serwera");
        logFrame.setBounds(100, 100, 600, 400);
        logFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        logTextArea = new JTextArea();
        logTextArea.setEditable(false);

        JScrollPane scrollPane = new JScrollPane(logTextArea);
        logFrame.getContentPane().add(scrollPane, BorderLayout.CENTER);

        logFrame.setVisible(true);
    }

    private void log(String message) {
        SwingUtilities.invokeLater(() -> {
        	String formattedTime = currentTime.format(formatter);
            logTextArea.append("["+formattedTime+"]: "+ message + "\n");
            logTextArea.setCaretPosition(logTextArea.getDocument().getLength());
        });
    }

    private void startServer() {
        try {
            serverSocket = new ServerSocket(5554);
            log("Serwer uruchomiony na porcie 5554");

            while (true) {
                Socket socket = serverSocket.accept();

                ServerThread serverThread = new ServerThread(socket);
                new Thread(serverThread).start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    class ServerThread implements Runnable {
        private Socket socket;

        public ServerThread(Socket socket) {
            this.socket = socket;
        }

        @Override
        public void run() {
            try {
                BufferedReader input = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                PrintWriter output = new PrintWriter(socket.getOutputStream(), true);

                String request = input.readLine();

                switch (request) {
                    case "LOGIN":
                        handleLogin(input, output);
                        break;
                    case "SHOW_GRADES":
                        handleShowGrades(input, output);
                        break;
                    case "ADD_GRADES":
                        handleAddGrades(input, output);
                        break;
                    case "DELETE_GRADES":
                        handleDeleteGrades(input, output);
                        break;
                    case "ADD_STUDENT":
                        handleAddStudent(input, output);
                        break;
                    case "DELETE_STUDENT":
                        handleDeleteStudent(input, output);
                        break;
                    case "LINK_STUDENT":
	                    handleLinkStudent(input, output);
	                    break;
                    case "ADD_CLASS":
                        handleAddClass(input, output);
                        break;
                    case "DELETE_CLASS":
                        handleDeleteClass(input, output);
                        break;
                    case "SHOW_CLASSES":
                        handleShowClasses(input, output);
                        break;
                    case "SHOW_STUDENTS":
                        handleShowStudents(input, output);
                        break;
                    case "SHOW_STUDENTS_ADMIN":
                        handleShowStudentsAdmin(input, output);
                        break;
                    default:
                        log("Nieznane zapytanie: " + request);
                        output.println("FAILURE");
                }

                socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        private void sendLogs(String message) {
        	DatabaseHandler.sendLogs(message);
        }

        private void handleLogin(BufferedReader input, PrintWriter output) {
            try {
                String username = input.readLine();
                String password = input.readLine();

                int queryRes = DatabaseHandler.checkLogin(username, password);
                if (queryRes > 0) {
                    String role = DatabaseHandler.authenticateUser(username, password);
                    output.println("SUCCESS");
                    output.println(queryRes);
                    output.println(role);
                    log("Zalogowano użytkownika " + username + " (id: " + queryRes + ", rola: " + role + ")");
                } else {
                    output.println("FAILURE");
                    log("Nieudana próba logowania użytkownika " + username);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        public void handleShowGrades(BufferedReader input, PrintWriter output) {
            try {
                String studentId = input.readLine();
                DatabaseHandler.sendStudentGrades(Integer.parseInt(studentId), output);
                log("Wysłano zapytanie o oceny studenta o id: " + studentId);
            } catch (IOException | NumberFormatException e) {
                e.printStackTrace();
            }
        }
        public void handleAddGrades(BufferedReader input, PrintWriter output) {
            try {
            	String teacherId = input.readLine();
                String studentId = input.readLine();
                String subjectName = input.readLine();
                String grade = input.readLine();
                DatabaseHandler.addGrade(Integer.parseInt(studentId), subjectName, Integer.parseInt(grade), output);
                log("Wysłano zapytanie o dodanie oceny studentowi o id: " + studentId+" przez nauczyciela o id: "+teacherId);
            } catch (IOException | NumberFormatException e) {
            	output.println("Błąd podczas dodawania oceny");
            	log("Błąd podczas dodawania oceny");
                e.printStackTrace();
            }
        }
        public void handleDeleteGrades(BufferedReader input, PrintWriter output) {
            try {
            	String teacherId = input.readLine();
                String studentId = input.readLine();
                String subjectName = input.readLine();
                String grade = input.readLine();
                DatabaseHandler.deleteGrade(Integer.parseInt(studentId), subjectName, Integer.parseInt(grade), output);
                log("Wysłano zapytanie o usunięcie oceny studentowi o id: " + studentId+" przez nauczyciela o id: "+teacherId);
            } catch (IOException | NumberFormatException e) {
            	output.println("Błąd podczas usuwania oceny");
            	log("Błąd podczas usuwania oceny");
                e.printStackTrace();
            }
        }
        public void handleAddStudent(BufferedReader input, PrintWriter output) {
            try {
            	String login = input.readLine();
            	String password = input.readLine();
            	String name = input.readLine();
            	String surname = input.readLine();
                DatabaseHandler.addStudent(login, password, name, surname, output);
                log("Wysłano zapytanie o dodanie studenta);
            } catch (IOException | NumberFormatException e) {
            	output.println("Błąd podczas dodawania studenta");
            	log("Błąd podczas dodawania studenta");
                e.printStackTrace();
            }
        }
        public void handleDeleteStudent(BufferedReader input, PrintWriter output) {
            try {
            	String id = input.readLine();
                DatabaseHandler.deleteStudent(id, output);
                log("Wysłano zapytanie o usuniecie studenta);
            } catch (IOException | NumberFormatException e) {
            	output.println("Błąd podczas usuwania studenta");
            	log("Błąd podczas usuwania studenta");
                e.printStackTrace();
            }
        }
        public void handleAddClass(BufferedReader input, PrintWriter output) {
            try {
            	String className = input.readLine();
                DatabaseHandler.addClass(className, output);
                log("Wysłano zapytanie o dodanie klasy);
            } catch (IOException | NumberFormatException e) {
            	output.println("Błąd podczas dodawania klasy");
            	log("Błąd podczas dodawania klasy");
                e.printStackTrace();
            }
        }
        public void handleDeleteClass(BufferedReader input, PrintWriter output) {
            try {
            	String className = input.readLine();
            	DatabaseHandler.deleteClass(className, output);
                log("Wysłano zapytanie o usuniecie klasy);
            } catch (IOException | NumberFormatException e) {
            	output.println("Błąd podczas usuwania klasy");
            	log("Błąd podczas usuwania klasy");
                e.printStackTrace();
            }
        }
        public void handleLinkStudent(BufferedReader input, PrintWriter output) {
            try {
            	String id = input.readLine();
            	String klasa = input.readLine();
                DatabaseHandler.linkStudent(id,klasa, output);
                log("Wysłano zapytanie o przypisanie klasy do studenta);
            } catch (IOException | NumberFormatException e) {
            	output.println("Błąd podczas przypisywania klasy do studenta");
            	log("Błąd podczas przypisywania klasy do studenta");
                e.printStackTrace();
            }
        }
        public void handleShowClasses(BufferedReader input, PrintWriter output) {
            try {
                DatabaseHandler.sendClasses(output);
            } catch (NumberFormatException e) {
                e.printStackTrace();
            }
        }
        public void handleShowStudents(BufferedReader input, PrintWriter output) {
            try {
                String classId = input.readLine();
                DatabaseHandler.sendStudents(classId, output);
            } catch (IOException | NumberFormatException e) {
                e.printStackTrace();
            }
        }
        public void handleShowStudentsAdmin(BufferedReader input, PrintWriter output) {
            try {
                DatabaseHandler.sendStudentsAdmin(output);
            } catch (NumberFormatException e) {
                e.printStackTrace();
            }
        }
        
        LocalTime currentTime = LocalTime.now();

        // Zdefiniuj format, np. "HH:mm:ss"
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm:ss");
        
        private void log(String message) {
        	sendLogs(message);
        	SwingUtilities.invokeLater(() -> {
            	String formattedTime = currentTime.format(formatter);
                logTextArea.append("["+formattedTime+"]: "+ message + "\n");
                logTextArea.setCaretPosition(logTextArea.getDocument().getLength());
            });
        }
    }
}
