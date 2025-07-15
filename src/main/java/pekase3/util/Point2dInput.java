package pekase3.util;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;
import javax.swing.event.ChangeListener;

import net.miginfocom.swing.MigLayout;
import pk2.util.Point2D;

public class Point2dInput extends JPanel {
    private JSpinner spX;
    private JSpinner spY;

    public Point2dInput(String name){
        super();
        this.setLayout(new MigLayout());
        this.setBorder(BorderFactory.createTitledBorder(name));

        JLabel lbX = new JLabel("X:");
        JLabel lbY = new JLabel("Y:");

        spX = new JSpinner(new SpinnerNumberModel(0, 0.0, 100000.0, 1.0));
        spY = new JSpinner(new SpinnerNumberModel(0, 0.0, 100000.0, 1.0));

        this.add(lbX);
        this.add(spX);
        this.add(lbY);
        this.add(spY);
    }

    public void setValue(double x, double y){
        spX.setValue(x);
        spY.setValue(y);
    }

    public void setValue(Point2D point){
        spX.setValue(point.getX());
        spY.setValue(point.getY());
    }

    public Point2D getValue(){

        double x = spX.getValue() instanceof Integer? (int) spX.getValue() : (double) spX.getValue();
        double y = spY.getValue() instanceof Integer? (int) spY.getValue() : (double) spY.getValue();

        return new Point2D(x, y);
    }

    public void addChangeListener(ChangeListener listener){
        this.spX.addChangeListener(listener);
        this.spY.addChangeListener(listener);
    }    
}
