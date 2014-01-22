#!/bin/sh
find . -type f -name "*.java" | xargs javadoc -classpath .:gs-core-1.2.jar:gs-algo-1.2.jar:gs-ui-1.2.jar -d docs/
