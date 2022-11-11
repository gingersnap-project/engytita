# Engytita #

## Origin of the name

Engytita is greek for proximity

# Running on local Kind K8s
1. `make kind-cluster build-all-images push-all-images deploy`
2. Access client application on local machine via `http://127.0.0.1:8080/`

This creates a local kind cluster backed by a local docker repository available at `localhost:5001`. All of the
required images are built locally and then pushed to the docker repository so that they can be consumed by k8s components.
Upon completion, all services are deployed and the client application is exposed via a NodePort so that its endpoints
can be access on the local machine.

# Running on Openshift
1. `make kind-cluster build-all-images push-all-images deploy IMG_REGISTRY=quay.io/engytita OPENSHIFT=true`
2. Access client application via created host/port: `kubectl -n demo get route client`
