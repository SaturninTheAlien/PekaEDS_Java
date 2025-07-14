package pekaep.ui;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import net.miginfocom.swing.MigLayout;
import pk2.filesystem.PK2FileSystem;

public class EpisodePage extends JPanel {

    private JTextField tfEpisodePath;

    public EpisodePage(){
        super();
        this.setLayout(new MigLayout());

        this.tfEpisodePath = new JTextField();
        JButton btnEpisode = new JButton("Browse");
        btnEpisode.addActionListener( e-> {
            JFileChooser fc = new JFileChooser(PK2FileSystem.getAssetsPath(PK2FileSystem.EPISODES_DIR));
            fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
            
            var res = fc.showOpenDialog(null);
            
            if (res == JFileChooser.APPROVE_OPTION) {
                this.tfEpisodePath.setText(fc.getSelectedFile().getPath());
            }
        });

        

        JLabel header = new JLabel("<html> <span style=\"font-weight:bold; font-size: 25px\"> Select episode to pack! </span> </html>");
        this.add(header, "cell 0 0");
        this.add(this.tfEpisodePath, "cell 0 1, width 500px");
        this.add(btnEpisode, "cell 0 2");
    }

    public String getSelectedEpisode(){
        return this.tfEpisodePath.getText();
    }
}
