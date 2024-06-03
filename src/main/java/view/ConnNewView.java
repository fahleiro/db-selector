package view;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.io.*;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class ConnNewView extends JDialog {

    private JTextField nameTextField;
    private JTextField urlTextField;
    private JTextField usernameTextField;
    private JPasswordField passwordField;
    private JComboBox<String> typeComboBox;
    private JComboBox<String> encryptComboBox;
    private JComboBox<String> trustServerCertComboBox;

    public ConnNewView(Frame owner) {
        super(owner, "New connection", true); // Cria um JDialog modal

        // Configurações da janela
        setSize(700, 250);
        setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(owner);
        setResizable(false); // Torna a janela irredimensionável

        // Criando o painel principal com GridBagLayout
        JPanel mainPanel = new JPanel(new GridBagLayout());
        mainPanel.setBorder(BorderFactory.createTitledBorder("Main Panel"));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 5, 5, 5);

        // Adicionando rótulo e campo de texto para "Name"
        JLabel nameLabel = new JLabel("Name:");
        gbc.gridx = 0;
        gbc.gridy = 0;
        mainPanel.add(nameLabel, gbc);

        nameTextField = new JTextField(20);
        gbc.gridx = 1;
        gbc.gridy = 0;
        mainPanel.add(nameTextField, gbc);

        // Adicionando rótulo e campo de texto para "URL"
        JLabel urlLabel = new JLabel("URL:");
        gbc.gridx = 0;
        gbc.gridy = 2;
        mainPanel.add(urlLabel, gbc);

        urlTextField = new JTextField(20); // Atualizado para usar a variável de classe
        gbc.gridx = 1;
        gbc.gridy = 2;
        mainPanel.add(urlTextField, gbc);

        // Adicionando rótulo e listbox para "Type"
        JLabel typeLabel = new JLabel("Type:");
        gbc.gridx = 0;
        gbc.gridy = 3;
        mainPanel.add(typeLabel, gbc);

        String[] types = {"Oracle", "SQLServer"};
        typeComboBox = new JComboBox<>(types); // Atualizado para usar a variável de classe
        gbc.gridx = 1;
        gbc.gridy = 3;
        mainPanel.add(typeComboBox, gbc);

        // Adicionando rótulo e campo de texto para "Username"
        JLabel usernameLabel = new JLabel("Username:");
        gbc.gridx = 0;
        gbc.gridy = 4;
        mainPanel.add(usernameLabel, gbc);

        usernameTextField = new JTextField(20); // Atualizado para usar a variável de classe
        gbc.gridx = 1;
        gbc.gridy = 4;
        mainPanel.add(usernameTextField, gbc);

        // Adicionando rótulo e campo de texto para "Password"
        JLabel passwordLabel = new JLabel("Password:");
        gbc.gridx = 0;
        gbc.gridy = 5;
        mainPanel.add(passwordLabel, gbc);

        passwordField = new JPasswordField(20); // Atualizado para usar a variável de classe
        gbc.gridx = 1;
        gbc.gridy = 5;
        mainPanel.add(passwordField, gbc);

        // Botão para salvar a conexão
        JButton saveButton = new JButton("Save");
        gbc.gridx = 1;
        gbc.gridy = 6;
        gbc.anchor = GridBagConstraints.EAST;
        mainPanel.add(saveButton, gbc);

        // Botão para testar a conexão
        JButton testButton = new JButton("Test");
        gbc.gridx = 2;
        gbc.gridy = 6;
        gbc.anchor = GridBagConstraints.WEST;
        mainPanel.add(testButton, gbc);

        // Adicionando listbox para "Encrypt"
        JLabel encryptLabel = new JLabel("Encrypt:");
        gbc.gridx = 2;
        gbc.gridy = 3;
        mainPanel.add(encryptLabel, gbc);

        String[] encryptOptions = {"true", "false"};
        encryptComboBox = new JComboBox<>(encryptOptions);
        gbc.gridx = 3;
        gbc.gridy = 3;
        mainPanel.add(encryptComboBox, gbc);

        // Adicionando listbox para "Trust Server Certificate"
        JLabel trustServerCertLabel = new JLabel("Trust Server Certificate:");
        gbc.gridx = 5;
        gbc.gridy = 3;
        mainPanel.add(trustServerCertLabel, gbc);

        String[] trustServerCertOptions = {"true", "false"};
        trustServerCertComboBox = new JComboBox<>(trustServerCertOptions);
        gbc.gridx = 6;
        gbc.gridy = 3;
        mainPanel.add(trustServerCertComboBox, gbc);

        // Inicialmente escondendo os listboxes
        encryptLabel.setVisible(false);
        encryptComboBox.setVisible(false);
        trustServerCertLabel.setVisible(false);
        trustServerCertComboBox.setVisible(false);

        // Adicionando listener para mostrar/ocultar listboxes baseados no tipo selecionado
        typeComboBox.addItemListener(new ItemListener () {
            @Override
            public void itemStateChanged(ItemEvent e) {
                if (e.getStateChange() == ItemEvent.SELECTED) {
                    boolean isSQLServer = typeComboBox.getSelectedItem().toString().equalsIgnoreCase("SQLServer");
                    encryptLabel.setVisible(isSQLServer);
                    encryptComboBox.setVisible(isSQLServer);
                    trustServerCertLabel.setVisible(isSQLServer);
                    trustServerCertComboBox.setVisible(isSQLServer);
                }
            }
        });

        // Adicionando o painel principal à janela
        add(mainPanel);

        // Adicionando ação ao botão Save
        saveButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                saveConnection();
            }
        });

        testButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                testConnection();
            }
        });

        // Carregar conexões existentes no início
    }

    private void saveConnection() {
        // Obter os valores dos campos
        String name = nameTextField.getText();
        String url = urlTextField.getText();
        String type = (String) typeComboBox.getSelectedItem();
        String username = usernameTextField.getText();
        String password = new String(passwordField.getPassword());

        // Verificar se todos os campos foram preenchidos
        if (name.isEmpty() || url.isEmpty() || username.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(this, "All fields are required", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Construir a URL de conexão para SQL Server
        String connectionUrl;
        if (type.equalsIgnoreCase("SQLServer")) {
            String encrypt = (String) encryptComboBox.getSelectedItem();
            String trustServerCert = (String) trustServerCertComboBox.getSelectedItem();
            connectionUrl = "jdbc:sqlserver://" + url + ";encrypt=" + encrypt + ";trustServerCertificate=" + trustServerCert + ";user=" + username + ";password=" + password + ";";
        } else {
            connectionUrl = url; // Preservar a URL fornecida para outros tipos de banco de dados
        }

        // Criar um objeto JSON com os valores da nova conexão
        JsonObject connectionDetails = new JsonObject();
        connectionDetails.addProperty("url", connectionUrl);
        connectionDetails.addProperty("type", type);
        connectionDetails.addProperty("username", username);
        connectionDetails.addProperty("password", password);

        // Ler o conteúdo atual do arquivo, se existir
        JsonObject existingConnections = new JsonObject();
        try {
            File file = new File("src/main/resources/conn.json");
            if (file.exists()) {
                FileReader fileReader = new FileReader(file);
                existingConnections = JsonParser.parseReader(fileReader).getAsJsonObject();
                fileReader.close();
            }
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, "Error reading connection file: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Adicionar a nova conexão ao conteúdo existente
        existingConnections.add(name, connectionDetails);

        // Salvar o objeto JSON atualizado em um arquivo conn.json
        try (FileWriter fileWriter = new FileWriter("src/main/resources/conn.json")) {
            Gson gson = new GsonBuilder()
                    .setPrettyPrinting()
                    .disableHtmlEscaping() // Desativar a codificação de caracteres especiais
                    .create();
            gson.toJson(existingConnections, fileWriter);
            JOptionPane.showMessageDialog(this, "Connection saved successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);

            // Limpar os campos após o salvamento
            nameTextField.setText("");
            urlTextField.setText("");
            usernameTextField.setText("");
            passwordField.setText("");
            typeComboBox.setSelectedIndex(0); // Opcionalmente, defina o índice padrão para o primeiro item
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, "Error saving connection: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void testConnection() {
        // Obter os valores dos campos
        String name = nameTextField.getText();
        String url = urlTextField.getText();
        String type = (String) typeComboBox.getSelectedItem();
        String username = usernameTextField.getText();
        String password = new String(passwordField.getPassword());

        // Verificar se todos os campos foram preenchidos
        if (name.isEmpty() || url.isEmpty() || username.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(this, "All fields are required", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Construir a URL de conexão para SQL Server
        String connectionUrl;
        if (type.equalsIgnoreCase("SQLServer")) {
            String encrypt = (String) encryptComboBox.getSelectedItem();
            String trustServerCert = (String) trustServerCertComboBox.getSelectedItem();
            connectionUrl = "jdbc:sqlserver://" + url + ";encrypt=" + encrypt + ";trustServerCertificate=" + trustServerCert + ";user=" + username + ";password=" + password + ";";
        } else {
            connectionUrl = url; // Preservar a URL fornecida para outros tipos de banco de dados
        }

        // Registrar o driver SQL Server
        try {
            Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
        } catch (ClassNotFoundException e) {
            JOptionPane.showMessageDialog(this, "SQL Server Driver not found: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Tentar estabelecer a conexão
        try (Connection connection = DriverManager.getConnection(connectionUrl)) {
            JOptionPane.showMessageDialog(this, "Connection successful!", "Success", JOptionPane.INFORMATION_MESSAGE);
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Connection failed: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }


}
