package pekaeds.ui.sectorlistpanel;

import net.miginfocom.swing.MigLayout;
import pekaeds.pk2.map.PK2Map;
import pekaeds.pk2.map.PK2MapSector;
import pekaeds.ui.listeners.PK2MapConsumer;
import pekaeds.ui.listeners.PK2SectorConsumer;
import pekaeds.ui.main.PekaEDSGUI;

import javax.swing.*;
import java.util.ArrayList;
import java.util.List;

public class SectorListPanel extends JPanel implements PK2MapConsumer {
    private List<PK2SectorConsumer> consumers = new ArrayList<>();

    private JList<String> lstSectors;
    private DefaultListModel<String> sectorModel;

    private JButton btnAdd;
    private JButton btnRemove;
    private JButton btnCopy;

    private PK2Map map;

    private PekaEDSGUI edsGUI;

    public SectorListPanel(PekaEDSGUI gui) {
        edsGUI = gui;

        setup();
    }

    private void setup() {
        sectorModel = new DefaultListModel<>();
        lstSectors = new JList<>(sectorModel);
        lstSectors.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        lstSectors.getSelectionModel().addListSelectionListener(e -> {
            if (map != null) {
                if (map.sectors.size() == sectorModel.size()) {
                    int selectedIndex = lstSectors.getSelectedIndex();

                    if (selectedIndex != -1) {
                        for (var c : consumers) {
                            c.setSector(map.sectors.get(lstSectors.getSelectedIndex()));
                        }
                    }
                }
            }
        });

        btnAdd = new JButton("Add");
        btnRemove = new JButton("Remove");
        btnCopy = new JButton("Duplicate");

        btnAdd.addActionListener(e -> {
            NewSectorDialog newSectorDialog = new NewSectorDialog();

            PK2MapSector newSector = newSectorDialog.showDialog();

            if (newSector != null) {
                map.addSector(newSector);
                sectorModel.addElement(newSector.name);

                edsGUI.setUnsavedChangesPresent(true);
            }
        });

        btnRemove.addActionListener(e -> {
            int index = lstSectors.getSelectedIndex();

            if (index != -1) {

                if(map.sectors.size() > 1){
                    int result = JOptionPane.showConfirmDialog(SectorListPanel.this,
                    "Do you really want to remove sector \""+map.sectors.get(index).name+"\"?",
                    "Confirmation",
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.QUESTION_MESSAGE);

                    if(result == JOptionPane.YES_OPTION){
                        map.removeSector(index);
                        sectorModel.remove(index);
                        edsGUI.setUnsavedChangesPresent(true);

                        lstSectors.setSelectedIndex(0);
                        for (var c : consumers) {
                            c.setSector(map.sectors.get(0));
                        }
                    }
                }
                else{
                    JOptionPane.showMessageDialog(SectorListPanel.this,
                    "Levels without any sectors are not allowed!"
                    +"\nPK2 is not set theory, it doesn't support the empty set."
                    +"\nPlease create a new sector before removing this one!",
                    "Cannot remove a sector!", JOptionPane.INFORMATION_MESSAGE);
                }
            }
        });

        btnCopy.addActionListener(e -> {
            int index = lstSectors.getSelectedIndex();

            if(index != -1){

                PK2MapSector sector = map.sectors.get(index);

                int result = JOptionPane.showConfirmDialog(SectorListPanel.this,
                    "Do you really want to duplicate sector \""+sector.name+"\"?",
                    "Confirmation",
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.QUESTION_MESSAGE);


                if(result == JOptionPane.YES_OPTION){
                    PK2MapSector newSector = new PK2MapSector(sector);
                    map.addSector(newSector);
                    sectorModel.addElement(newSector.name);
                    edsGUI.setUnsavedChangesPresent(true);

                }
            }
        });

        JPanel pnlButtons = new JPanel(new MigLayout("flowx"));
        pnlButtons.add(btnAdd);
        pnlButtons.add(btnRemove);
        pnlButtons.add(btnCopy);

        setLayout(new MigLayout());
        add(pnlButtons, "dock north");
        add(new JScrollPane(lstSectors), "dock center");
    }

    public void addSectorConsumer(PK2SectorConsumer consumer) {
        if (!consumers.contains(consumer)) {
            consumers.add(consumer);
        }
    }

    @Override
    public void setMap(PK2Map newMap) {
        map = newMap;

        if (!sectorModel.isEmpty()) sectorModel.clear();

        for (PK2MapSector sector : map.sectors) {
            sectorModel.addElement(sector.name);
        }
    }

    public void setSelectedSector(int sector) {
        lstSectors.setSelectedIndex(sector);
    }
}
