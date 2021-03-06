package com.xmlparser.xml;
import java.io.*;
import java.util.*;

public class XmlParser {
    private final List<String> extras = new ArrayList<>();
    private boolean verbose = false;
    private String xmlString = "";
    private String tab = "  ";
    private final Hashtable<String, String> tempAttributes = new Hashtable<>();
    private Node currentNode;
    private String filepath;
    private BufferedReader in;
    private boolean xmlFileLoaded =false;
    private int maxDepth = 0;
    private int numberOfNodes = 0;
    private int sumOfNodes = 0;

    public XmlParser addConfigurationString(String configurationString) {
        extras.add(configurationString);
        return this;
    }

    public void saveToFile(File file, String text) {
        PrintStream out = null;
        try  {
            out = new PrintStream(new FileOutputStream(file));
            out.print(text);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (out!=null) out.close();
        }
    }
    public List<String> getConfigurationStrings() {
        return extras;
    }

    public XmlParser clearConfigurationStrings() {
        extras.clear();
        return this;
    }

    public String toXml() {
        String configuration = "";
        for (String s: extras) configuration+=s+"\n";
        if (currentNode==null) {
            return "";
        }
        return configuration + currentNode.toXml(tab);
    }

    public XmlParser setTab(String tab) {
        this.tab = tab;
        return this;
    }

    public XmlParser setVerbose(boolean verbose) {
        this.verbose = verbose;
        return this;
    }

    public int getAverageDepth() {
        return (sumOfNodes+1)/(numberOfNodes+1);
    }

    public int getMaximumDepth() {
        return maxDepth+1;
    }
    /*
        Loads in the XML File as a string;
        This just sets the filepath to whatever you want, and creates a BufferedReader that is reusable;
        This saves on time every time we need to read in a line;
         */
    public XmlParser loadXmlFile(File file) {
        this.filepath = file.getPath();   //Set the filepath name;
        try {
            in = new BufferedReader(new InputStreamReader(new FileInputStream(file)));  //Create the BufferedReader;
            xmlFileLoaded = true;
        } catch (Exception e) {
            e.printStackTrace();    //Catch the error and print it out;
        }
        return this;
    }

    private String readFile() {
        try {
            String line;
            String response = "";
            while ((line=in.readLine())!=null) {
                response+=line;
            }
            return response;
        } catch (Exception e) {
            return null;
        }
    }
    private String readNextLine() {
        try {
            String line;
            if ((line=in.readLine())!=null) {
                return line;
            }
            return null;
        } catch (Exception e) {
            return null;
        }
    }
    private void parseAttribute(String attribute) {
        int pivot = attribute.indexOf('=');
        String attributeName = attribute.substring(0, pivot);
        String attributeData = attribute.substring(pivot+1).replace("\"", "").replace("'", "");
        tempAttributes.put(attributeName, attributeData);
    }

    private void parseAttributes(String attributes) {
        while (attributes.length()>0) {
            int equals = attributes.indexOf("=");
            if (equals<1) {
                if (verbose) System.out.println("Bad attribute found: ("+attributes+")");
                return; //Bad tag, it isn't xml. Just disregard the attributes
            }
            int firstQuote = attributes.indexOf("\"");
            int secondQuote = attributes.indexOf("\"", firstQuote+1);
            if (firstQuote==-1 || secondQuote==-1) {
                firstQuote = attributes.indexOf("'");
                secondQuote = attributes.indexOf("'", firstQuote+1);
            }
            if (firstQuote==-1||secondQuote==-1) {
                if (verbose) System.out.println("Bad attribute found: ("+attributes+")");
                return; //Bad tag, it isn't xml. Just disregard the attributes
            }
            parseAttribute(attributes.substring(0, secondQuote+1).trim());
            attributes = attributes.substring(secondQuote+1);
        }
    }

    private void parseTag(String tag) {
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

        //Check if it is maximum depth
        if (currentNode!=null) {
            if (currentNode.getLevel() + 1 > maxDepth) maxDepth = currentNode.getLevel();
            numberOfNodes++;
            sumOfNodes += currentNode.getLevel();
        }

        boolean isNullTag = (tag.charAt(tag.length()-2)=='/');
        //It is a null tag i.e <tagName/>
        if (isNullTag) {
            //Now that we have our attributes populated in tempAttributes, we create a new node
            Node node = new Node(tagName.substring(0, tagName.length()-1)).setParent(currentNode).addAttributes(tempAttributes);
            return;
        }
        //If it is the root node
        if (currentNode==null) {
            currentNode= new Node(tagName).addAttributes(tempAttributes);
            return;
        }
        //Since we know its not a null tag, is not a configuration tag, lets create a normal tag
        Node node = new Node(tagName).setParent(currentNode).addAttributes(tempAttributes).setLevel(currentNode.getLevel()+1);
        if (verbose) System.out.println("Creating link "+currentNode.getName() + " -> " + node.getName());
        currentNode = node;
    }

    private void parseBlock(String block) {
        if (block.contains("<") || block.contains(">")) {
            char tagStart = block.charAt(1);
            //Check if it is an end tag
            if (tagStart=='/') {
                if (currentNode.getParent()!=null) currentNode = currentNode.getParent();
            } else {
                tempAttributes.clear();
                char configurationChar = block.charAt(1);
                if (configurationChar=='!' || configurationChar=='?') {
                    extras.add(block);
                    if (verbose) System.out.println("Configuration element found: " + block);
                } else {
                    parseTag(block);
                }
            }
        } else {
            if (currentNode!=null && currentNode.getChildren().size()==0) currentNode.addData(block);
        }
    }

    private void parseLine(String line) {
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

    private void parse() {
        //parseLine(readFile());
        xmlString = readFile();
        while (xmlString != null) {
            parseLine(xmlString);
            xmlString = readNextLine();
        }
    }

    public Node parseXml() {
        if (xmlString.equals("")) {
            if (filepath.equals("") && xmlFileLoaded) {
                System.out.println("Please load XML file first by calling 'class'.LoadXML(Filename)");
                return null;
            }
        }
        double time = System.currentTimeMillis();
        parse();
        time = (System.currentTimeMillis()-time);
        if(verbose) System.out.printf("Completed in: %.0f milliseconds (%.3f seconds)\n", time, time*.001);
        return currentNode;
    }
}