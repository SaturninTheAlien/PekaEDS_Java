package pekaeds.ui.mapmetadatapanel;

import net.miginfocom.swing.MigLayout;
import pekaeds.ui.listeners.TextFieldChangeListener;
import pekaeds.ui.mapposition.MapPositionDialog;
import pk2.filesystem.PK2FileSystem;
import pk2.level.PK2Level;
import pk2.settings.Settings;
import pk2.sprite.io.SpriteMissing;
import pk2.util.GFXUtils;
import pekaeds.ui.main.PekaEDSGUI;
import pekaeds.ui.listeners.PK2MapConsumer;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.awt.image.RasterFormatException;
import java.text.ParseException;
import java.util.HashMap;
import java.util.Map;

import org.tinylog.Logger;

public class MapMetadataPanel extends JPanel implements PK2MapConsumer, ActionListener, ChangeListener {

    private static final int ICONS_NUMBER = 44;

    private boolean canFireChanges = false;
    private ChangeListener changeListener;
    private ChangeEvent changeEvent = new ChangeEvent(this);
    
    private JTextField tfMapName;
    private JTextField tfAuthor;
    private JTextField tfLua;

    private JSpinner spLevelNumber;
    private JSpinner spTime;
    
    private JComboBox<String> cbIcons;
    private JComboBox<String> cbGameMode;

    private JSpinner spMapPosX;
    private JSpinner spMapPosY;
    
    private JButton btnPositionMap;
    
    private Map<Object, BufferedImage> iconMap = new HashMap<>();
    
    private MapPositionDialog mapPositionDialog;
    
    private PK2Level level = null;
    
    public MapMetadataPanel(PekaEDSGUI ui) {
        loadIcons();
        
        mapPositionDialog = new MapPositionDialog();
                
        setupUI();
        setListeners();
    }

    private static String getIconName(int id){
        var iconNames = Settings.getMapProfile().getMapIconNames();
        if(id < iconNames.size()){
            return iconNames.get(id);
        }
        else if(id < 22){
            return ("Custom Icon #"+(id+1));
        }
        else{
            return "PK2stuff2.bmp Custom Icon #"+(id - 21);
        }
    }

    
    // TODO Optimization: Put this in a SwingWorker?
    private void loadIcons() {

        BufferedImage sheet1, sheet2;

        try{
            sheet1 = ImageIO.read( PK2FileSystem.getPK2StuffFile());
        }
        catch(Exception e){
            Logger.warn(e, "Unable to load icon image file: {}", PK2FileSystem.getPK2StuffFile());
            sheet1 = null;
        }

        try{
            sheet2 = ImageIO.read( PK2FileSystem.getPK2Stuff2File());
        }
        catch(Exception e){
            Logger.warn(e, "Unable to load icon image file: {}", PK2FileSystem.getPK2StuffFile());
            sheet2 = null;
        }

        for (int i = 0; i < ICONS_NUMBER; i++) {
            BufferedImage img = null;

            try{
                if(i < 22 && sheet1!=null){
                    img = sheet1.getSubimage(1 + (i * 28), 452, 27, 27);
                }
                else if(sheet2!=null){
                    img = sheet2.getSubimage(1 + ((i - 22) * 28), 452, 27, 27);
                }
            }
            catch(RasterFormatException e){
                Logger.warn(e, "Unable to get subimage for icon: "+i);
            }

            if(img==null){
                img = SpriteMissing.getMissingTextureImage();
            }
            else{
                img = GFXUtils.makeTransparent(img);
            }

            iconMap.put(getIconName(i), img);
        }
    }
    
    private void setupUI() {
        var lblMapName = new JLabel("Name:");
        tfMapName = new JTextField();

        var lblAuthor = new JLabel("Author:");
        tfAuthor = new JTextField();
        
        var lblLevelNr = new JLabel("Level nr.:");
        spLevelNumber = new JSpinner();
    
        var lblTime = new JLabel("Time (sec):");
        spTime = new JSpinner();
            
        var lblIcon = new JLabel("Icon:");
        cbIcons = new JComboBox<>();
    
        var lblMapX = new JLabel("Map X:");
        spMapPosX = new JSpinner();
    
        var lblMapY = new JLabel("Map Y:");
        spMapPosY = new JSpinner();

        JLabel lblLua = new JLabel("Lua🌜 script:");
        this.tfLua = new JTextField();

        JLabel lblGameMode = new JLabel("Game mode:");
        this.cbGameMode = new JComboBox<>();

        for(String s: Settings.getMapProfile().getGameModes()){
            cbGameMode.addItem(s);
        }

        for(int i=0;i<ICONS_NUMBER;++i){
            cbIcons.addItem(getIconName(i));
        }
        
        cbIcons.setRenderer(new MapIconRenderer(iconMap));
        
        btnPositionMap = new JButton("Set position");
        
        // Lay out components
        var p = new JPanel();
        p.setLayout(new MigLayout("wrap 3"));

        p.add(lblMapName);
        p.add(tfMapName, "span 2, width 100px");
        
        p.add(lblAuthor);
        p.add(tfAuthor, "span 2, width 100px");
           
        p.add(new JSeparator(JSeparator.HORIZONTAL), "span 3");
        
        p.add(lblLevelNr);
        p.add(spLevelNumber, "span 2");
        
        p.add(lblTime);
        p.add(spTime, "span 2");

        p.add(new JSeparator(JSeparator.HORIZONTAL), "span 3");
                
        p.add(lblIcon);
        p.add(cbIcons, "span 2, width 100px");

        p.add(new JSeparator(JSeparator.HORIZONTAL), "span 3");

        p.add(lblLua);
        p.add(this.tfLua, "span 2, width 100px");
    
        p.add(new JSeparator(JSeparator.HORIZONTAL), "span 3");

        p.add(lblGameMode);
        p.add(this.cbGameMode, "span 2, width 100px");

        p.add(new JSeparator(JSeparator.HORIZONTAL), "span 3");

        p.add(lblMapX);
        p.add(spMapPosX, "span 2");
    
        p.add(lblMapY);
        p.add(spMapPosY, "span 2");
        
        p.add(btnPositionMap);



        add(p, BorderLayout.CENTER);
    }

    @Override
    public void setMap(PK2Level m) {
        this.level = m;
        
        this.canFireChanges = false;
        
        tfMapName.setText(level.name);
        tfAuthor.setText(level.author);
        tfLua.setText(level.lua_script);

        spLevelNumber.setValue(level.level_number);
        spTime.setValue(level.time);
                
        spMapPosX.setValue(level.icon_x);
        spMapPosY.setValue(level.icon_y);

        cbIcons.setSelectedIndex(level.icon_id);
        cbGameMode.setSelectedIndex(level.game_mode);        
        mapPositionDialog.setMapIcon(iconMap.get(getIconName(level.icon_id)), new Point(level.icon_x, level.icon_y));

        this.canFireChanges = true;
    }
    
    public void commitValues() {
        try {
            spLevelNumber.commitEdit();
            spTime.commitEdit();
            spMapPosX.commitEdit();
            spMapPosY.commitEdit();
            
            level.level_number = (int) spLevelNumber.getValue();
            level.time = (int) spTime.getValue();

            level.icon_id = cbIcons.getSelectedIndex();
            level.icon_x = (int) spMapPosX.getValue();
            level.icon_y = (int) spMapPosY.getValue();

            level.name = tfMapName.getText();
            level.author = tfAuthor.getText();
            level.lua_script = tfLua.getText();

            level.game_mode = cbGameMode.getSelectedIndex();

            level.level_number = (int) spLevelNumber.getValue();
            level.time = (int) spTime.getValue();

        } catch (ParseException e) {
            Logger.error(e);
        }
    }
    
    private void setListeners() {
        cbIcons.addActionListener(this);
        
        mapPositionDialog.setPositionSpinners(spMapPosX, spMapPosY); // Not the best way to this, but it's easy and it works for now.
        
        btnPositionMap.addActionListener(e -> {
            try {
                spMapPosX.commitEdit();
                spMapPosY.commitEdit();
            } catch (ParseException ex) {
                Logger.info("Unable to commit edit of spinners.");
            }
    
            int posX = (int) spMapPosX.getValue();
            int posY = (int) spMapPosY.getValue();

            level.icon_x = posX;
            level.icon_y = posY;
            
            mapPositionDialog.updatePosition(new Point(posX, posY));
            mapPositionDialog.setVisible(true);
            
            fireChanges();
        });
    }
    
    
    private void setChangeListeners() {
        var tfl = new TextFieldChangeListener(this);
        tfMapName.getDocument().addDocumentListener(tfl);
        tfAuthor.getDocument().addDocumentListener(tfl);
        tfLua.getDocument().addDocumentListener(tfl);
  
        spLevelNumber.addChangeListener(this);
        spTime.addChangeListener(this);

        cbIcons.addActionListener(this);
        cbGameMode.addActionListener(this);
    
        spMapPosX.addChangeListener(new MapPositionChangeListener());
        spMapPosY.addChangeListener(new MapPositionChangeListener());
    }

    public void setChangeListener(ChangeListener listener) {
        changeListener = listener;
        setChangeListeners();
    }
    
    private void fireChanges() {
        if (canFireChanges) {
            changeListener.stateChanged(changeEvent);
        }
    }

    private class MapPositionChangeListener implements ChangeListener {
        @Override
        public void stateChanged(ChangeEvent e) {
            if (canFireChanges) {
                level.icon_x = (int) spMapPosX.getValue();
                level.icon_y = (int) spMapPosY.getValue();
                
                mapPositionDialog.updatePosition(new Point(level.icon_x, level.icon_y));
                fireChanges();
            }
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        mapPositionDialog.updateIconImage(iconMap.get(getIconName(cbIcons.getSelectedIndex())));

        // This is needed for the ChangeListener to be triggered, so the editor can ask the user if they want to save unsaved changes
        fireChanges();
    }

    @Override
    public void stateChanged(ChangeEvent e) {
        fireChanges();
    }
}
