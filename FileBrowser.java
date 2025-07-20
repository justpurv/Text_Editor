
import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.io.IOException;

import javax.swing.*;
import javax.swing.JPanel;

public class FileBrowser extends JPanel implements ActionListener{
    JLabel file_list = new JLabel("Files : ");
    JButton new_file = new JButton("New");
    JButton open_file = new JButton("Open");
    JTextField newFile = new JTextField(10);
    ButtonGroup bg;
    File directory;

    public FileBrowser(String dir){
        directory = new File(dir);
        directory.mkdir();
        JPanel list = new JPanel(new GridLayout(directory.listFiles().length+3, 1));
        list.add(file_list);
        bg = new ButtonGroup();
        for(File file : directory.listFiles()){
            JPanel fileRow = new JPanel(new BorderLayout());
            JRadioButton radio = new JRadioButton(file.getName());
            radio.setActionCommand(file.getName());
            bg.add(radio);
            fileRow.add(radio, BorderLayout.CENTER);
            JButton deleteBtn = new JButton("Delete");
            deleteBtn.setBackground(new Color(180, 50, 50));
            deleteBtn.setForeground(Color.WHITE);
            deleteBtn.setFocusPainted(false);
            deleteBtn.setBorderPainted(false);
            deleteBtn.setOpaque(true);
            // Add action listener for delete
            deleteBtn.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    int confirm = JOptionPane.showConfirmDialog(FileBrowser.this, "Are you sure you want to delete '" + file.getName() + "'?", "Confirm Delete", JOptionPane.YES_NO_OPTION);
                    if (confirm == JOptionPane.YES_OPTION) {
                        if (file.delete()) {
                            JOptionPane.showMessageDialog(FileBrowser.this, "File deleted successfully.");
                            // Refresh the file browser
                            Login login = (Login) getParent();
                            login.add(new FileBrowser(directory.getAbsolutePath()), "fb");
                            login.cl.show(login, "fb");
                        } else {
                            JOptionPane.showMessageDialog(FileBrowser.this, "Failed to delete file.");
                        }
                    }
                }
            });
            fileRow.add(deleteBtn, BorderLayout.EAST);
            list.add(fileRow);
        }

        JPanel newPanel = new JPanel();
        newPanel.add(newFile);
        newPanel.add(new_file);
        list.add(open_file);
        list.add(newPanel);
        new_file.addActionListener(this);
        open_file.addActionListener(this);
        add(list);
    }

    @Override
    public void actionPerformed(ActionEvent e){
        Login login = (Login) getParent();
        if(e.getSource() == open_file){
            

            login.add(new Editor(new File(directory, bg.getSelection().getActionCommand()).getAbsolutePath()), "editor");

            login.cl.show(login, "editor");
        }

        if (e.getSource() == new_file) {
            String filePath = directory.getAbsolutePath() + File.separator + newFile.getText() + ".txt";
            File f = new File(filePath);

            if (newFile.getText().length() > 0 && !f.exists()) {
                try {
                    f.createNewFile(); // ✅ Create the file
                    login.add(new Editor(filePath), "editor");
                    login.cl.show(login, "editor");
                } catch (IOException ex1) {
                    ex1.printStackTrace();
                    JOptionPane.showMessageDialog(this, "Error creating file.");
                }
            } else {
                JOptionPane.showMessageDialog(this, "File already exists or name is empty.");
            }
        }

    } 
}
