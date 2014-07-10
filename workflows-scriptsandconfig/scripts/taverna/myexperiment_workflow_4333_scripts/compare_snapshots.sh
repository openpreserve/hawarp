# filter html pages with response code "200 ok" and create wayback request url
for url in `cat %%wayback_cdx_index%% | grep "text/html 200" | awk '{print "%%wayback_url%%" $2 "/" $3}'`; 
  do
    # md5 encoded image url as file name
    md5encimagename=`echo -n ${url} | md5sum - | awk '{print $1}'`
    echo "${url}:"
    # if image geometry is different, images are not compared pixelwise
    width_arc=$(exiftool %%output_directory%%/${md5encimagename}_arc.png | grep -i "^Image Width" | sed -e 's#.*: ##');
    width_warc=$(exiftool %%output_directory%%/${md5encimagename}_warc.png | grep -i "^Image Width" | sed -e 's#.*: ##');
    if [ $width_arc -eq $width_warc ]; 
      then 
        # compare arc snapshot image with warc snapshot image using imagemagick
        compare -metric AE %%output_directory%%/${md5encimagename}_arc.png %%output_directory%%/${md5encimagename}_warc.png %%output_directory%%/${md5encimagename}_diff.png 2>&1
      else 
        echo "Snapshot images have different image width"; 
    fi
    
  done
