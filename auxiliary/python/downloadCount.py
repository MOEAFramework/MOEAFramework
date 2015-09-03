# Counts the total number of downloads across Sourceforge and Github
import urllib2
import json
import datetime

count = 0

# get download count from Github
response = urllib2.urlopen("https://api.github.com/repos/MOEAFramework/MOEAFramework/releases")
content = json.load(response)
for release in content:
  for asset in release["assets"]:
    count = count + int(asset["download_count"])

# get download count from Sourceforge
response = urllib2.urlopen("http://sourceforge.net/projects/moeaframework/files/stats/json?start_date=2011-01-01&end_date=" + str(datetime.date.today()))
content = json.load(response)
count = count + int(content["summaries"]["time"]["downloads"])

# display the total download count
print "Total Downloads:", count
