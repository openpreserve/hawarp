droid-identify-hadoopjob
========================

Hadoop job for file identification using Droid.

Build project
-------------

    cd droid-identify-hadoopjob
    mvn install

Execute hadoop job
------------------

    hadoop jar target/droid-identify-hadoopjob-1.0-jar-with-dependencies.jar
      -n job_name -d /path/to/hdfs/input/directory