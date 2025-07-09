package pk2.ui;

import org.tinylog.Logger;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.io.IOException;

public class ImagePreviewFileChooser extends JFileChooser implements PropertyChangeListener {
    public static final int PREVIEW_TILESET = 0;
    public static final int PREVIEW_BACKGROUND = 1;
    
    private ImagePanel imagePanel;
    
    private int previewType;
    
    public ImagePreviewFileChooser(File basePath, int preview) {
        super(basePath);
        
        this.previewType = preview;
        
        setup();
    }
    
    public ImagePreviewFileChooser() {
        setup();
    }
    
    private void setup() {
        var imgPanel = new ImagePanel(320, 480); // Set the default to the size of the tileset.
    
        if (previewType == PREVIEW_BACKGROUND) {
            imgPanel = new ImagePanel(640, 480);
        }
        
        this.imagePanel = imgPanel;
    
        setAccessory(imagePanel);
    
        addPropertyChangeListener(this);
    }
    
    @Override
    public void propertyChange(PropertyChangeEvent e) {
        if (e.getPropertyName().equals(JFileChooser.SELECTED_FILE_CHANGED_PROPERTY)) {
            var selectedFile = (File) e.getNewValue();
    
            if (selectedFile != null) {
                try {
                    var img = ImageIO.read(selectedFile);
        
                    imagePanel.setImage(img);
                } catch (IOException ex) {
                    Logger.warn("Unable to load image file: {}" + selectedFile.getName());
                }
            }
        }
    }
}
