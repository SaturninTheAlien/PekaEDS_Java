package pekase3.panels.greta;

import javax.swing.*;

import org.json.JSONArray;

import pekase3.listener.UnsavedChangesListener;
import pekase3.panels.PekaSE2Panel;
import pk2.profile.SpriteProfile;
import pk2.sprite.PK2Sprite;

import java.awt.*;

public class CommandsPanel extends PekaSE2Panel {
    private JTextArea taCommands;
    
    public CommandsPanel() {
        setup();
    }
    
    private void setup() {
        taCommands = new JTextArea();
        
        generateLayout();
    }
    
    private void generateLayout() {
        setLayout(new BorderLayout());
        add(taCommands, BorderLayout.CENTER);
    }
    
    @Override
    public void setSprite(PK2Sprite sprite) {
        taCommands.setText(sprite.getCommands().toString(4));
    }
    
    @Override
    public void resetValues() {
        taCommands.setText("");
    }
    
    @Override
    public void setValues(PK2Sprite sprite) {

        sprite.setCommands(new JSONArray(taCommands.getText()));
    }
    
    @Override
    public void setProfileData(SpriteProfile profile) {
        // Not used here
    }
    
    @Override
    public void setUnsavedChangesListener(UnsavedChangesListener listener) {
        taCommands.getDocument().addDocumentListener(listener);
    }
}
