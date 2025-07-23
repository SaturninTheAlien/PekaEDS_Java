package pekaep.ui;

import java.io.FileNotFoundException;

import javax.swing.JLabel;
import javax.swing.JPanel;

import net.miginfocom.swing.MigLayout;
import pekaep.episode.PK2EpisodeAsset;

public class AssetInfoPanel extends JPanel {

    private static String getHeaderText(String content){
        return "<html> <span style=\"font-weight:bold\">"+ content+"</span> </html>";
    }

    private static String getErrorString(String content){
        return "<html> <span style=\"font-weight:bold; color:red\">"+ content+"</span> </html>";
    }

    private static String getOkayString(String content){
        return "<html> <span style=\"font-weight:bold; color:green\">"+ content+"</span> </html>";
    }



    private String listUnknowAIs(PK2EpisodeAsset asset){
        StringBuilder builder = new StringBuilder();
        builder.append("<html> <span style=\"font-weight:bold; color:orange\">Uknown AIs:</span> <ul>");

        for(int ai: asset.unknowsAIs){
            builder.append("<li>");
            builder.append(ai);
            builder.append("</li>");
        }

        builder.append("</ul> </html>");
        return builder.toString();
    }

    private JLabel lFilename;
    private JLabel lError;
    private JLabel lType;
    private JLabel lDependency;

    public AssetInfoPanel(){
        this.setLayout(new MigLayout());

        JLabel hFilename = new JLabel(getHeaderText("Filename:"));
        this.add(hFilename, "cell 0 0");

        this.lFilename = new JLabel("Select asset: ");
        this.add(lFilename, "cell 1 0");

        JLabel hType = new JLabel(getHeaderText("Type: "));
        this.add(hType, "cell 0 1");
        
        this.lType = new JLabel();
        this.add(lType, "cell 1 1");

        JLabel hRequiredFrom = new JLabel(getHeaderText("Required from:"));
        this.add(hRequiredFrom, "cell 0 2");

        this.lDependency = new JLabel();
        this.add(this.lDependency, "cell 1 2");

        this.lError = new JLabel();
        this.add(this.lError, "cell 0 3");

    }

    public void setAsset(PK2EpisodeAsset asset){
        this.lFilename.setText(asset.getName());
        this.lType.setText(asset.getType().getName());

        if(asset.getParent()==null){
            this.lDependency.setText("Episode");
        }
        else{
            this.lDependency.setText(asset.getParent().getName());
        }

        if(asset.isGood()){

            if(asset.unknowsAIs!=null && !asset.unknowsAIs.isEmpty()){
                this.lError.setText(listUnknowAIs(asset));
            }
            else{
                this.lError.setText(getOkayString("No problems with this file!"));
            }            
        }
        else{
            if(asset.loadingException==null || asset.loadingException instanceof FileNotFoundException){
                this.lError.setText(getErrorString("File not found!"));
            }
            else{
                this.lError.setText(getErrorString("File is corrupted!"));
            }
        }
    }
}
