
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

        // --- Apply dark theme to login form and its components ---
        java.awt.Color darkBg = new java.awt.Color(35, 35, 35); // #232323
        java.awt.Color lightFg = new java.awt.Color(248, 248, 242); // #f8f8f2
        java.awt.Color accent = new java.awt.Color(60, 60, 60); // #3c3c3c
        java.awt.Font monoFont = new java.awt.Font("Monospaced", java.awt.Font.PLAIN, 16);

        loginForm.setBackground(darkBg);
        panel1.setBackground(darkBg);
        setBackground(darkBg);

        userLabel.setForeground(lightFg);
        passLabel.setForeground(lightFg);
        userLabel.setFont(monoFont);
        passLabel.setFont(monoFont);

        username.setBackground(darkBg);
        username.setForeground(lightFg);
        username.setCaretColor(lightFg);
        username.setFont(monoFont);
        password.setBackground(darkBg);
        password.setForeground(lightFg);
        password.setCaretColor(lightFg);
        password.setFont(monoFont);

        JButton[] buttons = {loginBtn, registerBtn};
        for (JButton btn : buttons) {
            btn.setBackground(accent);
            btn.setForeground(lightFg);
            btn.setFocusPainted(false);
            btn.setBorderPainted(false);
            btn.setOpaque(true);
            btn.setFont(monoFont);
        }

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
                add(new FileBrowser(username.getText()), "fb");
                cl.show(this, "fb");
            } else {
                JOptionPane.showMessageDialog(this, "Invalid username or password.");
            }

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}
    public static void main(String[] args) {
        // --- Set global minimal dark theme for all Swing components ---
        Color darkBg = new Color(35, 35, 35);
        Color lightFg = new Color(248, 248, 242);
        Color accent = new Color(60, 60, 60);
        Font monoFont = new Font("Monospaced", Font.PLAIN, 16);

        UIManager.put("Panel.background", darkBg);
        UIManager.put("OptionPane.background", darkBg);
        UIManager.put("OptionPane.messageForeground", lightFg);
        UIManager.put("Button.background", accent);
        UIManager.put("Button.foreground", lightFg);
        UIManager.put("Button.font", monoFont);
        UIManager.put("Label.foreground", lightFg);
        UIManager.put("Label.background", darkBg);
        UIManager.put("TextField.background", darkBg);
        UIManager.put("TextField.foreground", lightFg);
        UIManager.put("TextField.caretForeground", lightFg);
        UIManager.put("TextField.font", monoFont);
        UIManager.put("PasswordField.background", darkBg);
        UIManager.put("PasswordField.foreground", lightFg);
        UIManager.put("PasswordField.caretForeground", lightFg);
        UIManager.put("PasswordField.font", monoFont);
        UIManager.put("TextArea.background", darkBg);
        UIManager.put("TextArea.foreground", lightFg);
        UIManager.put("TextArea.caretForeground", lightFg);
        UIManager.put("TextArea.font", monoFont);
        UIManager.put("ScrollPane.background", darkBg);
        UIManager.put("ScrollPane.foreground", lightFg);

        // --- Launch the main frame as before ---
        JFrame frame = new JFrame("Login/Register");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(500, 300);
        frame.setLocationRelativeTo(null);
        frame.setFont(monoFont);
        Login loginPanel = new Login();
        frame.add(loginPanel);
        frame.setVisible(true);
    }
}

