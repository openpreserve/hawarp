Hawarp
======
*Hadoop-based Web Archive Record Processing*

![Hawarp Logo](https://lh4.googleusercontent.com/RTeHNY7CDXRu3Vd3Lrny0G8DUhvjkrKxCSKA1Po9LFGzGYeqIVuqzZPMO83oeWzjcNoYvn3H9TxNe1XzVBdR6M4SwXtR06qj5wmXJa7XfVTLEsxyxdGS8v1gJHs596qPXQ?raw=true)


Usage Examples
--------------

Examples how these tools can be applied in context are available in form of Taverna Workflows published on myExperiment:

* [ARC to WARC Migration with CDX Index and wayback rendering screenshot comparison](http://www.myexperiment.org/workflows/4333.html)
* [ONB Web Archive Fits Characterisation using ToMaR](http://www.myexperiment.org/workflows/3933.html)

**Simple usage examples of the standaline Java executables**

Use arc2warc-migration-cli to migrate an ARC container file to a file in the new WARC format:

    java -jar hawarp/arc2warc-migration-cli/target/arc2warc-migration-cli-1.0-jar-with-dependencies.jar 
    -i /local/input/directory/ -o /local/output/directory/
    
Run droid-identification using Apache Hadoop:

    hadoop jar target/droid-identify-1.0.jar-with-dependencies.jar 
    -d /hdfs/path/to/textfiles/with/absolutefilepaths/ -n job_name
    
The input for this Hadoop job is a text file listing file paths (either local file system paths – accessible from each worker node – or hadoop distributed file system paths).

More usage example on other tools can be found in the documentation of the individual modules.

Hawarp Modules
--------------

The following set of command line interface applications is included in this 
project as modules:

**arc2warc-migration-cli**

Command line application to convert ARC container files to the new ISO standard
format WARC. 

[Download (v1.0)](http://dl.bintray.com/shsdev/generic/arc2warc-migration-cli-1.0-jar-with-dependencies.jar)

[Documentation](https://github.com/openplanets/hawarp/blob/master/arc2warc-migration-cli/README.md)

**CDX creator**

A tool to create CDX index files which are required if the wayback software is used to display archived resources contained in ARC or WARC container files.

[Download (v1.0)](http://dl.bintray.com/shsdev/generic/cdx-creator-1.0-jar-with-dependencies.jar)

[Documentation](https://github.com/openplanets/hawarp/blob/master/cdx-creator/README.md)

**droid-identify**

Hadoop Job for Identifying files using  DROID (Digital Record Object 
Identification), Version 6.1, http://digital-preservation.github.io/droid/. 

[Documentation](https://github.com/openplanets/hawarp/blob/master/droid-identify/README.md)

**unpack2temp-identify**

unpack2temp-identify is a tool to identify and/or characterise files packaged 
in container files using a standalone java application or a Hadoop job.

[Documentation](https://github.com/openplanets/hawarp/blob/master/unpack2temp-identify/README.md)

**tika-identify**

Hadoop Job for Identifying files using Apache Tika Version 1.0. 

[Documentation](https://github.com/openplanets/hawarp/blob/master/tika-identify/README.md)

**tomar-prepare-inputdata**

tomar-prepare-inputdata is a tool to prepare web archive container files in the 
ARC format which are stored in a Hadoop Distributed File System (HDFS) in order 
to allow processing of the individual files by means of the SCAPE Platform tool 
[Tomar](https://github.com/openplanets/tomar).

[Documentation](https://github.com/openplanets/hawarp/blob/master/tomar-prepare-inputdata/README.md)

Requirements
------------

* Java >= 1.7
* Apache Hadoop (e.g. [Cloudera CDH](http://www.cloudera.com/content/cloudera/en/products-and-services/cdh.html))
