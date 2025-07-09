package pekase3.panels.propertiespanel;

import net.miginfocom.swing.MigLayout;
import pekase3.listener.UnsavedChangesListener;
import pekase3.panels.PekaSE2Panel;
import pekase3.settings.Settings;
import pk2.filesystem.PK2FileSystem;
import pk2.profile.SpriteProfile;
import pk2.sprite.PK2Sprite;
import pk2.sprite.io.SpriteIO;
import pk2.sprite.io.UnsupportedSpriteFormatException;
import pk2.ui.SpriteFileChooser;
import pk2.util.GFXUtils;

import org.tinylog.Logger;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Map;
import java.util.Set;

public final class PropertiesPanel extends PekaSE2Panel {
    
    private JTextField tfName;
    private JSpinner hitboxWidth;
    private JSpinner hitboxHeight;
    
    private JComboBox<String> cbType;
    
    private JSpinner spWeight;
    private JSpinner spEnergy;
    private JSpinner spScore;
    private JSpinner spMaxSpeed;
    private JSpinner spMaxJump;
    
    private JCheckBox cbEnemy;
    //private JCheckBox cbBoss;
    private JCheckBox cbIndestructible;
    private JCheckBox cbKey;
    
    private JCheckBox cbObstacle;
    private JCheckBox cbWallUp;
    private JCheckBox cbWallDown;
    private JCheckBox cbWallLeft;
    private JCheckBox cbWallRight;
    private JCheckBox cbTileCheck;
    
    private JCheckBox cbShakes;
    private JCheckBox cbSwim;
    private JCheckBox cbGlide;
    
    private JCheckBox cbAlwaysBonus;
    private JTextField tfBonusSprite;
    private JSpinner spBonuses;
    private JButton btnBrowseBonus;
    
    private JSpinner spParallaxFactor;
   
    private JTextField tfTransformSprite;
    private JButton btnBrowseTransform;
    
    private JComboBox<String> cbDestruction;
    private JComboBox<String> cbDestructionEffect;
    
    private JComboBox<String> cbImmunity;
    
    private Settings settings;
    
    private SpriteFileChooser fileChooser;
    
    private final SpritePreview bonusSpritePreview;
    
    public PropertiesPanel(Settings settings) {
        this.settings = settings;
        
        fileChooser = new SpriteFileChooser();
        
        bonusSpritePreview = new SpritePreview(null, Map.of("Score", ""));
    
        bonusSpritePreview.setMaxSize(128, 128);
        
        setup();
    }
    
    private void setup() {
        tfName = new JTextField();
        
        hitboxWidth = new JSpinner();
        hitboxHeight = new JSpinner();
        
        cbType = new JComboBox<>();
        
        spWeight = new JSpinner(new SpinnerNumberModel(0, 0.0, 100000.0, 1.0));
        
        spEnergy = new JSpinner(new SpinnerNumberModel());
        spScore = new JSpinner(new SpinnerNumberModel());
        spMaxSpeed = new JSpinner(new SpinnerNumberModel(0, 0.0, 100000.0, 1.0));
        spMaxJump = new JSpinner(new SpinnerNumberModel());
        
        cbEnemy = new JCheckBox("Enemy");
        //cbBoss = new JCheckBox("Boss");
        cbIndestructible = new JCheckBox("Indestructible");
        cbKey = new JCheckBox("Key");
        
        cbObstacle = new JCheckBox("Obstacle");
        cbWallUp = new JCheckBox("Wall up");
        cbWallDown = new JCheckBox("Wall down");
        cbWallLeft = new JCheckBox("Wall left");
        cbWallRight = new JCheckBox("Wall right");
        cbTileCheck = new JCheckBox("Tile check");
        
        cbShakes = new JCheckBox("Shakes");
        cbSwim = new JCheckBox("Swim");
        cbGlide = new JCheckBox("Glide");
        
        cbAlwaysBonus = new JCheckBox("Always drop bonus");
        tfBonusSprite = new JTextField();
        spBonuses = new JSpinner();
        btnBrowseBonus = new JButton("Browse");
        
        spParallaxFactor = new JSpinner(new SpinnerNumberModel());
        
        tfTransformSprite = new JTextField();
        btnBrowseTransform = new JButton("Browse");
        
        cbDestruction = new JComboBox<>();
        cbDestructionEffect = new JComboBox<>();
        
        cbImmunity = new JComboBox<>();
        
        generateLayout();
        
        addListeners();
    }
    
    private void addListeners() {
        btnBrowseBonus.addActionListener(e -> {
            if (fileChooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
                fileChooser.setDialogTitle("Select a bonus sprite...");
                tfBonusSprite.setText(fileChooser.getSelectedFile().getName());
                
                updateBonusSpritePreview(tfBonusSprite.getText());
            }
        });
        
        btnBrowseTransform.addActionListener(e -> {
            if (fileChooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
                fileChooser.setDialogTitle("Set a transformation sprite...");
                tfTransformSprite.setText(fileChooser.getSelectedFile().getName());
            }
        });
    }
    
    private void generateLayout() {
        setLayout(new MigLayout("flowy, align center", "fill"));
        
        add(generateMiscPanel());
        add(generateValuesPanel());
        add(generatePropertiesPanel());
        add(generateAbilitiesPanel());
        add(generateCollisionPanel());
        add(generateDestructionPanel());
        add(generateTransformPanel());
        add(generateBonusPanel());
    }
    
    private JPanel generateMiscPanel() {
        var pnl = createTitledPanel("Misc:");
        
        var pnlName = new JPanel();
        pnlName.setLayout(new MigLayout("flowx"));
        pnlName.add(new JLabel("Editor Name:"));
        pnlName.add(tfName, "width 150px, wmax 150px");
        
        var pnlSpinners = new JPanel();
        pnlSpinners.setLayout(new MigLayout("", "[fill]"));
        pnlSpinners.add(new JLabel("Hitbox Width:"), "cell 0 0");
        pnlSpinners.add(hitboxWidth, "cell 1 0");
        pnlSpinners.add(new JLabel("Hitbox Height:"), "cell 0 1");
        pnlSpinners.add(hitboxHeight, "cell 1 1");
        
        pnlSpinners.add(new JLabel("Type:"), "cell 2 0");
        pnlSpinners.add(cbType, "cell 3 0");
        
        pnlSpinners.add(new JLabel("Immunity:"), "cell 2 1");
        pnlSpinners.add(cbImmunity, "cell 3 1");
        
        pnl.add(pnlName, "dock north");
        pnl.add(pnlSpinners, "dock center");
        
        return pnl;
    }
    
    private JPanel generateValuesPanel() {
        var pnl = createTitledPanel("Values:");
        pnl.setLayout(new MigLayout("", "[fill|fill]"));
        
        pnl.add(new JLabel("Weight:"));
        pnl.add(spWeight);
        
        pnl.add(new JLabel("Energy:"));
        pnl.add(spEnergy, "wrap");
        
        pnl.add(new JLabel("Score:"));
        pnl.add(spScore);
        
        pnl.add(new JLabel("Max Speed:"));
        pnl.add(spMaxSpeed, "wrap");
        
        pnl.add(new JLabel("Max jump:"));
        pnl.add(spMaxJump);
        
        pnl.add(new JLabel("Parallax Factor:"));
        pnl.add(spParallaxFactor);
        
        return pnl;
    }
    
    private JPanel generatePropertiesPanel() {
        var pnl = createTitledPanel("Properties:");
        
        pnl.add(cbEnemy);
        pnl.add(cbIndestructible);
        pnl.add(cbKey);
        
        return pnl;
    }
    
    private JPanel generateAbilitiesPanel() {
        var pnl = createTitledPanel("Abilities:");
        
        pnl.add(cbShakes);
        pnl.add(cbSwim);
        pnl.add(cbGlide);
        
        return pnl;
    }
    
    private JPanel generateCollisionPanel() {
        var pnl = createTitledPanel("Collision:");
        
        pnl.add(cbObstacle);
        pnl.add(cbTileCheck, "wrap");
        
        pnl.add(cbWallUp);
        pnl.add(cbWallDown, "wrap");
        pnl.add(cbWallLeft);
        pnl.add(cbWallRight);
        
        return pnl;
    }
    
    private JPanel generateBonusPanel() {
        var pnl = createTitledPanel("Bonus:");
        var pnlBonus = new JPanel();
        pnlBonus.setLayout(new MigLayout());
        
        pnlBonus.add(cbAlwaysBonus);
        pnlBonus.add(new JLabel("Bonus Amount:"));
        pnlBonus.add(spBonuses, "wrap");
        pnlBonus.add(new JLabel("Bonus Sprite:"), "wrap");
        pnlBonus.add(tfBonusSprite, "width 200px");
        pnlBonus.add(btnBrowseBonus);
        
        pnl.add(pnlBonus, "dock center");
        pnl.add(bonusSpritePreview, "dock east");
        
        return pnl;
    }
    
    private JPanel generateTransformPanel() {
        var pnl = createTitledPanel("Transformation Sprite:");
        pnl.setLayout(new MigLayout("", "[fill]", "[fill]"));
        
        pnl.add(tfTransformSprite, "pushx");
        pnl.add(btnBrowseTransform);
        
        return pnl;
    }
    
    private JPanel generateDestructionPanel() {
        var pnl = createTitledPanel("Destruction:");
        
        pnl.add(cbDestruction);
        pnl.add(cbDestructionEffect);
        
        return pnl;
    }
    
    private JPanel createTitledPanel(String title) {
        var pnl = new JPanel();
        pnl.setBorder(BorderFactory.createTitledBorder(title));
        pnl.setLayout(new MigLayout());
        
        return pnl;
    }
    
    @Override
    public void setSprite(PK2Sprite sprite) {
        tfName.setText(sprite.getName());
    
        hitboxWidth.setValue(sprite.getWidth());
        hitboxHeight.setValue(sprite.getHeight());
        
        cbType.setSelectedItem(settings.getSpriteProfile().getTypeMap().get(sprite.getType()));
        
        spWeight.setValue(sprite.getWeight());
        spEnergy.setValue(sprite.getEnergy());
        spScore.setValue(sprite.getScore());
        spMaxSpeed.setValue(sprite.getMaxSpeed());
        spMaxJump.setValue(sprite.getMaxJump());
        
        cbEnemy.setSelected(sprite.isEnemy());
        cbIndestructible.setSelected(sprite.isIndestructible());
        cbKey.setSelected(sprite.isKey());
        
        cbObstacle.setSelected(sprite.isObstacle());
        cbWallUp.setSelected(sprite.isWallUp());
        cbWallDown.setSelected(sprite.isWallDown());
        cbWallLeft.setSelected(sprite.isWallLeft());
        cbWallRight.setSelected(sprite.isWallRight());
        cbTileCheck.setSelected(sprite.isTileCheck());
        
        cbShakes.setSelected(sprite.isShakes());
        cbSwim.setSelected(sprite.canSwim());
        cbGlide.setSelected(sprite.canGlide());
        
        cbAlwaysBonus.setSelected(sprite.isAlwaysBonus());
        tfBonusSprite.setText(sprite.getBonusSpriteFile());
        spBonuses.setValue(sprite.getBonusAmount());
        
        if (!sprite.getBonusSpriteFile().isBlank()) {
            updateBonusSpritePreview(sprite.getBonusSpriteFile());
        } else {
            bonusSpritePreview.setImage(null);
            bonusSpritePreview.setProperty("Score", "");
        }
        
        spParallaxFactor.setValue(sprite.getParallaxFactor());
        
        tfTransformSprite.setText(sprite.getTransformationSpriteFile());
        
        int destructionType = sprite.getDestruction() >= 100 ? 100 : 0;
        cbDestruction.setSelectedItem(settings.getSpriteProfile().getDestructionType().get(destructionType));
        
        int destructionEffect = sprite.getDestruction() >= 100 ? sprite.getDestruction() - 100 : sprite.getDestruction();
        cbDestructionEffect.setSelectedItem(settings.getSpriteProfile().getDestructionEffects().get(destructionEffect));
        
        cbImmunity.setSelectedItem(settings.getSpriteProfile().getImmunityMap().get(sprite.getImmunityToDamageType()));
    }
    
    private void updateBonusSpritePreview(String file) {

        PK2Sprite bonusSprite = null;
        try {

            bonusSprite = SpriteIO.loadSpriteFile(PK2FileSystem.findSprite(file));           
            bonusSpritePreview.setProperty("Score", bonusSprite.getScore());            
        }
        catch(FileNotFoundException e){
            JOptionPane.showMessageDialog(this,"Bonus sprite \""+ file + "\" not found!" , "Sprite not found!", ERROR);
        }

        catch(IOException e){
            JOptionPane.showMessageDialog(this,"Unable to load \""+ file + "\"!" , "Sprite loading error!", ERROR);
            Logger.error(e);
        }

        catch (Exception e) {
            Logger.warn(e, "Unable to load bonus sprite file '" + file +"'!\n");
            e.printStackTrace();
        }


        if(bonusSprite!=null){
            try{
                GFXUtils.loadFirstFrame(bonusSprite);
                bonusSpritePreview.setImage(bonusSprite.getImage());

            }
            catch(IOException e){
                JOptionPane.showMessageDialog(this,"Unable to load \""+ bonusSprite.getImageFile() + "\"!" , "Unable to load sprite image!", ERROR);
                Logger.error(e);
            }
        }
    }
    
    @Override
    public void resetValues() {
        tfName.setText("");
        hitboxWidth.setValue(0);
        hitboxHeight.setValue(0);
        
        if (cbType.getItemCount() > 0) cbType.setSelectedIndex(0);
        
        spWeight.setValue(0);
        spEnergy.setValue(0);
        spScore.setValue(0);
        spMaxSpeed.setValue(0);
        spMaxJump.setValue(0);
        
        cbEnemy.setSelected(false);
        cbIndestructible.setSelected(false);
        cbKey.setSelected(false);
        
        cbObstacle.setSelected(false);
        cbWallUp.setSelected(false);
        cbWallDown.setSelected(false);
        cbWallLeft.setSelected(false);
        cbWallRight.setSelected(false);
        cbTileCheck.setSelected(false);
        cbShakes.setSelected(false);
        cbSwim.setSelected(false);
        cbGlide.setSelected(false);
        cbAlwaysBonus.setSelected(false);
    
        tfBonusSprite.setText("");
        spBonuses.setValue(0);
        
        spParallaxFactor.setValue(0);
        
        tfTransformSprite.setText("");
        
        if (cbDestruction.getItemCount() > 0) cbDestruction.setSelectedIndex(0);
        if (cbDestructionEffect.getItemCount() > 0) cbDestructionEffect.setSelectedIndex(0);
        if (cbImmunity.getItemCount() > 0) cbImmunity.setSelectedIndex(0);
        
        bonusSpritePreview.reset();
    }
    
    @Override
    public void setValues(PK2Sprite sprite) {
        sprite.setName(tfName.getText());
        
        sprite.setWidth((int) hitboxWidth.getValue());
        sprite.setHeight((int) hitboxHeight.getValue());
        
        int type = 0;
        for (var t : settings.getSpriteProfile().getTypeMap().entrySet()) {
            if (t.getValue().equals(cbType.getSelectedItem())) {
                type = t.getKey();
                
                break;
            }
        }

        sprite.setType(type);
        
        sprite.setWeight((double) spWeight.getValue());
        sprite.setEnergy((int) spEnergy.getValue());
        sprite.setScore((int) spScore.getValue());
        sprite.setMaxJump((int) spMaxJump.getValue());
        sprite.setMaxSpeed((double) spMaxSpeed.getValue());
        
        sprite.setEnemy(cbEnemy.isSelected());
        sprite.setIndestructible(cbIndestructible.isSelected());
        sprite.setKey(cbKey.isSelected());
        
        sprite.setObstacle(cbObstacle.isSelected());
        
        sprite.setWallUp(cbWallUp.isSelected());
        sprite.setWallDown(cbWallDown.isSelected());
        sprite.setWallRight(cbWallRight.isSelected());
        sprite.setWallLeft(cbWallLeft.isSelected());
        
        sprite.setTileCheck(cbTileCheck.isSelected());
        
        sprite.setShakes(cbShakes.isSelected());
        sprite.setSwim(cbSwim.isSelected());
        sprite.setGlide(cbGlide.isSelected());
        
        sprite.setAlwaysBonus(cbAlwaysBonus.isSelected());
        sprite.setBonusSpriteFile(tfBonusSprite.getText());
        sprite.setBonusAmount((int) spBonuses.getValue());
        
        sprite.setParallaxFactor((int) spParallaxFactor.getValue());
        
        sprite.setTransformationSpriteFile(tfTransformSprite.getText());
        
        int destruction = 0;
        for (var d : settings.getSpriteProfile().getDestructionType().entrySet()) {
            if (d.getValue().equals(cbDestruction.getSelectedItem())) {
                destruction = d.getKey();
                
                break;
            }
        }
        
        for (var d : settings.getSpriteProfile().getDestructionEffects().entrySet()) {
            if (d.getValue().equals(cbDestructionEffect.getSelectedItem())) {
                destruction += d.getKey();
                
                break;
            }
        }
        
        sprite.setDestruction(destruction);
        
        int immunity = 0;
        for (var d : settings.getSpriteProfile().getImmunityMap().entrySet()) {
            if (d.getValue().equals(cbImmunity.getSelectedItem())) {
                immunity = d.getKey();
                
                break;
            }
        }
        
        sprite.setImmunityToDamageType(immunity);
    }
    
    @Override
    public void setProfileData(SpriteProfile profile) {
        replaceComboBoxItems(cbDestruction, profile.getDestructionType().entrySet());
        replaceComboBoxItems(cbDestructionEffect, profile.getDestructionEffects().entrySet());
        replaceComboBoxItems(cbImmunity, profile.getImmunityMap().entrySet());
        replaceComboBoxItems(cbType, profile.getTypeMap().entrySet());
    }
    
    private void replaceComboBoxItems(JComboBox<String> comboBox, Set<Map.Entry<Integer, String>> entrySet) {
        comboBox.removeAllItems();
        
        for (var s : entrySet) {
            comboBox.addItem(s.getValue());
        }
    }
    
    @Override
    public void setUnsavedChangesListener(UnsavedChangesListener listener) {
        tfName.getDocument().addDocumentListener(listener);
        hitboxWidth.addChangeListener(listener);
        hitboxHeight.addChangeListener(listener);
        
        cbType.addActionListener(listener);
        
        spWeight.addChangeListener(listener);
        spEnergy.addChangeListener(listener);
        spScore.addChangeListener(listener);
        spMaxSpeed.addChangeListener(listener);
        spMaxJump.addChangeListener(listener);
        
        cbEnemy.addActionListener(listener);
        cbIndestructible.addActionListener(listener);
        cbKey.addActionListener(listener);
        
        cbObstacle.addActionListener(listener);
        cbWallUp.addActionListener(listener);
        cbWallDown.addActionListener(listener);
        cbWallLeft.addActionListener(listener);
        cbWallRight.addActionListener(listener);
        cbTileCheck.addActionListener(listener);
        cbShakes.addActionListener(listener);
        cbSwim.addActionListener(listener);
        cbGlide.addActionListener(listener);
        cbAlwaysBonus.addActionListener(listener);
        
        tfBonusSprite.getDocument().addDocumentListener(listener);
        spBonuses.addChangeListener(listener);
        
        spParallaxFactor.addChangeListener(listener);
        
        tfTransformSprite.getDocument().addDocumentListener(listener);
        
        cbDestruction.addActionListener(listener);
        cbDestructionEffect.addActionListener(listener);
        cbImmunity.addActionListener(listener);
    }
}
