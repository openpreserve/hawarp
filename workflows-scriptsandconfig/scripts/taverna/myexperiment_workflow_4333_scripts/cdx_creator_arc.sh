# create temporary directory
mkdir -p %%output_directory%%
# extract file name
filename=$(basename "%%inputfile%%")
# create cdx index (https://github.com/openplanets/hawarp/tree/master/cdx-creator) 
`java -jar %%cdx_creator_jar%% -i %%inputfile%% -o %%output_directory%%/${filename}.cdx -c %%configfilepath%% -d`
# output cdx index file path to stdout
echo -n %%output_directory%%/${filename}.cdx
