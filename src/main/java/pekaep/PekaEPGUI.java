package pekaep;

import java.awt.CardLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.JTextField;

import net.miginfocom.swing.MigLayout;
import pk2.filesystem.PK2FileSystem;

public class PekaEPGUI extends JFrame {

    static enum Page{
        EPISODE("episode"),
        LEVELS("levels");

        private final String name;

        private Page(String name){
            this.name = name;
        }

        public String getName(){
            return this.name;
        }
    }

    private JTextField tfEpisodePath;

    private JTextArea taLevelsList;

    private JPanel cardPanel;
    private CardLayout cardLayout;
    private JButton btnNext;
    private JButton btnBack;
    private Episode episode = null;

    Page currentPage = Page.EPISODE;

    private boolean loadEpisode(){
        if(this.tfEpisodePath.getText().isBlank()){
            JOptionPane.showMessageDialog(this,
            "Episode directory not selected!",
            "Directory not selected!",
            JOptionPane.ERROR_MESSAGE);
            return false;
        }

        File selectedDir = new File(this.tfEpisodePath.getText());
        if(!selectedDir.exists() || !selectedDir.isDirectory()){
            JOptionPane.showMessageDialog(this,
            "Selected directory does not exist:\n"+this.tfEpisodePath.getText(),
            "Not a PK2 episode!",
            JOptionPane.ERROR_MESSAGE);

            return false;
        }

        this.episode = new Episode(selectedDir);
        if(this.episode.getLevels().size()==0){
            JOptionPane.showMessageDialog(this,
            "No levels found, incorrect PK2 episode\n"+this.tfEpisodePath.getText(),
            "Not a PK2 episode!",
            JOptionPane.ERROR_MESSAGE);
        }
        else{
            return true;
        }
        return false;
    }

    private void updateLevelList(){
        StringBuilder sb = new StringBuilder();
        int levelCount = this.episode.getLevels().size();
        for(int i=0;i<levelCount;++i){
            sb.append(i);
            sb.append(" - ");
            sb.append(this.episode.getLevels().get(i).getName());
            sb.append("\n");
        }

        this.taLevelsList.setText(sb.toString());
    }

    private void nextStep(){

        switch (this.currentPage) {
            case EPISODE:
                if(this.loadEpisode()){
                    this.currentPage = Page.LEVELS;
                    this.updateLevelList();
                }
                break;
        
            default:
                break;
        }
        this.btnBack.setEnabled(this.currentPage!=Page.EPISODE);
        this.cardLayout.show(this.cardPanel, this.currentPage.getName());
    }

    private void previousStep(){
        
        switch (this.currentPage) {
            case LEVELS:
                this.currentPage = Page.EPISODE;                
                break;
        
            default:
                this.currentPage = Page.EPISODE;
                break;
        }

        this.btnBack.setEnabled(this.currentPage!=Page.EPISODE);
        this.cardLayout.show(this.cardPanel, this.currentPage.getName());
    }


    public PekaEPGUI(){
        super();


        this.cardPanel = new JPanel();
        this.cardLayout = new CardLayout();
        this.cardPanel.setLayout(this.cardLayout);


        JPanel episodeSelection = new JPanel();
        episodeSelection.setLayout(new MigLayout());

        this.tfEpisodePath = new JTextField();
        JButton btnEpisode = new JButton("Browse");
        btnEpisode.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                JFileChooser fc = new JFileChooser(PK2FileSystem.getAssetsPath(PK2FileSystem.EPISODES_DIR));
                fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
                
                var res = fc.showOpenDialog(null);
                
                if (res == JFileChooser.APPROVE_OPTION) {
                    PekaEPGUI.this.tfEpisodePath.setText(fc.getSelectedFile().getPath());
                }
            }            
        });


        episodeSelection.add(this.tfEpisodePath, "cell 0 0, width 400px");
        episodeSelection.add(btnEpisode, "cell 1 0");

        cardPanel.add(episodeSelection, Page.EPISODE.getName());

        JPanel panelLevels = new JPanel();
        panelLevels.setLayout(new MigLayout());
        panelLevels.add(new JLabel("Found levels:"), "cell 0 0");

        this.taLevelsList = new JTextArea();
        this.taLevelsList.setEnabled(false);
        this.taLevelsList.setText("No levels found!");
        panelLevels.add(this.taLevelsList, "cell 0 1");
        
        cardPanel.add(panelLevels, Page.LEVELS.getName());

        this.btnNext = new JButton("Next");
        this.btnBack = new JButton("back");
        this.btnBack.setEnabled(false);

        this.btnBack.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                PekaEPGUI.this.previousStep();
            }            
        });

        this.btnNext.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                PekaEPGUI.this.nextStep();
            }            
        });

        JPanel btnPanel = new JPanel();
        btnPanel.setLayout(new MigLayout());
        btnPanel.add(this.btnBack);
        btnPanel.add(this.btnNext);

        this.setLayout(new MigLayout());
        this.add(this.cardPanel, "dock center");
        this.add(btnPanel, "south");

        this.setTitle("Pekka Kana 2 episode packing tool");

        this.setMinimumSize(new Dimension(300, 150));
        this.setSize(800, 500);
        this.setLocationRelativeTo(null);
        this.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        this.setVisible(true);
    }

}
