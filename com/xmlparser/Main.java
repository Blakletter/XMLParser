package com.xmlparser;

import java.nio.charset.StandardCharsets;
import java.util.List;

public class Main {
    public static void main(String[] args) {

        System.out.println("Spinning up thread.");
        Thread thread = new Thread(() -> {
            XMLParser parser = new XMLParser();
            parser.LoadXMLFile("C:/Users/jerem/Documents/xmldocument.xml", StandardCharsets.UTF_8);
            parser.setVerbose(false);
            double milli = System.currentTimeMillis();
            Node node = parser.ParseXML();
            System.out.println("Parsing completed in " + (System.currentTimeMillis()-milli)/1000.0 + " Seconds.");

            List<Node> m = node.findNodes("name");
            System.out.println(m.size());
            for (Node value : m) {
                System.out.println(value.getData());
            }
        });
        thread.start();
    }
}
