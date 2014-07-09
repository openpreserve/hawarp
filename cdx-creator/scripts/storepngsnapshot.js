var page = require('webpage').create(),
  system = require('system'),
  address, outpng;
if (system.args.length !== 3) {
  console.log('Usage: phantomjs storepngsnapshot.js <some URL> <png output image path>');
  phantom.exit();
}
address = system.args[1];
outpng = system.args[2];
page.open(address, function(status) {
  if (status !== 'success') {
    console.log('FAIL to load the address');
  } else {
	page.render(outpng);
  }
  phantom.exit();
});

