package pekase3.panels.animation.preview;

import net.miginfocom.swing.MigLayout;
import pekase3.listener.UnsavedChangesListener;
import pekase3.panels.FrameImagePanel;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import java.awt.*;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.image.BufferedImage;

import java.util.ArrayList;
import java.util.List;

public class AnimationContainer extends JPanel implements MouseListener {
    
    private FrameImagePanel imagePanel;
    private JTextField tfFrameNumber;
    
    private static List<BufferedImage> framesList = new ArrayList<>();
    
    public AnimationContainer() {
        imagePanel = new FrameImagePanel();
        tfFrameNumber = new JTextField();
        tfFrameNumber.setText("0");
        
        tfFrameNumber.setMaximumSize(new Dimension(30, 25));
        
        setLayout(new MigLayout("flowy"));
        
        add(imagePanel);
        add(tfFrameNumber, "alignx 50%,aligny 50%");
        
        addListeners();
    }
    
    private void addListeners() {
        tfFrameNumber.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                int frame = Integer.parseInt(tfFrameNumber.getText());
                
                // In Pekka Kana 2 animations start at index 1, index 0 means no frame.
                if (frame > 0 && frame <= framesList.size()) {
                    imagePanel.setImage(framesList.get(frame - 1));
                } else {
                    imagePanel.setImage(null);
                }
            }
            
            @Override
            public void removeUpdate(DocumentEvent e) {
            }
            
            @Override
            public void changedUpdate(DocumentEvent e) {
            }
        });
        
        tfFrameNumber.addFocusListener(new FocusAdapter() {
            @Override
            public void focusLost(FocusEvent e) {
                super.focusLost(e);
                
                if (tfFrameNumber.getText().isBlank()) {
                    tfFrameNumber.setText("0");
                }
            }
        });
        
        addMouseListener(this);
    }
    
    public void reset() {
        imagePanel.setImage(null);
        tfFrameNumber.setText("0");
    }
    
    public byte getFrameNumber() {
        return (byte) Integer.parseInt(tfFrameNumber.getText());
    }
    
    public void setFrameNumber(int number) {
        tfFrameNumber.setText(Integer.toString(number));
    }
    
    public void setImage(BufferedImage image) {
        imagePanel.setImage(image);
    }
    
    public static void setFramesList(List<BufferedImage> list) {
        framesList = list;
    }
    
    public void setUnsavedChangesListener(UnsavedChangesListener listener) {
        tfFrameNumber.getDocument().addDocumentListener(listener);
    }
    
    @Override
    public void mouseClicked(MouseEvent e) {
    
    }
    
    @Override
    public void mousePressed(MouseEvent e) {
        if (e.getButton() == MouseEvent.BUTTON3) {
            setFrameNumber(0);
        }
    }
    
    @Override
    public void mouseReleased(MouseEvent e) {
    
    }
    
    @Override
    public void mouseEntered(MouseEvent e) {
    
    }
    
    @Override
    public void mouseExited(MouseEvent e) {
    
    }
    
    public void setFramePreviewDimensions(int width, int height) {
        imagePanel.setPreviewSize(width, height);
    }
}