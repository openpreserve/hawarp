# extract arc file name
filename=$(basename "%%arc_input_file%%")
# append warc file name to the filename-filepath mapping file
echo "${filename}\t%%arc_input_file%%" >> %%wayback_pathindex_file%%

# append cdx file to wayback directory
cat %%arc_cdx_file%% >> %%wayback_cdx_index%%
