import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Creation time: 20:15
 * Created by Dominik on 04.08.2015.
 */
public class SettingsManagerPanel extends JPanel {
    private JLabel lblUpdated;
    private JLabel lblConvertToMp3;
    private JLabel lblRemoveGEMA;
    private JLabel lblRemoveMp4;
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
    private JPanel panel;
    private JLabel lblCheckMinSize;
    private JCheckBox checkMinimumSize;

    // TODO: Use this panel also in SettingsManagerWindow for codereduction ! ! !

    public SettingsManagerPanel(SettingsManager man, FreshUI win) {
        //lblSavePath = new JLabel("Standard save path:");
        lblUpdated = new JLabel("");
        btnSelectStandardSave = new JButton("Select standard save path:");
        btnSelectStandardSave.addActionListener(e -> {
            String path = "";

            dirChooser = new JFileChooser();
            dirChooser.setDialogTitle("Select the path where files will be stored ...");
            dirChooser.setCurrentDirectory(new File(System.getProperty("user.dir")));
            dirChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
            dirChooser.setAcceptAllFileFilterUsed(false);

            if (dirChooser.showOpenDialog(win) == JFileChooser.APPROVE_OPTION)
                path = dirChooser.getSelectedFile().getAbsolutePath();

            if (CGlobals.CURRENT_OS == OS.Windows)
                path = path.replace("\\", "\\\\");

            txtSavePath.setText(path);
            btnSave.doClick();
        });
        txtSavePath = new JTextField(man.GetStandardSavePath());
        lblConvertToMp3 = new JLabel("Convert to mp3");
        checkConvertToMp3 = new JCheckBox("", man.GetConvertToMP3());
        checkConvertToMp3.addActionListener(e -> btnSave.doClick());
        lblRemoveMp4 = new JLabel("Remove video files after mp3 created");
        checkRemoveMp4 = new JCheckBox("", man.GetRemoveMP4());
        checkRemoveMp4.addActionListener(e -> btnSave.doClick());
        lblRemoveGEMA = new JLabel("Remove GEMA");
        checkRemoveGEMA = new JCheckBox("", man.GetRemoveGEMA());
        checkRemoveGEMA.addActionListener(e -> btnSave.doClick());
        lblCheckMinSize = new JLabel("Allow window to get smaller than minimum size (restart needed!)");
        checkMinimumSize = new JCheckBox("");
        checkMinimumSize.addActionListener(e -> btnSave.doClick());
        //lblFFMPEGFile = new JLabel("FFMPEG-Directory");
        btnSelectFFMPEG = new JButton("Select FFMPEG-Directory");
        btnSelectFFMPEG.addActionListener(e -> {
            String path = "";

            dirChooser = new JFileChooser();
            dirChooser.setDialogTitle("Select the path where FFMPEG.exe is located");
            dirChooser.setCurrentDirectory(new File(System.getProperty("user.dir")));
            dirChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
            dirChooser.setAcceptAllFileFilterUsed(false);

            if (dirChooser.showOpenDialog(win) == JFileChooser.APPROVE_OPTION)
                path = dirChooser.getSelectedFile().getAbsolutePath();

            if (CGlobals.CURRENT_OS == OS.Windows)
                path = path.replace("\\", "\\\\");

            txtFFMPEG.setText(path);
            btnSave.doClick();
        });
        txtFFMPEG = new JTextField(man.GetFFMPEGDir().replace("{wd}", System.getProperty("user.dir")));

        btnCancel = new JButton("Cancel");
        btnCancel.addActionListener(e -> {
            // do nothing
            //vs setVisible(false);
        });
        btnSave = new JButton("Save");
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

                lblUpdated.setText("Updated settings! Last change: " + new SimpleDateFormat("HH:mm:ss").format(new Date()));
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        });

        panel = new JPanel(new GridLayout(0, 2));
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
        panel.add(lblUpdated);
        //panel.add(btnCancel);
        //panel.add(btnSave);

        settingsFile = man.GetSettingsFile();
    }

    public JPanel getPanel() {
        panel.setBorder(BorderFactory.createLineBorder(Color.BLACK));
        return panel;
    }
}
