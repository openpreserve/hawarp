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
    [-i <arg>] [-o <arg>] [-x <arg>]  [-p] [-d] [-h] [-l] 

with the following options:

     -i,--input <arg>    HDFS Input directory with ARC files. [required].
     -o,--output <arg>   HDFS Output directory where the WARC files will be
                         stored. [required].
     -x,--iregex <arg>   Only input paths matching the regular expression will
                         be processed (default: ".*"). [optional].
     -p,--payloadid      Do payload mime type identification. [optional].
     -d,--digest         Calculate sha1 payload digest. [optional].
     -c,--comprwarc      Create compressed WARC file. [optional].
     -l,--local          Use local file system instead of HDFS (debugging).
                         [optional].
     -h,--help           print this message [optional].

For example, to process all files which have an ".arc.gz" extension, the 
following command would be used:

    hadoop jar hawarp/arc2warc-migration/target/arc2warc-migration-1.0-jar-with-dependencies.jar 
    -i /hdfs/input/directory/ -o /hdfs/output/directory/ -x ".*\.arc\.gz"

Note that it is possible to use local file sytem inputs and outputs by using the
-l flag, reading/writing from/to HDFS is the default.
