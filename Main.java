package com.xmlparser;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;

public class Main {
    public static void main(String[] args) {

        System.out.println("Spinning up thread.");
        Thread thread = new Thread(() -> {
            XMLParser parser = new XMLParser();
            parser.LoadXMLFile("C:/Users/Jeremy/Documents/xmldocument.xml", StandardCharsets.UTF_8);
            parser.setVerbose(false);
            double milli = System.currentTimeMillis();
            Node node = parser.ParseXML();
            System.out.println("Parsing completed in " + (System.currentTimeMillis()-milli)/1000.0 + " Seconds.");

            ArrayList<Node> m = node.findNodes("author");
            System.out.println(m.size());
            for (Node value : m) {
                //String attr = value.getAttribute("id");
                System.out.println(value.getData());
            }


        });
        thread.start();
    }
}