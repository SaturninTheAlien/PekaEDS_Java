package pekase3.panels.greta;

import net.miginfocom.swing.MigLayout;
import pekase3.listener.UnsavedChangesListener;
import pekase3.panels.PekaSE2Panel;
import pekase3.util.Point2dInputOptional;
import pk2.profile.SpriteProfile;
import pk2.sprite.PK2Sprite;

import javax.swing.*;

public class GretaPropertiesPanel extends PekaSE2Panel {
    private JCheckBox chkAlwaysActive;
    
    private JSpinner spDeadWeight;
    private JCheckBox chkUseDeadWeight;

    private JLabel lInfoId;
    private JSpinner spInfoID;

    private JComboBox<String> cbBlendMode;
    private JSpinner spBlendAlpha;


    private JComboBox<String> cbAmbientEffect;

    private SpriteProfile profile;


    private Point2dInputOptional pAttack1Offset;
    private Point2dInputOptional pAttack2Offset;
    
    public GretaPropertiesPanel() {
        setup();
    }
    
    public void setup() {
        chkAlwaysActive = new JCheckBox("Always active");
        spDeadWeight = new JSpinner(new SpinnerNumberModel(0.0, -5.0, 5.0, 0.1));
        spDeadWeight.setEnabled(false);
        
        chkUseDeadWeight = new JCheckBox("Dead Weight:");
        chkUseDeadWeight.addActionListener(e -> {
            spDeadWeight.setEnabled(chkUseDeadWeight.isSelected());
        });

        lInfoId = new JLabel("Info:");
        spInfoID = new JSpinner();

        cbBlendMode = new JComboBox<>();
        spBlendAlpha = new JSpinner();
        cbAmbientEffect = new JComboBox<>();

        pAttack1Offset = new Point2dInputOptional("Attack 1");
        pAttack2Offset = new Point2dInputOptional("Attack 2");
        
        generateLayout();
    }
    
    private void generateLayout() {

        JPanel panel1 = new JPanel();

        panel1.setLayout(new MigLayout("flowy"));
        panel1.setBorder(BorderFactory.createTitledBorder("Greta Properties"));
        panel1.add(chkAlwaysActive);
        panel1.add(chkUseDeadWeight);
        panel1.add(spDeadWeight);
        panel1.add(lInfoId);
        panel1.add(spInfoID);

        JPanel panel2 = new JPanel();
        panel2.setLayout(new MigLayout("flowy"));
        panel2.setBorder(BorderFactory.createTitledBorder("Advanced Graphics"));

        panel2.add(new JLabel("Blend mode:"));
        panel2.add(cbBlendMode);
        panel2.add(new JLabel("Blend alpha:"));
        panel2.add(spBlendAlpha);
        panel2.add(new JLabel("Ambient effect:"));
        panel2.add(cbAmbientEffect);

        JPanel panel3 = new JPanel();
        panel3.setBorder(BorderFactory.createTitledBorder("Attack Offsets"));
        panel3.setLayout(new MigLayout("flowy"));
        panel3.add(pAttack1Offset);
        panel3.add(pAttack2Offset);

        this.setLayout(new MigLayout());

        this.add(panel1);
        this.add(panel2);
        this.add(panel3);
    }
    
    @Override
    public void setSprite(PK2Sprite sprite) {
        chkAlwaysActive.setSelected(sprite.isAlwaysActive());
        
        chkUseDeadWeight.setSelected(sprite.hasDeadWeight());
        spDeadWeight.setEnabled(sprite.hasDeadWeight());
        
        if (sprite.hasDeadWeight()) {
            spDeadWeight.setValue(sprite.getDeadWeight());
        }

        spInfoID.setValue(sprite.getInfoID());
        cbBlendMode.setSelectedItem( profile.getBlendModeMap().get(sprite.getBlendMode()));
        spBlendAlpha.setValue(sprite.getBlendAlpha());
        cbAmbientEffect.setSelectedItem(profile.getAmbientEffects().get(sprite.getAmbientEffect()));

        pAttack1Offset.setValue(sprite.getAttack1Offset());
        pAttack2Offset.setValue(sprite.getAttack2Offset());
    }
    
    @Override
    public void resetValues() {
        chkAlwaysActive.setSelected(false);
        chkUseDeadWeight.setSelected(false);
        spDeadWeight.setEnabled(false);
        spDeadWeight.setValue(0.0);

        spInfoID.setValue(0);
        cbBlendMode.setSelectedIndex(0);
        spBlendAlpha.setValue(50);
        cbAmbientEffect.setSelectedIndex(0);

        pAttack1Offset.setValue(null);
        pAttack2Offset.setValue(null);
    }
    
    @Override
    public void setValues(PK2Sprite sprite) {
        sprite.setAlwaysActive(chkAlwaysActive.isSelected());
        
        boolean hasDeadWeight = chkUseDeadWeight.isSelected();
        sprite.setHasDeadWeight(hasDeadWeight);
        
        if (hasDeadWeight) {
            sprite.setDeadWeight((double) spDeadWeight.getValue());
        }

        sprite.setInfoID((int) spInfoID.getValue());

        int blendMode = 0;
        for (var d : profile.getBlendModeMap().entrySet()) {
            if (d.getValue().equals(this.cbBlendMode.getSelectedItem())) {
                blendMode = d.getKey();
                break;
            }
        }

        sprite.setBlendMode(blendMode);

        sprite.setBlendAlpha((int) spBlendAlpha.getValue());

        int ambientEffect = 0;
        for (var d : profile.getAmbientEffects().entrySet()) {
            if (d.getValue().equals(this.cbAmbientEffect.getSelectedItem())) {
                ambientEffect = d.getKey();
                break;
            }
        }

        sprite.setAmbientEffect(ambientEffect);

        sprite.setAttack1Offset(pAttack1Offset.getValue());
        sprite.setAttack2Offset(pAttack2Offset.getValue());
    }
    
    @Override
    public void setProfileData(SpriteProfile profile) {
        this.profile = profile;
        replaceComboBoxItems(this.cbBlendMode, profile.getBlendModeMap().entrySet());
        replaceComboBoxItems(this.cbAmbientEffect, profile.getAmbientEffects().entrySet());
    }
    
    @Override
    public void setUnsavedChangesListener(UnsavedChangesListener listener) {
        chkAlwaysActive.addActionListener(listener);
        spDeadWeight.addChangeListener(listener);
        spInfoID.addChangeListener(listener);
        cbBlendMode.addActionListener(listener);
        spBlendAlpha.addChangeListener(listener);
        cbAmbientEffect.addActionListener(listener);
        pAttack1Offset.addChangeListener(listener);
        pAttack2Offset.addChangeListener(listener);
    }
}