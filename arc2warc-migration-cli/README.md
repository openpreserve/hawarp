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

usage: hadoop jar
       target/arc2warc-migration-1.0-SNAPSHOT-jar-with-dependencies.jar
       [-h] [-i <arg>] [-m] [-o <arg>] [-r <arg>] [-x]
 -h,--help           print this message [optional].
 -i,--input <arg>    Path to input. [required].
 -m,--mimeident      Do payload mime type identification. [optional].
 -o,--output <arg>   Path to output. [optional].
 -r,--regex <arg>    Only input paths matching the regular expression will
                     be processed. [optional].
 -x,--comprwarc      Create compressed WARC file. [optional].

Example:

    java -jar hawarp/arc2warc-migration-cli/target/arc2warc-migration-cli-1.0-jar-with-dependencies.jar 
    -i /local/input/directory/ -o /local/output/directory/

And to process only files which have a specific extension (e.g. ".arc.gz"), 
a regular expression can be used as a filter for input files (parameter -r):

    java -jar hawarp/arc2warc-migration-cli/target/arc2warc-migration-cli-1.0-jar-with-dependencies.jar 
    -i /local/input/directory/ -o /local/output/directory/ -r ".*\.arc\.gz"

Read more about how this tool can be used in a scalable fashion 
[here](http://www.openplanetsfoundation.org/blogs/2014-03-07-some-reflections-scalable-arc-warc-migration)

This tool will do deduplication of arc records by reading the metadata arc files (actually, the crawler logs in the metadata arc files). Read more about this 
[here](http://www.openplanetsfoundation.org/blogs/2014-03-24-arc-warc-migration-how-deal-de-duplicated-records)

