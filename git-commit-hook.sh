#!/bin/bash

### block commiting empty files ###
## loop through all staged files
for file in $(git diff --cached --name-only)

do 
    # if the file is empty, block the commit
    if [ ! -s "$file" ]; then
        echo "X File '$file' is empty. Please add some content before commiting."
        exit 1
    fi
done

# if all files are valid allow the commit
exit 0

#!/bin/bash

## commit msg format checker
if ! grep -qE "^(feat|fix|docs):" "$1"; then
    echo "X commit messsage must start with feat:, fix: or docs:"
    exit 1
fi

### Large file blocker ### 

for file in $(git diff --cached --name-only); do
    if [ $(stat -c%s "$file") -gt 50000000 ]; then
        echo "X File '$file' is too large!"
        exit 1
    fi
done

### auto remove trailing spaces or extra newliines ###
 
sed -i 's/[ \t]*$//' "$file" # remove trailing spaces

### manage pre-commit hook easily ###
### for compex use pre-commit framework (python based) ##

.pre-commit-config.yaml

repos:
    - repo: https://github.com/pre-commit/pre-commit-hooks
      rev: v4.4.0
      hooks:
        - id: trailing-whitespace
        - id: end-of-file-fixer
        - id: check-yaml

## to activate it: pre-commit install


