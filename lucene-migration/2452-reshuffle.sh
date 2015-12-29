#!/bin/bash

mkdir -p solr/solrj/src/java
mkdir -p solr/solrj/src/test/org/apache/solr/client/solrj
mkdir -p solr/solrj/src/test-files/solr/conf

shopt -s extglob
mv solr/src/test-files/{books.csv,sampleDateFacetResponse.xml} solr/solrj/src/test-files/
mv solr/src/test-files/solr/shared solr/solrj/src/test-files/solr/

cp solr/src/test-files/README solr/solrj/src/test-files/
cp solr/src/test-files/solr/crazy-path-to-schema.xml solr/solrj/src/test-files/solr/
cp solr/src/test-files/solr/conf/{schema.xml,schema-replication1.xml,solrconfig-slave1.xml} solr/solrj/src/test-files/solr/conf/

mv solr/src/solrj/org solr/solrj/src/java/
mv solr/src/common/org/apache/solr/common solr/solrj/src/java/org/apache/solr/
mv solr/src/test/org/apache/solr/common solr/solrj/src/test/org/apache/solr/
mv solr/src/test/org/apache/solr/client/solrj/!(SolrJettyTestBase.java) solr/solrj/src/test/org/apache/solr/client/solrj

mkdir -p solr/test-framework/src/java
mv solr/src/test-framework/org solr/test-framework/src/java/
mv solr/src/test/org/apache/solr/client/solrj/SolrJettyTestBase.java solr/test-framework/src/java/org/apache/solr/
mv solr/src/test/org/apache/solr/util/ExternalPaths.java solr/test-framework/src/java/org/apache/solr/util/

mkdir -p solr/core/src
mv solr/src/{java,test,test-files} solr/core/src/
mv solr/src/webapp/src/org/apache/solr/* solr/core/src/java/org/apache/solr/

mkdir solr/webapp
mv solr/src/webapp/web solr/webapp/
mv solr/src/{scripts,dev-tools} solr/
mv solr/src/site solr/site-src

rm -rf solr/src

mkdir dev-tools/maven/solr/core
mv dev-tools/maven/solr/src/pom.xml.template dev-tools/maven/solr/core
mkdir dev-tools/maven/solr/test-framework
mv dev-tools/maven/solr/src/test-framework/pom.xml.template dev-tools/maven/solr/test-framework
mkdir dev-tools/maven/solr/solrj
mv dev-tools/maven/solr/src/solrj/pom.xml.template dev-tools/maven/solr/solrj
mkdir dev-tools/maven/solr/webapp
mv dev-tools/maven/solr/src/webapp/pom.xml.template dev-tools/maven/solr/webapp/
rm -rf dev-tools/maven/solr/src
