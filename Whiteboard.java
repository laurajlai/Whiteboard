package whiteboard;

/*import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JTextPane;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import java.awt.Color;
import java.awt.Component; */

import javax.swing.*;
import javax.swing.text.*;
import javax.swing.filechooser.FileSystemView;
import javax.swing.text.rtf.RTFEditorKit;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import java.io.*;

import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;

public final class Whiteboard extends JFrame implements ActionListener
{
    private static JTextPane area;
    private static JFrame frame;
    private static int return_value = 0;
    private JMenuItem dark_mode;

    // constructor
    public Whiteboard()
    {
        run();
    }

    public void run()
    {
        try 
        {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } 
        catch (ClassNotFoundException | InstantiationException | 
        IllegalAccessException | UnsupportedLookAndFeelException ex) 
        {
            Logger.getLogger(Whiteboard.class.getName()).log(Level.SEVERE, null, ex);
        }

        // main menu
        JMenuBar main_menu = new JMenuBar();

        // create the file menu with the following functions: new, open, save, quit
        JMenu menu_file = new JMenu("File");
        JMenuItem menuitem_new = new JMenuItem("New");
        JMenuItem menuitem_open = new JMenuItem("Open");
        JMenuItem menuitem_save = new JMenuItem("Save");
        JMenuItem menuitem_quit = new JMenuItem("Quit");

        // add these items to the File menu
        menu_file.add(menuitem_new);
        menu_file.add(menuitem_open);
        menu_file.add(menuitem_save);
        menu_file.add(menuitem_quit);

        menuitem_new.addActionListener(this);
        menuitem_open.addActionListener(this);
        menuitem_save.addActionListener(this);
        menuitem_quit.addActionListener(this);

        // create a new menu, Style, with the following functions: dark mode and color
        // changer
        JMenu menu_style = new JMenu("Style");
        dark_mode = new JMenuItem("Dark Mode");
        JMenuItem color_changer = new JMenuItem("Change font color");

        // add to the Style menu
        menu_style.add(dark_mode);
        menu_style.add(color_changer);

        dark_mode.addActionListener(this);
        color_changer.addActionListener(this);

        // add menus to main menu
        main_menu.add(menu_file);
        main_menu.add(menu_style);

        frame = new JFrame("Whiteboard");
        area = new JTextPane();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.add(new JScrollPane(area));
        frame.add(area);
        frame.setJMenuBar(main_menu);  // Add the menu bar to the frame
        frame.setSize(640, 480);
        frame.setVisible(true);
        frame.repaint();
    }

    @Override
    public void actionPerformed(ActionEvent e) 
    {
        String ingest = null;
        JFileChooser jfc = 
        new JFileChooser(FileSystemView.getFileSystemView().getHomeDirectory());
        jfc.setDialogTitle("Choose destination.");
        jfc.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);

        String ae = e.getActionCommand();
        File f = null;

        // OPEN
        if (ae.equals("Open")) 
        {
            return_value = jfc.showOpenDialog(null);
            if (return_value == JFileChooser.APPROVE_OPTION) 
            {
                f = new File(jfc.getSelectedFile().getAbsolutePath());
                try (FileInputStream fis = new FileInputStream(f)) 
                {
                    String filename = f.getName().toLowerCase();
                    area.setText("");
                    if (filename.endsWith(".rtf")) 
                    {
                        System.out.println("Opening as RTF");
                        RTFEditorKit rtf = new RTFEditorKit();
                        area.setEditorKit(rtf);
                        area.read(fis, null);
                    } 
                    else 
                    {
                        System.out.println("Opening as plaintext");
                        StyledEditorKit styledKit = new StyledEditorKit();
                        area.setEditorKit(styledKit);
                        area.read(new InputStreamReader(fis), null);
                    }
                } 
                catch (IOException ex) 
                {
                    JOptionPane.showMessageDialog(null, "Error.");
                }
            }
        }
        // SAVE
        else if (ae.equals("Save")) 
        {
            return_value = jfc.showSaveDialog(null);
            if (return_value == JFileChooser.APPROVE_OPTION)
            {
                f = new File(jfc.getSelectedFile().getAbsolutePath());
                try (FileOutputStream fos = new FileOutputStream(f)) 
                {
                    if (!f.getName().toLowerCase().endsWith(".rtf")) 
                    {
                        f = new File(f.getAbsolutePath() + ".rtf");
                    }
                    RTFEditorKit rtf = new RTFEditorKit();
                    rtf.write(fos, area.getDocument(), 0, area.getDocument().getLength());
                } 
                catch (IOException | BadLocationException ex) 
                {
                    JOptionPane.showMessageDialog(null, "Error.");
                }
            }
        }
        // NEW
        else if (ae.equals("New")) 
        {
            area.setText("");
        }
        // QUIT 
        else if (ae.equals("Quit")) 
        { 
            System.exit(0); 
        }
        // DARK MODE
        else if (ae.equals("Dark Mode")) 
        {
            frame.getContentPane().setBackground(Color.BLACK);
            area.setBackground(Color.BLACK);
            area.setForeground(Color.WHITE);
            dark_mode.setText("Light Mode");
            dark_mode.setActionCommand("Light Mode");
            frame.repaint();
        }
        // LIGHT MODE
        else if (ae.equals("Light Mode")) 
        {
            frame.getContentPane().setBackground(Color.WHITE);
            area.setBackground(Color.WHITE);
            area.setForeground(Color.BLACK);
            dark_mode.setText("Dark Mode");
            dark_mode.setActionCommand("Dark Mode");
            frame.repaint();
        }
        else if (ae.equals("Change font color")) 
        {
            Color newColor = 
            JColorChooser.showDialog(null, "Choose a color", Color.BLACK);
            if (newColor != null) 
            {
                StyledDocument doc = area.getStyledDocument();
                int selectionStart = area.getSelectionStart();
                int selectionEnd = area.getSelectionEnd();

                if (selectionStart != selectionEnd) 
                { 
                    SimpleAttributeSet attributes = new SimpleAttributeSet();
                    StyleConstants.setForeground(attributes, newColor);
                    doc.setCharacterAttributes(selectionStart, selectionEnd - selectionStart, attributes, false);
                } 
                else 
                { 
                    SimpleAttributeSet attributes = new SimpleAttributeSet();
                    StyleConstants.setForeground(attributes, newColor);
                    area.setCharacterAttributes(attributes, false);
                }
            }
        }
    }
}