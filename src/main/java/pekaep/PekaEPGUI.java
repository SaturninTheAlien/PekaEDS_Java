package pekaep;

import java.awt.CardLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.List;

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
import pekaeds.ui.filefilters.ZipFilter;
import pk2.filesystem.PK2FileSystem;

public class PekaEPGUI extends JFrame {

    static enum Page{
        EPISODE("episode", true, false),
        LEVELS("levels", true, true),
        MISSING("missing", false, true),
        FINAL("zip", true, true);

        private final String name;
        private final boolean nextBtn;
        private final boolean backBtn;

        private Page(String name, boolean nextBtn, boolean backBtn){
            this.name = name;
            this.nextBtn = nextBtn;
            this.backBtn = backBtn;
        }

        public String getName(){
            return this.name;
        }

        public boolean hasNextBtn(){
            return this.nextBtn;
        }

        public boolean hasBackBtn(){
            return this.backBtn;
        }

    }

    private JLabel lSelectEpisode;
    private JTextField tfEpisodePath;

    private JLabel lFoundLevels;    
    private JList<String> jLevelList;
    private DefaultListModel<String> levelListModel;

    private JLabel lMissing;
    private JList<String> jMissingList;
    private DefaultListModel<String> missingListModel;
    private List<PK2EpisodeAsset> missingAssets;

    private AssetInfoPanel missingAssetInfoPanel;

    private JLabel lFinal;

    private JPanel cardPanel;
    private CardLayout cardLayout;
    private JButton btnNext;
    private JButton btnBack;
    private PK2Episode episode = null;

    private static final Dimension preferredListSize = new Dimension(150, 400);

    private Page currentPage = Page.EPISODE;
    

    public PekaEPGUI(){
        super();

        //EpisodeProfile.prepareProfile();
        
        PK2EpisodeAsset.loadEpisodeProfile();


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

        this.lSelectEpisode = new JLabel("<html> <span style=\"font-weight:bold; font-size: 25px\"> Select episode to pack! </span> </html>");
        episodeSelection.add(this.lSelectEpisode, "cell 0 0");
        episodeSelection.add(this.tfEpisodePath, "cell 0 1, width 500px");
        episodeSelection.add(btnEpisode, "cell 0 2");

        /*/JLabel testLabel = new JLabel("<html><span style=\"color:red; font-weight:bold\"> Test </span></html>");
        episodeSelection.add(testLabel, "cell 0 2");*/

        cardPanel.add(episodeSelection, Page.EPISODE.getName());



        JPanel panelLevels = new JPanel();
        this.lFoundLevels = new JLabel("<html> <span style=\"font-weight:bold; font-size: 25px\"> Found Levels </span> </html>");

        panelLevels.setLayout(new MigLayout("fill", "[grow]", "[pref!][grow]"));
        
        panelLevels.add(this.lFoundLevels, "cell 0 0");

        this.levelListModel = new DefaultListModel<>();        
        this.jLevelList = new JList<>(this.levelListModel);

        JScrollPane levelListScrollPane = new JScrollPane(this.jLevelList);
        levelListScrollPane.setPreferredSize(preferredListSize);


        panelLevels.add(levelListScrollPane, "cell 0 1, grow, push");        
        cardPanel.add( panelLevels, Page.LEVELS.getName());



        JPanel assetsPanel = new JPanel();
        assetsPanel.setLayout(new MigLayout("fill", "[grow]", "[pref!][pref!][grow]"));

        this.lMissing = new JLabel("<html> <span style=\"font-weight:bold; color:red; font-size: 25px\"> Missing assets! </span> </html>");

        JLabel missinginfo = new JLabel("These files are missing or they are corrupted!");
        
        assetsPanel.add(this.lMissing, "cell 0 0");
        assetsPanel.add(missinginfo, "cell 0 1");

        this.missingListModel = new DefaultListModel<>();

        this.jMissingList = new JList<>(this.missingListModel);
        this.jMissingList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
                
        this.missingAssetInfoPanel = new AssetInfoPanel();
        this.missingAssetInfoPanel.setVisible(false);

        this.jMissingList.getSelectionModel().addListSelectionListener(e -> {

            int index = this.jMissingList.getSelectedIndex();
            if(index>=0 && index < this.missingAssets.size()){
                this.missingAssetInfoPanel.setAsset( this.missingAssets.get(index));
                this.missingAssetInfoPanel.setVisible(true);
            }
            else{
                this.missingAssetInfoPanel.setVisible(false);
            }
        });

        JPanel assetsPanelG1 = new JPanel();
        assetsPanelG1.setLayout(new MigLayout("fill", "[150lp:n:200lp][grow]", "[grow]"));

        JScrollPane assetListScrollPane = new JScrollPane(this.jMissingList);
        assetListScrollPane.setPreferredSize(preferredListSize);

        assetsPanelG1.add(assetListScrollPane, "grow, push, cell 0 0");
        assetsPanelG1.add(this.missingAssetInfoPanel, "grow, push, cell 1 0");
        

        assetsPanel.add(assetsPanelG1, "cell 0 2, grow, push");
        cardPanel.add( assetsPanel, Page.MISSING.getName());

        JPanel finalPanel = new JPanel();
        finalPanel.setLayout(new MigLayout());

        this.lFinal = new JLabel("<html> <span style=\"font-weight:bold; color:green; font-size: 25px\"> Everything is alright! </span> </html>");
        finalPanel.add(this.lFinal, "cell 0 0");
        
        JLabel info = new JLabel("All dependencies satisfied!");
        JLabel info2 = new JLabel("Press finish to export the ZIP file!");

        finalPanel.add(info, "cell 0 1");
        finalPanel.add(info2, "cell 0 2");

        cardPanel.add(new JScrollPane(finalPanel), Page.FINAL.getName());


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

    private void loadAssets(){
        this.episode.findAssets();
        this.episode.sortAssets();

        this.missingListModel.clear();
        this.missingAssets = this.episode.getMissingAssetsList();
        
        
        for(PK2EpisodeAsset asset: this.missingAssets){
            this.missingListModel.addElement(asset.getName());
        }
    }

    private void createZip(){
        JFileChooser fc = new JFileChooser();
        fc.setFileFilter(new ZipFilter());

        var res = fc.showSaveDialog(this);
        if(res == JFileChooser.APPROVE_OPTION){

            File file = fc.getSelectedFile();
            try{
                PK2EpisodeIO.saveZip(this.episode, file);
                JOptionPane.showMessageDialog(this,
                "Successlully packed episode into a zip file:\n"+file.getAbsolutePath(),
                "Successfully zipped episode",                
                JOptionPane.INFORMATION_MESSAGE);

                this.dispose();
            }
            catch(Exception e){
                JOptionPane.showMessageDialog(this,
                "Cannot zip episode!\nException happened:\n"+e.getMessage(),
                "Cannot zip episode!",
                JOptionPane.ERROR_MESSAGE);
            }            
        }        
    }

    private void nextStep(){

        this.btnBack.setEnabled(false);
        this.btnNext.setEnabled(false);

        switch (this.currentPage) {
            case EPISODE:
                if(this.loadEpisode()){
                    this.currentPage = Page.LEVELS;
                    this.updateLevelList();
                }
                break;
            case LEVELS:
                this.loadAssets();
                if(this.missingAssets.isEmpty()){
                    this.currentPage = Page.FINAL;
                }
                else{
                    this.currentPage = Page.MISSING;
                }               
                break;
            
            case FINAL:
                this.createZip();
                break;

            default:
                break;
        }

        this.updatePage();        
    }

    private void previousStep(){
        this.btnBack.setEnabled(false);
        this.btnNext.setEnabled(false);
        
        switch (this.currentPage) {
            case LEVELS:
                this.currentPage = Page.EPISODE;                
                break;
            case MISSING:
                this.currentPage = Page.LEVELS;
                break;
            case FINAL:
                this.currentPage = Page.LEVELS;
                break;                
            default:
                this.currentPage = Page.EPISODE;
                break;
        }

        this.updatePage();
    }

    private void updatePage(){

        this.btnBack.setEnabled(this.currentPage.hasBackBtn());
        this.btnNext.setEnabled(this.currentPage.hasNextBtn());
        this.btnNext.setText( this.currentPage == Page.FINAL ? "Finish" : "Next");
        this.cardLayout.show(this.cardPanel, this.currentPage.getName());

    }
    

}
