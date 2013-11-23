Spacip [![Build Status](https://api.travis-ci.org/shsdev/spacip.png)](https://travis-ci.org/shsdev/spacip) 
============

Spacip is a tool to prepare web archive containers in the ARC format
to be used as input to be processed by the SCAPE Platform. By unpacking
the ARC files into HDFS. Furthermore, it provides a key-value file
with file names and record keys and an input file which allows using
the SCAPE PT-Mapred application for processing the files on a cluster.

Dependency
----------

This application is using the Cloudera CDH3u5 Hadoop distribution,
see the corresponding dependendy in the maven file:

    <dependency>
        <groupId>org.apache.hadoop</groupId>
        <artifactId>hadoop-core</artifactId>
        <version>0.20.2-cdh3u5</version>
    </dependency>

The application produces an input file for the SCAPE Platform tool
PT-Mapred which can be downloaded here:

    https://github.com/openplanets/scape/tree/master/pt-mapred

Installation
------------

Maven is used to build the application, change to the directory of the
application and type

    mvn install

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
    -o,--jop <arg>   Job output hdfs path (e.g. '/user/name/joboutput/').
                     [optional, default: 'spacip_joboutput'].
    -t,--thp <arg>   Tool output hdfs path (e.g. '/user/name/tooloutput/').
                     [optional, default 'spacip_tooloutput'].
    -u,--ofs <arg>   Unpack hdfs path (e.g. '/user/name/unpacked/').
                     [optional, default 'spacip_unpacked'].

Only the input files directory is required, all other parameters have 
default values.

The input directory (parameter -d) must contain (a) text file(s) which 
contains absolute HDFS paths to the container files.
 
If the hadoop job runs successfully, various files are created in the 
job output directory (default: spacip_joboutput) wrapped inside a 
timestamp directory:

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
