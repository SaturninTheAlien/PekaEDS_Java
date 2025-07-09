package pekase3.panels.ailists;

import net.miginfocom.swing.MigLayout;
import pekase3.FileFormat;
import pekase3.listener.UnsavedChangesListener;
import pekase3.panels.PekaSE2Panel;
import pekase3.profile.SpriteProfile;
import pekase3.settings.Settings;
import pk2.sprite.PK2Sprite;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.text.DefaultFormatter;
import javax.swing.text.DefaultFormatterFactory;

import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.List;

public class AIListPanel extends PekaSE2Panel {
    private Settings settings;
    
    private List<JComboBox<String>> cbAiList;
    private List<JSpinner> spAiList;
    private List<JPanel> aiPanelList;
    
    private List<Integer> aiIdList;
    private List<String> aiTextList;

    private JPanel pnlAddAI;
    
    private UnsavedChangesListener unsavedChangesListener;
    
    public AIListPanel(Settings settings) {
        this.settings = settings;
        
        cbAiList = new ArrayList<>();
        spAiList = new ArrayList<>();
        aiPanelList = new ArrayList<>();
        
        setup();
    }
    
    private void setup() {
        setLayout(new MigLayout("flowy, align center"));
        
        JButton btnAdd = new JButton("Add AI");
        btnAdd.addActionListener(e -> {
            addComboBox(cbAiList.size() + 1);
            
            unsavedChangesListener.stateChanged(null);
        });
        
        JButton btnRemove = new JButton("Remove Last");
        btnRemove.addActionListener(e -> {
            if (aiPanelList.size() > 1) {
                cbAiList.remove((cbAiList.size() - 1));
                spAiList.remove((spAiList.size() - 1));
                
                JPanel pnl = aiPanelList.get(aiPanelList.size() - 1);
                remove(pnl);
                
                aiPanelList.remove(aiPanelList.size() - 1);
                
                revalidate();
                repaint();
                
                unsavedChangesListener.stateChanged(null);
            }
        });
        
        pnlAddAI = new JPanel();
        pnlAddAI.setLayout(new MigLayout("flowx"));
        pnlAddAI.add(btnAdd);
        pnlAddAI.add(btnRemove);
        add(pnlAddAI);
        
        pnlAddAI.setVisible(false);

        for (int i = 0; i < 10; i++) {
            addComboBox(i);
        }
    }

    public void setFileFormat(FileFormat format) {
        pnlAddAI.setVisible(format == FileFormat.GRETA);
    }
    
    public void setSprite(PK2Sprite sprite) {
        for (int i = 0; i < sprite.getAiList().size(); i++) {
            cbAiList.get(i).setSelectedItem(settings.getSpriteProfile().getAiPatternMap().get(sprite.getAiList().get(i)));
            spAiList.get(i).setValue(sprite.getAiList().get(i));
        }
    }
    
    @Override
    public void resetValues() {
        for (var c : cbAiList) {
            if (c.getItemCount() > 0) c.setSelectedIndex(0);
        }
        
        for (var s : spAiList) {
            s.setValue(0);
        }
    }
    
    @Override
    public void setValues(PK2Sprite sprite) {
        sprite.getAiList().clear();
        
        for (int i = 0; i < 10; i++) {
            sprite.getAiList().add((int) spAiList.get(i).getValue());
        }
    }
    
    @Override
    public void setProfileData(SpriteProfile profile) {
        aiIdList = profile.getAiPatternMap().keySet().stream().toList();
        aiTextList = profile.getAiPatternMap().values().stream().toList();
        
        for (var cb : cbAiList) {
            cb.removeAllItems();
            
            for (var item : aiTextList) {
                cb.addItem(item);
            }
        }
        
        for (var sp : spAiList) {
            sp.setModel(new SpinnerListModel(aiIdList));
        }
    }
    
    @Override
    public void setUnsavedChangesListener(UnsavedChangesListener listener) {
        this.unsavedChangesListener = listener;
        
        for (int i = 0; i < 10; i++) {
            cbAiList.get(i).addActionListener(listener);
            spAiList.get(i).addChangeListener(listener);
        }
    }
    
    private void addComboBox(int index) {
        var cbAi = new JComboBox<String>();
        
        var sp = new JSpinner();

        if (aiIdList == null) {
            sp.setModel(new SpinnerListModel(List.of(0))); // Have to provide a list of one int, because SpinnerListModel's empty constructor for some reason provides one empty string???
        } else {
            sp.setModel(new SpinnerListModel(aiIdList));
            
            // When the aiIdList is set the aiTextList will also be set
            for (var item : aiTextList) {
                cbAi.addItem(item);
            }
        }
        
        sp.setEditor(new CustomEditor(sp));
        sp.addChangeListener(new SpinnerListener(cbAi, sp));
        
        var pnl = new JPanel();
        pnl.setLayout(new MigLayout("flowx"));
        pnl.add(new JLabel((index + 1) + ": "), "width 5%");
        pnl.add(cbAi);
        pnl.add(sp);
        
        aiPanelList.add(pnl);
        
        add(pnl);
        
        cbAi.addItemListener(l -> {
            if (cbAi.getSelectedIndex() > -1 && cbAi.getSelectedIndex() < aiIdList.size()) {
                sp.setValue(aiIdList.get(cbAi.getSelectedIndex()));
            }
        });
        
        spAiList.add(sp);
        cbAiList.add(cbAi);
        
        revalidate();
        repaint();
    }
    
    private class SpinnerListener implements ChangeListener {
        private JComboBox<String> comboBox;
        private JSpinner spinner;
        
        public SpinnerListener(JComboBox<String> comboBox, JSpinner spinner) {
            this.comboBox = comboBox;
            
            this.spinner = spinner;
        }
        
        @Override
        public void stateChanged(ChangeEvent e) {
            comboBox.setSelectedItem(settings.getSpriteProfile().getAiPatternMap().get((int) spinner.getValue()));
        }
    }
    
    class CustomEditor extends JFormattedTextField implements ChangeListener {
        
        private final JSpinner spinner;
        
        public CustomEditor(JSpinner spinner) {
            super();
            
            this.spinner = spinner;
            
            var formatter = new DefaultFormatter();
            formatter.setOverwriteMode(true);
            
            setFormatterFactory(new DefaultFormatterFactory(formatter));
            
            updateValue();
            
            spinner.addChangeListener(this);
            
            addActionListener((ActionEvent e) -> {
                this.spinner.getModel().setValue(this.getValue());
            });
        }
        
        @Override
        public void stateChanged(ChangeEvent e) {
            updateValue();
        }
        
        private void updateValue() {
            setValue((int) spinner.getModel().getValue());
        }
    }
}
