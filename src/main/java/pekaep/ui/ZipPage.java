package pekaep.ui;

import javax.swing.JLabel;
import javax.swing.JPanel;

import net.miginfocom.swing.MigLayout;

public class ZipPage extends JPanel {

    public ZipPage(){
        this.setLayout(new MigLayout());

        JLabel header = new JLabel("<html> <span style=\"font-weight:bold; color:green; font-size: 25px\"> Everything is alright! </span> </html>");
        this.add(header, "cell 0 0");
        
        JLabel info = new JLabel("All dependencies satisfied!");
        JLabel info2 = new JLabel("Press finish to export the ZIP file!");

        this.add(info, "cell 0 1");
        this.add(info2, "cell 0 2");
    }
    
}
