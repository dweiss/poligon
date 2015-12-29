#!/bin/bash

rm -rf .repo
mkdir .repo
cd .repo
git init

echo -e "line1\nline2\nline3" > file-master.txt
git add -A . && git commit -m "c1"

git checkout --orphan branch
git reset --hard

echo -e "line1\nline2\nline3" > file-1.txt
git add -A . && git commit -m "c2"

mkdir subfolder
mv file-1.txt subfolder/
git add -A . && git commit -m "c3"

git checkout master
git merge branch

#
# Only log --follow works here.
#

echo "#log"          && git log subfolder/file-1.txt
echo "#log --follow" && git log --follow subfolder/file-1.txt
echo "#log -C"       && git log -C subfolder/file-1.txt
echo "#log -M"       && git log -M subfolder/file-1.txt
