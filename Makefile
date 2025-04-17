.SILENT:
.PHONY: help

done = printf "\e[32m ✔ Done\e[0m\n\n";

## This help screen
help:
	printf "Available commands\n\n"
	awk '/^[a-zA-Z\-\_0-9]+:/ { \
		helpMessage = match(lastLine, /^## (.*)/); \
		if (helpMessage) { \
			helpCommand = substr($$1, 0, index($$1, ":")-1); \
			helpMessage = substr(lastLine, RSTART + 3, RLENGTH); \
			printf "\033[33m%-40s\033[0m %s\n", helpCommand, helpMessage; \
		} \
	} \
	{ lastLine = $$0 }' $(MAKEFILE_LIST)

PROJECT_NAME=keycloak-tiktok

## Build the package
build:
	mvn clean package
.PHONY: build

## Run tests
test:
	mvn test
.PHONY: test
