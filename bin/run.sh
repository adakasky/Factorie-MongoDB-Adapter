#!/bin/bash

MEMORY=10g
SPECIFIC_PROJECT_ROOT = "/home/umaguest/Factorie-MongoDB-Adapter"
$SPECIFIC_PROJECT_ROOT/bin/run_class.sh -Xmx$MEMORY adapter.Adapter --option=value