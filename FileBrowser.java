import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.io.IOException;
import javax.swing.*;
import javax.swing.border.EmptyBorder;

public class FileBrowser extends JPanel implements ActionListener {
    JButton new_file_button = new JButton("New");
    JButton open_file_button = new JButton("Open");
    JButton delete_file_button = new JButton("Delete");
    JTextField new_file_textfield = new JTextField(15);
    JList<String> file_list;
    DefaultListModel<String> list_model;
    File directory;

    public FileBrowser(String dir) {
        directory = new File(dir);
        if (!directory.exists()) {
            directory.mkdir();
        }

        // --- UI Colors and Fonts ---
        Color bg_color = new Color(40, 44, 52);
        Color fg_color = new Color(220, 220, 220);
        Color accent_color = new Color(97, 218, 251);
        Font label_font = new Font("Arial", Font.BOLD, 16);
        Font text_font = new Font("Arial", Font.PLAIN, 14);

        // --- Main Panel ---
        setLayout(new BorderLayout(10, 10));
        setBackground(bg_color);
        setBorder(new EmptyBorder(15, 15, 15, 15));

        // --- Header ---
        JLabel title_label = new JLabel("File Browser");
        title_label.setFont(new Font("Arial", Font.BOLD, 24));
        title_label.setForeground(fg_color);
        title_label.setHorizontalAlignment(SwingConstants.CENTER);
        add(title_label, BorderLayout.NORTH);

        // --- File List ---
        list_model = new DefaultListModel<>();
        File[] files = directory.listFiles();
        if (files != null) {
            for (File file : files) {
                list_model.addElement(file.getName());
            }
        }
        file_list = new JList<>(list_model);
        file_list.setBackground(bg_color);
        file_list.setForeground(fg_color);
        file_list.setSelectionBackground(accent_color);
        file_list.setSelectionForeground(Color.BLACK);
        file_list.setFont(text_font);
        file_list.setCellRenderer(new FileListCellRenderer());
        JScrollPane scroll_pane = new JScrollPane(file_list);
        scroll_pane.setBorder(BorderFactory.createLineBorder(accent_color));
        add(scroll_pane, BorderLayout.CENTER);

        // --- Bottom Panel ---
        JPanel bottom_panel = new JPanel(new GridBagLayout());
        bottom_panel.setBackground(bg_color);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);

        // New File Input
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        new_file_textfield.setFont(text_font);
        bottom_panel.add(new_file_textfield, gbc);

        // New File Button
        gbc.gridx = 1;
        gbc.weightx = 0;
        style_button(new_file_button, accent_color);
        bottom_panel.add(new_file_button, gbc);

        // Action Buttons
        gbc.gridx = 2;
        gbc.weightx = 0;
        JPanel action_button_panel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        action_button_panel.setBackground(bg_color);
        style_button(open_file_button, accent_color);
        style_button(delete_file_button, new Color(255, 100, 100));
        action_button_panel.add(open_file_button);
        action_button_panel.add(delete_file_button);
        bottom_panel.add(action_button_panel, gbc);

        add(bottom_panel, BorderLayout.SOUTH);

        // --- Action Listeners ---
        open_file_button.addActionListener(this);
        new_file_button.addActionListener(this);
        delete_file_button.addActionListener(this);
    }

    private void style_button(JButton button, Color color) {
        button.setFont(new Font("Arial", Font.BOLD, 14));
        button.setBackground(color);
        button.setForeground(Color.BLACK);
        button.setFocusPainted(false);
        button.setBorder(new EmptyBorder(10, 20, 10, 20));
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        String selected_file = file_list.getSelectedValue();

        if (e.getSource() == open_file_button) {
            if (selected_file != null) {
                open_file(selected_file);
            } else {
                JOptionPane.showMessageDialog(this, "Please select a file to open.", "No File Selected", JOptionPane.WARNING_MESSAGE);
            }
        } else if (e.getSource() == new_file_button) {
            create_new_file();
        } else if (e.getSource() == delete_file_button) {
            if (selected_file != null) {
                delete_file(selected_file);
            } else {
                JOptionPane.showMessageDialog(this, "Please select a file to delete.", "No File Selected", JOptionPane.WARNING_MESSAGE);
            }
        }
    }

    private void open_file(String file_name) {
        Login login = (Login) getParent();
        String file_path = new File(directory, file_name).getAbsolutePath();
        login.add(new Editor(file_path), "editor");
        login.cl.show(login, "editor");
    }

    private void create_new_file() {
        String new_file_name = new_file_textfield.getText().trim();
        if (!new_file_name.isEmpty()) {
            if (!new_file_name.contains(".")) {
                new_file_name += ".txt";
            }
            
            File new_file = new File(directory, new_file_name);
            if (!new_file.exists()) {
                try {
                    if (new_file.createNewFile()) {
                        list_model.addElement(new_file.getName());
                        new_file_textfield.setText("");
                    }
                } catch (IOException ex) {
                    JOptionPane.showMessageDialog(this, "Error creating file: " + ex.getMessage(), "File Creation Error", JOptionPane.ERROR_MESSAGE);
                }
            } else {
                JOptionPane.showMessageDialog(this, "File already exists.", "File Exists", JOptionPane.WARNING_MESSAGE);
            }
        } else {
            JOptionPane.showMessageDialog(this, "Please enter a file name.", "Empty File Name", JOptionPane.WARNING_MESSAGE);
        }
    }

    private void delete_file(String file_name) {
        int confirm = JOptionPane.showConfirmDialog(this, "Are you sure you want to delete '" + file_name + "'?", "Confirm Delete", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            File file_to_delete = new File(directory, file_name);
            if (file_to_delete.delete()) {
                list_model.removeElement(file_name);
            } else {
                JOptionPane.showMessageDialog(this, "Failed to delete the file.", "Deletion Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    // Custom cell renderer to add padding to JList items
    private static class FileListCellRenderer extends DefaultListCellRenderer {
        @Override
        public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
            JLabel label = (JLabel) super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
            label.setBorder(new EmptyBorder(5, 10, 5, 10));
            return label;
        }
    }
}
