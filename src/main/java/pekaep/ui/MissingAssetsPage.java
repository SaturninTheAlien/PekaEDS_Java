package pekaep.ui;

import java.util.List;

import javax.swing.DefaultListModel;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ListSelectionModel;

import net.miginfocom.swing.MigLayout;
import pekaep.episode.PK2EpisodeAsset;

public class MissingAssetsPage extends JPanel {

    private DefaultListModel<String> missingListModel;
    private JList<String> jMissingList;
    private AssetInfoPanel missingAssetInfoPanel;
    private List<PK2EpisodeAsset> missingAssets;

    public MissingAssetsPage(){
        this.setLayout(new MigLayout("fill", "[grow]", "[pref!][pref!][grow]"));

        JLabel title = new JLabel("<html> <span style=\"font-weight:bold; color:red; font-size: 25px\"> Missing assets! </span> </html>");

        JLabel info = new JLabel("These files are missing or they are corrupted!");
        
        this.add(title, "cell 0 0");
        this.add(info, "cell 0 1");

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
        //assetListScrollPane.setPreferredSize(preferredListSize);

        assetsPanelG1.add(assetListScrollPane, "grow, push, cell 0 0");
        assetsPanelG1.add(this.missingAssetInfoPanel, "grow, push, cell 1 0");
        

        this.add(assetsPanelG1, "cell 0 2, grow, push");
    }

    public void update(List<PK2EpisodeAsset> missingAssetsIn){
        this.missingAssets = missingAssetsIn;

        this.missingListModel.clear();
        for(PK2EpisodeAsset asset: this.missingAssets){
            this.missingListModel.addElement(asset.getName());
        }
    }
}
