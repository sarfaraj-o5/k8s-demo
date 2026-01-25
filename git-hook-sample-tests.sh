#!/bin/bash


## minimum word count check ##

msg=$(cat "$1")
word_count=$(echo "$msg" | wc -w)

if ["$word_count" -lt 5 ]; then
    echo "X commit message must be atleast 5 words!"
    exit 1
fi 

### enforce message format (type: description) ###

#!/bin/bash

msg=$(cat "$1")


if ! echo "$msg" | grep -qE "^(feat|fix|docs|chore|test|refactor): +"; then
    echo "X commit message must start with type: description (e.g.,feat: add login)"
    exit 1
fi 


### Block forbidden words or placeholders
#!/bin/bash

msg=$(cat "$1")


if  echo "$msg" | grep -qE "(WIP|asdf|temp)"; then
    echo "X commit message contains forbidden placeholder words."
    exit 1
fi 


###  Maximum length check ###
#!/bin/bash

msg=$(head -n1 "$1")


if  ["${#msg}" -gt 72]; then
    echo "X commit message should exceed 72 characters in the first line."
    exit 1
fi 


### Run test based on message type ###
#!/bin/bash

msg=$(cat "$1")


if  echo "$msg" | grep -qE "^(test|fix)"; then
    npm test || exit 1
fi 


### Block empty commit messages ###
#!/bin/bash

msg=$(cat "$1" | tr -d '\n')


if  [ -z "$msg" ]; then
    echo "X Empty commit messages is not allowed!"
    exit 1
fi 

