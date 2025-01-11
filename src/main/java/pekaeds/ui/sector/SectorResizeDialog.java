package pekaeds.ui.sector;

import net.miginfocom.swing.MigLayout;
import pekaeds.pk2.map.PK2MapSector;
import pekaeds.ui.listeners.PK2SectorConsumer;
import pekaeds.ui.listeners.RectangleChangeListener;
import pekaeds.ui.main.PekaEDSGUI;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;

public class SectorResizeDialog extends JDialog
        implements RectangleChangeListener, ChangeListener, PK2SectorConsumer, WindowListener {

    private RectangleChangeListener resizeListener;

    private JSpinner spStartX;
    private JSpinner spStartY;
    private JSpinner spWidth;
    private JSpinner spHeight;

    private JButton btnApply;

    private Rectangle resizeRect = new Rectangle();

    private PekaEDSGUI eds;

    public SectorResizeDialog(PekaEDSGUI edsUI) {
        eds = edsUI;

        spStartX = new JSpinner(new SpinnerNumberModel(0, -100000, 10000, 1));
        spStartY = new JSpinner(new SpinnerNumberModel(0, -100000, 10000, 1));
        spWidth = new JSpinner(new SpinnerNumberModel(PK2MapSector.CLASSIC_WIDTH, 25, 10000, 1));
        spHeight = new JSpinner(new SpinnerNumberModel(PK2MapSector.CLASSIC_HEIGHT, 15, 10000, 1));

        btnApply = new JButton("Apply");

        btnApply.addActionListener(e->{

            int startX = (int) spStartX.getValue();
            int startY = (int) spStartY.getValue();
            int width = (int) spWidth.getValue();
            int height = (int) spHeight.getValue();

            eds.resizeSector(startX, startY, width, height);

            spStartX.setValue(0);
            spStartY.setValue(0);
        });

        spStartX.addChangeListener(this);
        spStartY.addChangeListener(this);
        spWidth.addChangeListener(this);
        spHeight.addChangeListener(this);

        JPanel pnl = new JPanel();
        pnl.setLayout(new MigLayout("wrap 2, gap 5px"));
        pnl.add(new JLabel("Start X:"));
        pnl.add(spStartX);
        pnl.add(new JLabel("Start Y:"));
        pnl.add(spStartY);
        pnl.add(new JLabel("Width:"));
        pnl.add(spWidth);
        pnl.add(new JLabel("Height:"));
        pnl.add(spHeight);
        pnl.add(btnApply);

        add(pnl);

        pack();

        setLocationRelativeTo(null);
        setResizable(false);
        setAlwaysOnTop(true);
        addWindowListener(this);
        setTitle("Resizing sector...");
    }

    public void setSector(PK2MapSector sector) {
        spStartX.setValue(0);
        spStartY.setValue(0);
        spWidth.setValue(sector.getWidth());
        spHeight.setValue(sector.getHeight());
    }

    @Override
    public void rectangleChanged(Rectangle newRectangle) {
        spStartX.setValue(newRectangle.x / 32);
        spStartY.setValue(newRectangle.y / 32);
        spWidth.setValue(newRectangle.width / 32);
        spHeight.setValue(newRectangle.height / 32);
    }

    public void setResizeListener(RectangleChangeListener listener) {
        resizeListener = listener;
    }

    @Override
    public void stateChanged(ChangeEvent e) {
        // TODO There is some kind of bug, probably here where the x and y act weird when changing them in the MapPanel
        resizeRect.x = (int) spStartX.getValue() * 32;
        resizeRect.y = (int) spStartY.getValue() * 32;
        resizeRect.width = (int) spWidth.getValue() * 32;
        resizeRect.height = (int) spHeight.getValue() * 32;

        resizeListener.rectangleChanged(resizeRect);
    }

    @Override
    public void windowOpened(WindowEvent e) {

    }

    @Override
    public void windowClosing(WindowEvent e) {
       eds.cancelResizing();
    }

    @Override
    public void windowClosed(WindowEvent e) {

    }

    @Override
    public void windowIconified(WindowEvent e) {

    }

    @Override
    public void windowDeiconified(WindowEvent e) {

    }

    @Override
    public void windowActivated(WindowEvent e) {

    }

    @Override
    public void windowDeactivated(WindowEvent e) {

    }
}
