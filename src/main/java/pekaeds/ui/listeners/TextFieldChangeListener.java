package pekaeds.ui.listeners;

import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

public class TextFieldChangeListener implements DocumentListener {
    private ChangeListener changeListener;
    private ChangeEvent changeEvent = new ChangeEvent(this);
    
    public TextFieldChangeListener(ChangeListener listener) {
        this.changeListener = listener;
    }
    
    @Override
    public void insertUpdate(DocumentEvent e) {
        if (changeListener != null) {
            changeListener.stateChanged(changeEvent);
        }
    }
    
    @Override
    public void removeUpdate(DocumentEvent e) {
        if (changeListener != null) changeListener.stateChanged(changeEvent);
    }
    
    @Override
    public void changedUpdate(DocumentEvent e) {
        if (changeListener != null) {
            changeListener.stateChanged(changeEvent);
        }
    }
}
