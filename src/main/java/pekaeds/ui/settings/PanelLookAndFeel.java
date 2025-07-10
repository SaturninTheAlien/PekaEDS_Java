package pekaeds.ui.settings;

import java.util.ArrayList;

import javax.swing.*;

import net.miginfocom.swing.MigLayout;
import pekaeds.ui.main.IPekaEdsApp;
import pekaeds.ui.misc.LookAndFeelHelper;
import pk2.settings.Settings;

public class PanelLookAndFeel extends JPanel implements ISettingsPanel {

    private IPekaEdsApp eds;
    private JComboBox<String> cbTheme = new JComboBox<>();
    


    private SettingsDialog settingsDialog;

    public PanelLookAndFeel(IPekaEdsApp eds, SettingsDialog settingsDialog){
        this.eds = eds;
        this.settingsDialog = settingsDialog;

        this.setBorder(BorderFactory.createTitledBorder("Appearance"));
        this.setLayout(new MigLayout());

        var lblTheme = new JLabel("Theme:");
        //pnlPath.setBackground(new Color(45, 45, 46));
        this.add(lblTheme, "cell 0 0");
        this.add(this.cbTheme, "cell 0 1, width 200px");

        ArrayList<String> themes = LookAndFeelHelper.getSupportedThemesList();

        int theme_index = themes.indexOf(Settings.getLookAndFeel());
        if(theme_index==-1){
            theme_index = themes.size();
            themes.add(Settings.getLookAndFeel());
        }

        DefaultComboBoxModel<String> themeModel = (DefaultComboBoxModel<String>) this.cbTheme.getModel();
        themeModel.addAll(themes);

    }


    @Override
    public void saveSettings() {

        String selectedTheme = (String) this.cbTheme.getSelectedItem();
        Settings.setLookAndFeel(selectedTheme);
        LookAndFeelHelper.updateTheme();
        SwingUtilities.updateComponentTreeUI(settingsDialog);
        eds.updateLookAndFeel();
    }

    @Override
    public void setupValues() {
        this.cbTheme.setSelectedItem(Settings.getLookAndFeel());
    }
    
}