package pekaep;

import java.awt.CardLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import net.miginfocom.swing.MigLayout;
import pekaeds.ui.filefilters.ZipFilter;
import pekaep.episode.PK2Episode;
import pekaep.episode.PK2EpisodeAsset;
import pekaep.episode.PK2EpisodeIO;
import pekaep.ui.EpisodePage;
import pekaep.ui.LevelListPage;
import pekaep.ui.MissingAssetsPage;
import pekaep.ui.ZipPage;

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

    private EpisodePage episodePanel;
    private LevelListPage levelListPanel;
    private MissingAssetsPage missingAssetListPanel;
    private ZipPage zipPanel;


    private JPanel cardPanel;
    private CardLayout cardLayout;
    private JButton btnNext;
    private JButton btnBack;
    private PK2Episode episode = null;

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

        this.episodePanel = new EpisodePage();
        cardPanel.add(this.episodePanel, Page.EPISODE.getName());

        this.levelListPanel = new LevelListPage();
        cardPanel.add( this.levelListPanel, Page.LEVELS.getName());

        this.missingAssetListPanel = new MissingAssetsPage();        
        cardPanel.add(missingAssetListPanel, Page.MISSING.getName());

        this.zipPanel = new ZipPage();
        cardPanel.add(new JScrollPane(this.zipPanel), Page.FINAL.getName());


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

        String episodeStr = this.episodePanel.getSelectedEpisode();

        if(episodeStr.isBlank()){
            JOptionPane.showMessageDialog(this,
            "Episode directory not selected!",
            "Directory not selected!",
            JOptionPane.ERROR_MESSAGE);
            return false;
        }

        File selectedDir = new File(episodeStr);
        if(!selectedDir.exists() || !selectedDir.isDirectory()){
            JOptionPane.showMessageDialog(this,
            "Selected directory does not exist:\n"+episodeStr,
            "Not a PK2 episode!",
            JOptionPane.ERROR_MESSAGE);

            return false;
        }

        this.episode = new PK2Episode(selectedDir);
        if(this.episode.getLevels().size()==0){
            JOptionPane.showMessageDialog(this,
            "No levels found, incorrect PK2 episode\n"+episodeStr,
            "Not a PK2 episode!",
            JOptionPane.ERROR_MESSAGE);
        }
        else{
            return true;
        }
        return false;
    }

    private void loadAssets(){
        this.episode.findAssets();
        this.episode.sortAssets();

        List<PK2EpisodeAsset> missingAssets = this.episode.getMissingAssetsList();
        this.missingAssetListPanel.update(missingAssets);

        if(missingAssets.isEmpty()){
            this.currentPage = Page.FINAL;
        }
        else{
            this.currentPage = Page.MISSING;
        }  

    }

    private void createZip(){
        JFileChooser fc = new JFileChooser();
        fc.setFileFilter(new ZipFilter());

        var res = fc.showSaveDialog(this);
        if(res == JFileChooser.APPROVE_OPTION){

            File file = fc.getSelectedFile();
            if(!file.getName().endsWith(".zip")){
                file = new File(file.getAbsolutePath()+".zip");
            }

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
                    this.levelListPanel.update(this.episode.getLevels());
                }
                break;
            case LEVELS:
                this.loadAssets();
                             
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
