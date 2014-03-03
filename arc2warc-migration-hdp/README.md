arc2warc-migration
==================

Convert ARC files to WARC files using Hadoop

Installation
------------

Install artifact in your local repository:

    cd arc2warc-migration
    mvn install

Create executable jar with dependencies:

    cd arc2warc-migration
    mvn assembly:single

Usage
-----

Execute hadoop job from the command line:

    hadoop jar
    target/arc2warc-migration-1.0-SNAPSHOT-jar-with-dependencies.jar
    [-i <arg>] [-o <arg>] [-x <arg>]  [-p] [-l] [-h] 

with the following options:

     -i,--input <arg>    HDFS Input directory with ARC files. [required].
     -o,--output <arg>   HDFS Output directory where the WARC files will be
                         stored. [required].
     -x,--iregex <arg>   Only input paths matching the regular expression will
                         be processed (default: ".*"). [optional].
     -p,--payloadid      Do payload mime type identification. [optional].
     -c,--comprwarc      Create compressed WARC file. [optional].
     -t,--localtest      Start application as a local java application without 
                         hadoop (testing). [optional]
     -l,--local          Use local file system instead of HDFS (debugging).
                         [optional].
     -h,--help           print this message [optional].

The migration can be tested locally using the standalone java application without 
Hadoop by using the -t flag (test):

    java -jar hawarp/arc2warc-migration/target/arc2warc-migration-1.0-jar-with-dependencies.jar 
    -i /local/input/directory/ -o /local/output/directory/ -t 

Also with Hadoop, for testing and debugging purposes, local file sytem inputs and 
outputs can be used instead of HDFS by using the -l flag (reading/writing 
from/to HDFS is the default):

    hadoop jar hawarp/arc2warc-migration/target/arc2warc-migration-1.0-jar-with-dependencies.jar 
    -i /local/input/directory/ -o /local/output/directory/ -l

An example of default Hadoop processing, for example, to process all files in an 
HDFS directory:

    hadoop jar hawarp/arc2warc-migration/target/arc2warc-migration-1.0-jar-with-dependencies.jar 
    -i /hdfs/input/directory/ -o /hdfs/output/directory/

And to process only files which have a specific extension (e.g. ".arc.gz"), 
a regular expression can be used as a filter for input files (parameter -x):

    hadoop jar hawarp/arc2warc-migration/target/arc2warc-migration-1.0-jar-with-dependencies.jar 
    -i /hdfs/input/directory/ -o /hdfs/output/directory/ -x ".*\.arc\.gz"