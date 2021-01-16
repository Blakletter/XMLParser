package com.xmlparser.xml;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.*;

public class Node {
    private int level=0;
    private byte[] data = null;
    private Node parent = null;
    private byte[] name;
    private List<Node> children = new ArrayList<>();
    private Hashtable<String, String> attributes = null;

    public Node(String name) {
        this.name = name.getBytes();
    }

    public Node navigateInside(String name) {
        for (int i = 0; i<children.size(); i++) {
            if (name.equals(children.get(i).getName())) {
                return children.get(i);
            }
        }
        return null;
    }

    public Node navigateInside(int index) {
        if (children.size()>index) return children.get(index);
        return null;
    }

    public boolean hasChildren() {
        return !children.isEmpty();
    }

    public Node clearChildren() {
        this.children.clear();
        return this;
    }

    public int getLevel() {
        return level;
    }

    public Node setLevel(int level) {
        this.level = level;
        return this;
    }


    public List<Node> getChildren() {
        return children;
    }

    protected String toXml(String tab) {
        String response = "\n";
        for (int i=0; i<getLevel(); i++) response+=tab;
        response += this.toString();
        for (Node n : children) {
            response += n.toXml(tab).stripTrailing();
        }

        if (getData()==null) {
            response+="\n";
            for (int i=0; i<getLevel(); i++) response+=tab;
        }
        if (getData()!=null || hasChildren()) response += "</"+getName()+">";
        return response;
    }

    public Node addAttributes(Hashtable table) {
        if (attributes==null) attributes = new Hashtable<>();
        if (table!=null && !table.isEmpty()) attributes.putAll(table);
        return this;
    }
    public Node addAttribute(String key, String value) {
        if (attributes==null) attributes = new Hashtable<>();
        attributes.put(key, value);
        return this;
    }

    public Hashtable<String, String> getAttributes() {
        return attributes;
    }



    public ArrayList<Node> getAllWithName(String name) {
        ArrayList<Node> response = new ArrayList<>();
        if (name.equals(getName())) {
            response.add(this);
        }
        for (Node child: children) {
            response.addAll(child.getAllWithName(name));
        }
        return response;
    }
    public String getData() {
        if (this.data == null) return null;
        return new String(this.data);
    }
    public Node setData(String data) {
        if (data==null) return this;
        this.data = data.getBytes();
        return this;
    }
    public Node addData(String data){
        if (data==null) {
            this.data = null;
            return this;
        }
        if (this.data==null) this.data = data.getBytes();

        else if (children.size()==0) this.data = (getData()+System.lineSeparator()+data).getBytes();
        return this;
    }

    public String getName() {
        return new String(name);
    }

    public Node setName(String name) {
        this.name = name.getBytes();
        return this;
    }

    public ArrayList<Node> getAllChildrenWithAttribute(String key, String value) {
        ArrayList<Node> response = new ArrayList<>();
        String myAttribute = attributes.get(key);
        if (myAttribute!=null && myAttribute.equals(value)) {
            response.add(this);
        }
        for (Node child:children) {
            response.addAll(child.getAllChildrenWithAttribute(key, value));
        }
        return response;
    };

    public Node getParent() {
        return parent;
    }


    public Node setParent(Node parent) {
        setLevel(parent.getLevel()+1);
        parent.addData(null);
        parent.children.add(this);
        this.parent = parent;
        return this;
    }


    @Override
    public String toString() {
        String response = "";
        String _attributes = "";
        //The name of the tag
        response+=  "<"+ getName();
        //Go through the attributes
        if (attributes!=null) {
            Enumeration<String> enumeration = attributes.keys();
            while (enumeration.hasMoreElements()) {
                String key = enumeration.nextElement();
                _attributes += key + "=" + "\"" + attributes.get(key)+ "\" ";
            }
        }
        response +=  (!_attributes.equals("")?" "+_attributes.trim():"");
        boolean isNullTag = (children.isEmpty() && getData()==null);
        if (isNullTag) return response + "/>";
        response+=">";
        if (children.size() == 0) response += getData();
        return response;
    }
}
