package com.xmlparser;

public class Main {
    public static void main(String[] args)  {

        System.out.println("Spinning up thread.");
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                //Load in the xml file
                XMLParser xml1 = new XMLParser().loadXMLFile("xmldocument.xml").setVerbose(false);
                Node xml1Root = xml1.parseXML();

                xml1.clearConfigurationStrings();
                xml1.addConfigurationString("<!DOCTYPE root>");
                xml1Root.navigateInside(1).clearChildren();
                xml1Root.setName("root");
                System.out.println(xml1.toXml(xml1Root));
            }
        });
        thread.start();
    }
}