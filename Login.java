
import java.awt.*;
import java.awt.event.*;
import java.io.BufferedReader;
import java.io.FileReader;
import java.security.MessageDigest;

import javax.swing.*;

public class Login extends JPanel implements ActionListener {
    JLabel userLabel = new JLabel("Username:");
    JTextField username = new JTextField();
    JLabel passLabel = new JLabel("Password:");
    JPasswordField password = new JPasswordField();
    JButton loginBtn = new JButton("Login");
    JButton registerBtn = new JButton("Register");

    JPanel loginForm = new JPanel(new GridLayout(3, 2));
    JPanel panel1 = new JPanel();
    public CardLayout cl;

    public Login() {
        setLayout(new CardLayout());
        cl = (CardLayout) getLayout();

        loginForm.add(userLabel);
        loginForm.add(username);
        loginForm.add(passLabel);
        loginForm.add(password);
        loginForm.add(loginBtn);
        loginForm.add(registerBtn);

        panel1.add(loginForm);
        add(panel1, "login");

        loginBtn.addActionListener(this);
        registerBtn.addActionListener(this);

        // Add Register panel once
        add(new Register(), "register");
    }


    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == registerBtn) {
            add(new Register(), "register");
            cl.show(this, "register");
        }

        if (e.getSource() == loginBtn) {
            String userInput = username.getText();
            String passInput = new String(password.getPassword());

            try (BufferedReader reader = new BufferedReader(new FileReader("passwords.txt"))) {
                String line;
                boolean found = false;

                // Hash entered password
                MessageDigest md = MessageDigest.getInstance("SHA-256");
                md.update(passInput.getBytes());
                byte[] bytedata = md.digest();
                StringBuilder sb = new StringBuilder();
                for (byte b : bytedata) {
                    sb.append(String.format("%02x", b));
                }
                String hashedPass = sb.toString();

                // Compare with file
                while ((line = reader.readLine()) != null) {
                    String[] parts = line.split(" ");
                    if (parts.length == 2 && parts[0].equals(userInput) && parts[1].equals(hashedPass)) {
                        found = true;
                        break;
                    }
                }

                if (found) {
                // show success message
                JOptionPane.showMessageDialog(this, "You have logged in!");
            } else {
                JOptionPane.showMessageDialog(this, "Invalid username or password.");
            }

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}
    public static void main(String[] args) {
        JFrame frame = new JFrame("Login/Register");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(500, 300);
        frame.setLocationRelativeTo(null);
        frame.setFont(new Font("JetBrainsMono NF", Font.BOLD, 15));
        Login loginPanel = new Login();
        frame.add(loginPanel);
        frame.setVisible(true);
    }
}

