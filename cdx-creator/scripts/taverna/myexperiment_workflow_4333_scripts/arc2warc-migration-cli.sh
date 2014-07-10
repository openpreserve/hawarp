# do not proceed if output directory exists
if [ -d "%%output_directory%%" ]; then
  echo "Error: output directory %%output_directory%% exists"
  exit 1
fi
# create output directory
mkdir -p %%output_directory%%
# get file name without directory path
filename=$(basename "%%arc_input_file%%")
# execute arc to warc migration (https://github.com/openplanets/hawarp/tree/master/arc2warc-migration-cli)
`java -jar %%arc2warc_jar_path%% -i %%arc_input_file%% -o %%output_directory%%/${filename}.warc`
# output arc file path to stdout
echo -n %%arc_input_file%%
