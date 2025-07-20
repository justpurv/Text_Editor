
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.security.*;
import java.util.Arrays;
import java.util.StringTokenizer;
import javax.swing.*;

public class Register extends JPanel implements ActionListener {
    JLabel userLabel = new JLabel("Username:");
    JTextField usernameField = new JTextField();
    JLabel passLabel = new JLabel("Password:");
    JPasswordField passField = new JPasswordField();
    JLabel confirmLabel = new JLabel("Confirm Password:");
    JPasswordField confirmField = new JPasswordField();
    JButton registerBtn = new JButton("Register");
    JButton backBtn = new JButton("Back");

    public Register() {
        JPanel form = new JPanel(new GridLayout(4, 2));
        form.add(userLabel);
        form.add(usernameField);
        form.add(passLabel);
        form.add(passField);
        form.add(confirmLabel);
        form.add(confirmField);
        form.add(registerBtn);
        form.add(backBtn);

        registerBtn.addActionListener(this);
        backBtn.addActionListener(this);
        add(form);
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

