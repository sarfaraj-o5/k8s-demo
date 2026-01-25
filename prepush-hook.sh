#!/bin/bash

## display success msg
echo "Commit completed! Don't forget to run 'git push'"

### append commit summary to local log filie
git log -1 --pretty=format:"%h | %s |%an | %cd" >>> .commit-history.txt

## run test (non-blocking)
echo "Running tests after commit..."
npm test

####  random tips 
tips=(
    "Tip: use 'git log --graph --oneline' to see the commit tree"
    "Tip: 'git stash' saves your uncommited changes safely"
    "Tip: 'git diff HEAD~1' shows changes from last commit"
)
random=$((RANDOM % ${#tips[@]}))
echo "${tips[$random]}"

### send notifications

msg=$(git log -1 --pretty=format:"%s by %an")
curl -X POST -H "Content-Type: application/json" \
    -d "{\"test\":\"New commit: $msg\"}" \
    https://hooks.com

## sync repo to backup folder

rsync -av --exclude='.git' ./ /home/backup/my-repo

### clear terminal
clear
echo "Commit completed! Terminal refreshed."
