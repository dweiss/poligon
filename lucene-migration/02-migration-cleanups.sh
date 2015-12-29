
# Merge separate git repositories into one.
#
# release tag pattern: releases/{lucene,solr}/x.y.z
# branch name pattern: history/branches/{lucene,solr}/

mkdir 05-cleanups
cd 05-cleanups
pwd

# Clean up Lucene's pre-merge repo:
cp -R ../01-lucene ./
cd 01-lucene
git tag releases/lucene/1.0.1            origin/tags/LUCENE_1_0_1
git tag releases/lucene/1.2-rc1          origin/tags/lucene_1_2_rc1
git tag releases/lucene/1.2-rc2          origin/tags/lucene_1_2_rc2
git tag releases/lucene/1.2-rc3          origin/tags/lucene_1_2_rc3
git tag releases/lucene/1.2-rc4          origin/tags/lucene_1_2_rc4
git tag releases/lucene/1.2-rc5          origin/tags/lucene_1_2_rc5
git tag releases/lucene/1.3-rc1          origin/tags/lucene_1_3_rc1
git tag releases/lucene/1.3-rc2          origin/tags/lucene_1_3_rc2
git tag releases/lucene/1.3-rc3          origin/tags/lucene_1_3_rc3
git tag releases/lucene/1.2              origin/tags/lucene_1_2_final
git tag releases/lucene/1.3              origin/tags/lucene_1_3_final
git tag releases/lucene/1.4-rc1          origin/tags/lucene_1_4_rc1
git tag releases/lucene/1.4-rc2          origin/tags/lucene_1_4_rc2
git tag releases/lucene/1.4-rc3          origin/tags/lucene_1_4_rc3
git tag releases/lucene/1.4              origin/tags/lucene_1_4_final
git tag releases/lucene/1.4.1            origin/tags/lucene_1_4_1
git tag releases/lucene/1.4.2            origin/tags/lucene_1_4_2
git tag releases/lucene/1.4.3            origin/tags/lucene_1_4_3
git tag releases/lucene/1.9-rc1          origin/tags/lucene_1_9_rc1
git tag releases/lucene/1.9              origin/tags/lucene_1_9_final
git tag releases/lucene/1.9.1            origin/tags/lucene_1_9_1
git tag releases/lucene/2.0.0            origin/tags/lucene_2_0_0
git tag releases/lucene/2.1.0            origin/tags/lucene_2_1_0
git tag releases/lucene/2.2.0            origin/tags/lucene_2_2_0
git tag releases/lucene/2.3.0            origin/tags/lucene_2_3_0
git tag releases/lucene/2.3.1            origin/tags/lucene_2_3_1
git tag releases/lucene/2.3.2            origin/tags/lucene_2_3_2
git tag releases/lucene/2.4.0            origin/tags/lucene_2_4_0
git tag releases/lucene/2.4.1            origin/tags/lucene_2_4_1
git tag releases/lucene/2.9.0            origin/tags/lucene_2_9_0
git tag releases/lucene/2.9.1            origin/tags/lucene_2_9_1
git tag releases/lucene/2.9.2            origin/tags/lucene_2_9_2
git tag releases/lucene/3.0.0            origin/tags/lucene_3_0_0
git tag releases/lucene/3.0.1            origin/tags/lucene_3_0_1

git tag grafts/lucene-latest             remotes/origin/trunk

# Locate solr copy point
HASH=`git log --all --grep="trunk@924321" --format="%h"`
git tag grafts/lucene-solr-copy $HASH


# Locate oldest SVN commit's hash
HASH=`git log --all --grep="@149564" --format="%h"`
git tag grafts/lucene-oldest             $HASH

cd ..

# Clean up Solr's incubator repo:
cp -R ../02-solr-incubator ./
cd 02-solr-incubator
git tag releases/solr/1.1.0         origin/tags/release-1.1.0
git tag grafts/solr-incubator-latest  remotes/origin/trunk
HASH=`git log --all --grep="@369921" --format="%h"`
git tag grafts/solr-incubator-oldest  $HASH

cd ..

# Clean up Solr's post-incubator, pre-merge with Lucene
cp -R ../03-solr ./
cd 03-solr
git tag history/branches/solr/cloud      remotes/origin/cloud
git tag history/branches/solr/branch-1.3 remotes/origin/branch-1.3
git tag releases/solr/1.4.0              origin/tags/release-1.4.0
git tag releases/solr/1.3.0              origin/tags/release-1.3.0
git tag releases/solr/1.2.0              origin/tags/release-1.2.0
git tag grafts/solr-latest           remotes/origin/solr
HASH=`git log --all --grep="trunk@497231" --format="%h"`
git tag grafts/solr-oldest           $HASH

cd ..

# Clean up Lucene-Solr merged repo:

cp -R ../04-lucene-solr ./
cd 04-lucene-solr

git tag grafts/lucene-solr-latest        remotes/trunk
HASH=`git log --all --grep="newtrunk@924461" --format="%h"`
git tag grafts/lucene-solr-oldest        $HASH
HASH=`git log --all --grep="newtrunk@924490" --format="%h"`
git tag grafts/lucene-solr-oldest-merged $HASH

git tag releases/lucene-solr/3.1           origin/tags/lucene_solr_3_1
git tag releases/lucene-solr/3.2           origin/tags/lucene_solr_3_2
git tag releases/lucene-solr/3.3           origin/tags/lucene_solr_3_3
git tag releases/lucene-solr/3.4.0         origin/tags/lucene_solr_3_4_0
git tag releases/lucene-solr/3.5.0         origin/tags/lucene_solr_3_5_0
git tag releases/lucene-solr/3.6.0         origin/tags/lucene_solr_3_6_0
git tag releases/lucene-solr/3.6.1         origin/tags/lucene_solr_3_6_1
git tag releases/lucene-solr/3.6.2         origin/tags/lucene_solr_3_6_2
git tag releases/lucene-solr/4.0.0         origin/tags/lucene_solr_4_0_0
git tag releases/lucene-solr/4.0.0-alpha   origin/tags/lucene_solr_4_0_0_ALPHA
git tag releases/lucene-solr/4.0.0-beta    origin/tags/lucene_solr_4_0_0_BETA
git tag releases/lucene-solr/4.10.0        origin/tags/lucene_solr_4_10_0
git tag releases/lucene-solr/4.10.1        origin/tags/lucene_solr_4_10_1
git tag releases/lucene-solr/4.10.2        origin/tags/lucene_solr_4_10_2
git tag releases/lucene-solr/4.10.3        origin/tags/lucene_solr_4_10_3
git tag releases/lucene-solr/4.10.4        origin/tags/lucene_solr_4_10_4
git tag releases/lucene-solr/4.1.0         origin/tags/lucene_solr_4_1_0
git tag releases/lucene-solr/4.2.0         origin/tags/lucene_solr_4_2_0
git tag releases/lucene-solr/4.2.1         origin/tags/lucene_solr_4_2_1
git tag releases/lucene-solr/4.3.0         origin/tags/lucene_solr_4_3_0
git tag releases/lucene-solr/4.3.1         origin/tags/lucene_solr_4_3_1
git tag releases/lucene-solr/4.4.0         origin/tags/lucene_solr_4_4_0
git tag releases/lucene-solr/4.5.0         origin/tags/lucene_solr_4_5_0
git tag releases/lucene-solr/4.5.1         origin/tags/lucene_solr_4_5_1
git tag releases/lucene-solr/4.6.0         origin/tags/lucene_solr_4_6_0
git tag releases/lucene-solr/4.6.1         origin/tags/lucene_solr_4_6_1
git tag releases/lucene-solr/4.7.0         origin/tags/lucene_solr_4_7_0
git tag releases/lucene-solr/4.7.1         origin/tags/lucene_solr_4_7_1
git tag releases/lucene-solr/4.7.2         origin/tags/lucene_solr_4_7_2
git tag releases/lucene-solr/4.8.0         origin/tags/lucene_solr_4_8_0
git tag releases/lucene-solr/4.8.1         origin/tags/lucene_solr_4_8_1
git tag releases/lucene-solr/4.9.0         origin/tags/lucene_solr_4_9_0
git tag releases/lucene-solr/4.9.1         origin/tags/lucene_solr_4_9_1
git tag releases/lucene-solr/5.0.0         origin/tags/lucene_solr_5_0_0
git tag releases/lucene-solr/5.1.0         origin/tags/lucene_solr_5_1_0
git tag releases/lucene-solr/5.2.0         origin/tags/lucene_solr_5_2_0
git tag releases/lucene-solr/5.2.1         origin/tags/lucene_solr_5_2_1
git tag releases/lucene-solr/5.3.0         origin/tags/lucene_solr_5_3_0
git tag releases/lucene-solr/5.3.1         origin/tags/lucene_solr_5_3_1
git tag releases/lucene-solr/5.4.0         origin/tags/lucene_solr_5_4_0 

# Create branches for branch_3x, branch_4x, branch_5x
git co origin/branch_3x -b branch_3x
git co origin/branch_4x -b branch_4x
git co origin/branch_5x -b branch_5x
git co origin/trunk     -b trunk

# Tagging as historical

git tag history/branches/lucene-solr/LUCENE-2878                     origin/LUCENE-2878
git tag history/branches/lucene-solr/LUCENE-5622                     origin/LUCENE-5622
git tag history/branches/lucene-solr/LUCENE-5716                     origin/LUCENE-5716
git tag history/branches/lucene-solr/LUCENE-6481                     origin/LUCENE-6481
git tag history/branches/lucene-solr/LUCENE2793                      origin/LUCENE2793
git tag history/branches/lucene-solr/blocktree_3030                  origin/blocktree_3030
git tag history/branches/lucene-solr/bulkpostings                    origin/bulkpostings
git tag history/branches/lucene-solr/cleanup2878                     origin/cleanup2878
git tag history/branches/lucene-solr/docvalues                       origin/docvalues
git tag history/branches/lucene-solr/fieldtype                       origin/fieldtype
git tag history/branches/lucene-solr/fieldtype_conflicted            origin/fieldtype_conflicted
git tag history/branches/lucene-solr/flexscoring                     origin/flexscoring
git tag history/branches/lucene-solr/fqmodule_2883                   origin/fqmodule_2883
git tag history/branches/lucene-solr/ghost_of_4456                   origin/ghost_of_4456
git tag history/branches/lucene-solr/leaky3147                       origin/leaky3147
git tag history/branches/lucene-solr/lucene2510                      origin/lucene2510
git tag history/branches/lucene-solr/lucene2621                      origin/lucene2621
git tag history/branches/lucene-solr/lucene2858                      origin/lucene2858
git tag history/branches/lucene-solr/lucene2878                      origin/lucene2878
git tag history/branches/lucene-solr/lucene3069                      origin/lucene3069
git tag history/branches/lucene-solr/lucene3305                      origin/lucene3305
git tag history/branches/lucene-solr/lucene3312                      origin/lucene3312
git tag history/branches/lucene-solr/lucene3453                      origin/lucene3453
git tag history/branches/lucene-solr/lucene3606                      origin/lucene3606
git tag history/branches/lucene-solr/lucene3622                      origin/lucene3622
git tag history/branches/lucene-solr/lucene3661                      origin/lucene3661
git tag history/branches/lucene-solr/lucene3733                      origin/lucene3733
git tag history/branches/lucene-solr/lucene3767                      origin/lucene3767
git tag history/branches/lucene-solr/lucene3795_lsp_spatial_module   origin/lucene3795_lsp_spatial_module
git tag history/branches/lucene-solr/lucene3837                      origin/lucene3837
git tag history/branches/lucene-solr/lucene3842                      origin/lucene3842
git tag history/branches/lucene-solr/lucene3846                      origin/lucene3846
git tag history/branches/lucene-solr/lucene3930                      origin/lucene3930
git tag history/branches/lucene-solr/lucene3969                      origin/lucene3969
git tag history/branches/lucene-solr/lucene4055                      origin/lucene4055
git tag history/branches/lucene-solr/lucene4100                      origin/lucene4100
git tag history/branches/lucene-solr/lucene4199                      origin/lucene4199
git tag history/branches/lucene-solr/lucene4236                      origin/lucene4236
git tag history/branches/lucene-solr/lucene4258                      origin/lucene4258
git tag history/branches/lucene-solr/lucene4335                      origin/lucene4335
git tag history/branches/lucene-solr/lucene4446                      origin/lucene4446
git tag history/branches/lucene-solr/lucene4456                      origin/lucene4456
git tag history/branches/lucene-solr/lucene4547                      origin/lucene4547
git tag history/branches/lucene-solr/lucene4765                      origin/lucene4765
git tag history/branches/lucene-solr/lucene4956                      origin/lucene4956
git tag history/branches/lucene-solr/lucene5012                      origin/lucene5012
git tag history/branches/lucene-solr/lucene5127                      origin/lucene5127
git tag history/branches/lucene-solr/lucene5178                      origin/lucene5178
git tag history/branches/lucene-solr/lucene5205                      origin/lucene5205
git tag history/branches/lucene-solr/lucene5207                      origin/lucene5207
git tag history/branches/lucene-solr/lucene5339                      origin/lucene5339
git tag history/branches/lucene-solr/lucene5376                      origin/lucene5376
git tag history/branches/lucene-solr/lucene5376_2                    origin/lucene5376_2
git tag history/branches/lucene-solr/lucene539399                    origin/lucene539399
git tag history/branches/lucene-solr/lucene5438                      origin/lucene5438
git tag history/branches/lucene-solr/lucene5468                      origin/lucene5468
git tag history/branches/lucene-solr/lucene5487                      origin/lucene5487
git tag history/branches/lucene-solr/lucene5493                      origin/lucene5493
git tag history/branches/lucene-solr/lucene5611                      origin/lucene5611
git tag history/branches/lucene-solr/lucene5650                      origin/lucene5650
git tag history/branches/lucene-solr/lucene5666                      origin/lucene5666
git tag history/branches/lucene-solr/lucene5675                      origin/lucene5675
git tag history/branches/lucene-solr/lucene5752                      origin/lucene5752
git tag history/branches/lucene-solr/lucene5786                      origin/lucene5786
git tag history/branches/lucene-solr/lucene5858                      origin/lucene5858
git tag history/branches/lucene-solr/lucene5904                      origin/lucene5904
git tag history/branches/lucene-solr/lucene5969                      origin/lucene5969
git tag history/branches/lucene-solr/lucene5995                      origin/lucene5995
git tag history/branches/lucene-solr/lucene6005                      origin/lucene6005
git tag history/branches/lucene-solr/lucene6065                      origin/lucene6065
git tag history/branches/lucene-solr/lucene6196                      origin/lucene6196
git tag history/branches/lucene-solr/lucene6238                      origin/lucene6238
git tag history/branches/lucene-solr/lucene6271                      origin/lucene6271
git tag history/branches/lucene-solr/lucene6299                      origin/lucene6299
git tag history/branches/lucene-solr/lucene6487                      origin/lucene6487
git tag history/branches/lucene-solr/lucene6508                      origin/lucene6508
git tag history/branches/lucene-solr/lucene6699                      origin/lucene6699
git tag history/branches/lucene-solr/lucene6780                      origin/lucene6780
git tag history/branches/lucene-solr/lucene6825                      origin/lucene6825
git tag history/branches/lucene-solr/lucene6835                      origin/lucene6835
git tag history/branches/lucene-solr/lucene6852                      origin/lucene6852
git tag history/branches/lucene-solr/lucene_solr_3_1                 origin/lucene_solr_3_1
git tag history/branches/lucene-solr/lucene_solr_3_2                 origin/lucene_solr_3_2
git tag history/branches/lucene-solr/lucene_solr_3_3                 origin/lucene_solr_3_3
git tag history/branches/lucene-solr/lucene_solr_3_4                 origin/lucene_solr_3_4
git tag history/branches/lucene-solr/lucene_solr_3_5                 origin/lucene_solr_3_5
git tag history/branches/lucene-solr/lucene_solr_3_6                 origin/lucene_solr_3_6
git tag history/branches/lucene-solr/lucene_solr_4_0                 origin/lucene_solr_4_0
git tag history/branches/lucene-solr/lucene_solr_4_1                 origin/lucene_solr_4_1
git tag history/branches/lucene-solr/lucene_solr_4_10                origin/lucene_solr_4_10
git tag history/branches/lucene-solr/lucene_solr_4_2                 origin/lucene_solr_4_2
git tag history/branches/lucene-solr/lucene_solr_4_3                 origin/lucene_solr_4_3
git tag history/branches/lucene-solr/lucene_solr_4_4                 origin/lucene_solr_4_4
git tag history/branches/lucene-solr/lucene_solr_4_5                 origin/lucene_solr_4_5
git tag history/branches/lucene-solr/lucene_solr_4_6                 origin/lucene_solr_4_6
git tag history/branches/lucene-solr/lucene_solr_4_7                 origin/lucene_solr_4_7
git tag history/branches/lucene-solr/lucene_solr_4_8                 origin/lucene_solr_4_8
git tag history/branches/lucene-solr/lucene_solr_4_9                 origin/lucene_solr_4_9
git tag history/branches/lucene-solr/lucene_solr_5_0                 origin/lucene_solr_5_0
git tag history/branches/lucene-solr/lucene_solr_5_1                 origin/lucene_solr_5_1
git tag history/branches/lucene-solr/lucene_solr_5_2                 origin/lucene_solr_5_2
git tag history/branches/lucene-solr/lucene_solr_5_3                 origin/lucene_solr_5_3
git tag history/branches/lucene-solr/lucene_solr_5_4                 origin/lucene_solr_5_4
git tag history/branches/lucene-solr/omitp                           origin/omitp
git tag history/branches/lucene-solr/pforcodec_3892                  origin/pforcodec_3892
git tag history/branches/lucene-solr/positions                       origin/positions
git tag history/branches/lucene-solr/preflexfixes                    origin/preflexfixes
git tag history/branches/lucene-solr/pseudo                          origin/pseudo
git tag history/branches/lucene-solr/realtime_search                 origin/realtime_search
git tag history/branches/lucene-solr/security                        origin/security
git tag history/branches/lucene-solr/slowclosing                     origin/slowclosing
git tag history/branches/lucene-solr/solr-5473                       origin/solr-5473
git tag history/branches/lucene-solr/solr2193                        origin/solr2193
git tag history/branches/lucene-solr/solr2452                        origin/solr2452
git tag history/branches/lucene-solr/solr3733                        origin/solr3733
git tag history/branches/lucene-solr/solr4470                        origin/solr4470
git tag history/branches/lucene-solr/solr5473                        origin/solr5473
git tag history/branches/lucene-solr/solr5914                        origin/solr5914
git tag history/branches/lucene-solr/solr7787                        origin/solr7787
git tag history/branches/lucene-solr/solr7790                        origin/solr7790
git tag history/branches/lucene-solr/solr_3159_jetty8                origin/solr_3159_jetty8
git tag history/branches/lucene-solr/solr_guice_restlet              origin/solr_guice_restlet
git tag history/branches/lucene-solr/solrcloud                       origin/solrcloud
git tag history/branches/lucene-solr/throwawaybranch                 origin/throwawaybranch

for ref in `git show-ref | grep "/origin/" | cut -f 2 -d ' '`; do
 git update-ref -d $ref
done


