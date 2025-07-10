package pekaeds.ui.misc;

import java.awt.Dialog;
import java.util.Arrays;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;

import net.miginfocom.swing.MigLayout;

public class AppSelectionDialog extends JDialog{
    private JComboBox<AppEnum> cbApp;

    private boolean appSelected = false;
    public AppSelectionDialog(Dialog owner){
        super(owner);

        JLabel lbApp = new JLabel("Select application");

        cbApp = new JComboBox<>(
            Arrays.stream(AppEnum.values()).filter(AppEnum::isVisible).toArray(AppEnum[]::new));

        cbApp.setSelectedIndex(0);

        setLayout(new MigLayout("flowy"));

        var btnOk = new JButton("OK");
        btnOk.addActionListener(e->{
            appSelected = true;
            setVisible(false);
        });

        JPanel panelApp = new JPanel();
        panelApp.setLayout(new MigLayout("align 50% 10%"));
        panelApp.add(lbApp, "cell 0 0");
        panelApp.add(cbApp, "cell 0 1");


        var pnlOkButton = new JPanel();
        pnlOkButton.setLayout(new MigLayout("fillx"));
        pnlOkButton.add(btnOk, "gapx push");

        this.add(panelApp, "dock center");
        this.add(pnlOkButton, "south");      

        setTitle("PekaEDS - App selection");
        setSize(300, 150);
        setLocationRelativeTo(null);
        setResizable(false);
        setModal(true);
        setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);

    }

    public AppEnum getSelectedApp(){
        this.setVisible(true);

        if(appSelected){
            return (AppEnum)this.cbApp.getSelectedItem();
        }

        
        return AppEnum.NOT_SELECTED;
    }
    
}
