package pekaeds.ui.sector;

import javax.imageio.ImageIO;
import java.util.Map;
import java.awt.*;
import java.io.*;

import javax.swing.*;


import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import net.miginfocom.swing.MigLayout;
import pekaeds.filechooser.ImagePreviewFileChooser;
import pekaeds.pk2.file.PK2FileSystem;
import pekaeds.pk2.map.PK2Map;
import pekaeds.pk2.map.PK2MapSector;
import pekaeds.settings.Settings;
import pekaeds.ui.filefilters.BMPImageFilter;
import pekaeds.ui.filefilters.MusicFilter;
import pekaeds.ui.listeners.PK2MapConsumer;
import pekaeds.ui.listeners.PK2SectorConsumer;
import pekaeds.ui.listeners.TextFieldChangeListener;
import pekaeds.ui.main.PekaEDSGUI;
import pekaeds.util.GFXUtils;

import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import org.tinylog.Logger;


public class SectorMetadataPanel extends JPanel
        implements PK2SectorConsumer,
                    PK2MapConsumer,
                    ChangeListener,
                    ActionListener {

    private static final BMPImageFilter BMP_IMAGE_FILTER = new BMPImageFilter();
    private static final MusicFilter MUSIC_FILTER = new MusicFilter();

    private ChangeListener changeListener;
    private ChangeEvent changeEvent = new ChangeEvent(this);

    private PekaEDSGUI gui;
    private boolean canFireChanges = false;

    private PK2MapSector sector;
    private PK2Map map;

    private JTextField tfSectorName;
    private JTextField tfTileset;
    private JTextField tfBgTileset;
    private JTextField tfBackground;
    private JTextField tfMusic;
    private JTextField tfPK2stuff;

    private JComboBox<String> cbWeather;
    private JComboBox<String> cbScrollingType;

    private JComboBox<String> cbSplashColor;
    private JComboBox<String> cbFireColor1;
    private JComboBox<String> cbFireColor2;

    private JButton btnBrowseTileset;
    private JButton btnBrowseBgTileset;
    private JButton btnBrowseBackground;
    private JButton btnBrowseMusic;

    private JButton btnResizeSector;

    private JButton btnRemoveBgTileset;

    private JSpinner spWidth;
    private JSpinner spHeight;

    private SpinnerNumberModel widthSpinnerModel;
    private SpinnerNumberModel heightSpinnerModel;

    private boolean editingExistingSector = true;

    public SectorMetadataPanel(PekaEDSGUI gui) {
        setupUI();
        addChangeListeners();

        this.gui = gui;
    }

    public SectorMetadataPanel() {
        editingExistingSector = false;

        setupUI();
        addButtonActions();
    }

    public static DefaultComboBoxModel<String> getFireColorsModel(){
        DefaultComboBoxModel<String> fireColorsModel = new DefaultComboBoxModel<String>();
        for (var col : Settings.getMapProfile().getFireColors().entrySet()) {
            fireColorsModel.addElement(col.getValue());
        }

        return fireColorsModel;
    }

    public void setChangeListener(ChangeListener listener) {
        this.changeListener = listener;
    }

    public void setupUI(){
        JLabel lblMapName = new JLabel("Sector name:");
        tfSectorName = new JTextField();
    
        JLabel lblTileset = new JLabel("Tileset:");
        tfTileset = new JTextField();

        JLabel lblTilesetBG = new JLabel("Tileset Bg:");
        tfBgTileset = new JTextField();
    
        JLabel lblBackground = new JLabel("Background:");
        tfBackground = new JTextField();
    
        JLabel lblMusic = new JLabel("Music:");
        tfMusic = new JTextField();

        JLabel lblPK2stuff = new JLabel("Custom gfx texture:");
        tfPK2stuff = new JTextField();
    
        JLabel lblWeather = new JLabel("Weather:");
        cbWeather = new JComboBox<>();
    
        JLabel lblScrolling = new JLabel("Scrolling:");
        cbScrollingType = new JComboBox<>();

        JLabel lblSplashColor = new JLabel("Splash color:");
        cbSplashColor = new JComboBox<>();

        JLabel lblFireColor1 = new JLabel("Fire color 1:");
        cbFireColor1 = new JComboBox<>();

        JLabel lblFireColor2 = new JLabel("Fire color 2:");
        cbFireColor2 = new JComboBox<>();
        
        DefaultComboBoxModel<String> scrollingModel = (DefaultComboBoxModel<String>) cbScrollingType.getModel();
        scrollingModel.addAll(Settings.getMapProfile().getScrollingTypes());
    
        DefaultComboBoxModel<String> weatherModel = (DefaultComboBoxModel<String>) cbWeather.getModel();
        weatherModel.addAll(Settings.getMapProfile().getWeatherTypes());

        DefaultComboBoxModel<String> splashColorsModel = new DefaultComboBoxModel<String>();
        for (var col : Settings.getMapProfile().getSplashColors().entrySet()) {
            splashColorsModel.addElement(col.getValue());
        }

        cbSplashColor.setModel(splashColorsModel);
        cbFireColor1.setModel(getFireColorsModel());
        cbFireColor2.setModel(getFireColorsModel());

        btnBrowseTileset = new JButton("...");
        btnBrowseBgTileset = new JButton("...");
        btnBrowseBackground = new JButton("...");
        btnBrowseMusic = new JButton("...");

        btnResizeSector = new JButton("Resize");

        btnRemoveBgTileset = new JButton("No different BG tileset");

        widthSpinnerModel = new SpinnerNumberModel(PK2MapSector.CLASSIC_WIDTH, 25, 100000, 1);
        spWidth = new JSpinner(widthSpinnerModel);

        heightSpinnerModel = new SpinnerNumberModel(PK2MapSector.CLASSIC_HEIGHT, 15, 100000, 1);
        spHeight = new JSpinner(heightSpinnerModel);

        // Lay out components
        JPanel p = new JPanel();
        p.setLayout(new MigLayout("wrap 3"));

        p.add(lblMapName);
        p.add(tfSectorName, "span 2, width 100px");
    
        p.add(new JSeparator(JSeparator.HORIZONTAL), "span 3");
    
        p.add(lblTileset);
        p.add(tfTileset, "width 100px");
        p.add(btnBrowseTileset);

        p.add(lblBackground);
        p.add(tfBackground, "width 100px");
        p.add(btnBrowseBackground);
    
        p.add(lblMusic);
        p.add(tfMusic, "width 100px");
        p.add(btnBrowseMusic);
               
        p.add(lblWeather);
        p.add(cbWeather, "span 2");
        
        p.add(lblScrolling);
        p.add(cbScrollingType, "span 2");

        p.add(new JSeparator(JSeparator.HORIZONTAL), "span 3");

        p.add(lblFireColor1);
        p.add(cbFireColor1);

        p.add(new JSeparator(JSeparator.HORIZONTAL), "span 3");

        p.add(lblFireColor2);
        p.add(cbFireColor2);

        p.add(new JSeparator(JSeparator.HORIZONTAL), "span 3");
        p.add(new JLabel("Optional:"));

        p.add(new JSeparator(JSeparator.HORIZONTAL), "span 3");

        p.add(lblTilesetBG);
        p.add(tfBgTileset, "width 100px");
        p.add(btnBrowseBgTileset);

        p.add(new JSeparator(JSeparator.HORIZONTAL), "span 3");

        p.add(btnRemoveBgTileset);

        p.add(new JSeparator(JSeparator.HORIZONTAL), "span 3");

        p.add(lblSplashColor);
        p.add(cbSplashColor);

        p.add(new JSeparator(JSeparator.HORIZONTAL), "span 3");

        p.add(lblPK2stuff);
        p.add(tfPK2stuff);

        p.add(new JSeparator(JSeparator.HORIZONTAL), "span 3");

        if (!editingExistingSector) {

            JButton btnClassicSize = new JButton("Classic size");
            JButton btnMinimalSize = new JButton("Minimal size");

            btnClassicSize.addActionListener(e->{
                spWidth.setValue(PK2MapSector.CLASSIC_WIDTH);
                spHeight.setValue(PK2MapSector.CLASSIC_HEIGHT);
            });


            btnMinimalSize.addActionListener(e->{
                spWidth.setValue(25);
                spHeight.setValue(15);
            });

            p.add(new JLabel("Width:"));
            p.add(spWidth);

            p.add(new JSeparator(JSeparator.HORIZONTAL), "span 3");

            p.add(new JLabel("Height:"));
            p.add(spHeight);

            p.add(new JSeparator(JSeparator.HORIZONTAL), "span 3");
            p.add(btnClassicSize);
            p.add(new JSeparator(JSeparator.HORIZONTAL), "span 3");
            p.add(btnMinimalSize);

        } /*else {
            p.add(btnResizeSector);
        }*/

        add(p, BorderLayout.CENTER);
    }

    public void addChangeListeners(){
        var tfl = new TextFieldChangeListener(this);
        tfSectorName.getDocument().addDocumentListener(tfl);

        tfTileset.getDocument().addDocumentListener(tfl);
        tfBackground.getDocument().addDocumentListener(tfl);
        tfMusic.getDocument().addDocumentListener(tfl);
        tfBgTileset.getDocument().addDocumentListener(tfl);
        tfPK2stuff.getDocument().addDocumentListener(tfl);

        cbWeather.addActionListener(this);
        cbScrollingType.addActionListener(this);

        cbSplashColor.addActionListener(this);
        cbFireColor1.addActionListener(this);
        cbFireColor2.addActionListener(this);

        addButtonActions();
    }

    private void addButtonActions() {
        btnBrowseTileset.addActionListener(e->{
            var fc = new ImagePreviewFileChooser(PK2FileSystem.getAssetsPath(PK2FileSystem.TILESET_DIR),
                    ImagePreviewFileChooser.PREVIEW_TILESET);

            fc.setDialogTitle("Select a tileset image...");
            fc.setFileFilter(BMP_IMAGE_FILTER);

            if (fc.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
                try {
                    var tilesetImage = ImageIO.read(fc.getSelectedFile());

                    tilesetImage = GFXUtils.setPaletteToBackgrounds(tilesetImage, sector.getBackgroundImage());

                    sector.tilesetName = fc.getSelectedFile().getName();
                    sector.tilesetImage = tilesetImage;
                    tfTileset.setText(sector.tilesetName);

                    if (editingExistingSector) {
                        gui.updateRepaintListeners();
                        fireChanges();
                    }
                } catch (IOException ex) {
                    Logger.warn(ex, "Unable to load tileset image.");
                }
            }
        });

        btnBrowseBgTileset.addActionListener(e->{
            var fc = new ImagePreviewFileChooser(PK2FileSystem.getAssetsPath(PK2FileSystem.TILESET_DIR),
                    ImagePreviewFileChooser.PREVIEW_TILESET);

            fc.setDialogTitle("Select a background tileset image...");

            // TODO Make the filter a static final member of this class
            fc.setFileFilter(BMP_IMAGE_FILTER);

            if (fc.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
                try {
                    var tilesetImage = ImageIO.read(fc.getSelectedFile());

                    tilesetImage = GFXUtils.setPaletteToBackgrounds(tilesetImage, sector.getBackgroundImage());

                    sector.tilesetBgName = fc.getSelectedFile().getName();
                    sector.tilesetBgImage = tilesetImage;

                    tfBgTileset.setText(sector.tilesetBgName);

                    if (editingExistingSector) {
                        gui.updateRepaintListeners();
                        fireChanges();
                    }
                } catch (IOException ex) {
                    Logger.warn(ex, "Unable to load tileset image.");
                }
            }
        });

        btnBrowseBackground.addActionListener(e -> {
            var fc = new ImagePreviewFileChooser(PK2FileSystem.getAssetsPath(PK2FileSystem.SCENERY_DIR),
                    ImagePreviewFileChooser.PREVIEW_BACKGROUND);

            fc.setDialogTitle("Select a background image...");

            fc.setFileFilter(BMP_IMAGE_FILTER);

            if (fc.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
                try {
                    var backgroundImage= ImageIO.read(fc.getSelectedFile());

                    // This is hacky, but I just want to get this project done and this works
                    // The tileset should only have its palette adjusted when a tileset image has been loaded, which is not the case when adding a new sector
                    if (editingExistingSector) {
                        var tilesetImage = GFXUtils.setPaletteToBackgrounds(sector.getTilesetImage(), backgroundImage);
                        sector.tilesetImage = tilesetImage;

                        if (sector.tilesetBgImage != null) {
                            sector.tilesetBgImage = GFXUtils.setPaletteToBackgrounds(sector.getTilesetBgImage(), backgroundImage);
                        }
                    }

                    sector.setBackgroundImage(backgroundImage);
                    sector.backgroundName = fc.getSelectedFile().getName();

                    tfBackground.setText(sector.backgroundName);

                    if (editingExistingSector) {
                        sector.updateSpritePalettes(map.getSpriteList());

                        gui.updateRepaintListeners();
                        fireChanges();
                    }
                } catch (IOException ex) {
                    Logger.warn(ex, "Unable to load background image.");
                }
            }
        });

        btnBrowseMusic.addActionListener(e -> {
            var fc = new JFileChooser(PK2FileSystem.getAssetsPath(PK2FileSystem.MUSIC_DIR));
            fc.setDialogTitle("Select a music file...");

            fc.setFileFilter(MUSIC_FILTER);

            if (fc.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
                tfMusic.setText(fc.getSelectedFile().getName());

                sector.musicName = tfMusic.getText();

                if (editingExistingSector) fireChanges();
            }
        });

        btnRemoveBgTileset.addActionListener(e -> {
            sector.tilesetBgName = null;
            sector.tilesetBgImage = null;
            tfBgTileset.setText("");

            if (editingExistingSector)  {
                gui.updateRepaintListeners();
                fireChanges();
            }
        });

        btnResizeSector.addActionListener(e -> {
            gui.startSectorResize();
        });
    }

    @Override
    public void setSector(PK2MapSector newSector) {
        sector = newSector;
        canFireChanges = false;

        tfSectorName.setText(sector.name);

        tfTileset.setText(sector.tilesetName);
        tfBackground.setText(sector.backgroundName);
        tfMusic.setText(sector.musicName);
        tfBgTileset.setText(sector.tilesetBgName);
        tfPK2stuff.setText(sector.pk2stuffName);

        cbWeather.setSelectedIndex(sector.weather);
        cbScrollingType.setSelectedIndex(sector.background_scrolling);

        Map<Integer, String> splashColors = Settings.getMapProfile().getSplashColors();
        cbSplashColor.setSelectedItem(splashColors.get(sector.splash_color));

        Map<Integer, String> fireColors = Settings.getMapProfile().getFireColors();

        cbFireColor1.setSelectedItem(fireColors.get(sector.fire_color_1));
        cbFireColor2.setSelectedItem(fireColors.get(sector.fire_color_2));

        spWidth.setValue(sector.getWidth());
        spHeight.setValue(sector.getHeight());

        canFireChanges = true;
    }

    public void setSectorData(PK2MapSector sector) {
        sector.name = tfSectorName.getText();

        sector.tilesetName = tfTileset.getText();
        sector.backgroundName = tfBackground.getText();
        sector.tilesetBgName = tfBgTileset.getText();
        sector.pk2stuffName = tfPK2stuff.getText();

        sector.musicName = tfMusic.getText();

        sector.weather = cbWeather.getSelectedIndex();
        sector.background_scrolling = cbScrollingType.getSelectedIndex();

        Map<Integer, String> splashColors = Settings.getMapProfile().getSplashColors();
        for (var c : splashColors.entrySet()) {
            if (c.getValue().equals(cbFireColor2.getSelectedItem())) {
                sector.splash_color = c.getKey();

                break;
            }
        }

        Map<Integer, String> fireColors = Settings.getMapProfile().getFireColors();
        for (var c : fireColors.entrySet()) {
            if (c.getValue().equals(cbFireColor1.getSelectedItem())) {
                sector.fire_color_1 = c.getKey();

                break;
            }
        }

        for (var c : fireColors.entrySet()) {
            if (c.getValue().equals(cbFireColor2.getSelectedItem())) {
                sector.fire_color_2 = c.getKey();

                break;
            }
        }

        sector.setSize(0, 0, (int) spWidth.getValue(), (int) spHeight.getValue());
    }

    private static int getMapColor(Map<Integer, String> colors, String value){
        for(var entry : colors.entrySet()){
            if(entry.getValue().equals(value)){
                return entry.getKey();
            }
        }

        return 0;
    }

    @Override
    public void stateChanged(ChangeEvent e) {
        if(canFireChanges){
            fireChanges();
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if(canFireChanges){
            fireChanges();
        }
    }

    private void fireChanges() {
        if (canFireChanges) {            
            changeListener.stateChanged(changeEvent);
        }
    }

    public void commitValues(){
        sector.name = tfSectorName.getText();
        sector.tilesetName = tfTileset.getText();
        sector.backgroundName = tfBackground.getText();
        sector.musicName = tfMusic.getText();
        sector.tilesetBgName = tfBgTileset.getText();
        sector.pk2stuffName = tfPK2stuff.getText();

        sector.weather = cbWeather.getSelectedIndex();
        sector.background_scrolling = cbScrollingType.getSelectedIndex();

        Map<Integer, String> splashColors = Settings.getMapProfile().getSplashColors();
        Map<Integer, String> fireColors = Settings.getMapProfile().getFireColors();

        sector.splash_color = getMapColor(splashColors, (String) cbSplashColor.getSelectedItem());
        sector.fire_color_1 = getMapColor(fireColors, (String) cbFireColor1.getSelectedItem());
        sector.fire_color_2 = getMapColor(fireColors, (String) cbFireColor2.getSelectedItem());
    }

    @Override
    public void setMap(PK2Map newMap) {
        map = newMap;
    }
}
