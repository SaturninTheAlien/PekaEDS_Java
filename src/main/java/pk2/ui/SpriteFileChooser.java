package pk2.ui;

import net.miginfocom.swing.MigLayout;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.filechooser.FileFilter;

import org.tinylog.Logger;

import pk2.filesystem.PK2FileSystem;
import pk2.settings.Settings;
import pk2.sprite.SpritePrototype;
import pk2.sprite.io.SpriteIO;
import pk2.sprite.io.SpriteMissing;
import pk2.util.GFXUtils;

import java.awt.image.BufferedImage;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.io.FileNotFoundException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.BasicFileAttributes;
import java.text.SimpleDateFormat;

public class SpriteFileChooser extends JFileChooser implements PropertyChangeListener {
    private JPanel previewPanel;

    private JButton btnVanillaDir;
    private JButton btnEpisodeDir;
    
    private ImagePanel imagePanel;
    
    private JLabel lblName;
    private JLabel lblNameVal;
    
    private JLabel lblType;
    private JLabel lblTypeVal;
    
    private JLabel lblFileCreated;
    private JLabel lblFileCreatedVal;
    
    private JLabel lblFileModified;
    private JLabel lblFileModifiedVal;


    public SpriteFileChooser(){
        this(PK2FileSystem.getAssetsPath(PK2FileSystem.SPRITES_DIR));
    }
    
    public SpriteFileChooser(File basePath) {
        super(basePath);
        
        setup();
        
        setAccessory(previewPanel);
        
        addPropertyChangeListener(this);

        setFileFilter(new FileFilter() {
            @Override
            public boolean accept(File f) {
                if (f.isDirectory()) return true;
                else {
                    String name = f.getName();
                    return name.endsWith(".spr2") || name.toLowerCase().endsWith(".spr");
                }
            }

            @Override
            public String getDescription() {
                return "Pekka Kana 2 Sprite file (*.spr2, *.spr)";
            }
        });
    }
    
    private void setup() {

        btnVanillaDir = new JButton("Vanilla dir");
        btnEpisodeDir = new JButton("Episode dir");

        btnVanillaDir.addActionListener(e->{
            File f = PK2FileSystem.getAssetsPath(PK2FileSystem.SPRITES_DIR);
            SpriteFileChooser.this.setCurrentDirectory(f);
        });

        File dir = PK2FileSystem.getEpisodeAssetsPath(PK2FileSystem.SPRITES_DIR);
        if(dir!=null){
            btnEpisodeDir.addActionListener(e->{
                SpriteFileChooser.this.setCurrentDirectory(dir);
            });
        }
        else{
            btnEpisodeDir.setEnabled(false);
        }     

        previewPanel = new JPanel();
        
        imagePanel = new ImagePanel(256, 200);
        
        lblName = new JLabel("Name:");
        lblNameVal = new JLabel();
        
        lblType = new JLabel("Type:");
        lblTypeVal = new JLabel();
        
        lblFileCreated = new JLabel("Created:");
        lblFileCreatedVal = new JLabel("");
        
        lblFileModified = new JLabel("Modified:");
        lblFileModifiedVal = new JLabel();

        previewPanel.setLayout(new MigLayout("insets 0 5 5 5"));

        var btnPanel = new JPanel();
        btnPanel.setLayout(new MigLayout("insets 0"));
        btnPanel.add(btnEpisodeDir, "cell 1 0");
        btnPanel.add(btnVanillaDir, "cell 2 0");
        
        previewPanel.add(btnPanel, "cell 0 0");
        previewPanel.add(imagePanel, "cell 0 1");
        //previewPanel.add(imagePanel, "dock north");
        
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
        
        previewPanel.add(dataPanel, "cell 0 2");
    }
    
    @Override
    public void propertyChange(PropertyChangeEvent e) {
        if (e.getPropertyName().equals(JFileChooser.SELECTED_FILE_CHANGED_PROPERTY)) {
            var selectedFile = (File) e.getNewValue();
            
            if (selectedFile != null) {
                try{
                    SpritePrototype spr = SpriteIO.getSpriteReader(selectedFile).readSpriteFile(selectedFile);

                    BufferedImage img;
                    try{
                        img = ImageIO.read(PK2FileSystem.findAsset(spr.getImageFile(), PK2FileSystem.SPRITES_DIR));
                        GFXUtils.adjustSpriteColor(img, spr.getColor());
                        img = GFXUtils.makeTransparent(img);
                        img = GFXUtils.getFirstSpriteFrame(spr, img);
                    }
                    catch(FileNotFoundException exception2){
                        img = SpriteMissing.getMissingTextureImage();
                    }
                    
                    spr.setImage(img);

                    imagePanel.setImage(spr.getImage(), true, 256, 200);
    
                    lblNameVal.setText(spr.getName());
                    lblTypeVal.setText(Settings.getSpriteProfile().getTypes().get(spr.getType() - 1));
                }
                catch(Exception spriteException){
                    imagePanel.setImage(SpriteMissing.getMissingImage(), true, 256, 200);
                    lblNameVal.setText("Loading error!");
                    lblTypeVal.setText("Unknown");
                    Logger.error(spriteException);
                }

                try{
                    Path f = Paths.get(selectedFile.getPath());
                    BasicFileAttributes attributes = null;

                    attributes = Files.readAttributes(f, BasicFileAttributes.class);    
                    SimpleDateFormat df = new SimpleDateFormat("HH:mm:ss dd.MM.yy"); // TODO Use local time/date format
    
                    lblFileCreatedVal.setText(df.format(attributes.creationTime().toMillis()));
                    lblFileModifiedVal.setText(df.format(attributes.lastModifiedTime().toMillis()));
                }
                catch(Exception exception){
                    Logger.error(exception);
                    lblFileCreatedVal.setText("Unknown");
                    lblFileModifiedVal.setText("Unknown");
                }
            }
        }
    }
}
