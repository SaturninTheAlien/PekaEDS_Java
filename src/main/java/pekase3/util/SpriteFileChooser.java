package pekase3.util;

import net.miginfocom.swing.MigLayout;
import pekase3.FileFormat;
import pekase3.filefilters.PK2GretaSpriteFilter;
import pekase3.filefilters.PK2LegacySpriteFilter;
import pekase3.panels.FrameImagePanel;
import pekase3.settings.Settings;
import pekase3.sprite.io.PK2SpriteReader;
import pekase3.sprite.io.PK2SpriteReader13;
import pekase3.sprite.io.PK2SpriteReaderGreta;
import pk2.util.GFXUtils;

import org.tinylog.Logger;

import javax.swing.*;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.attribute.BasicFileAttributes;
import java.text.SimpleDateFormat;

public class SpriteFileChooser extends JFileChooser implements PropertyChangeListener {
    private JPanel previewPanel;
    
    private FrameImagePanel imagePanel;
    
    private JLabel lblName;
    private JLabel lblNameVal;
    
    private JLabel lblType;
    private JLabel lblTypeVal;
    
    private JLabel lblFileCreated;
    private JLabel lblFileCreatedVal;
    
    private JLabel lblFileModified;
    private JLabel lblFileModifiedVal;
    
    private Settings settings;
    
    private PK2SpriteReader13 legacySpriteReader;
    private PK2SpriteReaderGreta gretaSpriteReader;
    private PK2SpriteReader spriteReader;
    
    private FileFormat fileFormat = FileFormat.LEGACY;
    
    public SpriteFileChooser(Settings settings, FileFormat fileFormat) {
        super(settings.getSpritesPath());
        
        this.fileFormat = fileFormat;
        
        legacySpriteReader = new PK2SpriteReader13();
        gretaSpriteReader = new PK2SpriteReaderGreta();

        if (fileFormat == FileFormat.GRETA) {
            spriteReader = gretaSpriteReader;
        } else {
            spriteReader = legacySpriteReader;
        }
        
        this.settings = settings;
        
        setup(fileFormat);
        
        setAccessory(previewPanel);
        
        addPropertyChangeListener(this);
    }
    
    private void setup(FileFormat fileFormat) {
        setDialogTitle("Select a Pekka Kana 2 sprite file...");

        if (fileFormat == FileFormat.GRETA) {
            setFileFilter(new PK2GretaSpriteFilter());
        } else {
            setFileFilter(new PK2LegacySpriteFilter());
        }
        
        previewPanel = new JPanel();
        
        imagePanel = new FrameImagePanel(192, 192, 192, 192);
        
        lblName = new JLabel("Name:");
        lblNameVal = new JLabel();
        
        lblType = new JLabel("Type:");
        lblTypeVal = new JLabel();
        
        lblFileCreated = new JLabel("Created:");
        lblFileCreatedVal = new JLabel("");
        
        lblFileModified = new JLabel("Modified:");
        lblFileModifiedVal = new JLabel();
        
        previewPanel.setLayout(new MigLayout());
        
        previewPanel.add(imagePanel, "dock north");
        
        var dataPanel = new JPanel();
        dataPanel.setLayout(new MigLayout());
        dataPanel.add(lblName, "cell 0 0");
        dataPanel.add(lblNameVal, "cell 1 0");
        
        dataPanel.add(lblType, "cell 0 1");
        dataPanel.add(lblTypeVal, "cell 1 1");
        
        dataPanel.add(lblFileCreated, "cell 0 2");
        dataPanel.add(lblFileCreatedVal, "cell 1 2");
        
        dataPanel.add(lblFileModified, "cell 0 3");
        dataPanel.add(lblFileModifiedVal, "cell 1 3");
        
        previewPanel.add(dataPanel, "dock center");
    }
    
    @Override
    public void propertyChange(PropertyChangeEvent e) {
        if (e.getPropertyName().equals(JFileChooser.SELECTED_FILE_CHANGED_PROPERTY)) {
            var selectedFile = (File) e.getNewValue();
            
            if (selectedFile != null) {
                try {
                    String fileEnding = ".spr";
                    
                    if (fileFormat == FileFormat.GRETA) {
                        fileEnding = ".spr2";
                    }
                    
                    if (!selectedFile.getName().endsWith(fileEnding)) {
                        selectedFile = new File(selectedFile.getAbsolutePath() + fileEnding);
                    }
                    
                    if (selectedFile.exists()) {
                        var spr = spriteReader.load(selectedFile);
                        
                        var spriteSheetFile = new File(settings.getSpritesPath() + File.separatorChar + spr.getImageFile());
                        if (spriteSheetFile.exists()) {
                            GFXUtils.loadFirstFrame(spr, settings.getSpritesPath());
                            imagePanel.setImage(spr.getImage());
                        } else {
                            imagePanel.setImage(null);
                        }
                        
                        lblNameVal.setText(spr.getName());
                        lblTypeVal.setText(settings.getSpriteProfile().getTypeMap().get(spr.getType()));
                        
                        try {
                            var f = Paths.get(selectedFile.getPath());
                            
                            var attributes = Files.readAttributes(f, BasicFileAttributes.class);
                            
                            SimpleDateFormat df = new SimpleDateFormat("HH:mm:ss dd.MM.yy");

                            lblFileCreatedVal.setText(df.format(attributes.creationTime().toMillis()));
                            lblFileModifiedVal.setText(df.format(attributes.lastModifiedTime().toMillis()));
                        } catch (IOException ex) {
                            Logger.warn(ex);
                        }
                    }
                } catch (IOException | UnknownSpriteFormatException ex) {
                    Logger.warn(ex);
                }
            }
        }
    }
}
