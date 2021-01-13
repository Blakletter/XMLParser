package com.xmlparser;

public class Main {
    public static void main(String[] args)  {

        System.out.println("Spinning up thread.");
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                XMLParser parser = new XMLParser();
                parser.loadXMLFile("absolute-path-to-document.xml");
                parser.setVerbose(false);
                Node node = parser.parseXML();
                System.out.println(parser.toXML(node));
            }
        });
        thread.start();
    }
}