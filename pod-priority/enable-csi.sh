#!/bin/bash

## Enable CSI server side features

kubectl -n velero edit deployment/velero
kubectl -n velero edit daemonset/restic 

## Add below --feature-EnableCSI flag in both resources

    spec:
        containers:
        - args:
            - server
            - --features-EnableCSI

## Enable client side features

velero client config set features-EnableCSI

## run the below command for taking backup

velero backup create backup_name --include-namespaces namespace_name