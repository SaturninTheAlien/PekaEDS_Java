package pekase3.panels.spriteeditpane;

import javax.swing.*;
import javax.swing.event.ChangeListener;

import pekase3.listener.UnsavedChangesListener;
import pekase3.panels.PekaSE2Panel;
import pekase3.panels.ailists.AIListPanel;
import pekase3.panels.animation.AnimationsEditPanel;
import pekase3.panels.attackspanel.AttacksPanel;
import pekase3.panels.greta.CommandsPanel;
import pekase3.panels.greta.GretaPropertiesPanel;
import pekase3.panels.imagepanel.ImagePanel;
import pekase3.panels.propertiespanel.PropertiesPanel;
import pekase3.panels.soundspanel.SoundsPanel;
import pk2.profile.SpriteProfile;
import pk2.sprite.PK2Sprite;
import pk2.util.GFXUtils;

import java.util.ArrayList;
import java.util.List;

public class SpriteEditPane extends JTabbedPane {   
    
    private ImagePanel imagePanel;
    private AnimationsEditPanel animationsPanel;
    private AIListPanel aiListPanel;
    private SoundsPanel soundsPanel;
    private AttacksPanel attacksPanel;
    private PropertiesPanel propertiesPanel;
    private GretaPropertiesPanel gretaPropertiesPanel;
    private CommandsPanel commandsPanel;
    
    private List<PekaSE2Panel> panels;
    
    private SpriteEditPaneModel model = new SpriteEditPaneModel();
    
    private final UnsavedChangesListener unsavedChangesListener = new UnsavedChangesListener(this);
    
   
    public SpriteEditPane() {
        setup();
    }
    
    private void setup() {
        panels = new ArrayList<>();
        
        imagePanel = new ImagePanel(model);
        animationsPanel = new AnimationsEditPanel();
        aiListPanel = new AIListPanel();
        soundsPanel = new SoundsPanel();
        attacksPanel = new AttacksPanel();
        propertiesPanel = new PropertiesPanel();
        gretaPropertiesPanel = new GretaPropertiesPanel();
        commandsPanel = new CommandsPanel();
        
        model.addSpriteFramesChangeListener(animationsPanel);

        addPanel(imagePanel, "Image");
        addPanel(animationsPanel, "Animations");
        addPanel(aiListPanel, "AI", true);
        addPanel(attacksPanel, "Attack");
        addPanel(soundsPanel, "Sounds");
        addPanel(propertiesPanel, "Properties", true);
        addPanel(gretaPropertiesPanel, "Greta properties");
        addPanel(commandsPanel, "Commands");


       
        addUnsavedChangesListener();
    }
    
    private void addUnsavedChangesListener() {
        for (var p : panels) {
            p.setUnsavedChangesListener(unsavedChangesListener);
        }
    }
    
    public PK2Sprite setValues() {
        var sprite = new PK2Sprite();

        for(PekaSE2Panel p: panels){
            p.setValues(sprite);
        }
        
        return sprite;
    }
    
    public void setSprite(PK2Sprite sprite) {
        unsavedChangesListener.setIgnoreChanges(true);
        
        sprite.setFramesList(GFXUtils.cutFrames(sprite.getImage(), sprite.getFramesAmount(), sprite.getFrameX(), sprite.getFrameY(), sprite.getFrameWidth(), sprite.getFrameHeight()));
        
        model.setSprite(sprite);
        for(PekaSE2Panel p: panels){
            p.setSprite(model.getSprite());
        }
        
        unsavedChangesListener.setIgnoreChanges(false);
    }
    
    private void addPanel(PekaSE2Panel panel, String name, boolean wrapInScrollPane) {
        if (wrapInScrollPane) {
            var sp = new JScrollPane(panel);
            sp.getVerticalScrollBar().setUnitIncrement(8);
            
            add(sp, name);
        } else {
            add(panel, name);
        }
        
        panels.add(panel);
    }
    
    private void addPanel(PekaSE2Panel panel, String name) {
        addPanel(panel, name, false);
    }
    
    public void registerUnsavedChangesListener(ChangeListener listener) {
        unsavedChangesListener.addChangeListener(listener);
    }
    
    public void setUnsavedChangesPresent(boolean present) {
        model.setUnsavedChanges(present);
    }
    
    public boolean unsavedChangesPresent() {
        return model.unsavedChangesPresent();
    }
    
    public void resetValues() {
        unsavedChangesListener.setIgnoreChanges(true);

        for(PekaSE2Panel p: panels){
            p.resetValues();
        }
        
        unsavedChangesListener.setIgnoreChanges(false);
    }
    
    public void setSpriteProfile(SpriteProfile profile) {
        for (PekaSE2Panel panel : panels) {
            panel.setProfileData(profile);
        }
    }
}
