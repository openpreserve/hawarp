hawarp
======

Hadoop-based Web Archive Record Processing.

The project consists of a collection of tools to process web archive records
using hadoop. Various applications are aggregated as modules in this project:

arc2warc-migration
------------------

Convert ARC files to WARC files using Hadoop.

droid-identify
--------------

Hadoop Job for Identifying files using  DROID (Digital Record Object 
Identification), Version 6.1, http://digital-preservation.github.io/droid/. 

mapunpack2temp-identify
-----------------------

mapunpack2temp-identify is a tool to identify and/or characterise files packaged 
in container files using a standalone java application or a Hadoop job.

tika-identify
-------------

Hadoop Job for Identifying files using Apache Tika Version 1.0. 

tomar-prepare-inputdata
-----------------------

tomar-prepare-inputdata is a tool to prepare web archive container files in the 
ARC format which are stored in a Hadoop Distributed File System (HDFS) in order 
to allow processing of the individual files by means of the SCAPE Platform tool 
[Tomar](https://github.com/openplanets/tomar).