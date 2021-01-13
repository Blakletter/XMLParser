package com.xmlparser;
import java.util.*;

public class Node {
    private byte[] data = null;
    private Node parent = null;
    private byte[] id;
    private List<Node> children = new ArrayList<>();
    private Hashtable<String, String> attributes = null;
    private int child = 0;

    public Node(String id) {
        this.id = id.getBytes();
    }

    public String toXML() {
        char configurationChar = getID().charAt(0);
        boolean isConfiguration = (configurationChar=='?'||configurationChar=='!');


        String response = "";
        String _attributes = "";
        if (attributes!=null) {
            // get keys() from Hashtable and iterate
            Enumeration<String> enumeration = attributes.keys();
            // iterate using enumeration object
            while (enumeration.hasMoreElements()) {
                String key = enumeration.nextElement();
                _attributes += key + "=" + "\"" + attributes.get(key) + "\" ";
            }
            _attributes = _attributes.trim();
        }
        response+=  "\n<"+getID();
        response+=  (!_attributes.equals("")?" "+_attributes:"");
        if (isConfiguration) return response+configurationChar+">";

        boolean isNullTag = (children.isEmpty() && getData()==null);
        if (isNullTag) {
            return response + "\\>";
        }
        response+=">";
        if (children.size() > 0) {
            for (int i = 0; i < children.size(); i++) {
                response += children.get(i).toXML().stripTrailing();
            }
        } else {
            response += "\n" + getData();
        }

        response += (isNullTag) ? "" : "\n</"+getID()+">";
        return response;
    }

    public Node addAttribute(HashMap h) {
        if (attributes==null) attributes = new Hashtable<>();
        if (!h.isEmpty()) attributes.putAll(h);
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

    public ArrayList<Node> findNodes(String s) {
        ArrayList<Node> nodes = new ArrayList<>();
        if (new String(this.id).equals(s)) {
            nodes = new ArrayList<>();
            nodes.add(this);
        }
        for (int i=0; i<children.size(); i++) {
            ArrayList<Node> response = children.get(i).findNodes(s);
            if (response != null) {
                nodes = (nodes==null) ? new ArrayList<>() : nodes;
                nodes.addAll(response);
            }
        }
        return nodes;
    }

    public Node setData(String data){
        this.data = data.getBytes();
        return this;
    }

    public String getID() {
        return new String(id);
    }

    public Node getParent() {
        return parent;
    }

    public Node setParent(Node parent) {
        this.parent = parent;
        return this;
    }

    public Node addChild(Node node) {
        children.add(node);
        return this;
    }

    @Override
    public String toString() {
        return "Node{" +
                "data='" + data + '\'' +
                ", parent=" + parent +
                ", id='" + id + '\'' +
                ", children=" + children +
                ", child=" + child +
                '}';
    }
}
