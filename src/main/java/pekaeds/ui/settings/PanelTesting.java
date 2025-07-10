package pekaeds.ui.settings;

import pk2.settings.LevelTestingSettings;
import pk2.settings.Settings;

import javax.swing.*;

import net.miginfocom.swing.MigLayout;

public class PanelTesting extends JPanel implements ISettingsPanel{

    private JCheckBox cbDevMode;
    private JCheckBox cbCustomExecutable;
    private JCheckBox cbCustomDirectory;

    private JTextField tfDirectory;
    private JTextField tfExecutable;

    public PanelTesting(){
        this.cbDevMode = new JCheckBox("Dev mode");

        this.cbCustomExecutable = new JCheckBox("Use custom command");
        this.cbCustomDirectory = new JCheckBox("Use custom data directory");

        

        this.tfExecutable = new JTextField();
        this.tfDirectory = new JTextField();

        this.cbCustomExecutable.addActionListener(e->{
            tfExecutable.setEnabled(cbCustomExecutable.isSelected());
        });

        this.cbCustomDirectory.addActionListener(e->{
            tfDirectory.setEnabled(cbCustomDirectory.isSelected());
        });

        this.setBorder(BorderFactory.createTitledBorder("Testing parameters:"));
        this.setLayout(new MigLayout());

        this.add(this.cbDevMode, "cell 0 1");
        
        this.add(this.cbCustomExecutable, "cell 0 2");
        this.add(this.tfExecutable, "cell 1 2");

        this.add(this.cbCustomDirectory, "cell 0 3");
        this.add(this.tfDirectory, "cell 1 3");
    }

    public void setupValues(){
        LevelTestingSettings lts = Settings.levelTestingSettings;

        this.cbDevMode.setSelected(lts.devMode);

        this.cbCustomExecutable.setSelected(lts.customExecutable);
        this.cbCustomDirectory.setSelected(lts.customDataDirectory);

        this.tfExecutable.setText(lts.executable);
        this.tfDirectory.setText(lts.dataDirectory);

        this.tfExecutable.setEnabled(lts.customExecutable);
        this.tfDirectory.setEnabled(lts.customDataDirectory);
    }

    public void saveSettings(){
        LevelTestingSettings lts = Settings.levelTestingSettings;

        lts.devMode = this.cbDevMode.isSelected();
        lts.customExecutable = this.cbCustomExecutable.isSelected();
        lts.customDataDirectory = this.cbCustomDirectory.isSelected();

        lts.executable = this.tfExecutable.getText();
        lts.dataDirectory = this.tfDirectory.getText();
    }
}