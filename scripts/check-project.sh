#!/bin/bash
# Local quality check script

echo "Running Detekt..."
./gradlew detekt

echo "Running Ktlint..."
./gradlew ktlintCheck

echo "Running Unit Tests..."
./gradlew test

echo "Running Lint..."
./gradlew lint

if [ $? -eq 0 ]; then
    echo "Check passed!"
else
    echo "Check failed!"
    exit 1
fi
