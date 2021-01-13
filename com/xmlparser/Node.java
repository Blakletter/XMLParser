package com.xmlparser;
import java.util.*;
import com.xmlparser.Node;

public class Node {
    private byte[] data;
    private Node parent;
    private byte[] id;
    private final ArrayList<Node> children = new ArrayList<>();
    private HashMap<String, byte[]> attributes;
    private int child = 0;

    public Node(String id) {
        this.id = id.getBytes();
    }

    public void addAttribute(HashMap<String, byte[]> h) {
        attributes = new HashMap<>();
        attributes.putAll(h);
    }

    public String getAttribute(String name) {
        if (attributes != null) {
            return new String(attributes.get(name));
        } else {
            return null;
        }
    }

    public String getData() {
        if (this.data == null) {
            return null;
        }
        return new String(this.data);
    }

    public HashMap<String, byte[]> getAttributes() {
        return this.attributes;
    }

    public ArrayList<Node> getAllChildrenWithID(String id) {
        ArrayList<Node> nodes = new ArrayList<>();
        Node n;
        for (int i=0; i<children.size(); i++) {
            n = children.get(i);
            if (new String(n.getID()).equals(id)) {
                nodes.add(n);
            }
        }
        return nodes;
    }

    public ArrayList<Node> findNodes(String s) {
        ArrayList<Node> nodes = new ArrayList<>();
        if (new String(this.id).equals(s)) {
            nodes.add(this);
            if (this.getAttributes() != null) {
                System.out.println("Adding author to list with attribute " + this.getAttributes().toString());
            }
        }
        for (int i=0; i<children.size(); i++) {
            ArrayList<Node> response = children.get(i).findNodes(s);
            if (!response.isEmpty()) {
                nodes.addAll(response);
            }
        }
        return nodes;
    }


    public ArrayList<Node> getAllChildren() {
        return children;
    }

    public void setData(String data){
        this.data = data.getBytes();
    }

    public void setID(byte[] id) {
        this.id = id;
    }

    public String getID() {
        return new String(id);
    }

    public Node getParent() {
        return parent;
    }

    public Node nextChild() {
        return parent.moveNextChild();
    }

    public Node moveNextChild() {
        return children.get(++child);
    }

    public Node getChildWithID(String s) {
        for (int i =0; i<children.size(); i++) {
            if (children.get(i).getID().equals(s)) {
                child = i;
                return children.get(i);
            }
        }
        return null;
    }

    public int getChildrenCount() {
        return children.size();
    }

    public boolean childWithIDExists(String s) {
        for (int i =0; i<children.size(); i++) {
            if (children.get(i).getID() == s) {
                return true;
            }
        }
        return false;
    }

    public Node AddChild(String id) {
        Node newNode = new Node(id);
        newNode.parent = this;
        children.add(newNode);
        return newNode;
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
