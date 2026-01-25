#!/bin/bash

## run setup script on branch switch

if [ "$3" == "1" ]; then
    echo "You switched branch! Running setup..."
    if [ -f setup.sh ]; then
        bash setup.sh
    fi
fi

# auto copy .env file based on Branch

branch=$(git rev-parse --abbrev-ref HEAD)
cp ".env.$branch" .env 2>/dev/null && echo "Loaded .env.$branch" || echo "No .env file for $branch"


## clear cache or temp files 

rm -rf .cache dist tmp/
echo "Cleared local build/cache folders."

### show notifications on branch switch

branch=$(git rev-parse --abbrev-ref HEAD)
echo "Switched to branch: $branch"

#### Log checkout events
echo "$(date) - Switched from $1 to $2 (Branch: $3)" >> .checkou-history.log

## branch specific warnings or setups

branch=$(git rev-parse --abbrev-ref HEAD)

if [[ "$branch" == "prod" ]]; then
    echo "WARNING: You are on PRODUCTION branch"
fi

