package pekaep.ui;

import java.util.List;

import javax.swing.DefaultListModel;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ListSelectionModel;

import net.miginfocom.swing.MigLayout;
import pekaep.episode.PK2EpisodeAsset;

public class UnknowAIPage extends JPanel {


    private DefaultListModel<String> assetsListModel;
    private JList<String> jList;
    private AssetInfoPanel assetInfoPanel;
    private List<PK2EpisodeAsset> assets;

    private JCheckBox cbUnknowAIs;

    public UnknowAIPage(){
        this.setLayout(new MigLayout("fill", "[grow]", "[pref!][pref!][pref!][grow]"));

        JLabel title = new JLabel("<html> <span style=\"font-weight:bold; color:orange; font-size: 25px\"> Warning! </span> </html>");


        JLabel info = new JLabel("<html> These sprites contain unknown AIs! <br> It will cause problems when the game is updated! To fix it, remove listed AIs! </html>");
        
        this.add(title, "cell 0 0");
        this.add(info, "cell 0 1");
        //this.add(info2, "cell 0 2");

        this.assetsListModel = new DefaultListModel<>();

        this.jList = new JList<>(this.assetsListModel);
        this.jList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
                
        this.assetInfoPanel = new AssetInfoPanel();
        this.assetInfoPanel.setVisible(false);

        this.cbUnknowAIs = new JCheckBox("Remove unknown sprite AIs (recommended)");
        this.cbUnknowAIs.setSelected(false);

        this.add(this.cbUnknowAIs, "cell 0 2");

        this.jList.getSelectionModel().addListSelectionListener(e -> {

            int index = this.jList.getSelectedIndex();
            if(index>=0 && index < this.assets.size()){
                this.assetInfoPanel.setAsset( this.assets.get(index));
                this.assetInfoPanel.setVisible(true);
            }
            else{
                this.assetInfoPanel.setVisible(false);
            }
        });

        JPanel assetsPanelG1 = new JPanel();
        assetsPanelG1.setLayout(new MigLayout("fill", "[150lp:n:200lp][grow]", "[grow]"));

        JScrollPane assetListScrollPane = new JScrollPane(this.jList);
        //assetListScrollPane.setPreferredSize(preferredListSize);

        assetsPanelG1.add(assetListScrollPane, "grow, push, cell 0 0");
        assetsPanelG1.add(this.assetInfoPanel, "grow, push, cell 1 0");
        

        this.add(assetsPanelG1, "cell 0 3, grow, push");
    }

    public boolean shouldRemoveUnknownAIs(){
        return this.cbUnknowAIs.isSelected();
    }

    public void update(List<PK2EpisodeAsset> missingAssetsIn){

        this.cbUnknowAIs.setSelected(true);
        this.assets = missingAssetsIn;

        this.assetsListModel.clear();
        for(PK2EpisodeAsset asset: this.assets){
            this.assetsListModel.addElement(asset.getName());
        }
    }    
}
