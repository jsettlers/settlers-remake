#!/usr/bin/python

# Display the real diff - includes moved files.

import subprocess
import re
import sys
from os.path import basename

diff = subprocess.Popen(["git", "diff", "-M", "origin/master..HEAD"], stdout=subprocess.PIPE).communicate()[0]

lines = diff.split("\n")

deleted = dict()
newfiles = dict()

fileA = ""
fileB = ""



for i in range(0, len(lines)):
    line = lines[i]
    if (line.startswith("similarity index 100")):
        print "Simple rename:", fileA, "->", fileB;
    elif (line.startswith("deleted file")):
        deleted[basename(fileA)] = fileA
    elif (line.startswith("new file")):
        newfiles[basename(fileB)] = fileB
    files = re.match( r'diff --git a/(\S*) b/(\S*)', line)
    if files:
        fileA = files.group(1)
        fileB = files.group(2)
#        print fileA, "->", fileB

print "Moved files: "
for name in newfiles.keys():
    if not deleted.has_key(name):
        continue;
    print "Moved:", deleted[name], "->", newfiles[name]
    sys.stdout.flush()
    subprocess.call(["git", "--no-pager", "diff", "-M", "origin/master:" + deleted[name] + "..HEAD:" + newfiles[name]])

