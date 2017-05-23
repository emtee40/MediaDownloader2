import javax.swing.*;

/**
 * Represents entry point into MediaDownloader - generating a new UI
 */
public class FreshUI extends MainFrameBase {

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
