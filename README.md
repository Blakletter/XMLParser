
### Hello, welcome to my XML parser library :grin:

This library gives you the ability to load in xml, navigate and change the structure using a node based approach, and export the nodes back out into xml.

This library was created for fun, as a challenge, and because there are always better ways to make things work.

### Getting started

Clone the repo and import XMLParser.java and Node.java into your project

Usages:

Navigate inside a node structure, remove all of that nodes children and set some data there instead
```
File file = new File("xmldocument.xml").getAbsoluteFile();
XMLParser xmlTree = new XMLParser().loadXmlFile(file).setVerbose(false);
Node root = xml1.parseXML();
root.navigateInside("project").clearChildren().setName("New data");
System.out.println(xmlTree.toXml(root));
```
This would take an xml file of structure
```
<root>
    <project>
        <data>I am some data!</data>
    </project>
</root>
```
and print out xml of structure
```
<root>
   <project>New Data</project>
</root>
```


Find all elements with particular name and print out the nodes data
```
File file = new File("xmldocument.xml").getAbsoluteFile();
XMLParser tree = new XMLParser().loadXmlFile(file).setVerbose(false);
Node root = tree.parseXML();
ArrayList<Node> allNodes = root.getAllWithName("project");
for (Node node : allNodes) {
    System.out.println(node.getData());
}
```

Find all elements with particular attribute and print out the nodes data
```
File file = new File("xmldocument.xml").getAbsoluteFile();
XMLParser tree = new XMLParser().loadXmlFile(file).setVerbose(false);
Node root = tree.parseXML();
ArrayList<Node> allNodes = root.getAllChildrenWithAttribute("id", "67");
for (Node node : allNodes) {
    System.out.println(node.getData());
}
```

Read in the xml, add a new configuration string, and print out the resulting xml
```
File file = new File("xmldocument.xml").getAbsoluteFile();
XMLParser tree = new XMLParser().loadXmlFile(file).setVerbose(false);
Node root = tree.parseXML();
tree.clearConfigurationStrings().addConfigurationString("<!DOCTYPE root>");
System.out.println(tree.toXml(root));
}
```


