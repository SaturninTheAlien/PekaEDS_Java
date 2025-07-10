package pekase3;

import org.tinylog.Logger;

import pekaeds.data.PekaEDSVersion;
import pekaeds.ui.main.IPekaEdsApp;
import pekaeds.ui.settings.SettingsDialog;
import pekase3.dialogs.UnsavedChangesDialog;
import pekase3.panels.spriteeditpane.SpriteEditPane;
import pekase3.util.QuickSpriteTest;
import pk2.filesystem.PK2FileSystem;
import pk2.settings.Settings;
import pk2.sprite.PK2Sprite;
import pk2.sprite.io.SpriteIO;
import pk2.ui.SpriteFileChooser;
import pk2.util.GFXUtils;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

public class PekaSE3GUI extends JFrame implements ChangeListener, IPekaEdsApp {

    private SettingsDialog settingsDialog = null;
    private JTabbedPane tpSprite = new JTabbedPane(JTabbedPane.BOTTOM);    
    private JMenuBar menuBar;
    private JMenu mFile;
    private JMenuItem miFNew;
    private JMenuItem miFOpen;
    private JMenuItem miFSave;
    private JMenuItem miFSaveAs;
    private JMenuItem miQuit;
    private JMenuItem miQuickTest;
        
    private JMenu mOther;
    private JMenuItem mOSettings;
    private JMenuItem mOAbout;
    
    private File loadedFile;
    
    private SpriteEditPane editPane;    
    private String title;
    
    public void setup() {        
        createMenuBar();
        setJMenuBar(menuBar);
        setupEditPane();
        
        addListeners();
        
        addWindowListener(new PekaSE3GUIWindowListener(this));
        
        setupIcon();
        
        setTitle("PekaSE2");
        setMinimumSize(new Dimension(900, 700));
        setSize(900, 700);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        
        setVisible(true);
    }
    
    private void setupIcon() {
        try {
            var iconStream = getClass().getClassLoader().getResourceAsStream("pekase.png");
            var icon = ImageIO.read(iconStream);
            
            setIconImage(icon);
        } catch (IOException e) {
            Logger.warn(e, "Unable to set frame icon!");
        }
    }
    
    private void setupEditPane() {        
        editPane = new SpriteEditPane();
        editPane.registerUnsavedChangesListener(this);
        editPane.setSpriteProfile(Settings.getSpriteProfile());
        
        setContentPane(editPane);
        
        newFile();
    }
    
    /**
     * This method gets called after the user has set the game path.
     */
    public void pathSet() {
        setContentPane(tpSprite);
        
        setupEditPane();
    }
    
    
    private void loadSprite(String filename) {
        PK2Sprite sprite = null;
        
        try {
            var file = new File(filename);
            sprite =  SpriteIO.loadSpriteFile(file);

            File episodeDir = file.getParentFile();

            if(episodeDir.getName().equals(PK2FileSystem.SPRITES_DIR)){
                episodeDir = episodeDir.getParentFile();
                if(episodeDir.equals(PK2FileSystem.getAssetsPath())){
                    episodeDir = null;
                }
            }

            PK2FileSystem.setEpisodeDir(episodeDir);

            loadedFile = file;


            try {
                GFXUtils.loadSpriteImageSheet(sprite);
                editPane.setSprite(sprite);
                
            } catch (IOException e) {
                JOptionPane.showMessageDialog(null, "Unable to load the sprite image.\n" + e.getMessage(), "Unable to load sprite!", JOptionPane.ERROR_MESSAGE);
            }

            

        } catch (IOException e) {
            Logger.warn(e, "Unable to load sprite '" + filename + "'!\n");
            
            JOptionPane.showMessageDialog(null, "Unable to load the sprite file.\n" + e.getMessage(), "Unable to load sprite!", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void saveSprite(File file) {
        var sprite = editPane.setValues();
        
        try {
            SpriteIO.saveSprite(sprite, file);

        } catch (IOException e) {
            Logger.warn(e, "Unable to save sprite '" + file.getAbsolutePath() + "'!\n");
            
            JOptionPane.showMessageDialog(null, "Unable to save sprite!\n" + e.getMessage(), "Unable to save!", JOptionPane.ERROR_MESSAGE);
        }
        
        loadedFile = file;
        
        editPane.setUnsavedChangesPresent(false);
        updateTitle(file.getAbsolutePath(), false);
    }
    
    private void newFile() {        
        editPane.resetValues();
        editPane.setSprite(new PK2Sprite());
        
        String newSpriteName = "Unnamed.spr2";
        
        updateTitle(newSpriteName);
        
        editPane.setUnsavedChangesPresent(false);
    }
    
    private void createMenuBar() {
        menuBar = new JMenuBar();
        
        mFile = new JMenu("File");
        miFNew = new JMenuItem("New");
        miFOpen = new JMenuItem("Open");
        miFSave = new JMenuItem("Save");
        miFSaveAs = new JMenuItem("Save As...");
        miQuit = new JMenuItem("Quit");
        miQuickTest = new JMenuItem("Test sprite");
        
        mFile.add(miFNew);
        mFile.add(miFOpen);
        mFile.addSeparator();
        mFile.add(miFSave);
        mFile.add(miFSaveAs);
        mFile.addSeparator();
        mFile.add(miQuickTest);

        mFile.addSeparator();
        mFile.add(miQuit);
        
        mOther = new JMenu("Other");
        mOSettings = new JMenuItem("Settings");
        mOAbout = new JMenuItem("About");
        
        mOther.add(mOSettings);
        mOther.add(mOAbout);
        
        //mProfiles = new JMenu("Profiles");
        //addProfileMenuItems();
        
        menuBar.add(mFile);
        //menuBar.add(mProfiles);
        menuBar.add(mOther);
    }
    
    private void addListeners() {
        
        miFNew.addActionListener(e -> {
            newFile();
        });
        
        miFOpen.addActionListener(e -> {
            var fc = new SpriteFileChooser();
            
            if (fc.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {

                File selected = fc.getSelectedFile();
                this.loadSprite(selected.getAbsolutePath());
            }
        });
        
        miFSave.addActionListener(e -> {
            if (loadedFile == null) {
                var fc = new SpriteFileChooser();
                
                if (fc.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
                    saveSprite(fc.getSelectedFile());
                }
            } else {
                saveSprite(loadedFile);
            }
        });
        
        miFSaveAs.addActionListener(e -> {
            var fc = new SpriteFileChooser();
            if (loadedFile != null) fc.setSelectedFile(loadedFile);
            
            if (fc.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
                saveSprite(fc.getSelectedFile());
            }
        });
        
        mOAbout.addActionListener(e -> {
            JOptionPane.showMessageDialog(null, PekaEDSVersion.VERSION_STRING +", sprite editor", "About", JOptionPane.INFORMATION_MESSAGE);
        });

        miQuickTest.addActionListener(e -> {
            QuickSpriteTest tester = new QuickSpriteTest();
            try {
                tester.testSprite(editPane.setValues());
            }
            catch(FileNotFoundException e1){
                JOptionPane.showMessageDialog(null, "Missing dependency sprite\n"+e1.getMessage(), "Missing sprite", JOptionPane.ERROR_MESSAGE);
            }
            catch (IOException e1) {
                JOptionPane.showMessageDialog(null, e1.getMessage(), "Testing error", JOptionPane.ERROR_MESSAGE);
            }
        });
        
        miQuit.addActionListener(e -> {
            System.exit(0);
        });

        mOSettings.addActionListener(e->{
            if (settingsDialog == null) {
                settingsDialog = new SettingsDialog(this);
            }

            settingsDialog.setVisible(true);
        });
    }
    
    private void updateTitle(String title, boolean unsavedChanges) {
        this.title = title;
        
        if (unsavedChanges) title += "*";
        
        setTitle(title + " - PekaSE2");
    }
    
    private void updateTitle(String title) {
        this.title = title;
        
        updateTitle(title, false);
    }
    
    @Override
    public void stateChanged(ChangeEvent e) {
        updateTitle(title, true);
    }
    
    public void handleUnsavedChanges() {
        if (loadedFile != null) {
            saveSprite(loadedFile);
        } else {
            var fc = new SpriteFileChooser();
            
            if (fc.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
                saveSprite(fc.getSelectedFile());
            }
        }
    }
    
    void onClose() {
        if (editPane != null) {
            if (editPane.unsavedChangesPresent()) {
                UnsavedChangesDialog.show(this, true);
            } else {
                System.exit(0);
            }
        } else {
            System.exit(0);
        }
    }

    @Override
    public void setupOpenRecentMenu() {
        // TODO Auto-generated method stub
    }

    @Override
    public void updateLookAndFeel() {
        // TODO Auto-generated method stub
        SwingUtilities.updateComponentTreeUI(this);
    }
}
