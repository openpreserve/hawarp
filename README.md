arc2warc-hadoop
===============

Convert ARC files to WARC files using Hadoop

Installation
------------

    cd arc2warc-hadoop
    mvn install

Usage
-----

Execute hadoop job from the command line:

    hadoop jar target/arc2warc-hadoop-1.0.jar-with-dependencies.jar 
      -i <input directory> -o <output directory> [-l]

    -i --input  : HDFS Input directory with ARC files. [required].
    -o --output : HDFS Output directory where the WARC files will be stored. [required].
    -l --local  : Use local file system instead of HDFS (debugging). [optional].
