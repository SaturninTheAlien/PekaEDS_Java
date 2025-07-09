package pekase3.panels.soundspanel;

import javax.swing.*;

import pekase3.filefilters.WAVFilter;

import java.awt.event.ActionEvent;

public class BrowseWavAction extends AbstractAction {
    private static JFileChooser fileChooser;
    private final static WAVFilter wavFilter = new WAVFilter();
    
    private JTextField tfTarget; // Target textfield to store the path of the selected file
    private String spritesPath;
    
    public BrowseWavAction(JTextField target, String spritesPath) {
        this.tfTarget = target;
        this.spritesPath = spritesPath;
    }
    
    @Override
    public void actionPerformed(ActionEvent e) {
        if (fileChooser == null) {
            fileChooser = new JFileChooser(spritesPath);
            
            fileChooser.setDialogTitle("Select a WAV file...");
            fileChooser.setFileFilter(wavFilter);
        }
        
        if (fileChooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
            tfTarget.setText(fileChooser.getSelectedFile().getAbsolutePath());
        }
    }
}
