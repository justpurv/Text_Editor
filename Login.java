import java.awt.*;
import java.awt.event.*;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import javax.swing.*;
import javax.swing.border.EmptyBorder;

public class Login extends JPanel implements ActionListener {
    JTextField username_field = new JTextField(20);
    JPasswordField password_field = new JPasswordField(20);
    JButton login_button = new JButton("Login");
    JButton register_button = new JButton("Register");
    public CardLayout cl;

    public Login() {
        setLayout(new CardLayout());
        cl = (CardLayout) getLayout();

        // UI Colors and Fonts 
        Color bg_color = new Color(40, 44, 52); // Dark gray
        Color fg_color = new Color(220, 220, 220); // Light gray
        Color accent_color = new Color(97, 218, 251); // Light blue
        Font label_font = new Font("Arial", Font.BOLD, 14);
        Font text_font = new Font("Arial", Font.PLAIN, 14);

        // Login Panel 
        JPanel login_panel = new JPanel(new GridBagLayout());
        login_panel.setBackground(bg_color);
        login_panel.setBorder(new EmptyBorder(20, 20, 20, 20));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Title
        JLabel title_label = new JLabel("Welcome Back");
        title_label.setFont(new Font("Arial", Font.BOLD, 24));
        title_label.setForeground(fg_color);
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        login_panel.add(title_label, gbc);

        // Username
        gbc.gridy = 1;
        gbc.gridwidth = 1;
        gbc.anchor = GridBagConstraints.LINE_END;
        JLabel user_label = new JLabel("Username:");
        user_label.setFont(label_font);
        user_label.setForeground(fg_color);
        login_panel.add(user_label, gbc);

        gbc.gridx = 1;
        gbc.anchor = GridBagConstraints.LINE_START;
        username_field.setFont(text_font);
        login_panel.add(username_field, gbc);

        // Password
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.anchor = GridBagConstraints.LINE_END;
        JLabel pass_label = new JLabel("Password:");
        pass_label.setFont(label_font);
        pass_label.setForeground(fg_color);
        login_panel.add(pass_label, gbc);

        gbc.gridx = 1;
        gbc.anchor = GridBagConstraints.LINE_START;
        password_field.setFont(text_font);
        login_panel.add(password_field, gbc);

        // Buttons
        gbc.gridy = 3;
        gbc.gridx = 0;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        JPanel button_panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 0));
        button_panel.setBackground(bg_color);
        style_button(login_button, accent_color);
        style_button(register_button, accent_color);
        button_panel.add(login_button);
        button_panel.add(register_button);
        login_panel.add(button_panel, gbc);

        add(login_panel, "login");
        add(new Register(), "register");

        login_button.addActionListener(this);
        register_button.addActionListener(this);
    }

    private void style_button(JButton button, Color accent_color) {
        button.setFont(new Font("Arial", Font.BOLD, 14));
        button.setBackground(accent_color);
        button.setForeground(Color.BLACK);
        button.setFocusPainted(false);
        button.setBorder(new EmptyBorder(10, 20, 10, 20));
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == register_button) {
            cl.show(this, "register");
        } else if (e.getSource() == login_button) {
            String username = username_field.getText();
            String password = new String(password_field.getPassword());

            if (authenticate(username, password)) {
                JOptionPane.showMessageDialog(this, "Have A Good Text Editing!");
                add(new FileBrowser(username), "fb");
                cl.show(this, "fb");
            } else {
                JOptionPane.showMessageDialog(this, "Invalid username or password.", "Login Failed", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private boolean authenticate(String username, String password) {
        try (BufferedReader reader = new BufferedReader(new FileReader("passwords.txt"))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(" ");
                if (parts.length == 2 && parts[0].equals(username)) {
                    String stored_hash = parts[1];
                    return verify_password(password, stored_hash);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

    private boolean verify_password(String password, String stored_hash) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            md.update(password.getBytes());
            byte[] hashed_bytes = md.digest();
            StringBuilder sb = new StringBuilder();
            for (byte b : hashed_bytes) {
                sb.append(String.format("%02x", b));
            }
            return sb.toString().equals(stored_hash);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return false;
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("Text Editor");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setSize(400, 300);
            frame.setLocationRelativeTo(null);
            frame.add(new Login());
            frame.setVisible(true);
        });
    }
}

