# XMLParser | Written by Jeremy Colegrove

Parses XML in a very efficient manner, and constructs a node tree to navigate within

The example uses a multithreaded approach. It constructs the tree character by character from the text file, so even large files (2 million+ lines of xml) can be read in and stored relatively quickly.

In the example given, it loads in a small xml document, gets all of the <name> tags and displays their value.
