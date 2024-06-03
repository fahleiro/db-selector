package view;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class MainView extends JFrame {

    public MainView() {
        // Configurações da janela
        setTitle("Aplicação MVC");
        setSize(400, 300);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        // Criando o menu
        JMenuBar menuBar = new JMenuBar();
        JMenu menuConnections = new JMenu("Connections");
        JMenuItem newConnection = new JMenuItem("New");
        menuBar.add(menuConnections);
        menuConnections.add(newConnection);
        setJMenuBar(menuBar);

        // Adicionando ActionListener para o JMenuItem "New"
        newConnection.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Abrindo a janela ConnNewView como um diálogo modal
                ConnNewView connNewView = new ConnNewView(MainView.this);
                connNewView.setVisible(true);
            }
        });


        // Criando o painel principal com BorderLayout
        JPanel mainPanel = new JPanel(new BorderLayout());

        // Adicionando um label com "Hello World"
        JLabel helloLabel = new JLabel("Hello World", SwingConstants.CENTER);
        mainPanel.add(helloLabel, BorderLayout.CENTER);

        // Adicionando o painel principal à janela
        add(mainPanel);
    }

    public static void main(String[] args) {
        // Executa a aplicação
        SwingUtilities.invokeLater(() -> {
            MainView mainView = new MainView();
            mainView.setVisible(true);
        });
    }
}
