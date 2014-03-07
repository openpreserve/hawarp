#!/bin/bash
# Used in Taverna workflow:
# http://www.myexperiment.org/workflows/4144.html (processor: tomar_prepare_hadoopjob)
# Variables embraced by two %% are Taverna variables and must 
# be replaced either by a Taverna processor or manually in this script.
DATETIMETOKEN=`date +"%Y%m%d%H%M%S"`
HDFS_WORKING_DIR=%%hdfs_working_dir%%/$DATETIMETOKEN
HDFS_TOOLSPEC_DIR=${HDFS_WORKING_DIR}/toolspec
HDFS_DATA_OUT_DIR=${HDFS_WORKING_DIR}/dataout/
HDFS_INPUT_DIR=%%hdfs_input_dir%%/

# tomar control file and toolspec file
CONTROLFILE=tomar-controlfile.txt
TOOLSPEC=tomar-toolspec

# create directories
hadoop fs -mkdir $HDFS_DATA_OUT_DIR
hadoop fs -mkdir $HDFS_TOOLSPEC_DIR

# create toolspec file in hdfs toolspec directory
echo -n '%%toolspec%%' | hadoop fs -put - $HDFS_TOOLSPEC_DIR/${TOOLSPEC}.xml

hadoop fs -ls $HDFS_INPUT_DIR | grep .arc.gz$ | \
    awk '{gsub(".*/","",$8); print "'$TOOLSPEC'" " %%operation%% --input=\"hdfs://" "'$HDFS_INPUT_DIR'" $8 "\" --output=\"hdfs://" "'$HDFS_DATA_OUT_DIR'" $8 ".warc\""}' | \
#    head -10 | \
    hadoop fs -put - $HDFS_WORKING_DIR/${CONTROLFILE}

echo -n $DATETIMETOKEN
