import javax.imageio.ImageIO;
import javax.swing.*;

/**
 * MainFrameBase represents own base class for all frames in this program
 */
public class MainFrameBase extends JFrame {

    public MainFrameBase(String sTitle) {
        super(sTitle);
    }

    public MainFrameBase() {
        super();
        setProgramIcon();
    }

    private void setProgramIcon(){
        // Program icon
        try {
            setIconImage(ImageIO.read(getClass().getResource("/resources/images/main_icon.png")));
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}