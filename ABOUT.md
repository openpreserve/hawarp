Hawarp
======

What Is Hawarp?
---------------

Hawarp is a set of tools for processing web archive data by means of the Hadoop 
framework. The different tools are available as command line interface 
applications, each with it's own purpose, documentation, and usage modalities.

Hawarp is built on top of the web archive framework 
[JWAT](https://sbforge.org/display/JWAT/) which allows processing web archive records 
stored in ARC (old standard) and WARC (new ISO standard) container files. All 
the tools included in Hawarp make use of the JWAT framework to iterate over all 
records contained in a given set of web archive container files available in the 
ARC or WARC format in order to either extract information from the individual 
records or to migrate from one format to another, especially migrating from the 
old standard format ARC to the new WARC format.

Some of the tools, such as 
[hawarp/droid-identify](https://github.com/openplanets/hawarp/tree/master/droid-identify) 
and 
[hawarp/tika-identify](https://github.com/openplanets/hawarp/tree/master/tika-identify), 
are native [MapReduce](http://en.wikipedia.org/wiki/MapReduce) implementations 
which directly process web archive records in a distributed manner using a 
Hadoop cluster. The input of these tools are text files containing local file system or HDFS paths to the files to be identified. Local paths can only be used if the cluster uses a shared storage, i.e. all worker nodes must be able to access the file paths listed in the input file. See the [blog post about Hadoop-based DROID identification](http://www.openplanetsfoundation.org/blogs/2013-05-24-droid-file-format-identification-using-hadoop) to get an idea how these tool can be applied.

[hawarp/arc2warc-migration-cli](https://github.com/openplanets/hawarp/tree/master/arc2warc-migration-cli), 
is a standalone command line tool. In contrast to the aforementioned tools it does not include its own MapReduce implementation but is designed to be used together with [ToMaR](https://github.com/openplanets/tomar) for parallelized processing on a computer cluster. The blog post 
[Quality assured ARC to WARC migration](http://openplanetsfoundation.org/blogs/2014-07-10-quality-assured-arc-warc-migration) explains how this tool can be used in a web archive data migration scenario.

[hawarp/tomar-prepare-inputdata](https://github.com/openplanets/hawarp/tree/master/tomar-prepare-inputdata) 
is a tool to unpack web archive container files as a preparatory processing step to enable the processing of individual files contained in ARC or WARC files using [ToMaR](https://github.com/openplanets/tomar); 
an example for this is the characterisation tool [FITS](http://projects.iq.harvard.edu/fits) which can be applied to the 
individual files only if they are extracted in a first step. See the blog post [Web Archive FITS Characterisation using ToMaR](http://www.openplanetsfoundation.org/blogs/2013-12-16-web-archive-fits-characterisation-using-tomar) to get more information about how this tool is intended to be used together with other long-term preservation components.

[hawarp/unpack2temp-identify](https://github.com/openplanets/hawarp/tree/master/unpack2temp-identify) 
is a tool which unpacks ARC or WARC container files to 
the temporary local file system of cluster nodes in order to run various file 
format identification tools afterwards. In this case Hadoop is only used to run 
the unpacking and identification process in parallel.

Finally, the 
[hawarp/cdx-creator](https://github.com/openplanets/hawarp/tree/master/cdx-creator) is a tool to create CDX index files which are required 
if the wayback software is used to display archived resources contained in ARC 
or WARC container files. See the blog post 
[Quality assured ARC to WARC migration](http://openplanetsfoundation.org/blogs/2014-07-10-quality-assured-arc-warc-migration) 
to get an overview how the 
[hawarp/cdx-creator](https://github.com/openplanets/hawarp/tree/master/cdx-creator) 
is used to compare two versions of CDX index files, one created out of the 
original ARC and the other one of the migrated WARC file, in order to verify if 
the migration was successful.


What Can Hawarp Do For Me?
--------------------------

* Identify large lists of files using Apache Hadoop [hawarp/droid-identify](https://github.com/openplanets/hawarp/tree/master/droid-identify) 
and 
[hawarp/tika-identify](https://github.com/openplanets/hawarp/tree/master/tika-identify). 
* Migrate very large data sets available in the ARC format to the new ISO standard format WARC ([hawarp/arc2warc-migration-cli](https://github.com/openplanets/hawarp/tree/master/arc2warc-migration-cli) together with [ToMaR](https://github.com/openplanets/tomar)).
* Extract  files from ARC or WARC records included in container files so that some kind of property extraction can be applied to the individual files [hawarp/tomar-prepare-inputdata](https://github.com/openplanets/hawarp/tree/master/tomar-prepare-inputdata).
* Run a set of file format identification/characterisation tools on ARC or WARC container files to extract information about records in tabular form ([hawarp/unpack2temp-identify](https://github.com/openplanets/hawarp/tree/master/unpack2temp-identify) ).
* Create CDX index files from ARC or WARC container files ([hawarp/cdx-creator](https://github.com/openplanets/hawarp/tree/master/cdx-creator)).

Hawarp Can Be Used By
---------------------

* People maintaining a multi-terabyte web archives storing their data in the ARC/WARC format.
* People holding large amounts of data in the ARC/WARC format for some reason.
* People who have large amounts of web archive container files in the ARC format which they want to convert to the new ISO standard WARC.

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

Publications
------------

* [Quality assured ARC to WARC migration](http://openplanetsfoundation.org/blogs/2014-07-10-quality-assured-arc-warc-migration)
* [Will the real lazy pig please scale up: quality assured large scale image migration](http://openplanetsfoundation.org/blogs/2014-06-24-will-real-lazy-pig-please-scale-quality-assured-large-scale-image-migration)
* [ARC to WARC migration: How to deal with de-duplicated records?](http://openplanetsfoundation.org/blogs/2014-03-24-arc-warc-migration-how-deal-de-duplicated-records)
* [Some reflections on scalable ARC to WARC migration](http://openplanetsfoundation.org/blogs/2014-03-07-some-reflections-scalable-arc-warc-migration)
* Schmidt, R., Rella, M., and Schlarb, S. ToMaR - A Data Generator for Large Volumes of Content. In: IEEE/ACM CCGrid 2014: 14th IEEE/ACM International Symposium on Cluster, Cloud and Grid Computing, May 26-29 2014, pages 937–942, Chicago, USA, 2014. IEEE.

Credits
-------

* Alan Akbik <alan.akbik@tu-berlin.de>, Technical University of Berlin
* Asger Askov Blekinge <abr@statsbiblioteket.dk>, State and University Library of Denmark
* Kristin Dill <kristin.dill@onb.ac.at>, Austrian National Library
* Martin Schaller <martin.schaller@onb.ac.at>, Austrian National Library
* Matthias Rella <matthias.rella.fl@ait.ac.at>, Austrian Institute of Technology
* Sven Schlarb <sven.schlarb@onb.ac.at>, Austrian National Library
* Rainer Schmidt <rainer.schmidt@ait.ac.at>, Austrian Institute of Technology
