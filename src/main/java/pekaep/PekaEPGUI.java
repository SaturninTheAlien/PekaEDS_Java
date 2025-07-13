package pekaep;

import java.awt.CardLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;

import net.miginfocom.swing.MigLayout;
import pk2.filesystem.PK2FileSystem;

public class PekaEPGUI extends JFrame {

    static enum Page{
        EPISODE("episode"),
        LEVELS("levels"),
        ASSETS("assets");

        private final String name;

        private Page(String name){
            this.name = name;
        }

        public String getName(){
            return this.name;
        }
    }

    private JTextField tfEpisodePath;
    
    private JList<String> jLevelList;
    private JList<String> jAssetsList;

    private DefaultListModel<String> levelListModel;
    private DefaultListModel<String> assetsListModel;

    private JPanel cardPanel;
    private CardLayout cardLayout;
    private JButton btnNext;
    private JButton btnBack;
    private PK2Episode episode = null;

    Page currentPage = Page.EPISODE;

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

        this.levelListModel = new DefaultListModel<>();
        this.jLevelList = new JList<>(this.levelListModel);
        this.jLevelList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        panelLevels.add(this.jLevelList, "cell 0 1");        
        cardPanel.add( new JScrollPane(panelLevels), Page.LEVELS.getName());



        JPanel assetsPanel = new JPanel();
        assetsPanel.setLayout(new MigLayout());
        assetsPanel.add(new JLabel("Required assets:"), "cell 0 0");

        this.assetsListModel = new DefaultListModel<>();
        this.jAssetsList = new JList<>(this.assetsListModel);
        assetsPanel.add(this.jAssetsList, "cell 0 1");
        cardPanel.add( new JScrollPane(assetsPanel), Page.ASSETS.getName());




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

        this.episode = new PK2Episode(selectedDir);
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
        this.levelListModel.clear();
        for(File levelFile: this.episode.getLevels()){
            this.levelListModel.addElement(levelFile.getName());
        }
    }

    private void updateAssetsList(){        
        this.assetsListModel.clear();
        
        this.assetsListModel.addElement("Placeholder!");

        System.out.println("Done!");
        for(PK2EpisodeAsset asset: this.episode.getAssetList()){
            this.assetsListModel.addElement(asset.getName());
        }
    }

    private void nextStep(){

        switch (this.currentPage) {
            case EPISODE:
                if(this.loadEpisode()){
                    this.currentPage = Page.LEVELS;
                    this.updateLevelList();
                }
                break;
            case LEVELS:
                this.episode.findAssets();
                this.episode.sortAssets();
                
                this.updateAssetsList();

                this.currentPage = Page.ASSETS;
                break;
        
            default:
                break;
        }

        this.updatePage();        
    }

    private void previousStep(){
        
        switch (this.currentPage) {
            case LEVELS:
                this.currentPage = Page.EPISODE;                
                break;
            case ASSETS:
                this.currentPage = Page.LEVELS;
                break;        
            default:
                this.currentPage = Page.EPISODE;
                break;
        }

        this.updatePage();
    }

    private void updatePage(){
        this.btnBack.setEnabled(this.currentPage!=Page.EPISODE);
        this.cardLayout.show(this.cardPanel, this.currentPage.getName());

    }
    

}
