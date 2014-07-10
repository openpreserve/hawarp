# PhantomJS storepngsnapshot.js used below:
# ----------------------------------------
# var page = require('webpage').create(),
#   system = require('system'),
#   address, outpng;
# if (system.args.length !== 3) {
#   console.log('Usage: phantomjs storepngsnapshot.js <some URL> <png output image path>');
#   phantom.exit();
# }
# address = system.args[1];
# outpng = system.args[2];
# page.open(address, function(status) {
#   if (status !== 'success') {
#     console.log('FAIL to load the address');
#   } else {
# 	page.render(outpng);
#   }
#   phantom.exit();
# });
# ----------------------------------------

# filter html pages with response code ok and create wayback request url
for url in `cat %%wayback_cdx_index%% | grep "text/html 200" | awk '{print "%%wayback_url%%" $2 "/" $3}'`; 
  do
    md5encimagename=`echo -n ${url} | md5sum - | awk '{print $1}'`
    phantomjs %%phantomjs_snapshot_js%% $url %%output_directory%%/${md5encimagename}_warc.png;
  done
