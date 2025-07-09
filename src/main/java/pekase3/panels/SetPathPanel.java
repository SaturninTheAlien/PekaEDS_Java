package pekase3.panels;

import net.miginfocom.swing.MigLayout;
import pekase3.PekaSE3GUI;
import pekase3.settings.Settings;
import pekase3.settings.SettingsIO;

import javax.swing.*;

public class SetPathPanel extends JPanel {
    private Settings settings;
    private PekaSE3GUI mainGUI;
    
    public SetPathPanel(Settings settings, PekaSE3GUI ui) {
        this.mainGUI = ui;
        
        setup();
    }
    
    private void setup() {
        var lblPath = new JLabel("Path to the game:");
        var btnBrowse = new JButton("Browse");
        
        setLayout(new MigLayout("align 50% 50%"));
        
        add(lblPath, "cell 0 0");
        add(btnBrowse, "cell 0 1");
        
        btnBrowse.addActionListener(e -> {
            var fc = new JFileChooser();
            fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
            
            if (fc.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
                settings = new Settings();
                settings.setGamePath(fc.getSelectedFile().getAbsolutePath());
                
                SettingsIO.save(Settings.FILE, settings);
                
                mainGUI.pathSet();
            }
        });
    }
}
