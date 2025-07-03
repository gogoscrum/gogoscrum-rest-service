#!/bin/sh

# Author : Allen Lau
# Copyright (c) https://www.shimi-tech.com
# Description: A command line tool to generate flyway migration file that avoid version conflict.
# Script follows here:

function usage() {
    CLEAR='\033[0m'
    RED='\033[0;31m'
    if [ -n "$1" ]; then
        echo -e "${RED}👉 $1${CLEAR}\n"
    fi
    echo "A command line tool to generate flyway migration file that avoid version conflict."
    echo "Usage: $0  description"
    echo "Example: $0 alter_user"
    exit 1
}

function create_script() {
    file_name=src/main/resources/migrations/V

    current_time=$(date "+%Y.%m.%d.%H%M%S")
    echo "Current Time : $current_time"
    file_name=$file_name$current_time"__"$description."sql"
    echo "Migrations file : " "$file_name"
    touch $file_name
    echo "New File Location : 'resources/migrations'"
}

# usage
if [ "$1" != "" ]; then
   description="$1"
   create_script
else
   usage
fi
