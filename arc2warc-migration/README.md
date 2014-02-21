arc2warc-migration
==================

Convert ARC files to WARC files using Hadoop

Installation
------------

    cd arc2warc-migration
    mvn install

Usage
-----

Execute hadoop job from the command line:

    hadoop jar target/arc2warc-migration-1.0.jar-with-dependencies.jar 
      -i <input directory> -o <output directory> [-l]

    -i --input  : HDFS Input directory with ARC files. [required].
    -o --output : HDFS Output directory where the WARC files will be stored. [required].
    -l --local  : Use local file system instead of HDFS (debugging). [optional].
