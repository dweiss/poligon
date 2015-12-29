#!/bin/bash

# This script uses a custom version of bfg cleaner!
# => --replace-text truncates files to 0 bytes, does not remove them from history!


# Size of the git repo at this point ~= 469 mb
du -sh stitching/.git


# 1. Truncate all *.jar files to 0 bytes, throughout all of the history.
java -jar ../tools/bfg-1.12.9-SNAPSHOT-master-44191f3-dirty.jar --no-blob-protection -fi "*.jar" -fs 1G --replace-text ../tools/subst.in stitching
(cd stitching && git reflog expire --expire=now --all && git gc --prune=now --aggressive )

# Size of the git repo at this point ~= 212 mb
du -sh stitching/.git

# 2. Optional. Remove misc. large files.
java -jar ../tools/bfg-1.12.9-SNAPSHOT-master-44191f3-dirty.jar --protect-blobs-from master,branch_5x,branch_4x,branch_3x -fi "*.mem" -fs 1G --replace-text ../tools/subst.in .
java -jar ../tools/bfg-1.12.9-SNAPSHOT-master-44191f3-dirty.jar --protect-blobs-from master,branch_5x,branch_4x,branch_3x -fi "*.dat" -fs 1G --replace-text ../tools/subst.in .
java -jar ../tools/bfg-1.12.9-SNAPSHOT-master-44191f3-dirty.jar --protect-blobs-from master,branch_5x,branch_4x,branch_3x -fi "*.war" -fs 1G --replace-text ../tools/subst.in .
java -jar ../tools/bfg-1.12.9-SNAPSHOT-master-44191f3-dirty.jar --protect-blobs-from master,branch_5x,branch_4x,branch_3x -fi "*.zip" -fs 1G --replace-text ../tools/subst.in .
git reflog expire --expire=now --all && git gc --prune=now --aggressive
