cdx-creator
===========

Java-based command line application to create a CDX index for ARC and WARC 
files.

Installation
------------

Install artifact in your local repository:

    cd cdx-creator
    mvn install

Create executable jar with dependencies:

    cd cdx-creator
    mvn assembly:single

Usage
-----

Execute from the command line:

    java -jar
    target/cdx-creator-1.0-SNAPSHOT-jar-with-dependencies.jar
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
