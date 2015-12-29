
#
# Stiching histories
#

mkdir stitching
cd stitching

git init .
git co --orphan empty
git reset --hard
echo > empty.txt
git add -A .
git commit -m "Empty orphan."

git remote add --tags lucene-solr    ../04-lucene-solr
git fetch --tags lucene-solr

# Locale the merge-and-move-and-modify commit past which git doesn't show any history. 
MERGER=`git log --all --grep="solr2452@1144174"`
MERGED=`echo $MERGER | cut -f 2 -d ' '`
LEFT=`echo $MERGER | cut -f 4 -d ' '`
RIGHT=`echo $MERGER | cut -f 5 -d ' '`


# Apply renames/ moves without introducing any changes.
git co $LEFT -b left
CALL 2452-reshuffle.sh
git add -A .
git commit -am "Applied folder moves and renames before merge in SVN r1144174."

# Apply renames/ moves without introducing any changes.
git co $RIGHT -b right
rm -rf solr/core/src/test
CALL 2452-reshuffle.sh
git add -A .
git commit -am "Applied folder moves and renames before merge in SVN r1144174."

# Apply a graft to the problematic merge commit
git replace --graft $MERGED left right

git co empty
git br -D left right

git remote add --tags lucene         ../01-lucene
git fetch --tags lucene

git remote add --tags solr-incubator ../02-solr-incubator
git fetch --tags solr-incubator

git remote add --tags solr           ../03-solr
git fetch --tags solr


# Connect solr with solr-incubator
git diff            grafts/solr-oldest grafts/solr-incubator-latest
git replace --graft grafts/solr-oldest grafts/solr-incubator-latest

# Merge histories of Solr and Lucene into lucene-solr. We do an actual merge of two
# separate branches (solr and lucene), then graft all the subsequent lucene-solr 
# history on top of this merged branch.
git co grafts/solr-latest  -b solr-interim
mkdir solr
shopt -s extglob
mv !(solr)    solr/
git add -A .
git commit -m "SVN-GIT conversion, path copy emulation."

git co grafts/lucene-solr-copy -b lucene-interim
mkdir lucene
mv !(lucene) lucene/
mv .cvsignore lucene/
git add -A .
git commit -m "SVN-GIT conversion, path copy emulation."

git merge solr-interim -m "Merging after path copy emulation."

git diff            grafts/lucene-solr-oldest-merged lucene-interim 
git replace --graft grafts/lucene-solr-oldest-merged lucene-interim

git co empty
git br -D lucene-interim solr-interim

# Remove a dangling root commit that makes no sense and only creates confusion.
HASH=`git log --all --grep="lucene4258@1425437" --format="%h"`
PARENT=`git log --all --grep="trunk@1425342" --format="%h"`
git replace --graft $HASH $PARENT

git co lucene-solr/trunk
# Verify sanity by tracking IndexWriter's history all the way back to 2001.
git log --follow lucene/core/src/java/org/apache/lucene/index/IndexWriter.java       | tail -n 10
# Verify solr's core sanity by tracking SolrCore history all the way back to 2006.
git log --follow solr/core/src/java/org/apache/solr/core/SolrCore.java               | tail -n 10

# Create modern branches.
git co branch_3x
git co branch_4x
git co branch_5x
git co lucene-solr/trunk -b master

# Remove remotes
git remote remove lucene
git remote remove lucene-solr
git remote remove solr
git remote remove solr-incubator

# apply grafts to become permanent [takes a long time!]
git filter-branch --tag-name-filter cat  -- --all

# Remove grafts
for ref in `git replace`; do
  git replace -d $ref
done

# We don't need these anymore.
git tag -d grafts/lucene-solr-oldest

# Delete any backup (/original/) refs.
git show-ref
for i in `git show-ref | grep "/original" | cut -f 2 -d ' '`; do git update-ref -d $i; done

# git gc, prune, etc.
git reflog expire --expire=now --all && git gc --prune=now --aggressive

# Delete the empty branch
git co master
git br -D empty

# People on windows (should/ have to?): 
git config core.filemode false

# Sanity checks:
#   git co master
#   git log -1
#   git log --follow lucene/core/src/java/org/apache/lucene/index/IndexWriter.java
#   git log --follow solr/core/src/java/org/apache/solr/core/SolrCore.java       
#   git log --follow solr/solrj/src/java/org/apache/solr/client/solrj/SolrClient.java

