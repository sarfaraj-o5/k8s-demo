#!/bin/bash

## run below cmd for restoring from backup to diff namespace

velero restore create restore-name --from-backup backup-name --namespace-mappings namespace1:namespace2

## verify the new restored resources in namespace2

kubectl get pvc,pv,pof -n namespace2