
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

git tag releases/lucene-solr/3.1     lucene_solr_3_1
git tag releases/lucene-solr/3.2     lucene_solr_3_2
git tag releases/lucene-solr/3.3     lucene_solr_3_3
git tag releases/lucene-solr/3.4.0   lucene_solr_3_4_0
git tag releases/lucene-solr/3.5.0   lucene_solr_3_5_0
git tag releases/lucene-solr/3.6.0   lucene_solr_3_6_0
git tag releases/lucene-solr/3.6.1   lucene_solr_3_6_1
git tag releases/lucene-solr/3.6.2   lucene_solr_3_6_2
git tag releases/lucene-solr/4.0.0   lucene_solr_4_0_0
git tag releases/lucene-solr/4.0.0-alpha   lucene_solr_4_0_0_ALPHA
git tag releases/lucene-solr/4.0.0-beta    lucene_solr_4_0_0_BETA
git tag releases/lucene-solr/4.10.0   lucene_solr_4_10_0
git tag releases/lucene-solr/4.10.1   lucene_solr_4_10_1
git tag releases/lucene-solr/4.10.2   lucene_solr_4_10_2
git tag releases/lucene-solr/4.10.3   lucene_solr_4_10_3
git tag releases/lucene-solr/4.10.4   lucene_solr_4_10_4
git tag releases/lucene-solr/4.1.0   lucene_solr_4_1_0
git tag releases/lucene-solr/4.2.0   lucene_solr_4_2_0
git tag releases/lucene-solr/4.2.1   lucene_solr_4_2_1
git tag releases/lucene-solr/4.3.0   lucene_solr_4_3_0
git tag releases/lucene-solr/4.3.1   lucene_solr_4_3_1
git tag releases/lucene-solr/4.4.0   lucene_solr_4_4_0
git tag releases/lucene-solr/4.5.0   lucene_solr_4_5_0
git tag releases/lucene-solr/4.5.1   lucene_solr_4_5_1
git tag releases/lucene-solr/4.6.0   lucene_solr_4_6_0
git tag releases/lucene-solr/4.6.1   lucene_solr_4_6_1
git tag releases/lucene-solr/4.7.0   lucene_solr_4_7_0
git tag releases/lucene-solr/4.7.1   lucene_solr_4_7_1
git tag releases/lucene-solr/4.7.2   lucene_solr_4_7_2
git tag releases/lucene-solr/4.8.0   lucene_solr_4_8_0
git tag releases/lucene-solr/4.8.1   lucene_solr_4_8_1
git tag releases/lucene-solr/4.9.0   lucene_solr_4_9_0
git tag releases/lucene-solr/4.9.1   lucene_solr_4_9_1
git tag releases/lucene-solr/5.0.0   lucene_solr_5_0_0
git tag releases/lucene-solr/5.1.0   lucene_solr_5_1_0
git tag releases/lucene-solr/5.2.0   lucene_solr_5_2_0
git tag releases/lucene-solr/5.2.1   lucene_solr_5_2_1
git tag releases/lucene-solr/5.3.0   lucene_solr_5_3_0
git tag releases/lucene-solr/5.3.1   lucene_solr_5_3_1
git tag releases/lucene-solr/5.4.0   lucene_solr_5_4_0 

git tag -d before_flex_merge lucene_solr_3_1 lucene_solr_3_2 lucene_solr_3_3 lucene_solr_3_4_0 lucene_solr_3_5_0 lucene_solr_3_6_0 lucene_solr_3_6_1
git tag -d lucene_solr_3_6_2 lucene_solr_4_0_0 lucene_solr_4_0_0_ALPHA lucene_solr_4_0_0_BETA lucene_solr_4_10_0 lucene_solr_4_10_1 lucene_solr_4_10_2
git tag -d lucene_solr_4_10_3 lucene_solr_4_10_4 lucene_solr_4_1_0 lucene_solr_4_2_0 lucene_solr_4_2_1 lucene_solr_4_3_0 lucene_solr_4_3_1 lucene_solr_4_4_0
git tag -d lucene_solr_4_5_0 lucene_solr_4_5_1 lucene_solr_4_6_0 lucene_solr_4_6_1 lucene_solr_4_7_0 lucene_solr_4_7_1 lucene_solr_4_7_2 lucene_solr_4_8_0
git tag -d lucene_solr_4_8_1 lucene_solr_4_9_0 lucene_solr_4_9_1 lucene_solr_5_0_0 lucene_solr_5_1_0 lucene_solr_5_2_0 lucene_solr_5_2_1 lucene_solr_5_3_0 lucene_solr_5_3_1
git tag -d  lucene_solr_5_4_0 post_3_5_0_release_doc_changes realtime_DWPT_final_2011-05-02 

# Leaving as-is: branch_3x, branch_4x, branch_5x

# Tagging as historical

git tag history/branches/lucene-solr/LUCENE-2878                     LUCENE-2878
git tag history/branches/lucene-solr/LUCENE-5622                     LUCENE-5622
git tag history/branches/lucene-solr/LUCENE-5716                     LUCENE-5716
git tag history/branches/lucene-solr/LUCENE-6481                     LUCENE-6481
git tag history/branches/lucene-solr/LUCENE2793                      LUCENE2793
git tag history/branches/lucene-solr/blocktree_3030                  blocktree_3030
git tag history/branches/lucene-solr/bulkpostings                    bulkpostings
git tag history/branches/lucene-solr/cleanup2878                     cleanup2878
git tag history/branches/lucene-solr/docvalues                       docvalues
git tag history/branches/lucene-solr/fieldtype                       fieldtype
git tag history/branches/lucene-solr/fieldtype_conflicted            fieldtype_conflicted
git tag history/branches/lucene-solr/flexscoring                     flexscoring
git tag history/branches/lucene-solr/fqmodule_2883                   fqmodule_2883
git tag history/branches/lucene-solr/ghost_of_4456                   ghost_of_4456
git tag history/branches/lucene-solr/leaky3147                       leaky3147
git tag history/branches/lucene-solr/lucene2510                      lucene2510
git tag history/branches/lucene-solr/lucene2621                      lucene2621
git tag history/branches/lucene-solr/lucene2858                      lucene2858
git tag history/branches/lucene-solr/lucene2878                      lucene2878
git tag history/branches/lucene-solr/lucene3069                      lucene3069
git tag history/branches/lucene-solr/lucene3305                      lucene3305
git tag history/branches/lucene-solr/lucene3312                      lucene3312
git tag history/branches/lucene-solr/lucene3453                      lucene3453
git tag history/branches/lucene-solr/lucene3606                      lucene3606
git tag history/branches/lucene-solr/lucene3622                      lucene3622
git tag history/branches/lucene-solr/lucene3661                      lucene3661
git tag history/branches/lucene-solr/lucene3733                      lucene3733
git tag history/branches/lucene-solr/lucene3767                      lucene3767
git tag history/branches/lucene-solr/lucene3795_lsp_spatial_module   lucene3795_lsp_spatial_module
git tag history/branches/lucene-solr/lucene3837                      lucene3837
git tag history/branches/lucene-solr/lucene3842                      lucene3842
git tag history/branches/lucene-solr/lucene3846                      lucene3846
git tag history/branches/lucene-solr/lucene3930                      lucene3930
git tag history/branches/lucene-solr/lucene3969                      lucene3969
git tag history/branches/lucene-solr/lucene4055                      lucene4055
git tag history/branches/lucene-solr/lucene4100                      lucene4100
git tag history/branches/lucene-solr/lucene4199                      lucene4199
git tag history/branches/lucene-solr/lucene4236                      lucene4236
git tag history/branches/lucene-solr/lucene4258                      lucene4258
git tag history/branches/lucene-solr/lucene4335                      lucene4335
git tag history/branches/lucene-solr/lucene4446                      lucene4446
git tag history/branches/lucene-solr/lucene4456                      lucene4456
git tag history/branches/lucene-solr/lucene4547                      lucene4547
git tag history/branches/lucene-solr/lucene4765                      lucene4765
git tag history/branches/lucene-solr/lucene4956                      lucene4956
git tag history/branches/lucene-solr/lucene5012                      lucene5012
git tag history/branches/lucene-solr/lucene5127                      lucene5127
git tag history/branches/lucene-solr/lucene5178                      lucene5178
git tag history/branches/lucene-solr/lucene5205                      lucene5205
git tag history/branches/lucene-solr/lucene5207                      lucene5207
git tag history/branches/lucene-solr/lucene5339                      lucene5339
git tag history/branches/lucene-solr/lucene5376                      lucene5376
git tag history/branches/lucene-solr/lucene5376_2                    lucene5376_2
git tag history/branches/lucene-solr/lucene539399                    lucene539399
git tag history/branches/lucene-solr/lucene5438                      lucene5438
git tag history/branches/lucene-solr/lucene5468                      lucene5468
git tag history/branches/lucene-solr/lucene5487                      lucene5487
git tag history/branches/lucene-solr/lucene5493                      lucene5493
git tag history/branches/lucene-solr/lucene5611                      lucene5611
git tag history/branches/lucene-solr/lucene5650                      lucene5650
git tag history/branches/lucene-solr/lucene5666                      lucene5666
git tag history/branches/lucene-solr/lucene5675                      lucene5675
git tag history/branches/lucene-solr/lucene5752                      lucene5752
git tag history/branches/lucene-solr/lucene5786                      lucene5786
git tag history/branches/lucene-solr/lucene5858                      lucene5858
git tag history/branches/lucene-solr/lucene5904                      lucene5904
git tag history/branches/lucene-solr/lucene5969                      lucene5969
git tag history/branches/lucene-solr/lucene5995                      lucene5995
git tag history/branches/lucene-solr/lucene6005                      lucene6005
git tag history/branches/lucene-solr/lucene6065                      lucene6065
git tag history/branches/lucene-solr/lucene6196                      lucene6196
git tag history/branches/lucene-solr/lucene6238                      lucene6238
git tag history/branches/lucene-solr/lucene6271                      lucene6271
git tag history/branches/lucene-solr/lucene6299                      lucene6299
git tag history/branches/lucene-solr/lucene6487                      lucene6487
git tag history/branches/lucene-solr/lucene6508                      lucene6508
git tag history/branches/lucene-solr/lucene6699                      lucene6699
git tag history/branches/lucene-solr/lucene6780                      lucene6780
git tag history/branches/lucene-solr/lucene6825                      lucene6825
git tag history/branches/lucene-solr/lucene6835                      lucene6835
git tag history/branches/lucene-solr/lucene6852                      lucene6852
git tag history/branches/lucene-solr/lucene_solr_3_1                 lucene_solr_3_1
git tag history/branches/lucene-solr/lucene_solr_3_2                 lucene_solr_3_2
git tag history/branches/lucene-solr/lucene_solr_3_3                 lucene_solr_3_3
git tag history/branches/lucene-solr/lucene_solr_3_4                 lucene_solr_3_4
git tag history/branches/lucene-solr/lucene_solr_3_5                 lucene_solr_3_5
git tag history/branches/lucene-solr/lucene_solr_3_6                 lucene_solr_3_6
git tag history/branches/lucene-solr/lucene_solr_4_0                 lucene_solr_4_0
git tag history/branches/lucene-solr/lucene_solr_4_1                 lucene_solr_4_1
git tag history/branches/lucene-solr/lucene_solr_4_10                lucene_solr_4_10
git tag history/branches/lucene-solr/lucene_solr_4_2                 lucene_solr_4_2
git tag history/branches/lucene-solr/lucene_solr_4_3                 lucene_solr_4_3
git tag history/branches/lucene-solr/lucene_solr_4_4                 lucene_solr_4_4
git tag history/branches/lucene-solr/lucene_solr_4_5                 lucene_solr_4_5
git tag history/branches/lucene-solr/lucene_solr_4_6                 lucene_solr_4_6
git tag history/branches/lucene-solr/lucene_solr_4_7                 lucene_solr_4_7
git tag history/branches/lucene-solr/lucene_solr_4_8                 lucene_solr_4_8
git tag history/branches/lucene-solr/lucene_solr_4_9                 lucene_solr_4_9
git tag history/branches/lucene-solr/lucene_solr_5_0                 lucene_solr_5_0
git tag history/branches/lucene-solr/lucene_solr_5_1                 lucene_solr_5_1
git tag history/branches/lucene-solr/lucene_solr_5_2                 lucene_solr_5_2
git tag history/branches/lucene-solr/lucene_solr_5_3                 lucene_solr_5_3
git tag history/branches/lucene-solr/lucene_solr_5_4                 lucene_solr_5_4
git tag history/branches/lucene-solr/omitp                           omitp
git tag history/branches/lucene-solr/pforcodec_3892                  pforcodec_3892
git tag history/branches/lucene-solr/positions                       positions
git tag history/branches/lucene-solr/preflexfixes                    preflexfixes
git tag history/branches/lucene-solr/pseudo                          pseudo
git tag history/branches/lucene-solr/realtime_search                 realtime_search
git tag history/branches/lucene-solr/security                        security
git tag history/branches/lucene-solr/slowclosing                     slowclosing
git tag history/branches/lucene-solr/solr-5473                       solr-5473
git tag history/branches/lucene-solr/solr2193                        solr2193
git tag history/branches/lucene-solr/solr2452                        solr2452
git tag history/branches/lucene-solr/solr3733                        solr3733
git tag history/branches/lucene-solr/solr4470                        solr4470
git tag history/branches/lucene-solr/solr5473                        solr5473
git tag history/branches/lucene-solr/solr5914                        solr5914
git tag history/branches/lucene-solr/solr7787                        solr7787
git tag history/branches/lucene-solr/solr7790                        solr7790
git tag history/branches/lucene-solr/solr_3159_jetty8                solr_3159_jetty8
git tag history/branches/lucene-solr/solr_guice_restlet              solr_guice_restlet
git tag history/branches/lucene-solr/solrcloud                       solrcloud
git tag history/branches/lucene-solr/throwawaybranch                 throwawaybranch

git br -D  LUCENE-2878
git br -D  LUCENE-5622
git br -D  LUCENE-5716
git br -D  LUCENE-6481
git br -D  LUCENE2793
git br -D  blocktree_3030
git br -D  bulkpostings
git br -D  cleanup2878
git br -D  docvalues
git br -D  fieldtype
git br -D  fieldtype_conflicted
git br -D  flexscoring
git br -D  fqmodule_2883
git br -D  ghost_of_4456
git br -D  leaky3147
git br -D  lucene2510
git br -D  lucene2621
git br -D  lucene2858
git br -D  lucene2878
git br -D  lucene3069
git br -D  lucene3305
git br -D  lucene3312
git br -D  lucene3453
git br -D  lucene3606
git br -D  lucene3622
git br -D  lucene3661
git br -D  lucene3733
git br -D  lucene3767
git br -D  lucene3795_lsp_spatial_module
git br -D  lucene3837
git br -D  lucene3842
git br -D  lucene3846
git br -D  lucene3930
git br -D  lucene3969
git br -D  lucene4055
git br -D  lucene4100
git br -D  lucene4199
git br -D  lucene4236
git br -D  lucene4258
git br -D  lucene4335
git br -D  lucene4446
git br -D  lucene4456
git br -D  lucene4547
git br -D  lucene4765
git br -D  lucene4956
git br -D  lucene5012
git br -D  lucene5127
git br -D  lucene5178
git br -D  lucene5205
git br -D  lucene5207
git br -D  lucene5339
git br -D  lucene5376
git br -D  lucene5376_2
git br -D  lucene539399
git br -D  lucene5438
git br -D  lucene5468
git br -D  lucene5487
git br -D  lucene5493
git br -D  lucene5611
git br -D  lucene5650
git br -D  lucene5666
git br -D  lucene5675
git br -D  lucene5752
git br -D  lucene5786
git br -D  lucene5858
git br -D  lucene5904
git br -D  lucene5969
git br -D  lucene5995
git br -D  lucene6005
git br -D  lucene6065
git br -D  lucene6196
git br -D  lucene6238
git br -D  lucene6271
git br -D  lucene6299
git br -D  lucene6487
git br -D  lucene6508
git br -D  lucene6699
git br -D  lucene6780
git br -D  lucene6825
git br -D  lucene6835
git br -D  lucene6852
git br -D  lucene_solr_3_1
git br -D  lucene_solr_3_2
git br -D  lucene_solr_3_3
git br -D  lucene_solr_3_4
git br -D  lucene_solr_3_5
git br -D  lucene_solr_3_6
git br -D  lucene_solr_4_0
git br -D  lucene_solr_4_1
git br -D  lucene_solr_4_10
git br -D  lucene_solr_4_2
git br -D  lucene_solr_4_3
git br -D  lucene_solr_4_4
git br -D  lucene_solr_4_5
git br -D  lucene_solr_4_6
git br -D  lucene_solr_4_7
git br -D  lucene_solr_4_8
git br -D  lucene_solr_4_9
git br -D  lucene_solr_5_0
git br -D  lucene_solr_5_1
git br -D  lucene_solr_5_2
git br -D  lucene_solr_5_3
git br -D  lucene_solr_5_4
git br -D  omitp
git br -D  pforcodec_3892
git br -D  positions
git br -D  preflexfixes
git br -D  pseudo
git br -D  realtime_search
git br -D  security
git br -D  slowclosing
git br -D  solr-5473
git br -D  solr2193
git br -D  solr2452
git br -D  solr3733
git br -D  solr4470
git br -D  solr5473
git br -D  solr5914
git br -D  solr7787
git br -D  solr7790
git br -D  solr_3159_jetty8
git br -D  solr_guice_restlet
git br -D  solrcloud
git br -D  throwawaybranch


