package pekaep.ui;

import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;

import net.miginfocom.swing.MigLayout;

public class ZipPage extends JPanel {

    private JCheckBox cbIgnoreVanillaAssets;
    private JCheckBox cbCustomPK2stuff;
    private JCheckBox cbCustomPK2stuff2;

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

        this.cbCustomPK2stuff = new JCheckBox("Custom pk2stuff.png (or pk2stuff.bmp)");
        this.cbCustomPK2stuff2 = new JCheckBox("Custom pk2stuff2.png (or pk2stuff2.bmp)");
        
        this.add(this.cbIgnoreVanillaAssets, "cell 0 3");
        this.add(this.cbCustomPK2stuff, "cell 0 4");
        this.add(this.cbCustomPK2stuff2, "cell 0 5");
    }

    public boolean shouldIgnoreVanillaAssets(){
        return this.cbIgnoreVanillaAssets.isSelected();
    }

    public boolean shouldPackPK2stuff(){
        return this.cbCustomPK2stuff.isSelected();
    }

    public boolean shouldPackPK2stuff2(){
        return this.cbCustomPK2stuff2.isSelected();
    }
}
