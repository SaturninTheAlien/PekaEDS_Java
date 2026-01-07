package pekaeds.ui.decorator;

import java.util.LinkedHashMap;
import java.util.Map;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import org.tinylog.Logger;

import net.miginfocom.swing.MigLayout;
import pekaeds.decorator.GrassPlacer;
import pekaeds.decorator.ISectorDecorator;
import pekaeds.decorator.SlopeFixer;
import pekaeds.decorator.SolidGrassPlacer;
import pekaeds.decorator.StochasticTileReplacer;
import pekaeds.ui.listeners.PK2SectorConsumer;
import pekaeds.ui.main.PekaEDSGUI;
import pk2.level.PK2LevelSector;

public class DecoratorDialog extends JFrame implements PK2SectorConsumer{
    private PK2LevelSector sector;
    private PekaEDSGUI edsgui;

    private JComboBox<String> cbDecorator;
    private JTextField tfSeed;
    private JButton btnRun;


    private Map<String, ISectorDecorator> decorators = new LinkedHashMap<>();


    public DecoratorDialog(PekaEDSGUI edsgui){
        this.edsgui = edsgui;
        

        SlopeFixer slopeFixer = new SlopeFixer();
        this.decorators.put("Fix broken slopes", slopeFixer);

        ISectorDecorator diversifyFG = StochasticTileReplacer.getDefaultGroundMixer();
        this.decorators.put("Diversify foreground", diversifyFG);

        ISectorDecorator placeSolidGrass = SolidGrassPlacer.getInstance();
        this.decorators.put("Place solid grass", placeSolidGrass);

        ISectorDecorator placeSparseGrass = GrassPlacer.getSparseGrassPlacer();
        this.decorators.put("Place sparse grass", placeSparseGrass);

        ISectorDecorator placeDenseGrass = GrassPlacer.getDenseGrassPlacer();
        this.decorators.put("Place dense grass", placeDenseGrass);


        ISectorDecorator fullDecorator = new ISectorDecorator() {

            @Override
            public void perform(PK2LevelSector sector) throws Exception {
                placeSolidGrass.perform(sector);
                placeSparseGrass.perform(sector);
                slopeFixer.perform(sector);
                diversifyFG.perform(sector);
            }

            @Override
            public void setSeed(long seed) {
                placeSolidGrass.setSeed(seed);
                placeSparseGrass.setSeed(seed);
                slopeFixer.setSeed(seed);
                diversifyFG.setSeed(seed);
            }  
        };
        this.decorators.put("Decorate for me!", fullDecorator);


        this.setupUI();
        
    }

    private void setupUI(){

        this.cbDecorator = new JComboBox<>(this.decorators.keySet().toArray(new String[0]));
        this.cbDecorator.setSelectedIndex(0);

        this.tfSeed = new JTextField();
        this.tfSeed.setText("0");

        this.btnRun = new JButton("Run!");
        this.btnRun.addActionListener(e->{
            runDecorator();
        });


        

        JPanel panel = new JPanel(new MigLayout());


        StringBuilder builder = new StringBuilder();
        builder.append("<html>");
        builder.append("<span style=\"font-weight:bold; color:orange\">");
        builder.append("Decorators were intented to be used with tiles01.bmp!");
        builder.append("</span><br>");
        builder.append("You are using them with another tileset at your own risk!");
        builder.append("</html>");


        JLabel warning1 = new JLabel(builder.toString());
        panel.add(warning1, "cell 0 0");
        panel.add(this.cbDecorator, "cell 0 1");

        JPanel seedPanel = new JPanel();
        seedPanel.add(new JLabel("RNG Seed: "));
        seedPanel.add(this.tfSeed);

        panel.add(seedPanel,  "cell 0 2");
        panel.add(this.btnRun, "cell 0 3");


        this.add(panel);

        this.setTitle("Decorators");
        this.setSize(480, 200);

        this.setLocationRelativeTo(null);
        this.setResizable(false);
    }


    private static long parseSeed(String input) {
        try {
            return Long.parseLong(input);
        } catch (NumberFormatException e) {
            return input.hashCode();
        }
    }


    private void runDecorator(){
        try{
            ISectorDecorator decorator = this.decorators.get(this.cbDecorator.getSelectedItem());
            long seed = parseSeed(this.tfSeed.getText());
            decorator.setSeed(seed);
            decorator.perform(sector);
            edsgui.repaintView();
        }
        catch(Exception e){
            Logger.error(e);
        }
    }

    @Override
    public void setSector(PK2LevelSector sector) {
        this.sector = sector;
    }
}
