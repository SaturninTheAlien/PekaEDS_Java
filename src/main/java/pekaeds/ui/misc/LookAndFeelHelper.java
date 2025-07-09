package pekaeds.ui.misc;

import java.awt.Color;
import java.util.ArrayList;

import javax.swing.UIManager;

import org.tinylog.Logger;

import com.formdev.flatlaf.FlatDarculaLaf;
import com.formdev.flatlaf.FlatDarkLaf;
import com.formdev.flatlaf.FlatIntelliJLaf;
import com.formdev.flatlaf.FlatLightLaf;

import pk2.settings.Settings;

public class LookAndFeelHelper {

    private static ArrayList<String> themes = new ArrayList<>();
    static{
        themes.add("FlatLaf Light");
        themes.add("FlatLaf Dark");
        themes.add("FlatLaf IntelliJ");
        themes.add("FlatLaf Darcula");
    }

    public static String getDefaultTheme(){
        return "FlatLaf Light";
    }

    public static ArrayList<String> getSupportedThemesList(){
        return themes;
    }

    public static void updateTheme(){
        try {
            String theme = Settings.getLookAndFeel();
            switch (theme) {
                case "FlatLaf Light":
                    UIManager.setLookAndFeel(new FlatLightLaf());                    
                    break;
                
                case "FlatLaf Dark":
                    UIManager.setLookAndFeel(new FlatDarkLaf());                    
                    break;

                case "FlatLaf Darcula":
                    UIManager.setLookAndFeel(new FlatDarculaLaf());
                    break;

                case "FlatLaf IntelliJ":
                    UIManager.setLookAndFeel(new FlatIntelliJLaf());
                    break;
           
                default:
                    UIManager.setLookAndFeel(theme);
                    break;
            }

        } catch (Exception e) {
            Logger.error(e);
        }
    }

    public static Color getBGColor(){
        return UIManager.getColor("TextField.background");
    }

    public static Color getFGColor(){
        return UIManager.getColor("TextField.foreground");
    }


    
}
