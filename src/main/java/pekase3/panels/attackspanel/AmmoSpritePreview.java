package pekase3.panels.attackspanel;

import net.miginfocom.swing.MigLayout;
import pekase3.panels.FrameImagePanel;
import pekase3.settings.Settings;
import pk2.sprite.PK2Sprite;

import javax.swing.*;

import java.awt.*;

public class AmmoSpritePreview extends JPanel {
    private JLabel lblDamageType;
    private JLabel lblDamageAmount;
    private JLabel lblAttackPause;
    
    private FrameImagePanel imagePanel;
    
    private Settings settings;
    
    public AmmoSpritePreview(Settings settings) {
        this.settings = settings;
        
        setup();
    }
    
    private void setup() {
        imagePanel = new FrameImagePanel(128, 128, 128, 128);
        imagePanel.setPreferredSize(new Dimension(128, 128));
        imagePanel.setMinimumSize(new Dimension(128, 128));
        imagePanel.setMaximumSize(new Dimension(128, 128));
        
        lblDamageType = new JLabel("");
        lblDamageAmount = new JLabel("");
        lblAttackPause = new JLabel("");
        
        addComponents();
    }
    
    private void addComponents() {
        setLayout(new MigLayout());
        
        var pnl = new JPanel();
        pnl.setLayout(new MigLayout());
        pnl.add(new JLabel("Damage type: "));
        pnl.add(lblDamageType, "wrap");
        pnl.add(new JLabel("Damage: "));
        pnl.add(lblDamageAmount, "wrap");
        pnl.add(new JLabel("Attack pause: "));
        pnl.add(lblAttackPause);
        
        add(imagePanel, "dock center");
        add(pnl, "dock east");
    }
    
    public void setSprite(PK2Sprite sprite) {
        if (sprite != null) {
            lblDamageType.setText(settings.getSpriteProfile().getDamageMap().get(sprite.getDamageType()));
            lblDamageAmount.setText(Integer.toString(sprite.getDamage()));
            lblAttackPause.setText(Integer.toString(sprite.getAttackPause()));
            
            imagePanel.setImage(sprite.getImage());
        } else {
            lblDamageType.setText("");
            lblDamageAmount.setText("");
            lblAttackPause.setText("");
            
            imagePanel.setImage(null);
        }
    }
}
