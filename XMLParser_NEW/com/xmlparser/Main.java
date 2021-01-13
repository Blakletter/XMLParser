package com.xmlparser;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;

public class Main {
    public static void main(String[] args)  {

        System.out.println("Spinning up thread.");
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                XMLParser parser = new XMLParser();
                parser.loadXMLFile("C:/Users/jerem/Documents/xmldocument.xml");
                parser.setVerbose(false);
                Node node = parser.parseXML();
                System.out.println(parser.toXML(node));
            }
        });
        thread.start();
    }
}