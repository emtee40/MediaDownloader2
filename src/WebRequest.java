import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.io.Console;
import java.io.IOException;
import java.rmi.server.ExportException;
import java.util.Base64;
import java.util.Map;

/**
 * This class is a re-worked version of our old class JSoupAnalyze
 */
public class WebRequest {
    private String strCredentialsUsername;
    private String strCredentialsPassword;
    private String strCredentialsBase64;
    private String strUrl;
    private String strUserAgent =
            "Mozilla/5.0 (Windows NT 6.1) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/41.0.2228.0 Safari/537.36";

    private Document document;

    /**
     * Constructor for a WebRequest
     * @param webUrl String containing the url to analyze/parse as html
     */
    public WebRequest(String webUrl){
        this.strUrl = webUrl;
    }
    public WebRequest(String webUrl, String username, String password){
        this.strUrl = webUrl;
        setCredentials(username, password);
    }

    // Public methods

    /**
     * Parse given WebUrl to a document and uses credentials if given (.htaccess)
     * @param customUserAgent String define a user agent
     * @throws IOException Throws IOException if something went wrong
     */
    public void parse(String customUserAgent) {
        try {
            boolean useCredentials = (strCredentialsBase64.isEmpty()) ? false : true;
            // Do not use custom user agent
            if (!customUserAgent.isEmpty()) {
                this.strUserAgent = customUserAgent;
                this.document = EstablishConnection(this.strUrl, this.strUserAgent, useCredentials);
            }
        } catch (IOException e){
            ConsolePrinter.printError(e.getMessage());
        }
    }

    public void setCredentials(String strUsername, String strPassword){
        this.strCredentialsUsername = strUsername;
        this.strCredentialsPassword = strPassword;
        this.strCredentialsBase64 = new String(Base64.getEncoder().encode((this.strCredentialsUsername+":"
                +this.strCredentialsPassword).getBytes()));
    }

    public Elements getElementByTag(String strHtmlSelector){
        return document.select(strHtmlSelector);
    }

    // Private methods
    private Document EstablishConnection(String webURL, boolean useCredentials) throws IOException {
        /*
            Establish connection without credentials
         */
        if(!useCredentials){
            // Needed since Android is a bit bitchy with creating jsoup connections
            if (CGlobals.CURRENT_OS == OS.Undefined) {
                return Jsoup.connect(webURL).ignoreContentType(true).get();
            } else { // Every other OS like Windows, Linux and Mac
                return Jsoup.connect(webURL).ignoreContentType(true).timeout(0).get();
            }
        }

        /*
            Establish connection with credentials
         */
        else {
            if(this.strCredentialsBase64 == null)
                return null;
            if (CGlobals.CURRENT_OS == OS.Undefined) {
                return Jsoup
                        .connect(webURL)
                        .header("Authorization", "Basic " + this.strCredentialsBase64)
                        .ignoreContentType(true)
                        .get();
            } else {
                // Connection creation for common os'es
                return Jsoup
                        .connect(webURL)
                        .header("Authorization", "Basic " + this.strCredentialsBase64)
                        .ignoreContentType(true)
                        .timeout(0)
                        .get();
            }
        }
    }

    private Document EstablishConnection(String webURL, String userAgent, boolean useCredentials) throws IOException {
        /*
            Establish connection without credentials
         */
        if(!useCredentials) {
            // Needed since Android is a bit bitchy with creating jsoup connections
            if (CGlobals.CURRENT_OS == OS.Undefined) {
                return Jsoup.connect(webURL).ignoreContentType(true).userAgent(userAgent).get();
            } else { // Every other OS like Windows, Linux and Mac
                return Jsoup.connect(webURL).ignoreContentType(true).timeout(0).userAgent(userAgent).get();
            }
        }

        /*
            Establish connection with credentials
        */
        else {
            if(this.strCredentialsBase64 == null)
                return null; // maybe return custom error here

            if(CGlobals.CURRENT_OS == OS.Undefined) {
                return Jsoup
                        .connect(webURL)
                        .userAgent(userAgent)
                        .ignoreContentType(true)
                        .header("Authorization", "Basic " + this.strCredentialsBase64)
                        .get();
            } else {
                return Jsoup
                        .connect(webURL)
                        .userAgent(userAgent)
                        .timeout(0)
                        .ignoreContentType(true)
                        .header("Authorization", "Basic " + this.strCredentialsBase64)
                        .get();
            }
        }
    }

    private Document EstablishConnection(String webURL, Map<String, String> data, String userAgent, boolean useCredentials) throws IOException {
        /*
            Establish connection without credentials
         */
        if (!useCredentials) {
            // Needed since Android is a bit bitchy with creating jsoup connections
            if (CGlobals.CURRENT_OS == OS.Undefined) {
                return Jsoup.connect(webURL).ignoreContentType(true).userAgent(userAgent).data(data).post();
            } else { // Every other OS like Windows, Linux and Mac
                return Jsoup.connect(webURL).ignoreContentType(true).timeout(0).userAgent(userAgent).data(data).post();
            }
        }

        /*
            Establish connection with credentials
         */
        else {
            if (CGlobals.CURRENT_OS == OS.Undefined) {
                return Jsoup
                        .connect(webURL)
                        .userAgent(userAgent)
                        .data(data)
                        .ignoreContentType(true)
                        .header("Authorization", "Basic " + this.strCredentialsBase64)
                        .post();
            } else { // Every other OS like Windows, Linux and Mac
                return Jsoup
                        .connect(webURL)
                        .timeout(0)
                        .userAgent(userAgent)
                        .data(data)
                        .ignoreContentType(true)
                        .header("Authorization", "Basic " + this.strCredentialsBase64)
                        .post();
            }
        }
    }

    private Document EstablishConnection(String webURL, String[][] requestProperties, boolean useCredentials) throws IOException {
        /*
            Establish connection without credentials
         */
        if(!useCredentials){
            // Needed since Android is a bit bitchy with creating jsoup connections
            if (CGlobals.CURRENT_OS == OS.Undefined) {
                return Jsoup.connect(webURL).ignoreContentType(true).header(requestProperties[0][0], requestProperties[0][1]).get();
            } else { // Every other OS like Windows, Linux and Mac
                return Jsoup.connect(webURL).ignoreContentType(true).header(requestProperties[0][0], requestProperties[0][1]).timeout(0).get();
            }
        }

        /*
            Establish connection with credentials
         */
        else {
            if(this.strCredentialsBase64 == null)
                return null;
            // Needed since Android is a bit bitchy with creating jsoup connections
            if (CGlobals.CURRENT_OS == OS.Undefined) {
                return Jsoup
                        .connect(webURL)
                        .header("Authorization", "Basic " + this.strCredentialsBase64)
                        .header(requestProperties[0][0], requestProperties[0][1])
                        .ignoreContentType(true)
                        .get();
            } else { // Every other OS like Windows, Linux and Mac
                return Jsoup
                        .connect(webURL)
                        .header("Authorization", "Basic " + this.strCredentialsBase64)
                        .header(requestProperties[0][0], requestProperties[0][1])
                        .ignoreContentType(true)
                        .timeout(0)
                        .get();
            }
        }
    }

}
