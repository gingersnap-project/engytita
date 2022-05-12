IMG_REGISTRY ?= localhost:5001
CLIENT_IMG ?= $(IMG_REGISTRY)/client
SERVER_IMG ?= $(IMG_REGISTRY)/server
POSTGRES_IMG ?= $(IMG_REGISTRY)/postgres

export CONTAINER_TOOL ?= docker

ifeq ($(NATIVE),true)
	QUARKUS_BUILD_ARGS = "-Pnative -Dquarkus.native.container-build=true"
endif

.DEFAULT_GOAL := help

help:
	@awk '/^#/{c=substr($$0,3);next}c&&/^[[:alpha:]][[:alnum:]_-]+:/{print substr($$1,1,index($$1,":")),c}1{c=0}' $(MAKEFILE_LIST) | column -s: -t

.PHONY: client-image
## Build the client image
client-image:
	mvn package -am -pl test/client -Dquarkus.container-image.image=$(CLIENT_IMG) -Dquarkus.container-image.build=true $(QUARKUS_BUILD_ARGS)

.PHONY: client-push
## Push the client image
client-push:
	$(CONTAINER_TOOL) push $(CLIENT_IMG)

.PHONY: server-image
## Build the server image
server-image:
	mvn package -am -pl test/server -Dquarkus.container-image.image=$(SERVER_IMG) -Dquarkus.container-image.build=true $(QUARKUS_BUILD_ARGS)

.PHONY: server-push
## Push the server image
server-push:
	$(CONTAINER_TOOL) push $(SERVER_IMG)

.PHONY: postgres-image
## Build the postgres image
postgres-image:
	$(CONTAINER_TOOL) build test/data -t $(POSTGRES_IMG)

.PHONY: postgres-push
## Push the postgres image
postgres-push:
	$(CONTAINER_TOOL) push $(POSTGRES_IMG)

.PHONY: build-all-images
## Build all images
build-all-images: client-image server-image postgres-image

.PHONY: push-all-images
## Puash all images
push-all-images: client-push server-push postgres-push

.PHONY: kind-cluster
## Create a local kind cluster with image registry localhost:5000
kind-cluster:
	scripts/kind-with-registry.sh
# Load dependency images locally if available to stop them being repulled by kind
	kind load docker-image quay.io/infinispan/server:13.0 || true
	kind load docker-image redis:7.0.0 || true

.PHONY: deploy
## Deploy all components to the "demo" namespace on the local kind cluster
deploy:
	kubectl create namespace demo || true
	kubectl -n demo apply -f test/deploy/postgres.yaml
	kubectl -n demo apply -f test/deploy/infinispan.yaml
	kubectl -n demo apply -f test/deploy/redis.yaml
# Wait for all backend services to be available before starting the client and server
	kubectl -n demo wait deployment postgres --for condition=Available=True --timeout=90s
	kubectl -n demo wait deployment infinispan --for condition=Available=True --timeout=90s
	kubectl -n demo wait deployment redis --for condition=Available=True --timeout=90s

	kubectl -n demo apply -f test/deploy/server.yaml
	kubectl -n demo apply -f test/deploy/client.yaml

.PHONY: undeploy
## Undeploy all components deployed via the "deploy" target
undeploy:
	kubectl -n demo delete -f test/deploy/postgres.yaml || true
	kubectl -n demo delete -f test/deploy/server.yaml || true
	kubectl -n demo delete -f test/deploy/client.yaml || true
	kubectl -n demo delete -f test/deploy/infinispan.yaml || true
	kubectl -n demo delete -f test/deploy/redis.yaml || true
