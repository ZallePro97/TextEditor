package com.texteditor;

import com.EditAction;
import com.UndoManager;
import com.plugins.BigLetter;
import com.plugins.Plugin;
import com.plugins.PluginFactory;
import com.plugins.StatisticsPlugin;
import com.texteditor.clipboard_observer.ClipboardObserver;
import com.texteditor.cursor_observer.CursorObserver;
import com.texteditor.text_observer.TextObserver;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;
import java.util.function.Consumer;

public class TextEditor extends JComponent {

    public static final int ROW_START = 5;
    public static final int COLUMN_START = 15;
    public final int FONT_SIZE = 13;            // height ,, just to know, not gonna use
    public static final int NEW_LINE_SPACING = 15;
    public static final int CURSOR_START_X = 2;

    public static boolean INITIAL_CURSOR_STATE = true;

    public static boolean SHIFT_PRESSED = false;
    public static boolean POCETAK_OZNACAVANJA = false;

    public static boolean SELECTION = false;

    public static boolean CRTL_PRESSED = false;

    // za pamcenje stringova koji su oznaceni u sredini, tj kad imam vise od 2 oznace linije, trebam pamtit duljine stringova izmedu
    // bio je bug da se osjencani dio prosiri
    public HashMap<Integer, String> selectedStrings = new HashMap<>();

    public ClipboardStack clipboardStack = new ClipboardStack();

    private TextEditorModel textEditorModel;

    public TextEditor(TextEditorModel textEditorModel) {
        setFocusable(true);
        requestFocus();
        this.textEditorModel = textEditorModel;


        textEditorModel.attachCursorObserver(new CursorObserver() {
            @Override
            public void updateCursorLocation(Location loc) {
                repaint();
            }
        });

        textEditorModel.attachTextObserver(new TextObserver() {
            @Override
            public void updateText() {
                repaint();
            }
        });

        addKeyListener(new KeyListener() {
            @Override
            public void keyTyped(KeyEvent e) {

            }

            @Override
            public void keyPressed(KeyEvent e) {
                switch (e.getKeyCode()) {
                    case KeyEvent.VK_RIGHT:

                        if (!SHIFT_PRESSED) {
                            SELECTION = false;
                            textEditorModel.moveCursorRight();
                            break;
                        }

                        setupSelection();
                        textEditorModel.moveCursorRight();
                        break;
                    case KeyEvent.VK_LEFT:
                        setupSelection();
                        textEditorModel.moveCursorLeft();
                        break;
                    case KeyEvent.VK_UP:

                        if (!SHIFT_PRESSED) {
                            SELECTION = false;
                            textEditorModel.moveCursorUp();
                            break;
                        }

                        setupSelection();
                        textEditorModel.moveCursorUp();
                        break;
                    case KeyEvent.VK_DOWN:

                        if (!SHIFT_PRESSED) {
                            SELECTION = false;
                            textEditorModel.moveCursorDown();
                            break;
                        }

                        setupSelection();
                        textEditorModel.moveCursorDown();
                        break;
                    case KeyEvent.VK_BACK_SPACE:
                        System.out.println("Brisem");

                        if (SELECTION) {
                            textEditorModel.deleteRange(textEditorModel.getSelectionRange());
                            SELECTION = false;
                            break;
                        }

                        textEditorModel.deleteBefore();
                        break;
                    case KeyEvent.VK_ENTER:

                        if (SELECTION) {
                            SELECTION = false;
                            textEditorModel.deleteRange(textEditorModel.getSelectionRange());
                            textEditorModel.setSelectionRange(new LocationRange(new Location(0, 0), new Location(0, 0)));
                        }

                        textEditorModel.breakString();
                        break;
                    case KeyEvent.VK_SHIFT:
                        SHIFT_PRESSED = true;
                        POCETAK_OZNACAVANJA = true;
                        System.out.println("SHIFT pritisnut");
                        break;
                    case KeyEvent.VK_C:
                        if (e.isControlDown() && SELECTION) {
//                            System.out.println("DA HEHEE");
//                            System.out.println(textEditorModel.getSelectedText());
                            clipboardStack.push(textEditorModel.getSelectedText());
                            System.out.println("Stavljam na stog: " + clipboardStack.readFromTop());
                            break;
                        }
                    case KeyEvent.VK_X:
                        if (e.isControlDown() && SELECTION) {
                            clipboardStack.push(textEditorModel.getSelectedText());
                            textEditorModel.deleteRange(textEditorModel.getSelectionRange());
                            break;
                        }
                    case KeyEvent.VK_V:

                        if (e.isControlDown() && e.isShiftDown()) {
                            try {
                                System.out.println("PEJSTAM: " + clipboardStack.readFromTop());
                                textEditorModel.insert(clipboardStack.pop());
                                break;
                            } catch (EmptyStackException exc) {

                            }
                        }

                        if (e.isControlDown()) {

                            try {
                                System.out.println("PEJSTAM: " + clipboardStack.readFromTop());
                                textEditorModel.insert(clipboardStack.readFromTop());
                                break;
                            } catch (NullPointerException exc) {

                            }

                        }
                    case KeyEvent.VK_Z:

                        if (e.isControlDown()) {
                            System.out.println("UNDO");
                            textEditorModel.manager.undo();
                            break;
                        }
                    case KeyEvent.VK_Y:
                        if (e.isControlDown()) {
                            System.out.println("REDO");

                            if (!textEditorModel.manager.getRedoStack().isEmpty()) {
                                EditAction action = textEditorModel.manager.getRedoStack().pop();
                                action.execute_do();
                                textEditorModel.manager.getUndoStack().push(action);
                                textEditorModel.manager.notifyRedoItems();
                                textEditorModel.manager.notifyUndoItems();
                            }

                        }


                        default:
                            if (e.isControlDown()) {
                                break;
                            }

                            if (SELECTION) {
                                SELECTION = false;
                                textEditorModel.deleteRange(textEditorModel.getSelectionRange());
                                textEditorModel.setSelectionRange(new LocationRange(new Location(0, 0), new Location(0, 0)));
                            }

                            textEditorModel.insert(e.getKeyChar());
                            break;

                }
            }

            @Override
            public void keyReleased(KeyEvent e) {
                switch (e.getKeyCode()) {
                    case KeyEvent.VK_SHIFT:
                        SHIFT_PRESSED = false;

                        try {
                            if (textEditorModel.getSelectionRange().getLoc1().equals(textEditorModel.getSelectionRange().getLoc2())) {
                                SELECTION = false;
                                break;
                            }
                        } catch (NullPointerException exc) {
                            System.out.println("Nemam nista oznaceno");
                        }

                        textEditorModel.getSelectionRange().setLoc2(new Location(textEditorModel.getCursorLocation()));
                }
            }
        });

    }

    // provjera je li se oznacuje
    public void setupSelection() {
        if (SHIFT_PRESSED) {

            if (POCETAK_OZNACAVANJA) {
                SELECTION = true;
                textEditorModel.getSelectionRange().setLoc1(new Location(textEditorModel.getCursorLocation()));
                POCETAK_OZNACAVANJA = false;
            }

        }
    }

    public void paintComponent(Graphics g) {
        g.setColor(Color.BLACK);
        displaySelected(g);
        displayText(g);
        displayCursor(g);
    }

    public void displayText(Graphics g) {
        Iterator<String> iterator;
        int y = COLUMN_START;

        for (iterator = textEditorModel.allLines(); iterator.hasNext(); y += NEW_LINE_SPACING) {
            g.drawString(iterator.next(), ROW_START, y);
        }
    }

    @Override
    public void setFocusable(boolean focusable) {
        super.setFocusable(focusable);
    }

    public void displayCursor(Graphics g) {

        // when programm is started cursor is in upper left corner
        if (INITIAL_CURSOR_STATE) {
            g.drawString("|", CURSOR_START_X, COLUMN_START);
            INITIAL_CURSOR_STATE = false;
            return;
        }

        String line = textEditorModel.getLine(textEditorModel.getCursorLocation().getRow());
        String wantedLine = line.substring(0, textEditorModel.getCursorLocation().getColumn());

        System.out.println("Column: " + textEditorModel.getCursorLocation().getColumn());
        System.out.println(wantedLine);

        int x = CURSOR_START_X + g.getFontMetrics().stringWidth(wantedLine);
        int y = (textEditorModel.getCursorLocation().getRow() + 1) * NEW_LINE_SPACING;

        g.drawString("|", x, y);
    }

    public void displaySelected(Graphics g) {

        if (SELECTION) {
            g.setColor(Color.GRAY);

            // ovo je "obavezan" dio, uvijek se mora izvrsit
            String wantedLine = textEditorModel.getLines().get(textEditorModel.getSelectionRange().getLoc1().getRow());

            String beginSelected = wantedLine.substring(0, textEditorModel.getSelectionRange().getLoc1().getColumn());

            String endSelected = null;

            if (textEditorModel.getSelectionRange().getLoc1().getRow() == textEditorModel.getCursorLocation().getRow()) {
                endSelected = wantedLine.substring(textEditorModel.getSelectionRange().getLoc1().getColumn(), textEditorModel.getCursorLocation().getColumn());

            } else {
                endSelected = wantedLine.substring(textEditorModel.getSelectionRange().getLoc1().getColumn(), wantedLine.length());
            }

            // obavezno oznacavanje prvo oznacenog retka                                            // ok
            int x = 5 + g.getFontMetrics().stringWidth(beginSelected);
            int y = y = textEditorModel.getSelectionRange().getLoc1().getRow() * NEW_LINE_SPACING + 3;        // ok

            g.fillRect(x, y, g.getFontMetrics().stringWidth(endSelected), NEW_LINE_SPACING);


            // ako su oznacena 2 retka, tj jedan je oznacen i drugi je oznacen do nekog dijela
            // znaci taj drugi string je sigurno oznacen od pocetka
            if (textEditorModel.getSelectionRange().getLoc1().getRow() != textEditorModel.getCursorLocation().getRow()) {
                wantedLine = textEditorModel.getLines().get(textEditorModel.getCursorLocation().getRow());

                selectedStrings.put(textEditorModel.getCursorLocation().getRow(), wantedLine);

                String selected = wantedLine.substring(0, textEditorModel.getCursorLocation().getColumn());

                x = 5;
                y = textEditorModel.getCursorLocation().getRow() * NEW_LINE_SPACING + 3;

                g.fillRect(x, y, g.getFontMetrics().stringWidth(selected), NEW_LINE_SPACING);

            }

            // ako je oznaceno vise od 2 retka, znaci sve retke osim prvog i zadnjeg oznacenog trebam osjencat
            if (textEditorModel.getCursorLocation().getRow() - textEditorModel.getSelectionRange().getLoc1().getRow() > 1) {

                for (int i = textEditorModel.getSelectionRange().getLoc1().getRow() + 1; i < textEditorModel.getCursorLocation().getRow(); i++) {
                    wantedLine = textEditorModel.getLines().get(textEditorModel.getCursorLocation().getRow() - 1);

                    x = 5;
                    y = i * NEW_LINE_SPACING + 3;

                    try {
                        g.fillRect(x, y, g.getFontMetrics().stringWidth(selectedStrings.get(i)), NEW_LINE_SPACING);
                    } catch (IndexOutOfBoundsException exc) {

                    }

                }

            }


        }

        g.setColor(Color.BLACK);
    }

    public TextEditorModel getTextEditorModel() {
        return textEditorModel;
    }

    public void setTextEditorModel(TextEditorModel textEditorModel) {
        this.textEditorModel = textEditorModel;
    }





    public static void main(String[] args) {
        JFrame.setDefaultLookAndFeelDecorated(true);

        JFrame frame = new JFrame("Text Editor");

        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setBackground(Color.WHITE);
        frame.setSize(600, 600);

        TextEditorModel model = new TextEditorModel("Pasko\nproba\nsta ima\nkad je kraj?");

        TextEditor editor = new TextEditor(model);

        frame.add(editor, BorderLayout.CENTER);

        JMenuBar menuBar = new JMenuBar();

        frame.setJMenuBar(menuBar);

        JMenu fileMenu = new JMenu("File");
        JMenu editMenu = new JMenu("Edit");
        JMenu moveMenu = new JMenu("Move");
        JMenu pluginsMenu = new JMenu("Plugins");

        menuBar.add(fileMenu);
        menuBar.add(editMenu);
        menuBar.add(moveMenu);
        menuBar.add(pluginsMenu);

        JMenuItem openItem = new JMenuItem("Open");
        openItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JFileChooser fc = new JFileChooser();
                ArrayList<String> result = new ArrayList<>();
                fc.showOpenDialog(null);
                File file = fc.getSelectedFile();

                try {
                    Scanner input = new Scanner(file);

                    while (input.hasNextLine()) {
                        result.add(input.nextLine());
                    }
                    input.close();

                    model.setLines(result);
                    model.notifyTextObservers();

                } catch (FileNotFoundException exc) {

                }

            }
        });


        JMenuItem saveItem = new JMenuItem("Save");
        saveItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JFileChooser fc = new JFileChooser();
                fc.setCurrentDirectory(new File("/Users/zeljkohalle"));
                fc.setDialogTitle("Hello World");
                fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
                fc.showSaveDialog(null);
                File f = fc.getSelectedFile();
                try {
                    FileWriter fw = new FileWriter(f);

                    String result = "";
                    for (String s : model.getLines()) {
                        result += s + "\n";
                    }

                    fw.write(result);
                    fw.close();
                } catch (IOException exc) {

                }

            }
        });

        JMenuItem exitItem = new JMenuItem("Exit");
        exitItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.exit(0);
            }
        });

        fileMenu.add(openItem);
        fileMenu.add(saveItem);
        fileMenu.add(exitItem);

        JMenuItem undoItem = new JMenuItem("Undo");
        undoItem.setEnabled(false);

        model.manager.attachUndoItem(undoItem);

        undoItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                model.manager.undo();
            }
        });

        JMenuItem redoItem = new JMenuItem("Redo");
        redoItem.setEnabled(false);

        model.manager.attachRedoItem(redoItem);

        redoItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (!model.manager.getRedoStack().isEmpty()) {
                    EditAction action = model.manager.getRedoStack().pop();
                    action.execute_do();
                    model.manager.getUndoStack().push(action);
                }
            }
        });

        JMenuItem cutItem = new JMenuItem("Cut");
        cutItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (SELECTION) {
                    editor.clipboardStack.push(model.getSelectedText());
                    model.deleteRange(model.getSelectionRange());
                }
            }
        });

        JMenuItem copyItem = new JMenuItem("Copy");
        copyItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (SELECTION) {
                    editor.clipboardStack.push(model.getSelectedText());
                }
            }
        });

        JMenuItem pasteItem = new JMenuItem("Paste");
        pasteItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    System.out.println("PEJSTAM: " + editor.clipboardStack.readFromTop());
                    model.insert(editor.clipboardStack.pop());
                } catch (EmptyStackException exc) {

                }
            }
        });

        JMenuItem pasteAndTakeItem = new JMenuItem("Paste and Take");
        pasteAndTakeItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    System.out.println("PEJSTAM: " + editor.clipboardStack.readFromTop());
                    model.insert(editor.clipboardStack.pop());
                } catch (EmptyStackException exc) {

                }
            }
        });

        JMenuItem deleteSelectionItem = new JMenuItem("Delete selection");
        deleteSelectionItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (SELECTION) {
                    model.deleteRange(model.getSelectionRange());
                    SELECTION = false;
                }
            }
        });

        JMenuItem clearDocumentItem = new JMenuItem("Clear document");
        clearDocumentItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                ArrayList<String> newList = new ArrayList<>();
                newList.add("");
                model.setLines(newList);
                model.setCursorLocation(new Location(0, 0));
                model.notifyCursorObservers();
                model.notifyTextObservers();
            }
        });


        editMenu.add(undoItem);
        editMenu.add(redoItem);
        editMenu.add(cutItem);
        editMenu.add(copyItem);
        editMenu.add(pasteItem);
        editMenu.add(pasteAndTakeItem);
        editMenu.add(deleteSelectionItem);
        editMenu.add(clearDocumentItem);

        JMenuItem cursorToDocumentStartItem = new JMenuItem("Cursor to document start");
        cursorToDocumentStartItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                model.setCursorLocation(new Location(0, 0));
                model.notifyCursorObservers();
            }
        });

        JMenuItem cursorToDocumentEndItem = new JMenuItem("Cursor to document end");
        cursorToDocumentEndItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int endRow = model.getLines().size() - 1;
                int endColumn = model.getLine(endRow).length();

                model.setCursorLocation(new Location(endRow, endColumn));
                model.notifyCursorObservers();
            }
        });

        moveMenu.add(cursorToDocumentStartItem);
        moveMenu.add(cursorToDocumentEndItem);


        ServiceLoader<Plugin> plugins = PluginFactory.getPlugins();

        for (Plugin p : plugins) {
            JMenuItem plugin = new JMenuItem(p.getName());
            plugin.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    p.execute(model, model.manager, editor.clipboardStack);
                }
            });

            pluginsMenu.add(plugin);
        }

        JPanel panel = new JPanel();
        panel.setBackground(Color.WHITE);

        JLabel label = new JLabel("Cursor location: 0, 0, rows: " + model.getLines().size());

        model.attachCursorObserver(new CursorObserver() {
            @Override
            public void updateCursorLocation(Location loc) {
                label.setText("Cursor location: " + loc.toString() + ", rows: " + model.getLines().size());
            }
        });


        panel.add(label);

        frame.add(panel, BorderLayout.SOUTH);

        frame.setVisible(true);
    }

}
