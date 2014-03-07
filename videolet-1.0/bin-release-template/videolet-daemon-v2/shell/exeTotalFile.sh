#!/bin/sh
../indexer  videolet --rotate --config sphinx.video.ugc.3618.conf 
rm xmls/updateXmlFile.xml
cp xmls/updateXmlFile.xml_template xmls/updateXmlFile.xml
../indexer  delta --rotate --config sphinx.video.ugc.3618.conf
../searchd -c sphinx.video.ugc.3618.conf
