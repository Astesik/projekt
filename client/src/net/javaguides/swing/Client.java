package net.javaguides.swing;

import javax.swing.*;
import javax.swing.border.LineBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;

public class Client {
    private JFrame frame;
    private JTextField usernameField;
    private JPasswordField passwordField;
    private JComboBox<String> classComboBox;
    private static String clientID;
    private static String role;
    private String[] klasy = fetchClassesFromDatabase();
    private DefaultTableModel studentTableModel = new DefaultTableModel(){
        @Override
        public boolean isCellEditable(int row, int column) {
            return false; // Uniemożliwia edycję komórek
        }
    };
    private DefaultTableModel classesListModel = new DefaultTableModel(){
        @Override
        public boolean isCellEditable(int row, int column) {
            return false; // Uniemożliwia edycję komórek
        }
    };
    
    private DefaultTableModel classesTableModel = new DefaultTableModel(){
        @Override
        public boolean isCellEditable(int row, int column) {
            return false; // Uniemożliwia edycję komórek
        }
    };

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                new Client().initialize();
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }

    private void initialize() throws IOException {
        frame = new JFrame("Logowanie");
        frame.setSize(400, 250);
        frame.setResizable(false);
        frame.setLocationRelativeTo(null);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JPanel panel = new JPanel();
        frame.getContentPane().add(panel, "Center");
        placeComponents(panel);

        frame.setVisible(true);
    }

    private void placeComponents(JPanel panel) {
        panel.setLayout(null);
        panel.setSize(400, 230);
        
        int x = (panel.getWidth() - 165) / 2;
        JLabel userLabel = new JLabel("Użytkownik");
        userLabel.setBounds(x, 20, 165, 25);
        panel.add(userLabel);

        usernameField = new JTextField(20);
        usernameField.setBounds(x, 40, 165, 25);
        panel.add(usernameField);

        JLabel passwordLabel = new JLabel("Hasło");
        passwordLabel.setBounds(x, 70, 165, 25);
        panel.add(passwordLabel);

        passwordField = new JPasswordField(20);
        passwordField.setBounds(x, 90, 165, 25);
        panel.add(passwordField);

        JButton loginButton = new JButton("Zaloguj");
        // Ustawienia, aby guzik był na środku
        int buttonWidth = 165;
        int buttonHeight = 25;
        int buttonX = (panel.getWidth() - buttonWidth) / 2;
        int buttonY = 130;
        loginButton.setBounds(buttonX, buttonY, buttonWidth, buttonHeight);

        panel.add(loginButton);
        // stylizacja
        userLabel.setForeground(new Color(255, 255, 255));
        usernameField.setForeground(new Color(255, 255, 255));
        usernameField.setBackground(new Color(30, 30, 30));
        usernameField.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(Color.gray, 2, true),
                BorderFactory.createEmptyBorder(0, 5, 0, 5)));
        passwordLabel.setForeground(new Color(255, 255, 255));
        passwordField.setForeground(new Color(255, 255, 255));
        passwordField.setBackground(new Color(30, 30, 30));
        passwordField.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(Color.gray, 2, true),
                BorderFactory.createEmptyBorder(0, 5, 0, 5)));
        panel.setBackground(new Color(50, 50, 50));
        passwordLabel.setFont(new Font("Arial", Font.BOLD, 14));
        userLabel.setFont(new Font("Arial", Font.BOLD, 14));
        // loginButton
        loginButton.setBackground(new Color(30, 30, 30));
        loginButton.setFocusPainted(false);
        loginButton.setBorderPainted(false);
        loginButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        loginButton.setForeground(new Color(255, 255, 255));
        loginButton.setFont(new Font("Arial", Font.BOLD, 14));
        loginButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String username = usernameField.getText();
                String password = new String(passwordField.getPassword());

                try {
                    Socket socket = new Socket("localhost", 5554);
                    PrintWriter output = new PrintWriter(socket.getOutputStream(), true);
                    BufferedReader input = new BufferedReader(new InputStreamReader(socket.getInputStream()));

                    output.println("LOGIN");
                    output.println(username);
                    output.println(password);

                    String response = input.readLine();
                    clientID = input.readLine();
                    role = input.readLine();
                    if (response.equals("SUCCESS")) {
                        if (role.equals("student")) {
                            showStudentMainFrame(username);
                            fetchClassesFromDatabase();
                        } else if (role.equals("teacher")) {
                            showTeacherMainFrame(username);
                        } else if (role.equals("admin")) {
                            showAdminMainFrame(username);
                        } else {
                            JOptionPane.showMessageDialog(frame, "Nieznana rola użytkownika!");
                        }
                        frame.dispose();
                    } else {
                        JOptionPane.showMessageDialog(frame, "Błąd logowania!");
                    }

                    socket.close();
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            }
        });
    }

    private void showStudentMainFrame(String username) {
        JFrame mainFrame = new JFrame("System Oceniania - Student");
        mainFrame.setBounds(100, 100, 800, 600);

        // Panel tabela
        DefaultTableModel tableModel = new DefaultTableModel() {
        	@Override
            public boolean isCellEditable(int row, int column) {
                // Ustawianie, które komórki są edytowalne (true) i które nie (false)
                return false; // Tutaj możesz dostosować, które kolumny mają być edytowalne
            }};
        
            JTable table = new JTable(studentTableModel);

            table.getTableHeader().setReorderingAllowed(false);

            // Dodawanie kolumn do modelu
            studentTableModel.addColumn("Przedmiot");
            studentTableModel.addColumn("Ocena");

            // Pobieranie danych z bazy i dodawanie ich do modelu tabeli
            fetchDataFromDatabase(Integer.parseInt(clientID), studentTableModel);

        // Dodawanie tabeli do JScrollPane, aby można było przewijać wyniki
        JScrollPane scrollPane = new JScrollPane(table);
        mainFrame.getContentPane().add(scrollPane, BorderLayout.CENTER);
        JButton refreshButton = new JButton("Odśwież");
        refreshButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                refreshStudentTable();
            }

			private void refreshStudentTable() {
				studentTableModel.setRowCount(0);
	            fetchDataFromDatabase(Integer.parseInt(clientID), studentTableModel);
				
			}
        });

        mainFrame.getContentPane().add(refreshButton, BorderLayout.SOUTH);



        mainFrame.setVisible(true);
    }

    private void fetchDataFromDatabase(int studentId, DefaultTableModel tableModel) {
        try {
            Socket socket = new Socket("localhost", 5554);
            PrintWriter output = new PrintWriter(socket.getOutputStream(), true);
            output.println("SHOW_GRADES");
            output.println(studentId);
            System.out.println(studentId);

            BufferedReader input = new BufferedReader(new InputStreamReader(socket.getInputStream()));

            String Fizyka;
            while ((Fizyka = input.readLine()) != null && !Fizyka.equals("END")) {
                String Matematyka = input.readLine();
                String Polski = input.readLine();
                String Chemia = input.readLine();
                String Biologia = input.readLine();
                String Geografia = input.readLine();
                String Historia = input.readLine();
                String Informatyka = input.readLine();
                String WOS = input.readLine();
                String Angielski = input.readLine();
                String WF = input.readLine();
                tableModel.addRow(new Object[]{"Fizyka", Fizyka});
                tableModel.addRow(new Object[]{"Matematyka", Matematyka});
                tableModel.addRow(new Object[]{"Polski", Polski});
                tableModel.addRow(new Object[]{"Chemia", Chemia});
                tableModel.addRow(new Object[]{"Biologia", Biologia});
                tableModel.addRow(new Object[]{"Geografia", Geografia});
                tableModel.addRow(new Object[]{"Historia", Historia});
                tableModel.addRow(new Object[]{"Informatyka", Informatyka});
                tableModel.addRow(new Object[]{"WOS", WOS});
                tableModel.addRow(new Object[]{"Angielski", Angielski});
                tableModel.addRow(new Object[]{"WF", WF});
            }

            socket.close();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }
    private String[] fetchClassesFromDatabase() {
    	ArrayList<String> klasy = new ArrayList<String>();
        try {
            Socket socket = new Socket("localhost", 5554);
            PrintWriter output = new PrintWriter(socket.getOutputStream(), true);
            output.println("SHOW_CLASSES");

            BufferedReader input = new BufferedReader(new InputStreamReader(socket.getInputStream()));

            String subjectName;
            while ((subjectName = input.readLine()) != null && !subjectName.equals("END")) {
                String klasa = input.readLine();
                klasy.add(klasa);
            }
            this.klasy = klasy.toArray(new String[klasy.size()]);
            klasy.forEach(klasa -> System.out.println(klasa));
            socket.close();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        return this.klasy;
    }
    private void fetchStudentsFromDatabase(String classId, DefaultTableModel tableModel) {
        try {
            Socket socket = new Socket("localhost", 5554);
            PrintWriter output = new PrintWriter(socket.getOutputStream(), true);
            output.println("SHOW_STUDENTS");
            output.println(classId);

            BufferedReader input = new BufferedReader(new InputStreamReader(socket.getInputStream()));

            String id;
            while ((id = input.readLine()) != null && !id.equals("END")) {
                String name = input.readLine();
                String gradeFizyka = input.readLine();
                String gradeMatematyka = input.readLine();
                String gradePolski = input.readLine();
                String gradeChemia = input.readLine();
                String gradeBiologia = input.readLine();
                String gradeGeografia = input.readLine();
                String gradeHistoria = input.readLine();
                String gradeInformatyka = input.readLine();
                String gradeWOS = input.readLine();
                String gradeAngielski = input.readLine();
                String gradeWF = input.readLine();
                tableModel.addRow(new Object[]{id, name, gradeFizyka, gradeMatematyka, gradePolski, gradeChemia, gradeBiologia, gradeGeografia, gradeHistoria, gradeInformatyka, gradeWOS, gradeAngielski, gradeWF});
            }

            socket.close();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }
    
    private void fetchClassesAdminFromDatabase(DefaultTableModel tableModel) {
        try {
            Socket socket = new Socket("localhost", 5554);
            PrintWriter output = new PrintWriter(socket.getOutputStream(), true);
            output.println("SHOW_CLASSES");

            BufferedReader input = new BufferedReader(new InputStreamReader(socket.getInputStream()));

            String id;
            while ((id = input.readLine()) != null && !id.equals("END")) {
                String name = input.readLine();
                tableModel.addRow(new Object[]{id, name});
            }

            socket.close();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }
    private void fetchStudentsAdminFromDatabase(DefaultTableModel tableModel) {
        try {
            Socket socket = new Socket("localhost", 5554);
            PrintWriter output = new PrintWriter(socket.getOutputStream(), true);
            output.println("SHOW_STUDENTS_ADMIN");

            BufferedReader input = new BufferedReader(new InputStreamReader(socket.getInputStream()));

            String id;
            while ((id = input.readLine()) != null && !id.equals("END")) {
                String username = input.readLine();
                String password = input.readLine();
                String name = input.readLine();
                String className = input.readLine();
                tableModel.addRow(new Object[]{id, username, password, name, className});
            }

            socket.close();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }
    private void fetchAddGradeDatabase(String uczen, String przedmiot, String ocena) {
        try {
            Socket socket = new Socket("localhost", 5554);
            PrintWriter output = new PrintWriter(socket.getOutputStream(), true);
            output.println("ADD_GRADES");
            output.println(clientID);
            output.println(uczen);
            output.println(przedmiot);
            output.println(ocena);
            
            BufferedReader input = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            JOptionPane.showMessageDialog(frame, input.readLine());

            socket.close();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }
    private void fetchDeleteGradeDatabase(String uczen, String przedmiot, String ocena) {
        try {
            Socket socket = new Socket("localhost", 5554);
            PrintWriter output = new PrintWriter(socket.getOutputStream(), true);
            output.println("DELETE_GRADES");
            output.println(clientID);
            output.println(uczen);
            output.println(przedmiot);
            output.println(ocena);
            
            BufferedReader input = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            JOptionPane.showMessageDialog(frame, input.readLine());

            socket.close();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }
    private void fetchAddStudentDatabase(String login,String haslo,String imie,String nazwisko) {
        try {
            Socket socket = new Socket("localhost", 5554);
            PrintWriter output = new PrintWriter(socket.getOutputStream(), true);
            output.println("ADD_STUDENT");
            output.println(login);
            output.println(haslo);
            output.println(imie);
            output.println(nazwisko);
            
            BufferedReader input = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            JOptionPane.showMessageDialog(frame, input.readLine());

            socket.close();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }
    private void fetchDeleteStudentDatabase(String id) {
        try {
            Socket socket = new Socket("localhost", 5554);
            PrintWriter output = new PrintWriter(socket.getOutputStream(), true);
            output.println("DELETE_STUDENT");
            output.println(id);
            
            BufferedReader input = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            JOptionPane.showMessageDialog(frame, input.readLine());

            socket.close();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }
    private void listStudentToClassDatabase(String id, String klasa) {
        try {
            Socket socket = new Socket("localhost", 5554);
            PrintWriter output = new PrintWriter(socket.getOutputStream(), true);
            output.println("LINK_STUDENT");
            output.println(id);
            output.println(klasa);
            
            BufferedReader input = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            JOptionPane.showMessageDialog(frame, input.readLine());

            socket.close();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }
    private void fetchAddClassDatabase(String klasa) {
        try {
            Socket socket = new Socket("localhost", 5554);
            PrintWriter output = new PrintWriter(socket.getOutputStream(), true);
            output.println("ADD_CLASS");
            output.println(klasa);
            
            BufferedReader input = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            JOptionPane.showMessageDialog(frame, input.readLine());

            socket.close();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }
    private void fetchDeleteClassDatabase(String klasa) {
        try {
            Socket socket = new Socket("localhost", 5554);
            PrintWriter output = new PrintWriter(socket.getOutputStream(), true);
            output.println("DELETE_CLASS");
            output.println(klasa);
            
            BufferedReader input = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            JOptionPane.showMessageDialog(frame, input.readLine());

            socket.close();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }
    private void showAdminMainFrame(String username) {
        JFrame mainFrame = new JFrame("System Oceniania - Administrator");
        mainFrame.setExtendedState(JFrame.MAXIMIZED_BOTH);
        mainFrame.setBounds(100, 100, 1024, 768);

        JPanel mainPanel = new JPanel(new BorderLayout());

        // Left Panel for Class Selection (bez zmian)
        JPanel leftPanel = new JPanel(new FlowLayout());
        leftPanel.setPreferredSize(new Dimension(400, mainFrame.getHeight()));
        classComboBox = new JComboBox<>(klasy);
        JTable tableClasses = new JTable(classesListModel);
        tableClasses.getTableHeader().setReorderingAllowed(false);
        classesListModel.addColumn("ID");
        classesListModel.addColumn("Klasa");
        fetchClassesAdminFromDatabase(classesListModel);
        JScrollPane scrollPane1 = new JScrollPane(tableClasses);
        scrollPane1.setPreferredSize(new Dimension(400, 400));

        JPanel changeClasses1 = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JPanel changeClasses2 = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JLabel classLabel = new JLabel("Klasa:");
        JTextField classField = new JTextField();
        classField.setPreferredSize(new Dimension(100, 30));
        JButton addClassButton = new JButton("Dodaj Klasę");
        addClassButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
            	addClass();
                refreshStudentTable();
            }

			private void addClass() {
				String klasa = classField.getText();
				fetchAddClassDatabase(klasa);
			}

			private void refreshStudentTable() {
				classesListModel.setRowCount(0);
				fetchClassesAdminFromDatabase(classesListModel);
				
			}
        });
        addClassButton.setPreferredSize(new Dimension(200, 30));
        JButton removeClassButton = new JButton("Usuń Klasę");
        removeClassButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
            	removeClass();
                refreshStudentTable();
            }

			private void removeClass() {
				String klasa = classField.getText();
				fetchDeleteClassDatabase(klasa);
			}

			private void refreshStudentTable() {
				classesListModel.setRowCount(0);
				fetchClassesAdminFromDatabase(classesListModel);
				
			}
        });
        removeClassButton.setPreferredSize(new Dimension(200, 30));
        changeClasses1.add(classLabel);
        changeClasses1.add(classField);
        changeClasses2.add(addClassButton);
        changeClasses2.add(removeClassButton);

        leftPanel.add(scrollPane1);
        leftPanel.add(changeClasses1);
        leftPanel.add(changeClasses2);
        JButton logoutButton = new JButton("Wyloguj");
        logoutButton.setPreferredSize(new Dimension(180, 50));
        logoutButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                mainFrame.dispose();
            }
        });
        leftPanel.add(logoutButton);

        // Panel tabela
        DefaultTableModel tableModel = new DefaultTableModel() {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        JTable table = new JTable(studentTableModel);
        table.setPreferredSize(new Dimension(Integer.MAX_VALUE, mainFrame.getHeight()));
        table.getTableHeader().setReorderingAllowed(false);
        studentTableModel.addColumn("ID");
        studentTableModel.addColumn("Login");
        studentTableModel.addColumn("Hasło");
        studentTableModel.addColumn("Imie i nazwisko");
        studentTableModel.addColumn("Klasa");

        fetchStudentsAdminFromDatabase(studentTableModel);

        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setPreferredSize(new Dimension(Integer.MAX_VALUE, 500));

        // Panel ocen (bez zmian)
        JPanel gradesPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        gradesPanel.setPreferredSize(new Dimension(Integer.MAX_VALUE, 80));
        gradesPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JLabel loginLabel = new JLabel("Login:");
        JTextField loginField = new JTextField();
        loginField.setPreferredSize(new Dimension(100, 30));

        JLabel hasloLabel = new JLabel("Hasło:");
        JTextField hasloField = new JTextField();
        hasloField.setPreferredSize(new Dimension(100, 30));
        
        JLabel nameLabel = new JLabel("Imie:");
        JTextField nameField = new JTextField();
        nameField.setPreferredSize(new Dimension(100, 30));
        
        JLabel surnameLabel = new JLabel("Imie:");
        JTextField surnameField = new JTextField();
        surnameField.setPreferredSize(new Dimension(100, 30));

        JButton addStudentButton = new JButton("Dodaj ucznia");
        addStudentButton.setPreferredSize(new Dimension(200, 30));
        addStudentButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                addStudent();
                refreshStudentTable();
            }

            private void addStudent() {
                String login = loginField.getText();
                String haslo = hasloField.getText();
                String imie = nameField.getText();
                String nazwisko = surnameField.getText();
                fetchAddStudentDatabase(login, haslo, imie, nazwisko);
            }
            private void refreshStudentTable() {
				studentTableModel.setRowCount(0);
				fetchStudentsAdminFromDatabase(studentTableModel);
				
			}
        });
        JLabel idLabel = new JLabel("ID:");
        JTextField idField = new JTextField();
        idField.setPreferredSize(new Dimension(100, 30));
        JButton deleteStudentButton = new JButton("Usuń ucznia");
        deleteStudentButton.setPreferredSize(new Dimension(200, 30));
        deleteStudentButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                deleteStudent();
                refreshStudentTable();
            }

            private void deleteStudent() {
                String id = idField.getText();
                fetchDeleteStudentDatabase(id);
            }
            private void refreshStudentTable() {
				studentTableModel.setRowCount(0);
				fetchStudentsAdminFromDatabase(studentTableModel);
				
			}
        });
        JLabel idLabel2 = new JLabel("ID:");
        JTextField idField2 = new JTextField();
        idField2.setPreferredSize(new Dimension(50, 30));
        JTextField idField3 = new JTextField();
        idField3.setPreferredSize(new Dimension(50, 30));
        JButton loadStudentsButton = new JButton("Przypisz klase");
        loadStudentsButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
            	listStudent();
                refreshStudentTable();
            }

			private void listStudent() {
				String id = idField2.getText();
				String klasa = idField3.getText();
				listStudentToClassDatabase(id, klasa);
			}

			private void refreshStudentTable() {
				studentTableModel.setRowCount(0);
				fetchStudentsAdminFromDatabase(studentTableModel);
				
			}
        });
        gradesPanel.add(loginLabel);
        gradesPanel.add(loginField);
        gradesPanel.add(hasloLabel);
        gradesPanel.add(hasloField);
        gradesPanel.add(nameLabel);
        gradesPanel.add(nameField);
        gradesPanel.add(surnameLabel);
        gradesPanel.add(surnameField);
        gradesPanel.add(addStudentButton);
        gradesPanel.add(idLabel);
        gradesPanel.add(idField);
        gradesPanel.add(deleteStudentButton);
        gradesPanel.add(idLabel2);
        gradesPanel.add(idField2);
        gradesPanel.add(idField3);
        gradesPanel.add(loadStudentsButton);

        // Panel na dole z tabelą i przyciskami
        JPanel bottomPanel = new JPanel(new BorderLayout());
        bottomPanel.add(scrollPane, BorderLayout.NORTH);
        bottomPanel.add(gradesPanel, BorderLayout.CENTER);

        mainPanel.add(leftPanel, BorderLayout.WEST);
        mainPanel.add(bottomPanel, BorderLayout.CENTER);

        mainFrame.getContentPane().add(mainPanel);
        mainFrame.setVisible(true);
    }
    
    private void showTeacherMainFrame(String username) {
        JFrame mainFrame = new JFrame("System Oceniania - Nauczyciel");
        mainFrame.setExtendedState(JFrame.MAXIMIZED_BOTH);
        mainFrame.setBounds(100, 100, 1024, 768);

        JPanel mainPanel = new JPanel(new BorderLayout());

        // Left Panel for Class Selection
        JPanel leftPanel = new JPanel(new FlowLayout());
        leftPanel.setPreferredSize(new Dimension(200, mainFrame.getHeight()));
        classComboBox = new JComboBox<>(klasy);
        JButton loadStudentsButton = new JButton("Wyświetl uczniów");
        loadStudentsButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                refreshStudentTable();
            }

            private void refreshStudentTable() {
                classesTableModel.setRowCount(0);
                fetchStudentsFromDatabase((String) classComboBox.getSelectedItem(), classesTableModel);

            }
        });
        leftPanel.add(classComboBox);
        leftPanel.add(loadStudentsButton);
        JButton logoutButton = new JButton("Wyloguj");
        logoutButton.setPreferredSize(new Dimension(180, 50));
        logoutButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Zamknięcie bieżącego okna
                mainFrame.dispose();
            }
        });
        leftPanel.add(logoutButton);

        // Panel tabela
        DefaultTableModel tableModel = new DefaultTableModel() {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Umożliwia edycję komórek
            }
        };
        JTable table = new JTable(classesTableModel);
        table.getTableHeader().setReorderingAllowed(false);

        // Dodawanie kolumn do modelu
        classesTableModel.addColumn("ID");
        classesTableModel.addColumn("Imie i nazwisko");
        classesTableModel.addColumn("Fizyka");
        classesTableModel.addColumn("Matematyka");
        classesTableModel.addColumn("Polski");
        classesTableModel.addColumn("Chemia");
        classesTableModel.addColumn("Biologia");
        classesTableModel.addColumn("Geografia");
        classesTableModel.addColumn("Historia");
        classesTableModel.addColumn("Informatyka");
        classesTableModel.addColumn("WOS");
        classesTableModel.addColumn("Angielski");
        classesTableModel.addColumn("WF");

        // Pobieranie danych z bazy i dodawanie ich do modelu tabeli
        fetchStudentsFromDatabase("1A", classesTableModel);

        // Dodawanie tabeli do JScrollPane, aby można było przewijać wyniki
        JScrollPane scrollPane = new JScrollPane(table);
        mainPanel.add(leftPanel, BorderLayout.WEST);
        mainPanel.add(scrollPane, BorderLayout.CENTER);

        // Dodawanie panelu dodawania ocen na dole strony
        JPanel bottomPanel = new JPanel(new BorderLayout());

        JPanel gradesPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        gradesPanel.setPreferredSize(new Dimension(Integer.MAX_VALUE, 80));
        gradesPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JLabel studentLabel = new JLabel("Uczeń:");
        JTextField studentField = new JTextField();
        studentField.setPreferredSize(new Dimension(100, 30));

        JLabel subjectLabel = new JLabel("Przedmiot:");
        JTextField subjectField = new JTextField();
        subjectField.setPreferredSize(new Dimension(100, 30));

        JLabel gradeLabel = new JLabel("Ocena:");
        JTextField gradeField = new JTextField();
        gradeField.setPreferredSize(new Dimension(100, 30));

        JButton addGradeButton = new JButton("Dodaj Oceny");
        addGradeButton.setPreferredSize(new Dimension(200, 30));
        addGradeButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                addGrade();
            }

            private void addGrade() {
                String uczen = studentField.getText();
                String przedmiot = subjectField.getText();
                String ocena = gradeField.getText();
                fetchAddGradeDatabase(uczen, przedmiot, ocena);

            }

        });
        JButton deleteGradeButton = new JButton("Usuń Oceny");
        deleteGradeButton.setPreferredSize(new Dimension(200, 30));
        deleteGradeButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                deleteGrade();
            }

            private void deleteGrade() {
                String uczen = studentField.getText();
                String przedmiot = subjectField.getText();
                String ocena = gradeField.getText();
                fetchDeleteGradeDatabase(uczen, przedmiot, ocena);

            }

        });
        gradesPanel.add(studentLabel);
        gradesPanel.add(studentField);
        gradesPanel.add(subjectLabel);
        gradesPanel.add(subjectField);
        gradesPanel.add(gradeLabel);
        gradesPanel.add(gradeField);
        gradesPanel.add(addGradeButton);
        gradesPanel.add(deleteGradeButton);

        bottomPanel.add(gradesPanel, BorderLayout.SOUTH);
        mainPanel.add(bottomPanel, BorderLayout.SOUTH);

        mainFrame.getContentPane().add(mainPanel);
        mainFrame.setVisible(true);
    }
}
