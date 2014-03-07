#!/bin/bash
# Used in Taverna workflow:
# http://www.myexperiment.org/workflows/4144.html (processor: tomar_prepare_hadoopjob)
# Variables embraced by two %% are Taverna variables and must 
# be replaced either by a Taverna processor or manually in this script.
HDFS_WORKING_DIR=%%hdfs_working_dir%%/%%datetime_token%%

TOMAR_CONTROL_FILE=$HDFS_WORKING_DIR/tomar-controlfile.txt
TOMAR_TOOLSPEC_DIR=$HDFS_WORKING_DIR/toolspec/

JOBOUTPUT=$HDFS_WORKING_DIR/joboutput

# n = #files/(#workers * factor_between_1_and_10)
# 40 = 5000/(25 * 5)
/usr/bin/hadoop jar \
    /home/onbfue/pt-mapred-0.0.1-SNAPSHOT-jar-with-dependencies.jar \
    -i $TOMAR_CONTROL_FILE \
    -r $TOMAR_TOOLSPEC_DIR \
    -o $JOBOUTPUT \
    -n 40

echo -n $HDFS_WORKING_DIR/dataout
