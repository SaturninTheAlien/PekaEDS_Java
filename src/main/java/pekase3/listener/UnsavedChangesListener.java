package pekase3.listener;

import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import pekase3.panels.spriteeditpane.SpriteEditPane;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

public class UnsavedChangesListener implements ChangeListener, ActionListener, DocumentListener {
    private final ChangeEvent changeEvent = new ChangeEvent(this);
    
    private final SpriteEditPane editPane;

    private final List<ChangeListener> changeListeners = new ArrayList<>();
    
    // This is needed because the components will call this listener when a sprite gets loaded from a file. We need to ignore these changes. We only want the change events the user has caused by changing values.
    private boolean ignoreChanges = false;
    
    public UnsavedChangesListener(SpriteEditPane editPane) {
        this.editPane = editPane;
    }
    
    private void changePresent() {
        if (!ignoreChanges) {
            editPane.setUnsavedChangesPresent(true);
            
            for (var c : changeListeners) {
                c.stateChanged(changeEvent);
            }
        }
    }
    
    public void addChangeListener(ChangeListener listener) {
        if (!changeListeners.contains(listener)) {
            changeListeners.add(listener);
        }
    }
    
    @Override
    public void stateChanged(ChangeEvent e) {
        changePresent();
    }
    
    @Override
    public void actionPerformed(ActionEvent e) {
        changePresent();
    }
    
    @Override
    public void insertUpdate(DocumentEvent e) {
        changePresent();
    }
    
    @Override
    public void removeUpdate(DocumentEvent e) {
        changePresent();
    }
    
    @Override
    public void changedUpdate(DocumentEvent e) {
        changePresent();
    }
    
    public void setIgnoreChanges(boolean ignore) {
        this.ignoreChanges = ignore;
    }
}
