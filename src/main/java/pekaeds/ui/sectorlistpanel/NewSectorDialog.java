package pekaeds.ui.sectorlistpanel;

import pekaeds.pk2.map.PK2LevelUtils;
import pekaeds.pk2.map.PK2MapSector;
import pekaeds.ui.sector.SectorMetadataPanel;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

public class NewSectorDialog extends JDialog {
    private SectorMetadataPanel sectorMetadata;

    private PK2MapSector sector = null;

    public NewSectorDialog() {
        setup();
    }

    private void setup() {
        sectorMetadata = new SectorMetadataPanel();

        add(sectorMetadata, BorderLayout.CENTER);

        JPanel pnlButton = new JPanel();
        JButton btnAccept = new JButton("Accept");
        btnAccept.addActionListener(e -> {
            sectorMetadata.setSectorData(sector);

            dispose();
        });

        pnlButton.add(new JSeparator());
        pnlButton.add(btnAccept);

        add(pnlButton, BorderLayout.SOUTH);

        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                sector = null;
            }
        });

        setTitle("Create a new sector...");
        setModal(true);
        setSize(350, 640);
        setLocationRelativeTo(null);
    }

    public PK2MapSector showDialog() {
        sector = PK2LevelUtils.createDefaultSector();
        sector.name = "New Sector";
        sectorMetadata.setSector(sector);
        setVisible(true);

        return sector;
    }
}
