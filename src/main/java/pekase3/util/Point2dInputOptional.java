package pekase3.util;

import javax.swing.BorderFactory;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import net.miginfocom.swing.MigLayout;
import pk2.util.Point2D;

public class Point2dInputOptional extends JPanel{
    private JSpinner spX;
    private JSpinner spY;
    private JCheckBox cbHasValue;

    private ChangeListener mListener;

    public Point2dInputOptional(String name){
        super();
        this.setLayout(new MigLayout());
        this.setBorder(BorderFactory.createTitledBorder(name));

        JLabel lbHasValue = new JLabel("Has value:");
        JLabel lbX = new JLabel("X:");
        JLabel lbY = new JLabel("Y:");

        cbHasValue = new JCheckBox();
        spX = new JSpinner(new SpinnerNumberModel(0, 0.0, 100000.0, 1.0));
        spY = new JSpinner(new SpinnerNumberModel(0, 0.0, 100000.0, 1.0));

        this.cbHasValue = new JCheckBox();

        this.cbHasValue.addChangeListener(new ChangeListener() {

            @Override
            public void stateChanged(ChangeEvent e) {
                spX.setEnabled(cbHasValue.isSelected());
                spY.setEnabled(cbHasValue.isSelected());
                if(mListener!=null){
                    mListener.stateChanged(e);
                }
            }
            
        });

        this.add(lbHasValue);
        this.add(cbHasValue);
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
        if(point!=null){
            spX.setValue(point.getX());
            spY.setValue(point.getY());

            spX.setEnabled(true);
            spY.setEnabled(true);
            cbHasValue.setSelected(true);
        }
        else{
            spX.setValue(0);
            spY.setValue(0);

            spX.setEnabled(false);
            spY.setEnabled(false);
            cbHasValue.setSelected(false);
        }
    }

    public Point2D getValue(){

        if(cbHasValue.isSelected()){
            return new Point2D((double) spX.getValue(), (double) spY.getValue());
        }
        else{
            return null;
        }
    }

    public void addChangeListener(ChangeListener listener){
        this.mListener = listener;
        this.spX.addChangeListener(listener);
        this.spY.addChangeListener(listener);
    }        
}
