package com.xmlparser;

import com.xmlparser.xml.*;

import java.io.File;

public class Main {
    public static void main(String[] args)  {

        System.out.println("Spinning up thread.");
        Thread thread = new Thread(() -> {
            //Load in the xml file
            File file = new File("xmldocument.xml").getAbsoluteFile();
            XMLParser tree = new XMLParser().loadXmlFile(file).setVerbose(true);
            Node root = tree.parseXML();
            tree.clearConfigurationStrings().addConfigurationString("<!DOCTYPE root>");
            System.out.println(tree.toXml(root));

        });
        thread.start();
    }
}