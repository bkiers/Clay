#!/usr/bin/env python

import re
import os
import os.path
import fnmatch
import errno

cc_source_dir = '/tmp/cc';
cc_target_dir = 'site/cc';
css = open(cc_source_dir + '/.css/coverage.css').read() 

# Creates a directory at the given path, ignoring if the directory 
# already exists.
def mkdir_p(path):
  try:
    os.makedirs(path)
  except OSError as exc:
    if exc.errno == errno.EEXIST and os.path.isdir(path):
      pass
    else: raise  

# Iterate over all HTML files and copy them to the new location.
for root, dirnames, filenames in os.walk(cc_source_dir):

  for filename in fnmatch.filter(filenames, '*.html'):
    
    # Get the path to the original file.
    original_file = os.path.join(root, filename)
    
    # Create the path to the new location.
    copy_file = original_file.replace(cc_source_dir, cc_target_dir)
    copy_dir = os.path.dirname(os.path.realpath(copy_file))
    mkdir_p(copy_dir)
    
    # Read the HTML and replace the import-statement with the CSS.
    html = open(os.path.join(root, filename)).read()
    html = re.sub(r'[ \t]*@import.*?coverage.css";', css, html)
    
    # Write the modified HTML to the new file.
    out = open(copy_file, 'w')
    out.write(html)
    
   
    
