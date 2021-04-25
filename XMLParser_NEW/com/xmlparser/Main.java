package com.xmlparser;
import com.xmlparser.xml.*;

import java.io.File;
import java.util.ArrayList;

public class Main {
    public static void main(String[] args)  {
        Thread thread = new Thread(() -> {
            //Load in the xml file
            File file = new File("xmldocument.xml").getAbsoluteFile();
            XmlParser tree = new XmlParser().loadXmlFile(file).setVerbose(false);


            Node root = tree.parseXml();

            ArrayList<Node> foods = root.getAllWithName("food");


            for (Node food : foods) {
                new Node("thisisatest").setData("75%").setParent(food);
                new Node("style").setData("modern").setParent(food);
                new Node("interest").setData("moderate").setParent(food);
            }
            String xml = tree.setTab("  ").toXml();
            tree.saveToFile(new File("output.xml").getAbsoluteFile(), xml);
        });
        thread.start();
    }
}