hymnal-tool
===========

Extracts and converts LSM hymnals found online.

The broad goal is to make it easy for people to make prettier and better ways to view hymns electronically. Currently it only supports parsing the 2014 HTML versions of the hymnal found on

https://sites.google.com/site/hymnalfeedback/home

Feel free to fork the project and extend its parsing capability. For output you currently can have a single or multiple plain text or json files.

Quick Start
-----------

You need the following to compile and run the project:

* Java 1.6+
* Maven

After cloning the repo, do the following:

1.  Build project.

  ```
  mvn package
  ```

2.  Simply include the snapshot jar in the target folder in your project if you are using it in your own code. Otherwise You can use the command line tool in the Converter via a bash script. Running it without parameters will give you a usage message.
  
  ```
  ./script/convert.sh
  ```

  If you can't run bash script, reading the script should give you a good idea how to run it directly with java.

Have fun!
