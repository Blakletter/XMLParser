package com.xmlparser.xml;
import java.util.*;

public class Node {
    private int level=1;
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
        return children.isEmpty();
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

    public String toXml( ) {
        String response = this.toString();
        for (Node n : children) {
            response += n.toXml().stripTrailing();
        }
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

    public String getData() {
        if (this.data == null) {
            return null;
        }
        return new String(this.data);
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

    public Node setData(String data){
        if (data==null) {
            this.data = null;
            return this;
        }
        if (children.size()==0) this.data = data.getBytes();
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
        parent.setData(null);
        parent.children.add(this);
        this.parent = parent;
        return this;
    }


    @Override
    public String toString() {
        String response = "";
        String _attributes = "";
        //The name of the tag
        response+=  "\n<"+ getName();
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
        return response + ((getData()==null)?"\n":"") + "</"+ getName()+">";
    }
}
