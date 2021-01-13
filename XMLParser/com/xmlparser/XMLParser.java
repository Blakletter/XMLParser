package com.xmlparser;
import java.io.*;
import java.util.*;

public class XMLParser  {
    private List<Node> extras = new ArrayList<>();
    private boolean verbose = false;
    private String xmlString = "";
    private HashMap<String, String> tempAttributes = new HashMap<>();
    private Node currentNode;
    private String filepath;
    private BufferedReader in;

    public void addConfigurationNode(Node node) {
        extras.add(node);
    }

    public String toXML(Node node) {
        String _extras = "";
        for (int i=0; i<extras.size(); i++) {
            _extras += extras.get(i).toXML();
        }
        return _extras + node.toXML();
    }

    public boolean isVerbose() {
        return verbose;
    }

    public void setVerbose(boolean verbose) {
        this.verbose = verbose;
    }

    /*
        Loads in the XML File as a string;
        This just sets the filepath to whatever you want, and creates a BufferedReader that is reusable;
        This saves on time every time we need to read in a line;
         */
    public void loadXMLFile(String filepath) {
        this.filepath = filepath;   //Set the filepath name;
        try {
            in = new BufferedReader(new FileReader(filepath));  //Create the BufferedReader;
        } catch (Exception e) {
            e.printStackTrace();    //Catch the error and print it out;
        }
    }

    public String readNextLine() {
        try {
            String line = in.readLine();
            while (line.equals("")) {
                line = in.readLine();
            }
            return line.trim();
        } catch (Exception e) {
            return null;
        }
    }

    public void parseAttribute(String attribute) {
        int pivot = attribute.indexOf('=');
        String attributeName = attribute.substring(0, pivot);
        String attributeData = attribute.substring(pivot+1).replace("\"", "").replace("\'", "");;
        tempAttributes.put(attributeName, attributeData);
    }

    public void parseAttributes(String attributes) {
        while (attributes.length()>0) {
            int equals = attributes.indexOf("=");
            if (equals<1) {
                if (verbose) System.out.println("Bad attribute found: ("+attributes+")");
                return; //Bad tag, it isn't xml. Just disregard the attributes
            }
            int firstQuote = attributes.indexOf("\"");
            int secondQuote = attributes.indexOf("\"", firstQuote+1);
            if (firstQuote==-1 || secondQuote==-1) {
                firstQuote = attributes.indexOf("\'");
                secondQuote = attributes.indexOf("\'", firstQuote+1);
            }
            if (firstQuote==-1||secondQuote==-1) {
                if (verbose) System.out.println("Bad attribute found: ("+attributes+")");
                return; //Bad tag, it isn't xml. Just disregard the attributes
            }
            parseAttribute(attributes.substring(0, secondQuote+1).trim());
            attributes = attributes.substring(secondQuote+1);
        }
    }

    public void parseTag(String tag) {
        char tagStart = tag.charAt(1);
        boolean isExtra = (tagStart=='?' || tagStart =='!');
        if (verbose && isExtra) System.out.println("Found configuration tag " + tag);
        //Always a starting tag or extra tag
        String tagName;
        int space = tag.indexOf(' ');
        if (space<1) space = tag.length()-1;
        tagName  = tag.substring(1, space);
        String endTag = tag.substring(space, tag.length()-1).trim();
        if (endTag.length() > 0) {
            //Get the attributes here
            parseAttributes(endTag.trim());
        }


        boolean isNullTag = (tag.charAt(tag.length()-2)=='/');
        //It is a null tag i.e <tagName/>
        if (isNullTag) {
            //Now that we have our attributes populated in tempAttributes, we create a new node
            Node node = new Node(tagName.substring(0, tagName.length()-1)).setParent(currentNode).addAttribute(tempAttributes);
            currentNode.addChild(node);
            return;
        }
        //Is a configuration tag i.e <? or <!
        if (isExtra) {
            Node node = new Node(tagName).addAttribute(tempAttributes);
            extras.add(node);
            return;
        }
        //If it is the root node
        if (currentNode==null) {
            currentNode= new Node(tagName).addAttribute(tempAttributes);
            return;
        }
        //Since we know its not a null tag, is not a configuration tag, lets create a normal tag
        Node node = new Node(tagName).setParent(currentNode).addAttribute(tempAttributes);
        currentNode.addChild(node);
        if (verbose) System.out.println("Creating link "+currentNode.getID() + " -> " + node.getID());
        currentNode = node;
    }

    public void parseBlock(String block) {
        if (block.contains("<") || block.contains(">")) {
            char tagStart = block.charAt(1);
            //Check if it is an end tag
            if (tagStart=='/') {
                if (currentNode.getParent()!=null) currentNode = currentNode.getParent();
            } else {
                tempAttributes.clear();
                parseTag(block);
            }
        } else {
            if (currentNode!=null) currentNode.setData(block);
        }
    }

    public void parseLine(String line) {
        int tag;
        while (line.length()!=0) {
            int startTag = line.indexOf('<');
            int endTag = line.indexOf('>')+1;
            if (endTag<1) endTag=line.length();
            if (startTag<1) startTag=line.length();
            tag = Math.min(endTag, startTag); //whichever comes first, if none, the end of the line
            parseBlock(line.substring(0, tag));
            line = line.substring(tag);
        }
    }

    public void parse() {
        xmlString = readNextLine();
        while (xmlString != null) {
            parseLine(xmlString);
            xmlString = readNextLine();
        }
    }

    public Node parseXML() {
        if (xmlString.equals("")) {
            if (filepath.equals("")) {
                System.out.println("Please load XML file first by calling 'class'.LoadXML(Filename)");
                return null;
            }
        }
        parse();
        return currentNode;
    }
}