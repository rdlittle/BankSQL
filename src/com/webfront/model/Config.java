/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.webfront.model;

import com.webfront.app.Bank;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.MissingResourceException;
import java.util.Properties;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.collections.ObservableList;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.JDOMException;
import org.jdom2.input.SAXBuilder;
import org.jdom2.output.Format;
import org.jdom2.output.XMLOutputter;

/**
 *
 * @author rlittle
 */
public class Config {

    public ObservableList<Account> accountList;
    private String bankName;
    private String installDir;
    private String tmpDir;
    private String home;
    private String fileSep;
    private String systemTmpDir;
    private static Config config;

    private static final String root = "bank";

    protected String fileName;
    SAXBuilder jdomBuilder;
    Document xmlDoc;
    private final String configFileName = "config.xml";
    private final String defaultConfigFileName = ".bankSQL";
    private String x, y, w, h;
    private String width;
    private String height;

    private Config() {
        try {
            ResourceBundle bundle = ResourceBundle.getBundle("com.webfront.app.bank");
            if (bundle.containsKey("defaultWidth")) {
                width = bundle.getString("bankName");
            }
            if (bundle.containsKey("defaultheight")) {
                height = bundle.getString("defaultheight");
            }            
            
        } catch (MissingResourceException e) {
            Logger.getLogger(Bank.class.getName()).log(Level.WARNING, "Can't find resource bank.properties");
        }
        // Set up some system-dependant defaults
        Properties properties;
        properties = System.getProperties();
        installDir = properties.getProperty("java.io.tmpdir");
        home = properties.getProperty("user.home");
        fileSep = properties.getProperty("file.separator");
        systemTmpDir = properties.getProperty("java.io.tmpdir");
        tmpDir = systemTmpDir;

        if (fileSep.equals("/")) {
            tmpDir.replaceAll("/", "\\/");
        }

        // Find the default bootstrap file
        File defaultStartupFile = new File(home + fileSep + defaultConfigFileName);
        try {
            BufferedReader reader = new BufferedReader(new FileReader(defaultStartupFile));
            String startupLoc = reader.readLine();
            if (!"".equals(startupLoc)) {
                installDir = startupLoc;
            }
        } catch (FileNotFoundException ex) {
            // If the bootstrap file isn't found, create it
            try {
                try (FileWriter writer = new FileWriter(defaultStartupFile)) {
                    writer.write(home);
                }
            } catch (IOException ex1) {
                Logger.getLogger(Config.class.getName()).log(Level.SEVERE, null, ex1);
            }
        } catch (IOException ex) {
            Logger.getLogger(Config.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void setConfig() {
        FileWriter writer = null;
        File defaultStartupFile = new File(home + fileSep + defaultConfigFileName);
        try (FileWriter defaultWriter = new FileWriter(defaultStartupFile)) {
            defaultWriter.write(installDir);
        } catch (IOException ex) {
            Logger.getLogger(Config.class.getName()).log(Level.SEVERE, null, ex);
        }
        try {
            if (xmlDoc == null) {
                xmlDoc = new Document();
                Element rootElement = new Element(root);
                Element systemNode = new Element("system");
                Element windowNode = new Element("window");
                systemNode.addContent(new Element("installDir").addContent(getInstallDir()));
                windowNode.addContent(new Element("width").addContent(w));
                windowNode.addContent(new Element("height").addContent(h));

                String tmp = tmpDir;
                tmp.replaceAll("/", "\\/");
                systemNode.addContent(new Element("tmpDir").addContent(tmp));
                rootElement.addContent(systemNode);
                rootElement.addContent(windowNode);
                xmlDoc.setRootElement(rootElement);
            } else {
                Element root = xmlDoc.getRootElement();
                Element dims = root.getChild("window");
                dims.getChild("width").setText(getWidth());
                dims.getChild("height").setText(getHeight());
            }
            writer = new FileWriter(getInstallDir() + getFileSep() + configFileName);
            XMLOutputter xml = new XMLOutputter();
            xml.setFormat(Format.getPrettyFormat());
            writer.write(xml.outputString(xmlDoc));
        } catch (IOException ex) {
            Logger.getLogger(Config.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            try {
                if (writer != null) {
                    writer.close();
                }
            } catch (IOException ex) {
                Logger.getLogger(Config.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    public static Config getInstance() {
        if (config == null) {
            config = new Config();
        }
        return config;
    }

    public void getConfig() {
        jdomBuilder = new SAXBuilder();
        try {
            xmlDoc = jdomBuilder.build(getInstallDir() + getFileSep() + configFileName);
            Element docRoot = xmlDoc.getRootElement();
            Element systemNode = docRoot.getChild("system");
            Element windowNode = docRoot.getChild("window");
            installDir = systemNode.getChildText("installDir");
            tmpDir = systemNode.getChildText("tmpDir");

            if (windowNode == null) {
                windowNode = new Element("window");
                windowNode.addContent(new Element("width").setText("1300"));
                windowNode.addContent(new Element("height").setText("800"));
                docRoot.addContent(windowNode);
            } else {
                if (windowNode.getChild("width") == null) {
                    windowNode.addContent(new Element("width").setText("1300"));
                }
                if (windowNode.getChild("height") == null) {
                    windowNode.addContent(new Element("height").setText("800"));
                }
            }

            setWidth(windowNode.getChild("width").getText());
            setHeight(windowNode.getChild("height").getText());

        } catch (FileNotFoundException ex) {
            //Logger.getLogger(Config.class.getName()).log(Level.SEVERE, null, ex);
        } catch (JDOMException ex) {
            Logger.getLogger(Config.class
                    .getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(Config.class
                    .getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**
     * @return the installDir
     */
    public String getInstallDir() {
        return installDir;
    }

    /**
     * @param installDir the installDir to set
     */
    public void setInstallDir(String installDir) {
        this.installDir = installDir;
    }

    /**
     * @return the tmpDir
     */
    public String getTmpDir() {
        return tmpDir;
    }

    /**
     * @param tmpDir the tmpDir to set
     */
    public void setTmpDir(String tmpDir) {
        this.tmpDir = tmpDir;
    }

    /**
     * @return the home
     */
    public String getHome() {
        return home;
    }

    /**
     * @param home the home to set
     */
    public void setHome(String home) {
        this.home = home;
    }

    /**
     * @return the fileSep
     */
    public String getFileSep() {
        return fileSep;
    }

    /**
     * @param fileSep the fileSep to set
     */
    public void setFileSep(String fileSep) {
        this.fileSep = fileSep;
    }

    /**
     * @return the systemTmpDir
     */
    public String getSystemTmpDir() {
        return systemTmpDir;
    }

    /**
     * @param systemTmpDir the systemTmpDir to set
     */
    public void setSystemTmpDir(String systemTmpDir) {
        this.systemTmpDir = systemTmpDir;
    }

    /**
     * @return the bankName
     */
    public String getBankName() {
        return bankName;
    }

    /**
     * @param bankName the bankName to set
     */
    public void setBankName(String bankName) {
        this.bankName = bankName;
    }

    /**
     * @return the width
     */
    public String getWidth() {
        return width;
    }

    /**
     * @param width the width to set
     */
    public void setWidth(String width) {
        this.width = width;
    }

    /**
     * @return the height
     */
    public String getHeight() {
        return height;
    }

    /**
     * @param height the height to set
     */
    public void setHeight(String height) {
        this.height = height;
    }

}
