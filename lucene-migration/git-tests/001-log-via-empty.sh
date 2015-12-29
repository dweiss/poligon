#!/bin/bash

rm -rf .repo
mkdir .repo
cd .repo
git init

echo -e "line1\nline2\nline3" > file-1.txt
git add -A . && git commit -m "c1"

rm file-1.txt
git add -A . && git commit -m "c2"

echo -e "line1\nline2-modified\nline3" > file-1.txt
git add -A . && git commit -m "c3"

git log file-1.txt
