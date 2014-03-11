#!/bin/bash
# Used in Taverna workflow:
# http://www.myexperiment.org/workflows/4144.html (processor: tomar_prepare_hadoopjob)
# Variables embraced by two 'percent' symbols are Taverna variables, they must either be 
# substituted by Taverna input port values or replaced by other inputs in this
# script.

# Input variables
HDFS_WORKING_DIR_INPUT=%%hdfs_working_dir%%
HDFS_INPUT_DIR=%%hdfs_input_dir%%
HDFS_INPUT_DIR2=%%hdfs_input_dir2%%
OPERATION_INPUT=%%operation%%
TOOLSPEC_INPUT='%%toolspec%%'

# Derived variables
DATETIMETOKEN=`date +"%Y%m%d%H%M%S"`
HDFS_WORKING_DIR=${HDFS_WORKING_DIR_INPUT%/}/$DATETIMETOKEN
HDFS_TOOLSPEC_DIR=${HDFS_WORKING_DIR}/toolspec
HDFS_DATA_OUT_DIR=${HDFS_WORKING_DIR}/dataout/

# tomar control file and toolspec file
CONTROLFILE=tomar-controlfile.txt
TOOLSPEC=tomar-toolspec

# create directories
hadoop fs -mkdir $HDFS_DATA_OUT_DIR
hadoop fs -mkdir $HDFS_TOOLSPEC_DIR

# create toolspec file in hdfs toolspec directory
echo -n $TOOLSPEC_INPUT | hadoop fs -put - $HDFS_TOOLSPEC_DIR/${TOOLSPEC}.xml

# list and format control file line output
{ hadoop fs -ls ${HDFS_INPUT_DIR}; hadoop fs -ls ${HDFS_INPUT_DIR2}; } | grep .arc.gz$ | \
    awk '{gsub(".*/","",$8); print "'$TOOLSPEC'" " '$OPERATION_INPUT' " "--input=\"hdfs://" \
        "'${HDFS_INPUT_DIR%/}'/" $8 "\" --output=\"hdfs://" "'$HDFS_DATA_OUT_DIR'" $8 ".warc\""}' |  \
#    head -10 | \ 
    hadoop fs -put - $HDFS_WORKING_DIR/${CONTROLFILE}

echo -n $DATETIMETOKEN
