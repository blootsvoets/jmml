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

## How to run

Dependencies are Java, Maven and Graphviz.

Compile the code with

```
mvn install
```

and run it with

```
bin/jmml path/to/xmml_file.xml -g path/to/task_graph_file.pdf
```

Other options are shown when running `bin/jmml`.

## Code organization

The code has three modules: _jmml-api_, _jmml-specification_ and _jmml-util_. The _jmml-api_ module contains the actual algorithms, _jmml-specification_ contains the XML bindings to xMML and _jmml-util_ contains unit and formula handling.

