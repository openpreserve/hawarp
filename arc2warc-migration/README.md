arc2warc-migration
==================

Convert ARC files to WARC files using Hadoop

Installation
------------

Run install on the wrapper project hawarp before building individual 
components:

    cd hawarp
    mvn install

Install artifact in your local repository:

    cd hawarp/arc2warc-migration
    mvn install

Create executable jar with dependencies:

    cd hawarp/arc2warc-migration
    mvn assembly:single

Usage
-----

Execute hadoop job from the command line:

    hadoop jar target/arc2warc-migration-1.0.jar-with-dependencies.jar 
      -i <input directory> -o <output directory> [-l]

    -i --input  : HDFS Input directory with ARC files. [required].
    -o --output : HDFS Output directory where the WARC files will be stored. [required].
    -l --local  : Use local file system instead of HDFS (debugging). [optional].
