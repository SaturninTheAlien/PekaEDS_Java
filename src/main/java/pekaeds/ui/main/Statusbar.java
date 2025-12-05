package pekaeds.ui.main;

import net.miginfocom.swing.MigLayout;
import pekaeds.tool.Tool;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

public class Statusbar extends JPanel implements ChangeListener {
    private JLabel lblMouseXVal;
    private JLabel lblMouseYVal;

    private JLabel lblMouseTileXVal;
    private JLabel lblMouseTileYVal;
    
    private JLabel lblForegroundTileVal;
    private JLabel lblBackgroundTileVal;
    private JLabel lblSpriteVal;
    private JLabel lblSpriteFilename;
    
    //private JLabel lblSpritesPlacedVal;
    
    private JLabel lblLastSavedVal;
    
    //private PekaEDSGUI eds;
    
    public Statusbar(PekaEDSGUI edsUI) {
        //this.eds = edsUI;
        
        setupUI();
    }
    
    private void setupUI() {
        var lblMouseX = new JLabel("X:");
        lblMouseXVal = new JLabel("0");
        
        var lblMouseY = new JLabel("Y:");
        lblMouseYVal = new JLabel("0");

        var lblMouseTileX = new JLabel("TileX:");
        var lblMouseTileY = new JLabel("TileY:");

        this.lblMouseTileXVal = new JLabel("0");
        this.lblMouseTileYVal = new JLabel("0");

        
        var lblForegroundTile = new JLabel("Foreground:");
        lblForegroundTileVal = new JLabel("255");
        
        var lblBackgroundTile = new JLabel("Background:");
        lblBackgroundTileVal = new JLabel("255");
        
        var lblSprite = new JLabel("Sprite:");
        lblSpriteVal = new JLabel("255");
        
        lblSpriteFilename = new JLabel("(none)");
        
        /*var lblSpritesPlaced = new JLabel("Sprites placed: ");
        lblSpritesPlacedVal = new JLabel("0");*/
        
        var lblLastSaved = new JLabel("Last saved:");
        lblLastSavedVal = new JLabel("Not yet");

        setLayout(new MigLayout());
        //add(lblSpritesPlaced);
        //add(lblSpritesPlacedVal);
        
        add(new JSeparator());
        add(lblMouseX);
        add(lblMouseXVal);
        add(lblMouseY);
        add(lblMouseYVal);

        add(new JSeparator());
        add(lblMouseTileX);
        add(lblMouseTileXVal);

        add(lblMouseTileY);
        add(lblMouseTileYVal);
        
        add(new JSeparator());
        add(lblForegroundTile);
        add(lblForegroundTileVal);
        add(lblBackgroundTile);
        add(lblBackgroundTileVal);
        
        add(new JSeparator());
        add(lblSprite);
        add(lblSpriteVal);
        add(lblSpriteFilename);


        
        add(new JSeparator());
        add(new JPanel(), "width 100%");
        add(lblLastSaved);
        add(lblLastSavedVal);
        setPreferredSize(new Dimension(getWidth(), 30));
    }
    
    @Override
    public void stateChanged(ChangeEvent e) {
        lblMouseXVal.setText(Integer.toString(Tool.getToolInformation().getX()));
        lblMouseYVal.setText(Integer.toString(Tool.getToolInformation().getY()));

        this.lblMouseTileXVal.setText(Integer.toString(Tool.getToolInformation().getTileX()));
        this.lblMouseTileYVal.setText(Integer.toString(Tool.getToolInformation().getTileY()));
        
        lblForegroundTileVal.setText(Integer.toString(Tool.getToolInformation().getForegroundTile()));
        lblBackgroundTileVal.setText(Integer.toString(Tool.getToolInformation().getBackgroundTile()));
        
        lblSpriteVal.setText(Integer.toString(Tool.getToolInformation().getSpriteId()));
        lblSpriteFilename.setText("(" + Tool.getToolInformation().getSpriteFilename() + ")");
    }
    
    public void setLastChangedTime(LocalTime time) {
        var dateFormatter = DateTimeFormatter.ofPattern("HH:mm:ss"); // TODO use users time format?
        
        lblLastSavedVal.setText(time.format(dateFormatter));
    }
}
