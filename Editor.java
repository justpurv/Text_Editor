import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.awt.BorderLayout;

import javax.swing.*;
import javax.swing.event.UndoableEditEvent;
import javax.swing.event.UndoableEditListener;
import javax.swing.undo.UndoManager;


public class Editor extends JPanel implements ActionListener{
    File file; 
    JButton save = new JButton("SAVE");
    JButton save_close = new JButton("SAVE & CLOSE");
    JButton deleteBtn = new JButton("DELETE");
    JButton undoBtn = new JButton("Undo");
    JButton redoBtn = new JButton("Redo");
    JTextArea text = new JTextArea(500,500);
    private Login findLoginParent() {
        java.awt.Container parent = this.getParent();
        while (parent != null && !(parent instanceof Login)) {
            parent = parent.getParent();
        }
        return (Login) parent;
    }
    public Editor(String s){
        file = new File(s);
        save.addActionListener(this);
        save_close.addActionListener(this);
        deleteBtn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                int confirm = JOptionPane.showConfirmDialog(Editor.this, "Are you sure you want to delete this file?", "Confirm Delete", JOptionPane.YES_NO_OPTION);
                if (confirm == JOptionPane.YES_OPTION) {
                    if (file.delete()) {
                        JOptionPane.showMessageDialog(Editor.this, "File deleted successfully.");
                        Login login = (Login)getParent();
                        login.cl.show(login, "fb");
                    } else {
                        JOptionPane.showMessageDialog(Editor.this, "Failed to delete file.");
                    }
                }
            }
        });
        UndoManager undoManager = new UndoManager();
        text.getDocument().addUndoableEditListener(undoManager);
        undoBtn.addActionListener(e -> {
            if (undoManager.canUndo()) undoManager.undo();
        });
        redoBtn.addActionListener(e -> {
            if (undoManager.canRedo()) undoManager.redo();
        });
        text.getInputMap().put(KeyStroke.getKeyStroke("control Z"), "Undo");
        text.getActionMap().put("Undo", new AbstractAction() {
            public void actionPerformed(ActionEvent e) {
                if (undoManager.canUndo()) undoManager.undo();
            }
        });
        text.getInputMap().put(KeyStroke.getKeyStroke("control Y"), "Redo");
        text.getActionMap().put("Redo", new AbstractAction() {
            public void actionPerformed(ActionEvent e) {
                if (undoManager.canRedo()) undoManager.redo();
            }
        });

        setLayout(new BorderLayout());
        JPanel buttonPanel = new JPanel();

        // --- Minimal Dark Theme for Buttons ---
        java.awt.Color darkBg = new java.awt.Color(35, 35, 35); // #232323
        java.awt.Color lightFg = new java.awt.Color(248, 248, 242); // #f8f8f2
        java.awt.Color accent = new java.awt.Color(60, 60, 60); // Slightly lighter for button hover

        // Style buttons for dark theme
        JButton[] buttons = {save, save_close};
        for (JButton btn : buttons) {
            btn.setBackground(accent); // dark background
            btn.setForeground(lightFg); // light text
            btn.setFocusPainted(false); // remove focus border
            btn.setBorderPainted(false); // flat look
            btn.setOpaque(true);
        }
        // Add deleteBtn to the button panel and style it
        deleteBtn.setBackground(new java.awt.Color(180, 50, 50));
        deleteBtn.setForeground(java.awt.Color.WHITE);
        deleteBtn.setFocusPainted(false);
        deleteBtn.setBorderPainted(false);
        deleteBtn.setOpaque(true);
        undoBtn.setPreferredSize(new java.awt.Dimension(90, 36));
        redoBtn.setPreferredSize(new java.awt.Dimension(90, 36));
        buttonPanel.setBackground(darkBg); // dark background for button panel
        buttonPanel.add(undoBtn);
        buttonPanel.add(redoBtn);
        buttonPanel.add(save);
        buttonPanel.add(save_close);
        buttonPanel.add(deleteBtn);
        add(buttonPanel, BorderLayout.NORTH);

        // --- Minimal Dark Theme for Text Area ---
        text.setLineWrap(true);
        text.setWrapStyleWord(true);
        text.setBackground(darkBg); // dark background
        text.setForeground(lightFg); // light text
        text.setCaretColor(lightFg); // light caret
        text.setFont(new java.awt.Font("Monospaced", java.awt.Font.PLAIN, 16)); // monospaced font
        text.setBorder(javax.swing.BorderFactory.createEmptyBorder(10, 10, 10, 10)); // minimal padding
        
        text.setWrapStyleWord(true);
        // --- Minimal Dark Theme for Scroll Pane ---
        JScrollPane scrollPane = new JScrollPane(text);
        scrollPane.setPreferredSize(new java.awt.Dimension(600, 400));
        scrollPane.setBackground(darkBg); // dark background for scroll pane
        scrollPane.setBorder(javax.swing.BorderFactory.createEmptyBorder()); // remove border
        add(scrollPane, BorderLayout.CENTER);

        // --- Set background for main panel ---
        setBackground(darkBg);

        if(file.exists()){
            try {
              BufferedReader input = new BufferedReader(new FileReader(file));
              String line = input.readLine();
              while(line != null){
                  text.append(line+"\n");
                  line = input.readLine();
              } 
            } catch (Exception e) {
                // TODO: handle exception
            }
        }
    }
    
    @Override
    public void actionPerformed(ActionEvent e){
       try {
        FileWriter output = new FileWriter(file);
        output.write(text.getText());
        output.close();
        if(e.getSource() == save_close){
            Login login = findLoginParent();
            login.cl.show(login, "fb");
        }
       } catch (IOException e1) {
        // TODO: handle exception
        e1.printStackTrace();
       } 
    }
}
