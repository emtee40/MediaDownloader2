import java.io.*;
import java.util.ArrayList;
import java.util.Hashtable;

/**
 * Created by Robin on 08.06.2016.
 * <p>
 * Implements a wrapper for a standard ini-file with
 * <p>
 * - Sections      => [SECTION_NAME]
 * - Comments      => ;COMMENT
 * - Key-Value     => KEY = VALUE
 * <p>
 * Provides getter and setter for these values.
 */
public class IniFile {
    private String iniFilePath;
    private Hashtable<String, Hashtable<String, String>> iniFile = new Hashtable<>();
    //private static IniFile instance;

    //TODO: do sth. like singleton for each path/ make sure there will exist only one object for every ini-path
    public IniFile(String path) {
        iniFilePath = path;
    }

    public String getIniFilePath() {
        return iniFilePath;
    }

    /**
     * Reads the ini file and
     * writes it's values to internal Hashtable.
     * If ini file doesn't exist yet, it will
     * be created.
     *
     * @throws Exception when sth. went wrong until trying to read from disk.
     */
    public void read() throws Exception {
        File settings = new File(iniFilePath);
        if (!settings.exists()) {
            // Create new ini file
            if (!settings.createNewFile())
                throw new Exception("Could not create ini-file in working directory.");
        } else {
            // Read existing ini file to Hashtable
            BufferedReader reader = new BufferedReader(new FileReader(settings));
            String line = reader.readLine();
            while (line != null) {
                line = line.trim();

                // Comment
                if (line.startsWith(";")) {
                    line = reader.readLine();
                    continue;
                }
                // Section
                if (line.startsWith("[")) {
                    // Convention: section names in lower case letters
                    String sSectionName = line.replace("[", "").replace("]", "").trim().toLowerCase();
                    // Read section lines (will overwrite section
                    // if already exists...)
                    ArrayList<String> alSectLines = new ArrayList<>();
                    String sTrimmedLine;
                    // Read till next section
                    while ((line = reader.readLine()) != null &&
                            !(sTrimmedLine = line.trim()).startsWith("[")) {
                        // Not a comment...
                        if (!sTrimmedLine.startsWith(";"))
                            alSectLines.add(sTrimmedLine);
                    }
                    Hashtable<String, String> htSectionValues = getSectionValues(alSectLines);
                    this.iniFile.put(sSectionName, htSectionValues);
                }
                // Invalid => continue without reading into Hashtable
                else
                    line = reader.readLine();
            }

        }

    }

    /**
     * private helper-method for initial reading
     */
    private Hashtable<String, String> getSectionValues(ArrayList<String> alLines) {
        Hashtable<String, String> htSectionValues = new Hashtable<>();
        for (String line : alLines) {
            String[] splitted = line.split("=");
            if (splitted.length != 2)
                continue;
            // Convention also: Hashtable strings are lowercase
            htSectionValues.put(splitted[0].trim().toLowerCase(),
                    splitted[1].trim().toLowerCase());
        }
        return htSectionValues;
    }

    /**
     * Gets a ini-value. (caseinsensitive)
     *
     * @param sSectionName The section where to search for the key
     * @param sKeyName     The name of the key
     * @return Returns null if the key wasn't found in the section.
     */
    public String getIniValue(String sSectionName, String sKeyName) {
        Hashtable<String, String> htSection = iniFile.get(sSectionName.toLowerCase());
        if (htSection != null)
            return htSection.get(sKeyName.toLowerCase());
        return null;
    }

    /**
     * Sets a ini-file value.
     * e.g.
     * [settings]               // Section
     * removegema=false         // Key Value
     * <p>
     * All strings are stored in lowercase.
     *
     * @param sSectionName The Section where the key-value-pair goes to
     * @param sKeyName     The keyname
     * @param sValue       The value of the key
     */
    public void setIniValue(String sSectionName, String sKeyName, String sValue) {
        // TODO: TEST
        sSectionName = sSectionName.toLowerCase();
        if (!sectionExists(sSectionName))
            // Make new section
            iniFile.put(sSectionName, new Hashtable<>());

        // Make the section-entry
        iniFile.get(sSectionName).put(sKeyName.toLowerCase(), sValue.toLowerCase());
    }

    /**
     * Removes a key from a given section.
     * If key is not found nothing is done.
     *
     * @param sSectionName The name of the section where the key is stored
     * @param sKeyName     The name of the key
     */
    public void removeIniValue(String sSectionName, String sKeyName) {
        sSectionName = sSectionName.toLowerCase();
        Hashtable htSection = iniFile.get(sSectionName);
        if (htSection != null)
            htSection.remove(sKeyName.toLowerCase());
    }

    /**
     * Saves the settings to ini-file
     * in ini-file notation
     */
    public void save() throws IOException {
        // TODO: TEST
        File iniFileOnDisk = new File(iniFilePath);
        if (iniFileOnDisk.exists()) {
            BufferedWriter writer = new BufferedWriter(new FileWriter(iniFileOnDisk));

            // First append to stringbuilder...
            StringBuilder sb = new StringBuilder();
            String eof = System.getProperty("line.separator");

            for (String sSectionName : iniFile.keySet()) {
                Hashtable<String, String> htSection = iniFile.get(sSectionName);

                // If section is empty we don't have to write...
                if (htSection.isEmpty())
                    continue;

                sb.append("[");
                sb.append(sSectionName);
                sb.append("]");
                sb.append(eof);

                for (String sKey : htSection.keySet()) {
                    sb.append(sKey);
                    sb.append(" = ");
                    sb.append(htSection.get(sKey));
                    sb.append(eof);
                }
            }

            // ...Then write to disk
            writer.write(sb.toString());
            writer.flush();
            writer.close();
        }

    }

    private boolean sectionExists(String sSectionName) {
        return iniFile.containsKey(sSectionName.toLowerCase());
    }
}
