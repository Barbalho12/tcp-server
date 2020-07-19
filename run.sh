#!/bin/bash

mvn clean install package compile exec:java -Dexec.mainClass="com.barbalho.rocha.Server" -Dexec.cleanupDaemonThreads=false -Dexec.args="$*"