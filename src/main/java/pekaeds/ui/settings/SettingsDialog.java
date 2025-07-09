package pekaeds.ui.settings;

import java.util.List;
import java.util.ArrayList;
import net.miginfocom.swing.MigLayout;
import pekaeds.settings.Settings;
import pekaeds.ui.main.PekaEDSGUI;
import pekaeds.util.file.FHSUtils;

import javax.swing.*;
import java.awt.*;

public class SettingsDialog extends JDialog {
    private JTabbedPane tabbedPane;

    private List<ISettingsPanel> settingPanels = new ArrayList<>();
        
    public SettingsDialog(PekaEDSGUI pkeds) {
        tabbedPane = new JTabbedPane(JTabbedPane.LEFT);

        PanelGeneral panelGeneral = new PanelGeneral(pkeds);
        PanelDefaults panelDefaults = new PanelDefaults();
        PanelShortcuts panelShortcuts = new PanelShortcuts(pkeds);
        PanelTesting panelTesting = new PanelTesting();
        PanelLookAndFeel panelAppearance = new PanelLookAndFeel(pkeds, this);

        tabbedPane.add("General", new JScrollPane(panelGeneral));
        tabbedPane.add("Appearance", panelAppearance);
        tabbedPane.add("Defaults", panelDefaults);
        tabbedPane.add("Shortcuts", new JScrollPane(panelShortcuts));
        tabbedPane.add("Testing", new JScrollPane(panelTesting));

        settingPanels.add(panelGeneral);
        settingPanels.add(panelAppearance);
        settingPanels.add(panelDefaults);
        settingPanels.add(panelShortcuts);
        settingPanels.add(panelTesting);
        
        JPanel panelButtons = new JPanel();
        var btnOk = new JButton("OK");
        var btnCancel = new JButton("Cancel");
        
        btnOk.addActionListener(e -> {
            saveSettings();
            dispose();
        });
        
        btnCancel.addActionListener(e -> {
            dispose();
        });
        
        panelButtons.setLayout(new MigLayout());
        panelButtons.add(new JPanel(), "cell 0 0, width 100%");
        panelButtons.add(btnOk, "cell 1 0");
        panelButtons.add(btnCancel, "cell 2 0");
        
        add(tabbedPane, BorderLayout.CENTER);
        add(panelButtons, BorderLayout.SOUTH);
        
        setSize(new Dimension(640, 480));
        setResizable(false);
        setTitle("Settings");
    }
    
    private void saveSettings() {
        for(ISettingsPanel panel:this.settingPanels){
            panel.saveSettings();
        }        
        Settings.save(FHSUtils.getSettingsFile());
    }
    
    @Override
    public void setVisible(boolean visible) {
        super.setVisible(visible);

        for(ISettingsPanel panel:this.settingPanels){
            panel.setupValues();
        }
    }
}
