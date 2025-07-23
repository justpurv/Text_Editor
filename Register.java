
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.security.*;
import java.util.Arrays;
import java.util.StringTokenizer;
import javax.swing.*;
import javax.swing.border.EmptyBorder;

public class Register extends JPanel implements ActionListener {
    JTextField usernameField = new JTextField();
    JPasswordField passField = new JPasswordField();
    JPasswordField confirmField = new JPasswordField();
    JButton registerBtn = new JButton("Register");
    JButton backBtn = new JButton("Back");

    public Register() {
        setLayout(new GridBagLayout());
        JPanel form = new JPanel(new GridBagLayout());
        Color bg_color = new Color(40, 44, 52); // Dark gray
        Color fg_color = new Color(220, 220, 220); // Light gray
        Color accent_color = new Color(97, 218, 251); // Light blue
        Font label_font = new Font("Arial", Font.BOLD, 14);
        Font text_font = new Font("Arial", Font.PLAIN, 14);


        form.add(usernameField);
        form.add(passField);
        form.add(confirmField);
        form.add(registerBtn);
        form.add(backBtn); 

        form.setBackground(bg_color);
        form.setBorder(new EmptyBorder(20, 20, 20, 20));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Username
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.anchor = GridBagConstraints.LINE_END;
        JLabel user_label = new JLabel("Username:");
        user_label.setFont(label_font);
        user_label.setForeground(fg_color);
        form.add(user_label, gbc);

        gbc.gridx = 1;
        gbc.anchor = GridBagConstraints.LINE_START;
        usernameField .setFont(text_font);
        form.add(usernameField, gbc);

        // Password
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.anchor = GridBagConstraints.LINE_END;
        JLabel pass_label = new JLabel("Password:");
        pass_label.setFont(label_font);
        pass_label.setForeground(fg_color);
        form.add(pass_label, gbc);

        gbc.gridx = 1;
        gbc.anchor = GridBagConstraints.LINE_START;
        passField.setFont(text_font);
        form.add(passField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.anchor = GridBagConstraints.LINE_END;
        JLabel pass_label_confirm = new JLabel("Confirm Password:");
        pass_label_confirm.setFont(label_font);
        pass_label_confirm.setForeground(fg_color);
        form.add(pass_label_confirm, gbc);

        gbc.gridx = 1;
        gbc.anchor = GridBagConstraints.LINE_START;
        confirmField.setFont(text_font);
        form.add(confirmField, gbc);


        // Buttons
        gbc.gridy = 4;
        gbc.gridx = 0;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        JPanel button_panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 0));
        button_panel.setBackground(bg_color);
        style_button(registerBtn, accent_color);
        style_button(backBtn, accent_color);
        button_panel.add(registerBtn);
        button_panel.add(backBtn);
        form.add(button_panel, gbc);


        registerBtn.addActionListener(this);
        backBtn.addActionListener(this);
        add(form);
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
        if (e.getSource() == registerBtn) {
            String username = usernameField.getText().trim();
            String pass1 = new String(passField.getPassword());
            String pass2 = new String(confirmField.getPassword());

            if (username.isEmpty() || pass1.isEmpty() || pass2.isEmpty()) {
                JOptionPane.showMessageDialog(this, "All fields are required.");
                return;
            }

            if (!Arrays.equals(passField.getPassword(), confirmField.getPassword())) {
                JOptionPane.showMessageDialog(this, "Passwords do not match.");
                return;
            }

            try {
                // Check if user exists
                BufferedReader input = new BufferedReader(new FileReader("passwords.txt"));
                String line;
                while ((line = input.readLine()) != null) {
                    StringTokenizer st = new StringTokenizer(line);
                    if (username.equals(st.nextToken())) {
                        input.close();
                        JOptionPane.showMessageDialog(this, "User already exists.");
                        return;
                    }
                }
                input.close();

                // Hash password
                MessageDigest md = MessageDigest.getInstance("SHA-256");
                md.update(pass1.getBytes());
                byte[] hashedBytes = md.digest();
                StringBuilder sb = new StringBuilder();
                for (byte b : hashedBytes) {
                    sb.append(String.format("%02x", b));
                }

                // Save to file
                BufferedWriter output = new BufferedWriter(new FileWriter("passwords.txt", true));
                output.write(username + " " + sb.toString() + "\n");
                output.close();

                JOptionPane.showMessageDialog(this, "Registered successfully!");
                goToLogin();

            } catch (IOException | NoSuchAlgorithmException ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage());
            }
        }

        if (e.getSource() == backBtn) {
            goToLogin();
        }
    }

    
private void goToLogin() {
    Component c = this;
    while (c.getParent() != null) {
        c = c.getParent();
        if (c instanceof Container) {
            LayoutManager layout = ((Container) c).getLayout();
            if (layout instanceof CardLayout) {
                ((CardLayout) layout).show((Container) c, "login");
                break;
            }
        }
    }
}

}

