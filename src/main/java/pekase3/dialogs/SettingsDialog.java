package pekase3.dialogs;

import net.miginfocom.swing.MigLayout;
import pekase3.settings.Settings;
import pekase3.settings.SettingsIO;

import javax.swing.*;

import java.awt.*;

@Deprecated
public class SettingsDialog extends JDialog {
    private Settings settings;
    
    private JTextField tfGamePath;
    private JButton btnBrowse;
    
    private JButton btnOK;
    private JButton btnCancel;
    
    public SettingsDialog() {
        setAlwaysOnTop(true);
        setTitle("Settings");
        
        setSize(400, 120);
        
        setLocationRelativeTo(null);
        
        tfGamePath = new JTextField();
        btnBrowse = new JButton("Browse");
        
        btnOK = new JButton("OK");
        btnCancel = new JButton("Cancel");
        
        generateLayout();
        
        addListeners();
    }
    
    private void generateLayout() {
        var pnlPath = new JPanel();
        pnlPath.setLayout(new MigLayout());
        pnlPath.add(new JLabel("Game path:"));
        pnlPath.add(tfGamePath, "width 100%");
        pnlPath.add(btnBrowse);

        var pnlOkCancel = new JPanel();
        pnlOkCancel.setLayout(new MigLayout("", "[grow, right]", "[grow, bottom]"));
        pnlOkCancel.add(btnOK, "flowx, tag ok, cell 0 0");
        pnlOkCancel.add(btnCancel, "tag cancel, cell 0 0");
        
        add(pnlPath, BorderLayout.CENTER);
        add(pnlOkCancel, BorderLayout.PAGE_END);
    }
    
    private void addListeners() {
        btnBrowse.addActionListener(e -> {
            var fc = new JFileChooser();
            fc.setDialogTitle("Browse...");
            fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
            
            if (fc.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
                tfGamePath.setText(fc.getSelectedFile().getAbsolutePath());
            }
        });
        
        btnOK.addActionListener(e -> {
            settings.setGamePath(tfGamePath.getText());
            
            SettingsIO.save(Settings.FILE, settings);
            
            setVisible(false);
        });
        
        btnCancel.addActionListener(e -> {
            setVisible(false);
        });
    }
    
    public void setVisible(boolean visible) {
        super.setVisible(visible);
        
        tfGamePath.setText(settings.getGamePath());
    }
    
    public void setSettings(Settings settings) {
        this.settings = settings;
        
        tfGamePath.setText(settings.getGamePath());
    }
}
