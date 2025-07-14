package pekase3.panels.imagepanel;

import net.miginfocom.swing.MigLayout;
import pekase3.filefilters.ImageFilter;
import pekase3.listener.UnsavedChangesListener;
import pekase3.panels.PekaSE2Panel;
import pekase3.panels.imagepanel.spritesheetpanel.SpriteSheetPanel;
import pekase3.panels.imagepanel.spritesheetpanel.FrameEditMode.*;
import pekase3.panels.spriteeditpane.SpriteEditPaneModel;
import pk2.filesystem.PK2FileSystem;
import pk2.profile.SpriteProfile;
import pk2.sprite.PK2Sprite;
import pk2.util.GFXUtils;

import org.tinylog.Logger;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class ImagePanel extends PekaSE2Panel implements ChangeListener {
    private static final ImageFilter BMP_IMG_FILTER = new ImageFilter();
    
    private JTextField tfImage;
    private JButton btnBrowse;
    
    private JPanel pnlBorderColor;
    
    private SpriteSheetPanel spriteSheetPanel;
    
    private SpriteEditPaneModel spriteEditModel;
    
    private JButton btnUpdateFrames;
    private JToggleButton btnPickSeparatorColor;
    private JToggleButton btnDrawFrame;
    private JToggleButton btnDetectFrame;
    private JToggleButton btnSetFrameRange;
    private JToggleButton btnLockFrame;
    
    private JSpinner spFrameWidth;
    private JSpinner spFrameHeight;
    private JSpinner spFrameX;
    private JSpinner spFrameY;
    private JSpinner spFrameAmount;
    
    private JComboBox<String> cbColors;
    
    private PK2Sprite loadedSprite;
    
    private JToggleButton lastSelectedButton;

    private SpriteProfile profile;
    
    public ImagePanel(SpriteEditPaneModel spriteModel) {
        this.spriteEditModel = spriteModel;        
        setup();
    }
    
    public void setup() {
        spriteSheetPanel = new SpriteSheetPanel();
        spriteSheetPanel.getModel().addListener(this);
        
        setLayout(new MigLayout());
        
        tfImage = new JTextField();
        btnBrowse = new JButton("Browse");
        
        pnlBorderColor = new JPanel();
        pnlBorderColor.setMinimumSize(new Dimension(22, 22));
        pnlBorderColor.setPreferredSize(new Dimension(22, 22));
        pnlBorderColor.setBorder(BorderFactory.createLineBorder(Color.BLACK));
        
        btnUpdateFrames = new JButton("Update Frames");
        btnPickSeparatorColor = new JToggleButton("Pick");
        btnDrawFrame = new JToggleButton("Draw");
        btnDetectFrame = new JToggleButton("Detect");
        btnSetFrameRange = new JToggleButton("Set Range");
        btnLockFrame = new JToggleButton("Lock");
        btnLockFrame.setSelected(true);
        
        spFrameWidth = new JSpinner(new SpinnerNumberModel(0, 0, 10000, 1));
        spFrameHeight = new JSpinner(new SpinnerNumberModel(0, 0, 10000, 1));
        spFrameX = new JSpinner(new SpinnerNumberModel(0, 0, 10000, 1));
        spFrameY = new JSpinner(new SpinnerNumberModel(0, 0, 10000, 1));
        spFrameAmount = new JSpinner(new SpinnerNumberModel(0, 0, 10000, 1));
  
        cbColors = new JComboBox<>();
        
        setupToolTips();
        generateLayout();
        addListeners();
    }
    
    private void setupToolTips() {
        btnUpdateFrames.setToolTipText("Update the frame images after changing the data.");
        btnDetectFrame.setToolTipText("Automatically detect frame position and size by moving your mouse over the image.");
        btnLockFrame.setToolTipText("Lock the frame's position to a grid of (frame width, frame height) when moving it.");
        btnDrawFrame.setToolTipText("Manually draw the frame's width and height.");
        btnPickSeparatorColor.setToolTipText("Pick the frame border color by moving your mouse over it and then pressing the left mouse button.");
        btnSetFrameRange.setToolTipText("After detecting the frame, click once on the first frame, then another time on the last one.");
        
        spFrameX.setToolTipText("The first frame's x position in the sprite sheet.");
        spFrameY.setToolTipText("The first frame's y position in the sprite sheet.");
        
        spFrameWidth.setToolTipText("The first frame's width.");
        spFrameHeight.setToolTipText("The first frame's height.");
        
        spFrameAmount.setToolTipText("The amount of frame's this sprite has.");
        
        pnlBorderColor.setToolTipText("The color that separates each frame.");
    }
    
    private void generateLayout() {
        var pnlImage = new JPanel();
        pnlImage.setLayout(new MigLayout("flowx"));
        pnlImage.add(tfImage, "width 100%");
        pnlImage.add(btnBrowse);
        
        var pnlButtonAndColor = new JPanel();
        pnlButtonAndColor.setLayout(new MigLayout("flowx, fillx", "[fill]"));
        pnlButtonAndColor.add(btnPickSeparatorColor, "growx");
        pnlButtonAndColor.add(pnlBorderColor);
        
        var pnlButtons = new JPanel();
        pnlButtons.setLayout(new MigLayout("flowy,fillx", "[fill]"));
        pnlButtons.add(btnUpdateFrames);
        pnlButtons.add(btnDetectFrame);
        pnlButtons.add(btnSetFrameRange);
        pnlButtons.add(btnDrawFrame);
        pnlButtons.add(btnLockFrame);
        pnlButtons.add(pnlButtonAndColor);
        
        var pnlButtonsAndColor = new JPanel();
        pnlButtonsAndColor.setLayout(new MigLayout());
        pnlButtonsAndColor.setBorder(BorderFactory.createTitledBorder("Actions:"));
        pnlButtonsAndColor.add(pnlButtons, "dock center");
 
        var pnlFrameData = new JPanel();
        pnlFrameData.setLayout(new MigLayout("flowy"));
        pnlFrameData.setBorder(BorderFactory.createTitledBorder("Data:"));
        pnlFrameData.add(new JLabel("Frame X:"));
        pnlFrameData.add(spFrameX);
        
        pnlFrameData.add(new JLabel("Frame Y:"));
        pnlFrameData.add(spFrameY);
        
        pnlFrameData.add(new JLabel("Frame Width:"));
        pnlFrameData.add(spFrameWidth);
        
        pnlFrameData.add(new JLabel("Frame Height:"));
        pnlFrameData.add(spFrameHeight);
        
        pnlFrameData.add(new JLabel("Frame Amount:"));
        pnlFrameData.add(spFrameAmount);
        
        pnlFrameData.add(new JLabel("Color:"));
        pnlFrameData.add(cbColors);
        
        var pnlFrameContentImageAndPanel = new JPanel();
        pnlFrameContentImageAndPanel.setBorder(BorderFactory.createTitledBorder("Image:"));
        pnlFrameContentImageAndPanel.setLayout(new MigLayout());
        pnlFrameContentImageAndPanel.add(pnlImage, "dock north");
        pnlFrameContentImageAndPanel.add(spriteSheetPanel, "dock center");
        
        var pnlButtonsAndFrame = new JPanel();
        pnlButtonsAndFrame.setLayout(new MigLayout("flowy", "[fill]"));
        pnlButtonsAndFrame.add(pnlButtonsAndColor);
        pnlButtonsAndFrame.add(pnlFrameData);
        
        var pnlFrame = new JPanel();
        pnlFrame.setLayout(new MigLayout());
        pnlFrame.add(pnlButtonsAndFrame, "dock west");
        pnlFrame.add(pnlFrameContentImageAndPanel, "dock center");
        
        add(pnlFrame, "dock center");
    }
    
    private void addListeners() {
        btnBrowse.addActionListener(e -> {
            var fc = new JFileChooser(PK2FileSystem.getAssetsPath(PK2FileSystem.SPRITES_DIR));
            fc.setFileFilter(BMP_IMG_FILTER);
            
            if (fc.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
                tfImage.setText(fc.getSelectedFile().getName());
                spriteEditModel.getSprite().setImageFile(tfImage.getText());
                
                loadImage(fc.getSelectedFile());
            }
        });
        
        btnUpdateFrames.addActionListener(e -> {
            updateSpriteFrames();
        });
        
        btnPickSeparatorColor.addActionListener(e -> {
            spriteSheetPanel.setEditMode(PickSeparatorColorFrameMode.class);
            
            updateLastSelectedButton();
            lastSelectedButton = btnPickSeparatorColor;
        });
        
        btnLockFrame.addActionListener(e -> {
            spriteSheetPanel.lockToFrame(btnLockFrame.isSelected());
        });
        
        btnDrawFrame.addActionListener(e -> {
            updateLastSelectedButton();
            lastSelectedButton = btnDrawFrame;
            
            spriteSheetPanel.setEditMode(DrawFrameMode.class);
        });
        
        /*
        pnlBorderColor.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                super.mousePressed(e);
                
                if (!colorPalettePanelPopup.isVisible()) {
                    var compPos = pnlBorderColor.getLocationOnScreen();
                    colorPalettePanelPopup.setLocation((int) compPos.getX(), (int) (compPos.getY() + pnlBorderColor.getHeight()));
                    colorPalettePanelPopup.setVisible(true);
                }
            }
        });*/
        
        btnDetectFrame.addActionListener(e -> {
            updateLastSelectedButton();
            lastSelectedButton = btnDetectFrame;
            
            spriteSheetPanel.setEditMode(DetectFrameMode.class);
        });
        
        btnSetFrameRange.addActionListener(e -> {
            updateLastSelectedButton();
            lastSelectedButton = btnSetFrameRange;
            
            spriteSheetPanel.setEditMode(SetRangeFrameMode.class);
        });
        
        cbColors.addActionListener(e -> {
            if (loadedSprite != null) {
                int color = 255;
                for (var s : this.profile.getColorMap().entrySet()) {
                    if (cbColors.getSelectedItem() != null) {
                        if (cbColors.getSelectedItem().equals(s.getValue())) {
                            color = s.getKey();
                            
                            break;
                        }
                    }
                }
                
                if (loadedSprite.getColor() != color) {

                    loadedSprite.setColor(color);

                    if (color != 255) {
                        GFXUtils.adjustSpriteColor(loadedSprite.getImage(), color);

                    } else {
                        // If the color is 255 (Original) the image needs to be reloaded, because it's not possible to reverse the color adjustment.
                        String imageFilename = loadedSprite.getImageFile();
                        if(!tfImage.getText().isBlank()){
                            imageFilename = tfImage.getText();
                        }

                        try{
                            File file = PK2FileSystem.findAsset(imageFilename, PK2FileSystem.SPRITES_DIR);

                            spriteEditModel.getSprite().setImageFile(imageFilename);
                            this.loadImage(file);

                        } catch(IOException ex){
                            Logger.warn(ex);
                        }
                    }
                    
                    spriteSheetPanel.repaint();
                }
            }
        });
        
        //spFrameX.addChangeListener(l -> spriteSheetPanel.getModel().setFramePosition((int) spFrameX.getValue(), (int) spFrameY.getValue()));
        //spFrameY.addChangeListener(l -> spriteSheetPanel.getModel().setFramePosition((int) spFrameX.getValue(), (int) spFrameY.getValue()));
        //spFrameWidth.addChangeListener(l -> spriteSheetPanel.getModel().setFrameDimension((int) spFrameWidth.getValue(), (int) spFrameHeight.getValue()));
        //spFrameHeight.addChangeListener(l -> spriteSheetPanel.getModel().setFrameDimension((int) spFrameWidth.getValue(), (int) spFrameHeight.getValue()));
        
        spFrameAmount.addChangeListener(l -> {
            spriteSheetPanel.getModel().setFramesAmount((int) spFrameAmount.getValue());
        });
    }
    
    @Override
    public void setSprite(PK2Sprite sprite) {
        loadedSprite = sprite;
        
        tfImage.setText(sprite.getImageFile());
        
        spriteSheetPanel.setImage(sprite.getImage());
        
        spFrameX.getModel().setValue(sprite.getFrameX());
        spFrameY.getModel().setValue(sprite.getFrameY());
        spFrameWidth.getModel().setValue(sprite.getFrameWidth());
        spFrameHeight.getModel().setValue(sprite.getFrameHeight());
        spFrameAmount.getModel().setValue(sprite.getFramesAmount());

        cbColors.setSelectedItem(this.profile.getColorMap().get(sprite.getColor()));
        
        spriteSheetPanel.setFrameData(sprite.getFrameX(), sprite.getFrameY(), sprite.getFrameWidth(), sprite.getFrameHeight());
        
        //colorPalettePanelPopup.setColorPalette((IndexColorModel) sprite.getImage().getColorModel()); // I think this is unnecessary, because all sprite files use the background images palette.
        pnlBorderColor.setBackground(spriteSheetPanel.getBorderColor());
        
        spriteSheetPanel.getModel().setFramesAmount(sprite.getFramesAmount());
    }
    
    @Override
    public void resetValues() {
        pnlBorderColor.setBackground(null);
        
        spFrameX.setValue(0);
        spFrameY.setValue(0);
        
        spFrameWidth.setValue(0);
        spFrameHeight.setValue(0);
        
        spFrameAmount.setValue(0);

        if (cbColors.getItemCount() > 0) cbColors.setSelectedIndex(0);
        
        tfImage.setText("");
        
        spriteSheetPanel.setImage(null);
    }
    
    @Override
    public void setValues(PK2Sprite sprite) {
        sprite.setImageFile(tfImage.getText());
        
        sprite.setFrameX((int) spFrameX.getValue());
        sprite.setFrameY((int) spFrameY.getValue());
        
        sprite.setFrameWidth((int) spFrameWidth.getValue());
        sprite.setFrameHeight((int) spFrameHeight.getValue());
        
        sprite.setFramesAmount((int) spFrameAmount.getValue());
        
        int color = 0;
        for (var c : this.profile.getColorMap().entrySet()) {
            if (c.getValue().equals(cbColors.getSelectedItem())) {
                color = c.getKey();
                
                break;
            }
        }
        
        sprite.setColor(color);
    }
    
    @Override
    public void setProfileData(SpriteProfile profile) {
        this.profile = profile;

        cbColors.removeAllItems();
        var cm = new DefaultComboBoxModel<String>();
        
        for (var col : profile.getColorMap().entrySet()) {
            cm.addElement(col.getValue());
        }
        
        cbColors.setModel(cm);
    }
    
    @Override
    public void setUnsavedChangesListener(UnsavedChangesListener listener) {
        tfImage.getDocument().addDocumentListener(listener);
        
        spFrameX.addChangeListener(listener);
        spFrameY.addChangeListener(listener);
        spFrameWidth.addChangeListener(listener);
        spFrameHeight.addChangeListener(listener);
        spFrameAmount.addChangeListener(listener);

        cbColors.addActionListener(listener);
    }
    
    private void updateLastSelectedButton() {
        if (lastSelectedButton != null) lastSelectedButton.setSelected(false);
    }
    
    private void updateSpriteFrames() {
        var spr = spriteEditModel.getSprite();
        spriteEditModel.setSpriteFrames(GFXUtils.cutFrames(spr.getImage(), (int) spFrameAmount.getValue(), (int) spFrameX.getValue(), (int) spFrameY.getValue(), (int) spFrameWidth.getValue(), (int) spFrameHeight.getValue()));
    }
    
    private void loadImage(File file) {
        BufferedImage img = null;
        
        try {
            img = ImageIO.read(file);
            
            //colorPalettePanelPopup.setColorPalette((IndexColorModel) img.getColorModel());
            
            img = GFXUtils.makeTransparent(img);
            
            spriteEditModel.setSpriteImage(img);
            
            spriteSheetPanel.setImage(img);
            
            pnlBorderColor.setBackground(spriteSheetPanel.getBorderColor());
        } catch (IOException ex) {
            Logger.warn(ex);
        }
    }
    
    // It'd be better to create multiple change events/listeners, like FrameDataChangeListener, ModeListener, but I'm to lazy do change it now.
    @Override
    public void stateChanged(ChangeEvent e) {
        var frame = spriteSheetPanel.getModel().getCurrentFrameData();
        
        spFrameX.setValue(frame.x);
        spFrameY.setValue(frame.y);
        spFrameWidth.setValue(frame.width);
        spFrameHeight.setValue(frame.height);
        
        if ((int) spFrameAmount.getValue() != spriteSheetPanel.getModel().getFramesAmount()) {
            spFrameAmount.setValue(spriteSheetPanel.getModel().getFramesAmount());
        }
        
        if (!pnlBorderColor.getBackground().equals(spriteSheetPanel.getModel().getSeparatorColor())) {
            pnlBorderColor.setBackground(spriteSheetPanel.getModel().getSeparatorColor());
        }

        handleModeChange();
    }
    
    private void handleModeChange() {
        if (lastSelectedButton != null) lastSelectedButton.setSelected(false);
        
        // Kinda hacky workaround for something I don't want to deal with right now lol
        spriteSheetPanel.setCursor(Cursor.getDefaultCursor());
    }
}
