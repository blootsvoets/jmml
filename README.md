The Java Multiscale Modeling Language implementation
=====================================

The jMML library is an implementation of most of the language features of the Multiscale Modeling Language (MML).
It can read the XML format of MML (xMML) and generate
* a graphical coupling topology
* a graphical task graph
* a graphical domain subdivision
* a MUSCLE configuration file
* a MUSCLE skeleton configuration

The library is divided in three parts: jMML-util which contains all utility
classes like SI unit handling and data types. Then jMML-specification uses
JAXB to convert a xMML file into Java classes. This conversion is automatically
done based on the xMML XML Schema. Finally, jMML-api does content generation.
Although only 5 files can be generated, from the API any number of descriptions
of for instance a task graph or a coupling topology can be generated.

For building and deploying Maven is used.
