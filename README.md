hawarp
======

HAdoop-based Web Archive Record Processing.

This project consists of a collection of tools to process web archive records
using the Hadoop framework. 

Requires Java >= 1.7

Several command line applications are aggregated as modules in this project:

arc2warc-migration-cli
----------------------

Command line applicaiton to convert ARC container files to the new ISO standard
format WARC.

[Documentation](https://github.com/openplanets/hawarp/blob/master/arc2warc-migration-cli/README.md)

arc2warc-migration-cli
----------------------

Hadoop job to convert ARC container files to the new ISO standard format WARC.

[Documentation](https://github.com/openplanets/hawarp/blob/master/arc2warc-migration-hdp/README.md)

droid-identify
--------------

Hadoop Job for Identifying files using  DROID (Digital Record Object 
Identification), Version 6.1, http://digital-preservation.github.io/droid/. 

[Documentation](https://github.com/openplanets/hawarp/blob/master/droid-identify/README.md)

unpack2temp-identify
-----------------------

unpack2temp-identify is a tool to identify and/or characterise files packaged 
in container files using a standalone java application or a Hadoop job.

[Documentation](https://github.com/openplanets/hawarp/blob/master/unpack2temp-identify/README.md)

tika-identify
-------------

Hadoop Job for Identifying files using Apache Tika Version 1.0. 

[Documentation](https://github.com/openplanets/hawarp/blob/master/tika-identify/README.md)

tomar-prepare-inputdata
-----------------------

tomar-prepare-inputdata is a tool to prepare web archive container files in the 
ARC format which are stored in a Hadoop Distributed File System (HDFS) in order 
to allow processing of the individual files by means of the SCAPE Platform tool 
[Tomar](https://github.com/openplanets/tomar).

[Documentation](https://github.com/openplanets/hawarp/blob/master/tomar-prepare-inputdata/README.md)
