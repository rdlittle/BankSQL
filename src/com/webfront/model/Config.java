/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.webfront.model;

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
import javafx.geometry.Rectangle2D;
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
    private Rectangle2D dimensions;

    private Config() {
//        try {
//            ResourceBundle bundle = ResourceBundle.getBundle("com.webfront.app.bank");
//            if (bundle.containsKey("bankName")) {
//                bankName = bundle.getString("bankName");
//            }
//            
//        } catch (MissingResourceException e) {
//            System.out.println("Can't find bundle file ");
//        }
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
                Element appNode = new Element("application");
                Element dimsNode = new Element("dimensions");
                systemNode.addContent(new Element("installDir").addContent(getInstallDir()));
                dimsNode.addContent(new Element("width").addContent(w));
                dimsNode.addContent(new Element("height").addContent(h));
                appNode.addContent(dimsNode);

                String tmp = tmpDir;
                tmp.replaceAll("/", "\\/");
                systemNode.addContent(new Element("tmpDir").addContent(tmp));
                rootElement.addContent(systemNode);
                rootElement.addContent(appNode);
                xmlDoc.setRootElement(rootElement);
            } else {
                Element dims = xmlDoc.getRootElement().getChild("application").getChild("dimensions");
                dims.getChild("width").setText(w);
                dims.getChild("height").setText(h);
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
        dimensions = javafx.stage.Screen.getPrimary().getBounds();
        setWidth(null);
        setHeight(null);
        try {
            xmlDoc = jdomBuilder.build(getInstallDir() + getFileSep() + configFileName);
            Element docRoot = xmlDoc.getRootElement();
            Element systemNode = docRoot.getChild("system");
            Element appNode = docRoot.getChild("application");
            Element dimsNode = appNode.getChild("dimensions");
            installDir = systemNode.getChildText("installDir");
            tmpDir = systemNode.getChildText("tmpDir");
            if (dimsNode.getChild("width") != null) {
                setWidth(dimsNode.getChild("width").getText());
            }
            if (dimsNode.getChild("height") != null) {
                setHeight(dimsNode.getChild("height").getText());
            }
            if (getWidth() == null || getWidth().isEmpty()) {
                setWidth("1300");
            }
            if (getHeight() == null || getHeight().isEmpty()) {
                setHeight("800");
            }
            w = getWidth();
            h = getHeight();
            dimsNode.getChild("width").setText(w);
            dimsNode.getChild("height").setText(h);

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
