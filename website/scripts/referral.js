//var products = [], index = 0;
//
//products[0] = '<a href="http://www.amazon.com/gp/product/1118492129/ref=as_li_tl?ie=UTF8&camp=1789&creative=9325&creativeASIN=1118492129&linkCode=as2&tag=mofr-20&linkId=MHLL7UVXUM26BIO4"><img border="0" src="http://ws-na.amazon-adsystem.com/widgets/q?_encoding=UTF8&ASIN=1118492129&Format=_SL250_&ID=AsinImage&MarketPlace=US&ServiceVersion=20070822&WS=1&tag=mofr-20" ></a><img src="http://ir-na.amazon-adsystem.com/e/ir?t=mofr-20&l=as2&o=1&a=1118492129" width="1" height="1" border="0" alt="" style="border:none !important; margin:0px !important;" />';
//products[1] = '<a href="http://www.amazon.com/gp/product/0470743611/ref=as_li_tl?ie=UTF8&camp=1789&creative=9325&creativeASIN=0470743611&linkCode=as2&tag=mofr-20&linkId=QF6Q44D5OWNBSNQJ"><img border="0" src="http://ws-na.amazon-adsystem.com/widgets/q?_encoding=UTF8&ASIN=0470743611&Format=_SL250_&ID=AsinImage&MarketPlace=US&ServiceVersion=20070822&WS=1&tag=mofr-20" ></a><img src="http://ir-na.amazon-adsystem.com/e/ir?t=mofr-20&l=as2&o=1&a=0470743611" width="1" height="1" border="0" alt="" style="border:none !important; margin:0px !important;" />';
//products[2] = '<a href="http://www.amazon.com/gp/product/3639153278/ref=as_li_tl?ie=UTF8&camp=1789&creative=9325&creativeASIN=3639153278&linkCode=as2&tag=mofr-20&linkId=JZVRCVORNWX7T4KV"><img border="0" src="http://ws-na.amazon-adsystem.com/widgets/q?_encoding=UTF8&ASIN=3639153278&Format=_SL250_&ID=AsinImage&MarketPlace=US&ServiceVersion=20070822&WS=1&tag=mofr-20" ></a><img src="http://ir-na.amazon-adsystem.com/e/ir?t=mofr-20&l=as2&o=1&a=3639153278" width="1" height="1" border="0" alt="" style="border:none !important; margin:0px !important;" />';
//products[3] = '<a href="http://www.amazon.com/gp/product/3642072836/ref=as_li_tl?ie=UTF8&camp=1789&creative=9325&creativeASIN=3642072836&linkCode=as2&tag=mofr-20&linkId=CBNPKN6EFSHU76AU"><img border="0" src="http://ws-na.amazon-adsystem.com/widgets/q?_encoding=UTF8&ASIN=3642072836&Format=_SL250_&ID=AsinImage&MarketPlace=US&ServiceVersion=20070822&WS=1&tag=mofr-20" ></a><img src="http://ir-na.amazon-adsystem.com/e/ir?t=mofr-20&l=as2&o=1&a=3642072836" width="1" height="1" border="0" alt="" style="border:none !important; margin:0px !important;" />';
//products[4] = '<a href="http://www.amazon.com/gp/product/3662463083/ref=as_li_tl?ie=UTF8&camp=1789&creative=9325&creativeASIN=3662463083&linkCode=as2&tag=mofr-20&linkId=TFH5FWP5KPQ5LW42"><img border="0" src="http://ws-na.amazon-adsystem.com/widgets/q?_encoding=UTF8&ASIN=3662463083&Format=_SL250_&ID=AsinImage&MarketPlace=US&ServiceVersion=20070822&WS=1&tag=mofr-20" ></a><img src="http://ir-na.amazon-adsystem.com/e/ir?t=mofr-20&l=as2&o=1&a=3662463083" width="1" height="1" border="0" alt="" style="border:none !important; margin:0px !important;" />';
//products[5] = '<a href="http://www.amazon.com/gp/product/3639163524/ref=as_li_tl?ie=UTF8&camp=1789&creative=9325&creativeASIN=3639163524&linkCode=as2&tag=mofr-20&linkId=HDGCV2M32YNBMPQM"><img border="0" src="http://ws-na.amazon-adsystem.com/widgets/q?_encoding=UTF8&ASIN=3639163524&Format=_SL250_&ID=AsinImage&MarketPlace=US&ServiceVersion=20070822&WS=1&tag=mofr-20" ></a><img src="http://ir-na.amazon-adsystem.com/e/ir?t=mofr-20&l=as2&o=1&a=3639163524" width="1" height="1" border="0" alt="" style="border:none !important; margin:0px !important;" />';
//products[6] = '<a href="http://www.amazon.com/gp/product/3642101135/ref=as_li_tl?ie=UTF8&camp=1789&creative=9325&creativeASIN=3642101135&linkCode=as2&tag=mofr-20&linkId=22P32LIU3UKLLWO2"><img border="0" src="http://ws-na.amazon-adsystem.com/widgets/q?_encoding=UTF8&ASIN=3642101135&Format=_SL250_&ID=AsinImage&MarketPlace=US&ServiceVersion=20070822&WS=1&tag=mofr-20" ></a><img src="http://ir-na.amazon-adsystem.com/e/ir?t=mofr-20&l=as2&o=1&a=3642101135" width="1" height="1" border="0" alt="" style="border:none !important; margin:0px !important;" />';
//products[7] = '<a href="http://www.amazon.com/gp/product/3838302206/ref=as_li_tl?ie=UTF8&camp=1789&creative=9325&creativeASIN=3838302206&linkCode=as2&tag=mofr-20&linkId=GH4XO2FQVB6I263V"><img border="0" src="http://ws-na.amazon-adsystem.com/widgets/q?_encoding=UTF8&ASIN=3838302206&Format=_SL250_&ID=AsinImage&MarketPlace=US&ServiceVersion=20070822&WS=1&tag=mofr-20" ></a><img src="http://ir-na.amazon-adsystem.com/e/ir?t=mofr-20&l=as2&o=1&a=3838302206" width="1" height="1" border="0" alt="" style="border:none !important; margin:0px !important;" />';
//products[8] = '<a href="http://www.amazon.com/gp/product/0387332545/ref=as_li_tl?ie=UTF8&camp=1789&creative=9325&creativeASIN=0387332545&linkCode=as2&tag=mofr-20&linkId=EN6CI3HSVOD4OV52"><img border="0" src="http://ws-na.amazon-adsystem.com/widgets/q?_encoding=UTF8&ASIN=0387332545&Format=_SL250_&ID=AsinImage&MarketPlace=US&ServiceVersion=20070822&WS=1&tag=mofr-20" ></a><img src="http://ir-na.amazon-adsystem.com/e/ir?t=mofr-20&l=as2&o=1&a=0387332545" width="1" height="1" border="0" alt="" style="border:none !important; margin:0px !important;" />';
//products[9] = '<a href="http://www.amazon.com/gp/product/0470164026/ref=as_li_tl?ie=UTF8&camp=1789&creative=9325&creativeASIN=0470164026&linkCode=as2&tag=mofr-20&linkId=FGAPUSAGKZV7KIHG"><img border="0" src="http://ws-na.amazon-adsystem.com/widgets/q?_encoding=UTF8&ASIN=0470164026&Format=_SL250_&ID=AsinImage&MarketPlace=US&ServiceVersion=20070822&WS=1&tag=mofr-20" ></a><img src="http://ir-na.amazon-adsystem.com/e/ir?t=mofr-20&l=as2&o=1&a=0470164026" width="1" height="1" border="0" alt="" style="border:none !important; margin:0px !important;" />';
//products[10] = '<a href="http://www.amazon.com/gp/product/1441945709/ref=as_li_tl?ie=UTF8&camp=1789&creative=9325&creativeASIN=1441945709&linkCode=as2&tag=mofr-20&linkId=6TSOAN6KXK4SWU3Z"><img border="0" src="http://ws-na.amazon-adsystem.com/widgets/q?_encoding=UTF8&ASIN=1441945709&Format=_SL250_&ID=AsinImage&MarketPlace=US&ServiceVersion=20070822&WS=1&tag=mofr-20" ></a><img src="http://ir-na.amazon-adsystem.com/e/ir?t=mofr-20&l=as2&o=1&a=1441945709" width="1" height="1" border="0" alt="" style="border:none !important; margin:0px !important;" />';
//products[11] = '<a href="http://www.amazon.com/gp/product/1848215177/ref=as_li_tl?ie=UTF8&camp=1789&creative=9325&creativeASIN=1848215177&linkCode=as2&tag=mofr-20&linkId=SFFJJIILQLQKES6F"><img border="0" src="http://ws-na.amazon-adsystem.com/widgets/q?_encoding=UTF8&ASIN=1848215177&Format=_SL250_&ID=AsinImage&MarketPlace=US&ServiceVersion=20070822&WS=1&tag=mofr-20" ></a><img src="http://ir-na.amazon-adsystem.com/e/ir?t=mofr-20&l=as2&o=1&a=1848215177" width="1" height="1" border="0" alt="" style="border:none !important; margin:0px !important;" />';
//products[12] = '<a href="http://www.amazon.com/gp/product/1597492728/ref=as_li_tl?ie=UTF8&camp=1789&creative=9325&creativeASIN=1597492728&linkCode=as2&tag=mofr-20&linkId=A4FRTIOCDQN6C47X"><img border="0" src="http://ws-na.amazon-adsystem.com/widgets/q?_encoding=UTF8&ASIN=1597492728&Format=_SL250_&ID=AsinImage&MarketPlace=US&ServiceVersion=20070822&WS=1&tag=mofr-20" ></a><img src="http://ir-na.amazon-adsystem.com/e/ir?t=mofr-20&l=as2&o=1&a=1597492728" width="1" height="1" border="0" alt="" style="border:none !important; margin:0px !important;" />';
//products[13] = '<a href="http://www.amazon.com/gp/product/3642173896/ref=as_li_tl?ie=UTF8&camp=1789&creative=9325&creativeASIN=3642173896&linkCode=as2&tag=mofr-20&linkId=6UWT7ED6QDIJOSX2"><img border="0" src="http://ws-na.amazon-adsystem.com/widgets/q?_encoding=UTF8&ASIN=3642173896&Format=_SL250_&ID=AsinImage&MarketPlace=US&ServiceVersion=20070822&WS=1&tag=mofr-20" ></a><img src="http://ir-na.amazon-adsystem.com/e/ir?t=mofr-20&l=as2&o=1&a=3642173896" width="1" height="1" border="0" alt="" style="border:none !important; margin:0px !important;" />';
//products[14] = '<a href="http://www.amazon.com/gp/product/8132221834/ref=as_li_tl?ie=UTF8&camp=1789&creative=9325&creativeASIN=8132221834&linkCode=as2&tag=mofr-20&linkId=P26TNDTTZGI6FQFV"><img border="0" src="http://ws-na.amazon-adsystem.com/widgets/q?_encoding=UTF8&ASIN=8132221834&Format=_SL250_&ID=AsinImage&MarketPlace=US&ServiceVersion=20070822&WS=1&tag=mofr-20" ></a><img src="http://ir-na.amazon-adsystem.com/e/ir?t=mofr-20&l=as2&o=1&a=8132221834" width="1" height="1" border="0" alt="" style="border:none !important; margin:0px !important;" />';
//products[15] = '<a href="http://www.amazon.com/gp/product/0521878462/ref=as_li_tl?ie=UTF8&camp=1789&creative=9325&creativeASIN=0521878462&linkCode=as2&tag=mofr-20&linkId=XYLQE4SWVEIPQXPL"><img border="0" src="http://ws-na.amazon-adsystem.com/widgets/q?_encoding=UTF8&ASIN=0521878462&Format=_SL250_&ID=AsinImage&MarketPlace=US&ServiceVersion=20070822&WS=1&tag=mofr-20" ></a><img src="http://ir-na.amazon-adsystem.com/e/ir?t=mofr-20&l=as2&o=1&a=0521878462" width="1" height="1" border="0" alt="" style="border:none !important; margin:0px !important;" />';
//products[16] = '<a href="http://www.amazon.com/gp/product/3540856455/ref=as_li_tl?ie=UTF8&camp=1789&creative=9325&creativeASIN=3540856455&linkCode=as2&tag=mofr-20&linkId=XJWCB3ABCOJKKNHO"><img border="0" src="http://ws-na.amazon-adsystem.com/widgets/q?_encoding=UTF8&ASIN=3540856455&Format=_SL250_&ID=AsinImage&MarketPlace=US&ServiceVersion=20070822&WS=1&tag=mofr-20" ></a><img src="http://ir-na.amazon-adsystem.com/e/ir?t=mofr-20&l=as2&o=1&a=3540856455" width="1" height="1" border="0" alt="" style="border:none !important; margin:0px !important;" />';
//products[17] = '<a href="http://www.amazon.com/gp/product/364211217X/ref=as_li_tl?ie=UTF8&camp=1789&creative=9325&creativeASIN=364211217X&linkCode=as2&tag=mofr-20&linkId=Q2PWVMHEJXPZLI44"><img border="0" src="http://ws-na.amazon-adsystem.com/widgets/q?_encoding=UTF8&ASIN=364211217X&Format=_SL250_&ID=AsinImage&MarketPlace=US&ServiceVersion=20070822&WS=1&tag=mofr-20" ></a><img src="http://ir-na.amazon-adsystem.com/e/ir?t=mofr-20&l=as2&o=1&a=364211217X" width="1" height="1" border="0" alt="" style="border:none !important; margin:0px !important;" />';
//products[18] = '<a href="http://www.amazon.com/gp/product/3642067964/ref=as_li_tl?ie=UTF8&camp=1789&creative=9325&creativeASIN=3642067964&linkCode=as2&tag=mofr-20&linkId=MILDMEHVISQYFSX6"><img border="0" src="http://ws-na.amazon-adsystem.com/widgets/q?_encoding=UTF8&ASIN=3642067964&Format=_SL250_&ID=AsinImage&MarketPlace=US&ServiceVersion=20070822&WS=1&tag=mofr-20" ></a><img src="http://ir-na.amazon-adsystem.com/e/ir?t=mofr-20&l=as2&o=1&a=3642067964" width="1" height="1" border="0" alt="" style="border:none !important; margin:0px !important;" />';
//
//var tz = jstz.determine();
//
//if (tz.name().indexOf("Honolulu") >= 0 ||
//		tz.name().indexOf("Anchorage") >= 0 ||
//		tz.name().indexOf("Los_Angeles") >= 0 ||
//		tz.name().indexOf("Phoenix") >= 0 ||
//		tz.name().indexOf("Denver") >= 0 ||
//		tz.name().indexOf("Chicago") >= 0 ||
//		tz.name().indexOf("New_York") >= 0 ||
//		tz.name().indexOf("St_Johns") >= 0) {
//  index = Math.floor(Math.random() * products.length);
//  document.write('<h3>Recommended Book</h3>');
//  document.write(products[index]);
//} else {
//  var url = window.location.pathname;
//  var filename = url.substring(url.lastIndexOf('/')+1);
//
//  if (filename == "index.xml" ||
//		  filename == "documentation.xml" ||
//		  filename == "support.xml" ||
//		  filename == "contribute.xml" ||
//		  filename == "credits.xml" ||
//		  filename == "donate.xml") {
//	    document.write(
//	    		  '										<script type="text/javascript">\n' +
//				  '											google_ad_client = "ca-pub-5610668616453880";\n' +
//				  '											google_ad_slot = "9118867796";\n' +
//				  '											google_ad_width = 200;\n' +
//				  '											google_ad_height = 200;\n' +
//				  '										</script>\n' +
//				  '										<script type="text/javascript"\n' +
//				  '												src="http://pagead2.googlesyndication.com/pagead/show_ads.js">\n' +
//				  '										</script>');
//  } else {
//	    document.write(
//	    		  '										<script type="text/javascript">\n' +
//	    		  '											google_ad_client = "ca-pub-5610668616453880";\n' +
//	    		  '											google_ad_slot = "7891799492";\n' +
//	    		  '											google_ad_width = 160;\n' +
//	    		  '											google_ad_height = 600;\n' +
//	    		  '										</script>\n' +
//	    		  '										<script type="text/javascript"\n' +
//	    		  '												src="http://pagead2.googlesyndication.com/pagead/show_ads.js">\n' +
//	    		  '										</script>');
//  }
//}
