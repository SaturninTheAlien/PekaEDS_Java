package pekase3.panels.soundspanel;

import net.miginfocom.swing.MigLayout;
import pekase3.FileFormat;
import pekase3.listener.UnsavedChangesListener;
import pekase3.panels.PekaSE2Panel;
import pekase3.profile.SpriteProfile;
import pekase3.settings.Settings;
import pk2.sprite.PK2Sprite;

import org.tinylog.Logger;

import javax.sound.sampled.*;
import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class SoundsPanel extends PekaSE2Panel {
    public static final int SOUND_ENTRY_SPECIAL_1 = 5;
    public static final int SOUND_ENTRY_SPECIAL_2 = 6;
    
    private Settings settings;
    
    private List<SoundEntry> soundEntryList;
    
    private JCheckBox cbRandomFrequency;
    private JSpinner spSoundFrequency;
    
    public SoundsPanel(Settings settings) {
        this.settings = settings;
        
        setup();
    }
    
    public void setFileFormat(FileFormat fileFormat) {
        soundEntryList.get(SOUND_ENTRY_SPECIAL_1).setVisible(fileFormat == FileFormat.GRETA);
        soundEntryList.get(SOUND_ENTRY_SPECIAL_2).setVisible(fileFormat == FileFormat.GRETA);
    }
    
    private void setup() {
        setLayout(new MigLayout("flowy, align 50%"));
        
        cbRandomFrequency = new JCheckBox("Random Frequency");
        spSoundFrequency = new JSpinner(new SpinnerNumberModel());
        
        var pnl = new JPanel();
        pnl.setBorder(BorderFactory.createTitledBorder("Data:"));
        pnl.setLayout(new MigLayout());
        
        var pnlSndFrq = new JPanel(new MigLayout("flowx"));
        pnlSndFrq.add(new JLabel("Sound Frequency:"));
        pnlSndFrq.add(spSoundFrequency);
        
        pnl.add(pnlSndFrq, "dock center");
        pnl.add(cbRandomFrequency, "dock east");
        
        add(pnl, "dock north");
        
        generateSoundEntryComponents();
        
        var pnlSoundEntries = new JPanel(new MigLayout("flowy"));
        pnlSoundEntries.setBorder(BorderFactory.createTitledBorder("Sounds:"));
        
        for (var e : soundEntryList) {
            pnlSoundEntries.add(e);
        }
        
        add(pnlSoundEntries);
    }
    
    private void generateSoundEntryComponents() {
        soundEntryList = new ArrayList<>();
        
        soundEntryList.add(new SoundEntry("Damage"));
        soundEntryList.add(new SoundEntry("Knock out"));
        soundEntryList.add(new SoundEntry("Attack 1"));
        soundEntryList.add(new SoundEntry("Attack 2"));
        soundEntryList.add(new SoundEntry("Random"));
        
        soundEntryList.add(new SoundEntry("Special 1"));
        soundEntryList.add(new SoundEntry("Special 2"));
        
        soundEntryList.get(SOUND_ENTRY_SPECIAL_1).setVisible(false);
        soundEntryList.get(SOUND_ENTRY_SPECIAL_2).setVisible(false);
    }
    
    @Override
    public void setSprite(PK2Sprite sprite) {
        for (int i = 0; i < 5; i++) { // TODO Don't hardcode this?
            soundEntryList.get(i).setSoundFile(sprite.getSoundFile(i));
        }
        
        spSoundFrequency.setValue(sprite.getSoundFrequency());
        cbRandomFrequency.setSelected(sprite.isRandomSoundFrequency());
    }
    
    @Override
    public void resetValues() {
        spSoundFrequency.setValue(22050);
        cbRandomFrequency.setSelected(false);
        
        for (var se : soundEntryList) {
            se.setSoundFile("");
        }
    }
    
    @Override
    public void setValues(PK2Sprite sprite) {
        sprite.setRandomSoundFrequency(cbRandomFrequency.isSelected());
        sprite.setSoundFrequency((int) spSoundFrequency.getValue());
        
        for (int i = 0; i < 7; i++) {
            if (i < soundEntryList.size()) {
                sprite.setSoundFile(soundEntryList.get(i).getSoundFile(), i);
            } else {
                sprite.setSoundFile("", i);
            }
        }
    }
    
    @Override
    public void setProfileData(SpriteProfile profile) {
    
    }
    
    @Override
    public void setUnsavedChangesListener(UnsavedChangesListener listener) {
        cbRandomFrequency.addActionListener(listener);
        spSoundFrequency.addChangeListener(listener);
        
        for (var se : soundEntryList) {
            se.addUnsavedChangesListener(listener);
        }
    }
    
    private class SoundEntry extends JPanel {
        private JLabel lblName;
        private JTextField tfSoundFile;
        private JButton btnBrowse;
        private JButton btnPlay;
        
        public SoundEntry(String name) {
            lblName = new JLabel(name + ":");
            tfSoundFile = new JTextField();
            
            btnBrowse = new JButton("Browse");
            btnPlay = new JButton("Play");
            
            setPreferredSize(new Dimension(600, 20));
            
            setLayout(new MigLayout("fillx", "[][grow][][]"));
            add(lblName, "width 10%");
            add(tfSoundFile, "growx");
            add(btnBrowse);
            add(btnPlay);
            
            btnBrowse.addActionListener(new BrowseWavAction(tfSoundFile, settings.getSpritesPath()));
            
            btnPlay.addActionListener(e -> {
                try {
                    var stream = AudioSystem.getAudioInputStream(new File(settings.getSpritesPath() + File.separatorChar + tfSoundFile.getText()));
                    var info = new DataLine.Info(Clip.class, stream.getFormat());
                    
                    var clip = (Clip) AudioSystem.getLine(info);
                    clip.open(stream);
                    clip.start();
                } catch (UnsupportedAudioFileException | LineUnavailableException | IOException ex) {
                    Logger.warn(ex);
                }
            });
        }
        
        public void setSoundFile(String file) {
            tfSoundFile.setText(file);
        }
        
        public String getSoundFile() {
            return tfSoundFile.getText();
        }
        
        public void addUnsavedChangesListener(UnsavedChangesListener listener) {
            tfSoundFile.getDocument().addDocumentListener(listener);
        }
    }
}
