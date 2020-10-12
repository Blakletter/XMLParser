package com.xmlparser;
import java.io.*;
import java.nio.charset.Charset;
import java.util.*;

public class XMLParser  {
    private String filepath, xmlString = "";
    private Node mainNode = new Node("Main"), currentNode = mainNode;
    private HashMap<String, byte[]> tempAttributes = null;
    private int pos, prevPos, count=0;
    private char c;
    private boolean verbose = false, endReading = false;
    private BufferedReader bis;
    StringBuilder readChar = new StringBuilder(), tagName = new StringBuilder();

    public void setVerbose(boolean verbose) {
        this.verbose = verbose;
    }
    /*
    Loads in the XML File as a string;
    This just sets the filepath to whatever you want, and creates a BufferedReader that is reusable;
    This saves on time every time we need to read in a line;
     */
    public void LoadXMLFile(String filepath, Charset charset) {

        this.filepath = filepath;   //Set the filepath name;
        try {
            bis = new BufferedReader(new InputStreamReader(new FileInputStream(filepath), charset));
            //in = new BufferedReader(new FileReader(filepath));  //Create the BufferedReader;
        } catch (Exception e) {
            e.printStackTrace();    //Catch the error and print it out;
        }
    }

    /*
    This is what typically gets called when you need to read in another line to parse;
    There are certain cases where AddNextLine() gets called instead, but only when the current line doesnt end with a tag;
    This means there might be data that is being continued on another line;
     */
    private void ReadNextLine(boolean append) {
        readChar.delete(0, readChar.length());
        readChar.setLength(0);
        int c = -1;
        int numChars = 0;
        try {
            while (numChars++ < 150 && (c = bis.read()) != -1 && (c!='\n'     )) {
                readChar.append((char)c);
            }
            if (c == -1) {
                bis.close();
                endReading = true;
            }

            String s = readChar.toString();
            if (append) {
                xmlString = xmlString + s;
            } else {
                xmlString = s;
            }

            if (s.equals("") || xmlString.equals("")) {
                ReadNextLine(append);
            }
            //System.out.println("xmlString is " + xmlString);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    /*

    The above code is mostly just for the actual logic of reading in the file;
    Below is where most of the parsing code goes;
     */

    //Moves the 'cursor' forward one position and sets c to that character;
    private void movePosition() {
        if (pos >= xmlString.length()-1) {
            if (c != '>') {
                ReadNextLine(true);
                pos++;
                c = xmlString.charAt(pos);  //Set that character;
            } else {
                ReadNextLine(false);
                pos = 0;
                prevPos = 0;
                if (xmlString == null) {
                    c = '\u0000';
                } else {
                    c = xmlString.charAt(pos);
                }
            }
        } else {
            pos++;  //Move it forward one;
            c = xmlString.charAt(pos);  //Set that character;
        }
    }



    /*
    This is the highest method that is called in the parser, it is for parsing things
    In the form of key=value by using the equals sign as a marker;
     */
    private void ParseAttribute(String attribute) {
        attribute = attribute.replaceAll("[\"']", ""); //Remove any quotes that may be around it
        String key;
        String value;
        if (attribute.contains("=")) {

            key = attribute.substring(0, attribute.indexOf('='));   //Get everything to the left of the equals sign
            value = attribute.substring(attribute.indexOf('=') + 1);    //Get everything to the right of the equals sign
            tempAttributes.put(key, value.getBytes()); //Now place the key-value pair into the temporary HashMap;
        }
    }

    /*
    This gets called whenever the is a new tag that is found (when a < character is found)
    This is responsible for finding attributes, and getting the tag name;
    It finds attributes by checking for white spaces, sets a marker, and if it finds a '>' or another white space
    it places another marker there and gets the substring between the markers and sends it to the ParseAttribute()
     */
    private void ParseTag() {
        movePosition();
        tagName.delete(0, tagName.capacity());  //tagName is a StringBuilder to avoid constant String creation when appending;
        tagName.trimToSize();   //Set the StringBuilder to size 0;
        boolean addToTree = true;   //By default the tag will not be an ending tag. If it finds a '/' then this is flipped;
        if (c == '/') { //If it found a '/';
            addToTree = false;  //Switch from adding to taking away;
            movePosition(); //Move to the next character;
        }

        prevPos = pos;  //We now set our first marker to this new position, so we dont get '/' in the tag name;
        boolean hasAttr = false;    //hasAttr is flipped on if any whitespace is found in the tag, meaning there could be attributes. Attributes will only be found if an '=' is found in it;
        while (c != '>') {  //While the cursor has not reached the end of the tag, continue this loop
            if (c == ' ') { //If it encounters a white space
                hasAttr = true; //We then set this attribute to true
                tempAttributes = new HashMap<>(); //tempAttributes is a reusable HashMap for keeping track of key-value pairs for the attributes, which gets assigned to the node once it is created later on.
                if (xmlString.charAt(prevPos) == ' ') { //We then check if a whitespace has been found before. We check to see if the first marker has been placed
                    String attr = xmlString.substring(prevPos+1, pos);  //If it has already been placed, we get the substring
                    ParseAttribute(attr);   //We then pass that substring to be parsed into its attributes
                }
                prevPos = pos;  //After that, we move our first flag to our second flag so a second attribute can be found and so on
            }
            if (!hasAttr) { //If there has not been a whitespace found, we are still finding the tag name, so we append each character to our StringBuilder tagName;
                tagName.append(c);  //Append the character
            }
            movePosition(); //Increment each time the loop is run;
        }

        /*
        Now the loop has finished running for the tag,
        we simply deal with creating the node and setting its attributes
         */
        //This is silly, but if the attribute is at the end, it finds the '>' character and quits before it can add the attribute, so we have to check after the fact
        if (hasAttr) {
            String attr = xmlString.substring(prevPos+1, pos);
            ParseAttribute(attr);
        }


        prevPos = pos+1;    //This sets prev pos to the start of the next character after the tag
        String tagNameString = tagName.toString();  //Take the string builder and convert it to a string now that all the characters have been appended to it
        if (!tagNameString.contains("!") && !tagNameString.contains("?")) { //If it contains any of these characters, it is a definition tag so dont add it to the node tree
            if (addToTree) {    //If we are all good to go and can add it
                if (verbose) {
                    System.out.println(++count + "| Creating Link " + currentNode.getID() + "->" + tagNameString);
                }
                currentNode = currentNode.AddChild(tagNameString);  //We make a new node
                if (tempAttributes != null) {   //If it has some attributes
                    currentNode.addAttribute(tempAttributes);   //Add those attributes to the new node
                    tempAttributes = null;  //Now clear the temporary attribute holder for GC to collect
                }
            } else {    //If it was an ending tag, go back to the parent node
                currentNode = currentNode.getParent();  //If it is, move back to the parent
            }
        } else {    //If the tagname contains '!' or '?' then discard any attributes it got from it. Maybe in the future we can process these, but not in this version
            System.out.println("Throwing away tag <" + tagNameString+"...");
            tempAttributes = null;
        }
    }

    //Parses the whole xml string
    private void Parse() {
        while (!endReading) {   //While the EOF has no been met, keep reading the file
            movePosition(); //Continue moving the cursor along
            String data;   //initialize the data field, which is any data in between two tags
            if (c == '<') { //If we encounter the start of a tag we can process whatever data we found
                data = xmlString.substring(prevPos, pos).trim();    //get our data and remove trailing or leading spaces or carriage returns
                if (!data.isEmpty()) { //Make sure its not empty
                    currentNode.setData(data);  //Set this as our data for our new node
                }
                ParseTag(); //Now since we have our new tag, we call parse tag to parse it
            }
        }
    }


    //Starts the whole parsing engine off, setting certain variables and other stuff
    public Node ParseXML() {
        if (xmlString.equals("")) {
            if (filepath.equals("")) {
                System.out.println("Please load XML file first by calling 'class'.LoadXML(Filename)");
                return null;
            } else {
                ReadNextLine(false);
            }
        }
        pos = -1;
        prevPos = 0;
        Parse();
        return currentNode;
    }
}