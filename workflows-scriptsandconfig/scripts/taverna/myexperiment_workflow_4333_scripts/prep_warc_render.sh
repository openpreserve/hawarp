# extract arc file name
filename=$(basename "%%arc_input_file%%")
# append warc file name to the filename-filepath mapping file
echo "${filename}.warc\t%%output_directory%%/${filename}.warc" >> %%wayback_pathindex_file%%

# append cdx file to wayback directory
cat %%warc_cdx_file%% >> %%wayback_cdxindex_file%%
