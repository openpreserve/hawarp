arc2warc-migration-cli
======================

Java-based command line application to convert web archive ARC files to WARC 
files.

Installation
------------

Install artifact in your local repository:

    cd arc2warc-migration-cli
    mvn install

Create executable jar with dependencies:

    cd arc2warc-migration-cli
    mvn assembly:single

Usage
-----

Execute hadoop job from the command line:

    java -jar
    target/arc2warc-migration-1.0-SNAPSHOT-jar-with-dependencies.jar
       [-c] [-h] [-i <arg>] [-o <arg>] [-p] [-x <arg>]

with the following options:
       
        -c,--comprwarc      Create compressed WARC file. [optional].
        -h,--help           print this message [optional].
        -i,--input <arg>    Input directory containing ARC files or single ARC
                            file path. [required].
        -o,--output <arg>   Output directory or output WARC file path when using
                            input ARC file path. [required].
        -p,--payloadid      Do payload mime type identification. [optional].
        -x,--iregex <arg>   Only input paths matching the regular expression will
                            be processed. [optional].
Example:

    java -jar hawarp/arc2warc-migration-cli/target/arc2warc-migration-cli-1.0-jar-with-dependencies.jar 
    -i /local/input/directory/ -o /local/output/directory/

And to process only files which have a specific extension (e.g. ".arc.gz"), 
a regular expression can be used as a filter for input files (parameter -x):

    java -jar hawarp/arc2warc-migration-cli/target/arc2warc-migration-cli-1.0-jar-with-dependencies.jar 
    -i /hdfs/input/directory/ -o /hdfs/output/directory/ -x ".*\.arc\.gz"