package pekase3.panels;

import java.util.Map;
import java.util.Set;

import javax.swing.*;

import pekase3.listener.UnsavedChangesListener;
import pk2.profile.SpriteProfile;
import pk2.sprite.PK2Sprite;

public abstract class PekaSE2Panel extends JPanel {
    public abstract void setSprite(PK2Sprite sprite);
    public abstract void resetValues();
    public abstract void setValues(PK2Sprite sprite);
    
    public abstract void setProfileData(SpriteProfile profile);
    
    public abstract void setUnsavedChangesListener(UnsavedChangesListener listener);

    protected void replaceComboBoxItems(JComboBox<String> comboBox, Set<Map.Entry<Integer, String>> entrySet) {
        comboBox.removeAllItems();
        
        for (var s : entrySet) {
            comboBox.addItem(s.getValue());
        }
    }
}
