#!/bin/bash
#
# Convenience conversion script using maven

java -cp target/hymnal-tool-1.0-SNAPSHOT-jar-with-dependencies.jar \
com.soypig.hymnal.Converter "$@"
