#!/bin/bash

rm -rf repo
mkdir repo
cd repo

git init
echo -e "a\nb\nc" > file.txt && git add -A . && git commit -m "c1"
git co --orphan branch
git reset --hard
echo -e "a" > file.txt && git add -A . && git commit -m "c2-b"
git co master
echo -e "b" > file.txt && git add -A . && git commit -m "c3"


