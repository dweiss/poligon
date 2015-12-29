#!/bin/bash

rm -rf .repo
mkdir .repo
cd .repo
git init

echo -e "line1\nline2\nline3" > file-1.txt
git add -A . && git commit -m "c1"

rm file-1.txt
git add -A . && git commit -m "c2"

mkdir subfolder
echo -e "line1\nline2\nline3" > subfolder/file-1.txt
git add -A . && git commit -m "c3"

git log subfolder/file-1.txt
git log --follow subfolder/file-1.txt
git log -C subfolder/file-1.txt
git log -M subfolder/file-1.txt
