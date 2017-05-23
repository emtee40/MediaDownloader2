import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.net.URL;

/**
 * Represents entry point into MediaDownloader - generating a new UI
 */
public class FreshUI extends MainFrameBase implements ActionListener {
    private JTable downloadTable;
    public DefaultTableModel dTableModel;
    public JComboBox<DownloadPage> tlDownloadDomain;
    public JButton btnAddToList;
    public JButton btnDownload;
    public JTextField txtDownloadURL;
    public SettingsManager settingsManager;

    private final String[] tableHeader = {"Download URL", "Hoster", "Progress (in %)",
            "Remove GEMA", "Remove MP4", "Convert to MP3", "Local Path"};

    private JMenuBar menuBar;
    private JMenu menuMenu;
    private JMenu menuSpecial;
    private JMenu menuSettings;
    private JMenu menuHelp;
    private JMenuItem menuItemExport;
    private JMenuItem menuItemImport;
    private JMenuItem menuItemExit;
    private JMenuItem menuItemCrawler;
    private JMenuItem menuItemDevConsole;
    private JMenuItem menuItemSettingsWindow;
    private JMenuItem menuItemHelp;
    private JMenuItem menuItemAbout;

    /**
     * Init our FreshUI
     */
    public FreshUI(){
        CGlobals.init();

        if(CGlobals.CURRENT_OS == OS.Undefined) {
            ConsolePrinter.printWarning("Undefined OS detected. If this is a false positive please report to our GitHub");
        }

        try {
            CheckForUpdate();
        } catch (NullPointerException ex) {
            JOptionPane.showMessageDialog(this,
                    "Attention: Network Connection might be down.", "Network Error",
                    JOptionPane.ERROR_MESSAGE);
        }

        InitGUIComponents();
        InitActionListeners();
        InitWindowStandards();

        if (!settingsManager.GetMinimumSize())
            setMinimumSize(getSize());

    }

    /**
     * Check MediaDownloader for updates
     * @throws NullPointerException Thrown if no network available or other library problems
     */
    private void CheckForUpdate() throws NullPointerException {
        WebRequest webObj = new WebRequest("http://download.r3d-soft.de");
        webObj.parse("");
        String version = webObj.getElementByTag("a[class=btn btn-lg btn-outline]").text();
        version = version.replace("Download MediaDownloader v", "");
        String versionNumber = (version.split(" "))[0];
        if (!versionNumber.equals(CGlobals.VERSION_STRING)) {
            // Update available!
            String msg = "<html>" +
                    JHtmlOptionPane.STANDARD_HTML_STYLETAG_OPEN +
                    "A new software version is available at <a href='http://download.r3d-soft.de'>download.r3d-soft.de</a><br />" +
                    "Please download the new update to ensure every downloader works correctly<br />" +
                    "New version: " + versionNumber + "<br />" +
                    "Your version: " + CGlobals.VERSION_STRING + "<br />" +
                    JHtmlOptionPane.STANDARD_HTML_STYLETAG_CLOSE +
                    "</html>";

            JHtmlOptionPane.showMessageDialog(this, msg, "Update available", JOptionPane.INFORMATION_MESSAGE);
        }
    }

    // Handlers
    private void InitActionListeners() {
        menuItemImport.addActionListener(e -> {
            // Import DLC container
        });
        menuItemExport.addActionListener(e -> {
            // Export DLC container
        });
        menuItemExit.addActionListener(e -> System.exit(0));
        menuItemHelp.addActionListener(e -> {
            String msg = "<html> " + JHtmlOptionPane.STANDARD_HTML_STYLETAG_OPEN + "This tool allows you to download various files " +
                    "from many social/video platforms (eg. YouTube)." +
                    "<br />For the YouTube Downloader following terms are supported:" +
                    "<ul>" +
                    "<li>user:<i>USERNAME</i> (Add all videos from a channel)</li>" +
                    "<li>https://wwww.youtube.com/watch?v=<i>VIDEOID</i> (Just adds this video to the download list)</li>" +
                    "<li>https://www.youtube.com/user/<i>USERNAME</i> (Also add all videos from a channel)</li>" +
                    "</ul>" + JHtmlOptionPane.STANDARD_HTML_STYLETAG_OPEN +
                    "<b>For more information visit: <a href='http://r3d-soft.de/'>http://r3d-soft.de</a></b>" +
                    JHtmlOptionPane.STANDARD_HTML_STYLETAG_CLOSE +
                    JHtmlOptionPane.STANDARD_HTML_STYLETAG_CLOSE +
                    "</html>";

            JHtmlOptionPane.showMessageDialog(this, msg, "Help", JOptionPane.INFORMATION_MESSAGE);
        });
        menuItemAbout.addActionListener(e -> {
            String msg = "<html>" + JHtmlOptionPane.STANDARD_HTML_STYLETAG_OPEN +
                    "Thanks for using <b>MediaDownloader v" + CGlobals.VERSION_STRING + "</b> - written by R3DST0RM.<br />" +
                    "This software uses ffmpeg as MP3 converter all licenses can be found here: bin/licenses/<br /><br />" +
                    "This software is free software (GNU General Public License v2) - Source Code available at request:<br /><br />" +
                    "E-Mail: <a href=\"mailto:admin@r3d-soft.de\"><b>admin@r3d-soft.de</b></a><br />" +
                    "Website: <a href=\"http://r3d-soft.de\"><b>http://r3d-soft.de</b></a>" +
                    JHtmlOptionPane.STANDARD_HTML_STYLETAG_CLOSE +
                    "</html>";

            JHtmlOptionPane.showMessageDialog(this, msg, "Help", JOptionPane.INFORMATION_MESSAGE);
        });
        menuItemSettingsWindow.addActionListener(e -> settingsManager.ShowSettingsWindow(this));

        /*
            // TODO: Rework this function block
        menuItemCrawler.addActionListener(e -> {
            cwFrame = new CrawlerFrame(this);
            cwFrame.showWindow();
        });

        menuItemDevConsole.addActionListener(e -> {
            System.setOut(new PrintStream(devConsole.getStream()));
            System.setErr(new PrintStream(devConsole.getStream()));
            devConsole.showConsole();
        }); */

        // Write settings to ini file on disk on application exit
        this.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                if (settingsManager != null)
                    settingsManager.saveSettings();
                super.windowClosing(e);
            }
        });
    }

    private void InitWindowStandards() {
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        pack();
        setLocationRelativeTo(null);
        setTitle("MediaDownloader v" + CGlobals.VERSION_STRING + "(Fresh-UI)");
        setVisible(true);
    }

    private void InitGUIComponents() {
        // add menubar
        menuBar = new JMenuBar();
        menuMenu = new JMenu("Menu");
        menuSpecial = new JMenu("Tools");
        menuSettings = new JMenu("Settings");
        menuHelp = new JMenu("Help");

        menuItemExport = new JMenuItem("Export to DLC");
        menuItemImport = new JMenuItem("Import from DLC");
        menuItemExit = new JMenuItem("Exit");
        menuItemCrawler = new JMenuItem("Crawler");
        menuItemDevConsole = new JMenuItem("Dev Console");
        menuItemSettingsWindow = new JMenuItem("Settings");
        menuItemHelp = new JMenuItem("Help - Usage");
        menuItemAbout = new JMenuItem("? - About this tool");

        menuMenu.add(menuItemImport);
        menuMenu.add(menuItemExport);
        menuMenu.add(menuItemExit);
        menuSpecial.add(menuItemCrawler);
        menuSpecial.add(menuItemDevConsole);
        menuSettings.add(menuItemSettingsWindow);
        menuHelp.add(menuItemHelp);
        menuHelp.add(menuItemAbout);

        menuBar.add(menuMenu);
        menuBar.add(menuSpecial);
        menuBar.add(menuSettings);
        menuBar.add(menuHelp);

        settingsManager = new SettingsManager();

        tlDownloadDomain = new JComboBox<>();
        tlDownloadDomain.setModel(new DefaultComboBoxModel<>(DownloadPage.values()));

        downloadTable = new JTable();
        dTableModel = new DefaultTableModel();
        dTableModel.setColumnIdentifiers(tableHeader);
        downloadTable.getTableHeader().setReorderingAllowed(false);
        downloadTable.setModel(dTableModel);

        txtDownloadURL = new JTextField();
        btnAddToList = new JButton("Add to list");
        btnAddToList.addActionListener(this);
        btnDownload = new JButton("Download all!");
        btnDownload.addActionListener(this);

        // panel top
        JPanel topPanel = new JPanel(new BorderLayout());
        JPanel midTop = new JPanel(new GridLayout(0, 6));
        midTop.add(new JLabel("Download URL: "));
        midTop.add(txtDownloadURL);
        midTop.add(new JLabel("Hoster (automatically set)"));
        midTop.add(tlDownloadDomain);
        midTop.add(btnAddToList);
        midTop.add(btnDownload);
        topPanel.add(midTop, BorderLayout.CENTER);
        topPanel.add(menuBar, BorderLayout.NORTH);
        // panel middle
        getContentPane().add(new JScrollPane(downloadTable), BorderLayout.CENTER);
        getContentPane().add(topPanel, BorderLayout.NORTH);

        SettingsManagerPanel sMP = new SettingsManagerPanel(settingsManager, this);
        getContentPane().add(sMP.getPanel(), BorderLayout.SOUTH);

        // implement to remove by pressing del
        InputMap im = downloadTable.getInputMap(JTable.WHEN_FOCUSED);
        ActionMap am = downloadTable.getActionMap();

        im.put(KeyStroke.getKeyStroke(KeyEvent.VK_DELETE, 0), "DeleteRow");
        am.put("DeleteRow", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.out.println("Delete row");
                int row = downloadTable.getSelectedRow();

                if (row > -1) {
                    DefaultTableModel model = (DefaultTableModel) downloadTable.getModel();
                    model.removeRow(row);
                }
            }
        });
    }

    @Override
    public void actionPerformed(ActionEvent e) {

        if (e.getSource() == btnAddToList) {
            txtDownloadURL.setEditable(false);
            if (tlDownloadDomain.getSelectedItem().toString().equals("Auto_Detect")) {
                System.out.println("Auto-Detect URL Host");
                try {
                    URL detectDomain = new URL(txtDownloadURL.getText());
                    int index = getDownloadPage(detectDomain.getHost());
                    if (index != -1) {
                        tlDownloadDomain.setSelectedIndex(index);
                        // add to list with auto detect host!
                        LinkHandler.AddURLToTable(txtDownloadURL.getText(),
                                tlDownloadDomain.getItemAt(index).toString(), this);

                        tlDownloadDomain.setSelectedIndex(0);
                        txtDownloadURL.setText("");
                    } else {
                        JOptionPane.showMessageDialog(FreshUI.this, "No supported hoster found.",
                                "URL unsupported", JOptionPane.INFORMATION_MESSAGE);
                        txtDownloadURL.setText("");
                    }
                } catch (Exception ex) {
                    System.err.println("Malformed url try again");
                    JOptionPane.showMessageDialog(FreshUI.this, "Malformed URL detected. " +
                            "Please check the URL and try again.", "Error - Malformed URL", JOptionPane.ERROR_MESSAGE);
                }
            } else {
                // add to list without auto detect host
                LinkHandler.AddURLToTable(txtDownloadURL.getText(),
                        tlDownloadDomain.getSelectedItem().toString(), this);
            }

            txtDownloadURL.setEditable(true);
        }

        if (e.getSource() == btnDownload) {
            // echo error cause no links are found
            if (dTableModel.getRowCount() == 0) {
                JOptionPane.showMessageDialog(FreshUI.this, "No links provided. " +
                                "Please add links to the crawler (using the 'Add to list' button). " +
                                "If this error persists please contact admin@r3d-soft.de", "Error - No links provided",
                        JOptionPane.ERROR_MESSAGE);
                return;
            }

            // Start Downloading!
            LinkHandler.StartDownloading(this);
        }
    }

    public int getDownloadPage(String toDetect) {
        DownloadPage[] pages = DownloadPage.values();

        for (int i = 0; i < pages.length; i++) {
            if (toDetect.toLowerCase().contains(pages[i].toString().toLowerCase()))
                return i;
        }

        return -1;
    }


    /**
     * Main entry point for our java application
     * @param args Array program parameters
     */
    public static void main(String[] args) {
        try {
            if (args.length <= 0) {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
                new FreshUI(); // get a fancy window
            } else {
                // TODO: rework & implement console manager
            }
        } catch (UnsupportedLookAndFeelException | ClassNotFoundException | IllegalAccessException | InstantiationException e) {
            new FreshUI(); // start with native java ui
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}

enum DownloadPage {
    Auto_Detect, YouTube, SoundCloud, Uploaded, Vimeo
}
