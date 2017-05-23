import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.util.*;

/**
 * Creation time: 03:05
 * Created by Dominik on 28.04.2015.
 */
public class SettingsManager {
    private static final String STANDARD_SETTINGS_FILENAME = "settings.ini";
    private static final String STANDARD_SECTION_NAME = "mainsettings";
    private String workingDirectory;
    private IniFile iniFile;
    private String regexFile;
    private Hashtable<String, Object> htDefaultValues;

    // Settings key-strings
    private static final String CONVERT_TO_MP3 = "converttomp3";
    private static final String REMOVE_GEMA = "removegema";
    private static final String FFMPEG = "ffmpeg";
    private static final String REMOVE_MP4 = "removemp4";
    private static final String MIN_SIZE = "minsize";
    private static final String SAVE_PATH = "savepath";

    public SettingsManager() {
        workingDirectory = System.getProperty("user.dir");

        regexFile = workingDirectory;
        if (CGlobals.CURRENT_OS == OS.Windows)
            regexFile += "\\\\regex_temps.ini";
        else if (CGlobals.CURRENT_OS == OS.Linux)
            regexFile += "/regex_temps.ini";

        readIniFile();
        initDefaultValues();
    }

    public String getStandardSectionName() {
        return STANDARD_SECTION_NAME;
    }

    private void readIniFile() {
        String settingsFile = workingDirectory;
        if (CGlobals.CURRENT_OS == OS.Windows)
            settingsFile += "\\\\" + STANDARD_SETTINGS_FILENAME;
        else if (CGlobals.CURRENT_OS == OS.Linux)
            settingsFile += "/" + STANDARD_SETTINGS_FILENAME;

        iniFile = new IniFile(settingsFile);
        try {
            iniFile.read();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void initDefaultValues() {
        // Initialize default values
        // (if no entry in ini was found
        // this values are applied)
        htDefaultValues = new Hashtable<>();
        htDefaultValues.put(CONVERT_TO_MP3, true);
        htDefaultValues.put(REMOVE_GEMA, true);
        htDefaultValues.put(FFMPEG, "{wd}\\bin\\");
        htDefaultValues.put(REMOVE_MP4, false);
        htDefaultValues.put(MIN_SIZE, false);

        String sDefaultSave = "";
        switch (CGlobals.CURRENT_OS) {
            case Windows:
                sDefaultSave = "C:\\\\";
                break;
            case Linux:
                sDefaultSave = "/home/";
                break;
            default:
                sDefaultSave = "/";
        }
        htDefaultValues.put(SAVE_PATH, sDefaultSave);
    }

    public String GetStandardSavePath() {
        String sSavePath = iniFile.getIniValue(STANDARD_SECTION_NAME, SAVE_PATH);
        return sSavePath != null ? sSavePath : (String) htDefaultValues.get(SAVE_PATH);
    }

    // Default: true
    public boolean GetConvertToMP3() {
        String sConvertToMp3 = iniFile.getIniValue(STANDARD_SECTION_NAME, CONVERT_TO_MP3);
        if (sConvertToMp3 != null) {
            return sConvertToMp3.toLowerCase().equals("false") ? false : true;
        }
        return (boolean) htDefaultValues.get(CONVERT_TO_MP3);
    }

    // Default: true
    public boolean GetRemoveGEMA() {
        String sRemGema = iniFile.getIniValue(STANDARD_SECTION_NAME, REMOVE_GEMA);
        if (sRemGema != null) {
            return sRemGema.toLowerCase().equals("false") ? false : true;
        }
        return (boolean) htDefaultValues.get(REMOVE_GEMA);
    }

    public String GetSettingsFile() {
        return iniFile.getIniFilePath();
    }

    public void ShowSettingsWindow(FreshUI win) {
        new SettingsManagerWindow(this, win);
    }

    public void ShowSettingsWindow() {
        new SettingsManagerWindow(this);
    }

    // Default: {wd}\\bin\\
    public String GetFFMPEGDir() {
        if (CGlobals.CURRENT_OS.equals(OS.Linux))
            return "Not needed under Linux";

        String sRemGema = iniFile.getIniValue(STANDARD_SECTION_NAME, FFMPEG);
        return sRemGema != null ? sRemGema : (String) htDefaultValues.get(FFMPEG);
    }

    // Default: false
    public boolean GetRemoveMP4() {
        String sRemFiles = iniFile.getIniValue(STANDARD_SECTION_NAME, REMOVE_MP4);
        if (sRemFiles != null) {
            return sRemFiles.toLowerCase().equals("true") ? true : false;
        }
        return (boolean) htDefaultValues.get(REMOVE_MP4);
    }

    // Default: false
    public boolean GetMinimumSize() {
        String sMinSize = iniFile.getIniValue(STANDARD_SECTION_NAME, MIN_SIZE);
        if (sMinSize != null) {
            return sMinSize.toLowerCase().equals("true") ? true : false;
        }
        return (boolean) htDefaultValues.get(MIN_SIZE);
    }

    /*
        Setters do only set value if value has changed /
        if value is not the same as the default value.
        If default value is set the entry is removed
        from the ini file.
     */
    public void setSavePath(String sPath) {
        if (sPath.equals(htDefaultValues.get(SAVE_PATH))) {
            iniFile.removeIniValue(STANDARD_SECTION_NAME, SAVE_PATH);
            return;
        }
        if (!sPath.equals(GetStandardSavePath()))
            iniFile.setIniValue(STANDARD_SECTION_NAME, SAVE_PATH, sPath);

    }

    public void setConvertToMP3(boolean bConvMp3) {
        if (bConvMp3 == (boolean) htDefaultValues.get(CONVERT_TO_MP3)) {
            iniFile.removeIniValue(STANDARD_SECTION_NAME, CONVERT_TO_MP3);
            return;
        }
        if (bConvMp3 != GetConvertToMP3())
            iniFile.setIniValue(STANDARD_SECTION_NAME, CONVERT_TO_MP3, bConvMp3 + "");
    }

    public void setRemoveGema(boolean bRemGema) {
        if (bRemGema == (boolean) htDefaultValues.get(REMOVE_GEMA)) {
            iniFile.removeIniValue(STANDARD_SECTION_NAME, REMOVE_GEMA);
            return;
        }
        if (bRemGema != GetRemoveGEMA())
            iniFile.setIniValue(STANDARD_SECTION_NAME, REMOVE_GEMA, bRemGema + "");
    }

    public void setFFMPEGPath(String sPath) {
        if (sPath.equals(htDefaultValues.get(FFMPEG))) {
            iniFile.removeIniValue(STANDARD_SECTION_NAME, FFMPEG);
            return;
        }
        if (!sPath.equals(GetFFMPEGDir()))
            iniFile.setIniValue(STANDARD_SECTION_NAME, FFMPEG, sPath);
    }

    public void setRemoveMP4(boolean bRemMp4) {
        if (bRemMp4 == (boolean) htDefaultValues.get(REMOVE_MP4)) {
            iniFile.removeIniValue(STANDARD_SECTION_NAME, REMOVE_MP4);
        }
        if (bRemMp4 != GetRemoveMP4())
            iniFile.setIniValue(STANDARD_SECTION_NAME, REMOVE_MP4, bRemMp4 + "");
    }

    public void setMinSize(boolean bMinSize) {
        if (bMinSize == (boolean) htDefaultValues.get(MIN_SIZE)) {
            iniFile.removeIniValue(STANDARD_SECTION_NAME, MIN_SIZE);
            return;
        }
        if (bMinSize != GetMinimumSize())
            iniFile.setIniValue(STANDARD_SECTION_NAME, MIN_SIZE, bMinSize + "");
    }

    public void saveSettings() {
        try {
            iniFile.save();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public void WriteRegexToFile(String regex) {
        File regex_template = new File(regexFile);

        if (!regex_template.exists()) {
            // TODO: implement regex templates!
        } else {

        }
    }
}

class SettingsManagerWindow extends JDialog {
    private JLabel lblSavePath;
    private JLabel lblConvertToMp3;
    private JLabel lblRemoveGEMA;
    private JLabel lblRemoveMp4;
    private JLabel lblFFMPEGFile;
    private JTextField txtSavePath;
    private JTextField txtFFMPEG;
    private JCheckBox checkConvertToMp3;
    private JCheckBox checkRemoveGEMA;
    private JCheckBox checkRemoveMp4;
    private JButton btnSave;
    private JButton btnCancel;
    private String settingsFile;
    private JButton btnSelectFFMPEG;
    private JButton btnSelectStandardSave;
    private JFileChooser dirChooser;
    private JLabel lblCheckMinSize;
    private JCheckBox checkMinimumSize;

    public SettingsManagerWindow(SettingsManager man, FreshUI win) {
        setTitle("Change settings");

        //lblSavePath = new JLabel("Standard save path:");
        btnSelectStandardSave = new JButton("Select standard save path:");
        btnSelectStandardSave.addActionListener(e -> {
            String path = "";

            dirChooser = new JFileChooser();
            dirChooser.setDialogTitle("Select the path where files will be stored ...");
            dirChooser.setCurrentDirectory(new File(System.getProperty("user.dir")));
            dirChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
            dirChooser.setAcceptAllFileFilterUsed(false);

            if (dirChooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION)
                path = dirChooser.getSelectedFile().getAbsolutePath();

            if (CGlobals.CURRENT_OS == OS.Windows)
                path = path.replace("\\", "\\\\");

            txtSavePath.setText(path);
        });
        txtSavePath = new JTextField(man.GetStandardSavePath());
        lblConvertToMp3 = new JLabel("Convert to mp3");
        checkConvertToMp3 = new JCheckBox("", man.GetConvertToMP3());
        lblRemoveMp4 = new JLabel("Remove video files after mp3 created");
        checkRemoveMp4 = new JCheckBox("", man.GetRemoveMP4());
        lblRemoveGEMA = new JLabel("Remove GEMA");
        checkRemoveGEMA = new JCheckBox("", man.GetRemoveGEMA());
        lblCheckMinSize = new JLabel("Allow window to get smaller than minimum size");
        checkMinimumSize = new JCheckBox("");
        //lblFFMPEGFile = new JLabel("FFMPEG-Directory");
        btnSelectFFMPEG = new JButton("Select FFMPEG-Directory");
        btnSelectFFMPEG.addActionListener(e -> {
            String path = "";

            dirChooser = new JFileChooser();
            dirChooser.setDialogTitle("Select the path where FFMPEG.exe is located");
            dirChooser.setCurrentDirectory(new File(System.getProperty("user.dir")));
            dirChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
            dirChooser.setAcceptAllFileFilterUsed(false);

            if (dirChooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION)
                path = dirChooser.getSelectedFile().getAbsolutePath();

            if (CGlobals.CURRENT_OS == OS.Windows)
                path = path.replace("\\", "\\\\");

            txtFFMPEG.setText(path);
        });
        txtFFMPEG = new JTextField(man.GetFFMPEGDir().replace("{wd}", System.getProperty("user.dir")));

        btnCancel = new JButton("Cancel");
        btnCancel.addActionListener(e -> {
            dispose();
            //vs setVisible(false);
        });
        btnSave = new JButton("Save & Close");
        btnSave.addActionListener(e -> {
            try {
                // Values are stored to SettingsManager - object
                // and written to disk on application exit
                man.setSavePath(txtSavePath.getText());
                man.setConvertToMP3(checkConvertToMp3.isSelected());
                man.setRemoveGema(checkRemoveGEMA.isSelected());
                man.setFFMPEGPath(txtFFMPEG.getText().replace(System.getProperty("user.dir"), "{wd}"));
                man.setRemoveMP4(checkRemoveMp4.isSelected());
                man.setMinSize(checkMinimumSize.isSelected());

                JOptionPane.showMessageDialog(null, "Successfully accepted settings! Changes will apply on application restart...", "Success", JOptionPane.INFORMATION_MESSAGE);
                dispose();
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        });

        JPanel panel = new JPanel(new GridLayout(0, 2));
        //panel.add(lblSavePath);
        panel.add(btnSelectStandardSave);
        panel.add(txtSavePath);
        panel.add(lblConvertToMp3);
        panel.add(checkConvertToMp3);
        panel.add(lblRemoveMp4);
        panel.add(checkRemoveMp4);
        panel.add(lblRemoveGEMA);
        panel.add(checkRemoveGEMA);
        panel.add(lblCheckMinSize);
        panel.add(checkMinimumSize);
        //panel.add(lblFFMPEGFile);
        panel.add(btnSelectFFMPEG);
        panel.add(txtFFMPEG);
        panel.add(btnCancel);
        panel.add(btnSave);

        getContentPane().add(panel, BorderLayout.CENTER);
        setVisible(true);
        pack();
        setLocationRelativeTo(win);
        settingsFile = man.GetSettingsFile();
    }

    public SettingsManagerWindow(SettingsManager man) {
        new SettingsManagerWindow(man, null);
    }
}
