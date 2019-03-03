#!/bin/bash

mvn clean install -DskipTests
if [ -f testerCDouter.zip ] ; then
    rm testerCDouter.zip
fi

zip -r testerCDouter.zip pom.xml *.out src/main/*
