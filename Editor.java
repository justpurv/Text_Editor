
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.event.UndoableEditEvent;
import javax.swing.event.UndoableEditListener;
import javax.swing.undo.UndoManager;

public class Editor extends JPanel implements ActionListener {

    private File file;
    private JTextArea text_area = new JTextArea();
    private JButton save_button = new JButton("Save");
    private JButton save_and_close_button = new JButton("Save & Close");
    private JButton back_button = new JButton("Back");
    private JButton undo_button = new JButton("Undo");
    private JButton redo_button = new JButton("Redo");

    private UndoManager undo_manager = new UndoManager();
    private UndoableEditListener undo_listener;

    public Editor(String filePath) {
        file = new File(filePath);
        setup_ui();
        setup_undo_redo();
        load_file();
    }

    private void setup_ui() {
        Color bg_color = new Color(40, 44, 52);
        Color fg_color = new Color(220, 220, 220);
        Color accent_color = new Color(97, 218, 251);
        Font text_font = new Font("Monospaced", Font.PLAIN, 16);

        setLayout(new BorderLayout(10, 10));
        setBackground(bg_color);
        setBorder(new EmptyBorder(15, 15, 15, 15));

        // Title
        JLabel title_label = new JLabel(file.getName());
        title_label.setFont(new Font("Arial", Font.BOLD, 24));
        title_label.setForeground(fg_color);
        title_label.setHorizontalAlignment(SwingConstants.CENTER);
        add(title_label, BorderLayout.NORTH);

        // Text area
        text_area.setBackground(bg_color);
        text_area.setForeground(fg_color);
        text_area.setCaretColor(accent_color);
        text_area.setFont(text_font);
        text_area.setLineWrap(true);
        text_area.setWrapStyleWord(true);
        text_area.setBorder(new EmptyBorder(10, 10, 10, 10));

        JScrollPane scroll_pane = new JScrollPane(text_area);
        scroll_pane.setBorder(BorderFactory.createLineBorder(accent_color));
        add(scroll_pane, BorderLayout.CENTER);

        // Button Panel
        JPanel button_panel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        button_panel.setBackground(bg_color);
        style_button(undo_button, accent_color);
        style_button(redo_button, accent_color);
        style_button(save_button, accent_color);
        style_button(save_and_close_button, accent_color);
        style_button(back_button, new Color(255, 100, 100));

        button_panel.add(undo_button);
        button_panel.add(redo_button);
        button_panel.add(save_button);
        button_panel.add(save_and_close_button);
        button_panel.add(back_button);

        add(button_panel, BorderLayout.SOUTH);

        // Button listeners
        save_button.addActionListener(this);
        save_and_close_button.addActionListener(this);
        back_button.addActionListener(this);

        undo_button.addActionListener(e -> {
            if (undo_manager.canUndo())
                undo_manager.undo();
        });

        redo_button.addActionListener(e -> {
            if (undo_manager.canRedo())
                undo_manager.redo();
        });
    }

    private void setup_undo_redo() {
        // Store undo listener to detach/attach later
        undo_listener = new UndoableEditListener() {
            @Override
            public void undoableEditHappened(UndoableEditEvent e) {
                undo_manager.addEdit(e.getEdit());
            }
        };

        text_area.getDocument().addUndoableEditListener(undo_listener);

        // Keyboard shortcuts
        text_area.getInputMap(JComponent.WHEN_FOCUSED).put(
                KeyStroke.getKeyStroke(KeyEvent.VK_Z, InputEvent.CTRL_DOWN_MASK), "Undo");
        text_area.getActionMap().put("Undo", new AbstractAction() {
            public void actionPerformed(ActionEvent e) {
                if (undo_manager.canUndo())
                    undo_manager.undo();
            }
        });

        text_area.getInputMap(JComponent.WHEN_FOCUSED).put(
                KeyStroke.getKeyStroke(KeyEvent.VK_Y, InputEvent.CTRL_DOWN_MASK), "Redo");
        text_area.getActionMap().put("Redo", new AbstractAction() {
            public void actionPerformed(ActionEvent e) {
                if (undo_manager.canRedo())
                    undo_manager.redo();
            }
        });
    }

    private void style_button(JButton button, Color accent_color) {
        button.setFont(new Font("Arial", Font.BOLD, 14));
        button.setForeground(Color.BLACK);
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setContentAreaFilled(false);
        button.setOpaque(false);

        button.setBackground(accent_color);

        // Override the button painting
        button.setUI(new javax.swing.plaf.basic.BasicButtonUI() {
            @Override
            public void paint(Graphics g, JComponent c) {
                JButton b = (JButton) c;
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(b.getBackground());
                g2.fillRoundRect(0, 0, b.getWidth(), b.getHeight(), 15, 15);
                super.paint(g, c);
                g2.dispose();
            }
        });

        button.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                button.setBackground(accent_color.darker());
            }

            @Override
            public void mouseExited(java.awt.event.MouseEvent evt) {
                button.setBackground(accent_color);
            }
        });
    }

    private void load_file() {
        if (file.exists()) {
            try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
                text_area.getDocument().removeUndoableEditListener(undo_listener); // Temporarily detach
                text_area.read(reader, null);
                text_area.getDocument().addUndoableEditListener(undo_listener); // Reattach
                undo_manager.discardAllEdits(); // Clean slate for undo history
            } catch (IOException e) {
                JOptionPane.showMessageDialog(this,
                        "Error loading file: " + e.getMessage(),
                        "File Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void save_file() {
        try (FileWriter writer = new FileWriter(file)) {
            text_area.write(writer);
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this,
                    "Error saving file: " + e.getMessage(),
                    "File Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        Object source = e.getSource();
        if (source == save_button) {
            save_file();
        } else if (source == save_and_close_button) {
            save_file();
            go_back_to_browser();
        } else if (source == back_button) {
            go_back_to_browser();
        }
    }

    private void go_back_to_browser() {
        Container parent = getParent();
        while (parent != null && !(parent instanceof Login)) {
            parent = parent.getParent();
        }
        if (parent instanceof Login) {
            Login login = (Login) parent;
            login.cl.show(login, "fb");
        }
    }
}
