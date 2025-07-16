package pekaep.ui;

import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;

import net.miginfocom.swing.MigLayout;

public class ZipPage extends JPanel {

    private JCheckBox cbIgnoreVanillaAssets;

    public ZipPage(){
        this.setLayout(new MigLayout());

        JLabel header = new JLabel("<html> <span style=\"font-weight:bold; color:green; font-size: 25px\"> Everything is alright! </span> </html>");
        this.add(header, "cell 0 0");
        
        JLabel info = new JLabel("All dependencies satisfied!");
        JLabel info2 = new JLabel("Press finish to export the ZIP file!");

        this.add(info, "cell 0 1");
        this.add(info2, "cell 0 2");

        this.cbIgnoreVanillaAssets = new JCheckBox("Skip vanilla assets (recommended!)");
        this.cbIgnoreVanillaAssets.setSelected(true);
        
        this.add(this.cbIgnoreVanillaAssets, "cell 0 3");
    }

    public boolean shouldIgnoreVanillaAssets(){
        return this.cbIgnoreVanillaAssets.isSelected();
    }    
}
