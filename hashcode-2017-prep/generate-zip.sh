#!/bin/bash

mvn clean test
if [ -f testerCDouter.zip ] ; then
    rm testerCDouter.zip
fi

zip -r testerCDouter.zip pom.xml *.in.out src/main/*

