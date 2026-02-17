#!/bin/bash
cd /home/kavia/workspace/code-generation/kotlin-tic-tac-toe-321524/kotlin_frontend
./gradlew lint
LINT_EXIT_CODE=$?
if [ $LINT_EXIT_CODE -ne 0 ]; then
   exit 1
fi

