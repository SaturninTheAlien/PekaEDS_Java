package pekase3.panels.attackspanel;

import net.miginfocom.swing.MigLayout;
import pekase3.listener.UnsavedChangesListener;
import pekase3.panels.PekaSE2Panel;
import pk2.filesystem.PK2FileSystem;
import pk2.profile.SpriteProfile;
import pk2.settings.Settings;
import pk2.sprite.PK2Sprite;
import pk2.sprite.io.SpriteIO;
import pk2.ui.SpriteFileChooser;
import pk2.util.GFXUtils;

import org.tinylog.Logger;

import javax.swing.*;
import java.io.FileNotFoundException;
import java.io.IOException;

public class AttacksPanel extends PekaSE2Panel {
    private JTextField tfAmmoSprite1;
    private JTextField tfAmmoSprite2;
    
    private AmmoSpritePreview ammoSpritePreview1;
    private AmmoSpritePreview ammoSpritePreview2;
    
    private JButton btnBrowseAmmo1;
    private JButton btnBrowseAmmo2;
    
    
    private JSpinner spAtkDuration1;
    private JSpinner spAtkDuration2;
    
    private JSpinner spDamage;
    private JSpinner spLoadTime;
    private JSpinner spAttackPause;
    
    private JComboBox<String> cbDamageType;
    
    private SpriteFileChooser fileChooser;
    
    public AttacksPanel() {
        setup();
    }
    
    private void setup() {
        ammoSpritePreview1 = new AmmoSpritePreview();
        ammoSpritePreview2 = new AmmoSpritePreview();
        
        tfAmmoSprite1 = new JTextField();
        tfAmmoSprite2 = new JTextField();
        
        btnBrowseAmmo1 = new JButton("Browse");
        btnBrowseAmmo2 = new JButton("Browse");
        
        spAtkDuration1 = new JSpinner();
        spAtkDuration2 = new JSpinner();
        
        spDamage = new JSpinner(new SpinnerNumberModel());
        spLoadTime = new JSpinner(new SpinnerNumberModel());
        spAttackPause = new JSpinner(new SpinnerNumberModel());
        
        cbDamageType = new JComboBox<>();
        
        generateLayout();
        addListeners();
    }
    
    private void addListeners() {
        btnBrowseAmmo1.addActionListener(e -> {
            fileChooser.setDialogTitle("Select Ammo 1 sprite...");
            
            if (fileChooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
                setupAmmoSprite(tfAmmoSprite1, fileChooser.getSelectedFile().getName(), ammoSpritePreview1);
            }
        });
        
        btnBrowseAmmo2.addActionListener(e -> {
            fileChooser.setDialogTitle("Select Ammo 2 sprite...");
            
            if (fileChooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
                setupAmmoSprite(tfAmmoSprite2, fileChooser.getSelectedFile().getName(), ammoSpritePreview2);
            }
        });
    }
    
    private void generateLayout() {
        setLayout(new MigLayout("flowy, align 50% 5%"));
        
        var pnlSide = new JPanel();
        pnlSide.setLayout(new MigLayout("flowy"));
        pnlSide.setBorder(BorderFactory.createTitledBorder("Values:"));
        pnlSide.add(new JLabel("Damage type:"));
        pnlSide.add(cbDamageType);
        pnlSide.add(new JLabel("Attack 1 duration:"));
        pnlSide.add(spAtkDuration1);
        pnlSide.add(new JLabel("Attack 2 duration:"));
        pnlSide.add(spAtkDuration2);
        
        pnlSide.add(new JLabel("Damage:"));
        pnlSide.add(spDamage);
        
        pnlSide.add(new JLabel("Load time:"));
        pnlSide.add(spLoadTime);
        
        pnlSide.add(new JLabel("Attack pause:"));
        pnlSide.add(spAttackPause);
        
        var pnlAmmo1 = new JPanel();
        pnlAmmo1.setBorder(BorderFactory.createTitledBorder("Ammo 1:"));
        pnlAmmo1.setLayout(new MigLayout());
        pnlAmmo1.add(ammoSpritePreview1, "span 1 3");
        pnlAmmo1.add(tfAmmoSprite1, "width 80%");
        pnlAmmo1.add(btnBrowseAmmo1, "wrap");
        
        var pnlAmmo2 = new JPanel();
        pnlAmmo2.setBorder(BorderFactory.createTitledBorder("Ammo 2:"));
        pnlAmmo2.setLayout(new MigLayout());
        pnlAmmo2.add(ammoSpritePreview2, "span 1 3");
        pnlAmmo2.add(tfAmmoSprite2, "width 80%");
        pnlAmmo2.add(btnBrowseAmmo2, "wrap");
        
        add(pnlSide, "dock west");
        add(pnlAmmo1, "growx");
        add(pnlAmmo2);
    }
    
    @Override
    public void setSprite(PK2Sprite sprite) {
        if (!sprite.getAttack1SpriteFile().isBlank()) {
            setupAmmoSprite(tfAmmoSprite1, sprite.getAttack1SpriteFile(), ammoSpritePreview1);
        } else {
            tfAmmoSprite1.setText("");
            ammoSpritePreview1.setSprite(null);
        }
        
        if (!sprite.getAttack2SpriteFile().isBlank()) {
            setupAmmoSprite(tfAmmoSprite2, sprite.getAttack2SpriteFile(), ammoSpritePreview2);
        } else {
            tfAmmoSprite2.setText("");
            ammoSpritePreview2.setSprite(null);
        }
        
        spDamage.setValue(sprite.getDamage());
        spAttackPause.setValue(sprite.getAttackPause());
        
        spAtkDuration1.setValue(sprite.getAttack1Duration());
        spAtkDuration2.setValue(sprite.getAttack2Duration());
        
        spLoadTime.setValue(sprite.getLoadTime());
        
        cbDamageType.setSelectedItem(Settings.getSpriteProfile().getDamageMap().get(sprite.getDamageType()));
    }
    
    private void setupAmmoSprite(JTextField tfPath, String ammoSprite, AmmoSpritePreview preview) {

        tfPath.setText(ammoSprite);

        PK2Sprite spr = null;
        try{
            spr = SpriteIO.loadSpriteFile(PK2FileSystem.findSprite(ammoSprite));
        }
        catch(FileNotFoundException e){
            JOptionPane.showMessageDialog(this,"Ammo sprite \""+ ammoSprite + "\" not found!" , "Sprite not found!", ERROR);
        }

        catch(IOException e){
            JOptionPane.showMessageDialog(this,"Unable to load \""+ ammoSprite + "\"!" , "Sprite loading error!", ERROR);
            Logger.error(e);
        }

        
        if(spr!=null){
            try{
                GFXUtils.loadFirstFrame(spr);
                preview.setSprite(spr);
            }
            catch(IOException e){
                JOptionPane.showMessageDialog(this,"Unable to load \""+ spr.getImageFile() + "\"!" , "Unable to load sprite image!", ERROR);
                Logger.error(e);
            }
        }
    }
    
    @Override
    public void resetValues() {
        tfAmmoSprite1.setText("");
        tfAmmoSprite2.setText("");
        
        spAtkDuration1.setValue(0);
        spAtkDuration2.setValue(0);
        
        spDamage.setValue(0);
        
        spLoadTime.setValue(0);
        
        spAttackPause.setValue(0);
        
        if (cbDamageType.getItemCount() > 0) cbDamageType.setSelectedIndex(0);
        
        ammoSpritePreview1.setSprite(null);
        ammoSpritePreview2.setSprite(null);
    }
    
    @Override
    public void setValues(PK2Sprite sprite) {
        sprite.setAttack1SpriteFile(tfAmmoSprite1.getText());
        sprite.setAttack2SpriteFile(tfAmmoSprite2.getText());
        
        sprite.setAttack1Duration((int) spAtkDuration1.getValue());
        sprite.setAttack2Duration((int) spAtkDuration2.getValue());
        
        sprite.setDamage((int) spDamage.getValue());
        
        sprite.setLoadTime((int) spLoadTime.getValue());
        
        sprite.setAttackPause((int) spAttackPause.getValue());
        
        int damageType = 0;
        for (var d : Settings.getSpriteProfile().getDamageMap().entrySet()) {
            if (d.getValue().equals(cbDamageType.getSelectedItem())) {
                damageType = d.getKey();
                
                break;
            }
        }
        
        sprite.setDamageType(damageType);
    }
    
    @Override
    public void setProfileData(SpriteProfile profile) {
        cbDamageType.removeAllItems();
        
        for (var damageType : profile.getDamageMap().entrySet()) {
            cbDamageType.addItem(damageType.getValue());
        }
    }
    
    @Override
    public void setUnsavedChangesListener(UnsavedChangesListener listener) {
        tfAmmoSprite1.getDocument().addDocumentListener(listener);
        tfAmmoSprite2.getDocument().addDocumentListener(listener);
        
        spAtkDuration1.addChangeListener(listener);
        spAtkDuration2.addChangeListener(listener);
        
        spDamage.addChangeListener(listener);
        spLoadTime.addChangeListener(listener);
        spAttackPause.addChangeListener(listener);
        
        cbDamageType.addActionListener(listener);
    }
}
