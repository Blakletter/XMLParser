package com.xmlparser;

import com.xmlparser.xml.*;

import java.util.ArrayList;

public class Main {
    public static void main(String[] args)  {

        System.out.println("Spinning up thread.");
        Thread thread = new Thread(() -> {
            //Load in the xml file
            XMLParser xml1 = new XMLParser().loadXMLFile("xmldocument.xml").setVerbose(false);
            Node xml1Root = xml1.parseXML();

            Node node = xml1Root.navigateInside("project").navigateInside("children").navigateInside("sequence");
            System.out.println(xml1.toXml(node));
            //ArrayList<Node> hey = xml1Root.getAllChildrenWithAttribute("att", "hey");
            //for (Node n : hey) {
            //    System.out.println("Found node " + hey.toString());
            //}
            //xml1.clearConfigurationStrings();
            //xml1.addConfigurationString("<!DOCTYPE root>");
            //xml1Root.navigateInside(1).clearChildren();
            //xml1Root.setName("root");
            //System.out.println(xml1.toXml(xml1Root));
        });
        thread.start();
    }
}