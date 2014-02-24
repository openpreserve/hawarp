tomar-prepare-inputdata
=======================

Introduction
------------

tomar-prepare-inputdata is a tool to prepare web archive container files in the ARC format which 
are stored in a Hadoop Distributed File System (HDFS) in order to allow 
processing of the individual files by means of the SCAPE Platform tool 
[Tomar](https://github.com/openplanets/tomar).

It unpackages flat ARC container files to HDFS, creates a map that allows
identifying which file corresponds to which ARC record, and creates an
input file to be used with [Tomar](https://github.com/openplanets/tomar).

Install
-------

Install artifact in your local repository:

    cd tomar-prepare-inputdata
    mvn install

Create executable jar with dependencies:

    cd tomar-prepare-inputdata
    mvn assembly:single

Usage
-----

Invoke the application using hadoop without parameters to get the help
output with parameters that can be used: 

    hadoop jar ./target/tomar-prepare-inputdata-1.0-SNAPSHOT-jar-with-dependencies.jar  

The following parameters can be used in any order:

    -d,--dir <arg>   HDFS directory containing (the) text file(s) with HDFS
                     paths to container files (e.g. '/user/name/inputdir/').
                     [required].
    -n,--npt <arg>   Number of items to be processed per invokation (e.g. 50).
                     [optional, default: 50].

Important: The input directory (parameter -d) should not contain the ARC files, 
but it must contain (a) text file(s) with absolute HDFS paths to the ARC container 
files, for example:

hadoop jar ./target/tomar-prepare-inputdata-1.0-SNAPSHOT-jar-with-dependencies.jar  -d /user/name/inputpaths/
 
If the hadoop job runs successfully, various files are created in separate output 
directories.

First there is the directory where unpacked files are copied to:

    /user/name/tomar-prepare-inputdata_unpacked/

Second, there is the job output directory (default: tomar-prepare-inputdata_joboutput) which
contains different output files wrapped in a timestamp directory:

    ./tomar-prepare-inputdata_joboutput/1385143157862/_SUCCESS
    ./tomar-prepare-inputdata_joboutput/1385143157862/_logs
    ./tomar-prepare-inputdata_joboutput/1385143157862/keyfilmapping-m-00000
    ./tomar-prepare-inputdata_joboutput/1385143157862/part-r-00000
    ./tomar-prepare-inputdata_joboutput/1385143157862/tomarinput-m-00000

The 'keyfilemapping-*' file contains the container/record-identifier as 
key and the file name as value so that each unpacked file in HDFS can 
be clearly assigned to the corresponding record and the ARC container.

The 'tomarinput-*' file contains the input file which can be used as input
by [Tomar](https://github.com/openplanets/tomar) (-i parameter).

The 'part-r-00000' is an empty reducer file which can be ignored.

Note that, depending on the Hadoop configuration, failed hadoop tasks might be
re-scheduled to run on another node. This can lead to a higher number of 
unpacked files than there are records in the container because Hadoop does not 
take care of cleaning up files which have been created by the failed task. The 
output files listed above keep track of the generated files, but any additional 
files caused by task failures will be ignored. It just means that in case of
task failures some additional storage is required.

The job produces one output file per map task for the 'keyfilemapping' and
'tomarinput' output types. 

These files can be easily merged, e.g. for the 'keyfilemapping' using the 
following command:

    hadoop fs -cat ./tomar-prepare-inputdata_joboutput/1385143157862/keyfilmapping-m-* | hadoop fs -put - ./tomar-prepare-inputdata_joboutput/1385143157862/keyfilmapping-aggregated.txt

And for the [Tomar](https://github.com/openplanets/tomar) input file 
correspondingly:

    hadoop fs -cat ./tomar-prepare-inputdata_joboutput/1385143157862/tomarinput-m-* | hadoop fs -put - ./tomar-prepare-inputdata_joboutput/1385143157862/tomarinput-aggregated.txt

By that way [Tomar](https://github.com/openplanets/tomar) can be invoked using
the aggregated input file: 

    hadoop jar tomar.jar -i /user/onbfue/tomar-prepare-inputdata_joboutput/1385143157862/tomarinput-aggregated.txt -r /user/name/scape-toolspecs

where the value of the parameter -i is the aggregated [Tomar](https://github.com/openplanets/tomar) 
input file and the value of the -r parameter indicates the directory where the 
tool specification files for [Tomar](https://github.com/openplanets/tomar) can be found.

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