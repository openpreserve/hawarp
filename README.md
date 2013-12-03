Spacip [![Build Status](https://api.travis-ci.org/shsdev/spacip.png)](https://travis-ci.org/shsdev/spacip) 
============

Introduction
------------

Spacip is a tool to prepare web archive container files in the ARC format which 
are stored in a Hadoop Distributed File System (HDFS) in order to allow 
processing the individual files by means of the SCAPE Platform tool 
[Tomar](https://github.com/openplanets/tomar).

It unpackages flat ARC container files to HDFS, creates a map that allows
identifying which file corresponds to which ARC record, and creates an
input file to be used with [Tomar](https://github.com/openplanets/tomar).

Usage
-----

Invoke the application using hadoop without parameters to get the help
output with parameters that can be used: 

    hadoop jar ./target/spacip-1.0-SNAPSHOT-jar-with-dependencies.jar  

The following parameters can be used in any order:

    -d,--dir <arg>   HDFS directory containing (the) text file(s) with HDFS
                     paths to container files (e.g. '/user/name/inputdir/').
                     [required].
    -e,--ofs <arg>   output file suffix (e.g. '.fits.xml'). [optional,
                     default: '.fits.xml'].
    -h,--help        print this message [optional].
    -i,--sci <arg>   Scape platform invocation command (e.g. 'fits dirxml').
                     [optional, default: 'tool operation'].
    -n,--npt <arg>   Number of items to be processed per task (e.g. 50).
                     [optional, default: 50].
    -o,--jop <arg>   Job output hdfs path (e.g. '/user/name/spacip_joboutput/').
                     [optional, default: 'spacip_joboutput'].
    -t,--thp <arg>   Tool output hdfs path (e.g. '/user/name/spacip_tooloutput/').
                     [optional, default 'spacip_tooloutput'].
    -u,--ofs <arg>   Unpack hdfs path (e.g. '/user/name/spacip_unpacked/').
                     [optional, default 'spacip_unpacked'].

Only the input files directory is required, all other parameters have 
default values.

The input directory (parameter -d) must contain (a) text file(s) which 
contains absolute HDFS paths to the ARC container files, for example:

hadoop jar ./target/spacip-1.0-SNAPSHOT-jar-with-dependencies.jar  -d /user/name/inputpaths/
 
If the hadoop job runs successfully, various files are created in different
directories.

First there is the directory where the files contained in the ARC file are
unpackaged to:

    /user/name/spacip_unpacked/

Second, there is the job output directory (default: spacip_joboutput) which
contains different output files inside a timestamp directory:

    ./spacip_joboutput/1385143157862/_SUCCESS
    ./spacip_joboutput/1385143157862/_logs
    ./spacip_joboutput/1385143157862/keyfilmapping-m-00000
    ./spacip_joboutput/1385143157862/part-r-00000
    ./spacip_joboutput/1385143157862/ptmapredinput-m-00000

The 'keyfilemapping-*' file contains the container/record-identifier as 
key and the file name as value so that each unpacked file in HDFS can 
be clearly assigned to the corresponding record and the ARC container.

The 'ptmapredinput-*' file contains the input file which can be used
by the PT-Mapred application by providing it as value of the -i parameter.

The 'part-r-00000' is an empty reducer file which can be ignored.

Dependencies
------------

This application is using the Cloudera CDH3u5 Hadoop distribution,
see the corresponding dependendy in the maven file:

    <dependency>
        <groupId>org.apache.hadoop</groupId>
        <artifactId>hadoop-core</artifactId>
        <version>0.20.2-cdh3u5</version>
    </dependency>

The application produces an input file for the SCAPE Platform tool
[Tomar](https://github.com/openplanets/tomar).

Installation
------------

Maven is used to build the application, change to the directory of the
application and type

    mvn install