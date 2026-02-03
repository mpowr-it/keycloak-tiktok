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
INSTALL_TARGET=/Users/hollodotme/Sites/MPOWR-IT/LikeTik/iam/src/keycloak/providers

## Build the package
build:
	mvn clean package
.PHONY: build

## Run static checks
check-all: build
	bash ./scripts/check_version.sh
	$(done)
.PHONY: check-all

## Run tests
test:
	mvn test
.PHONY: test


## Install extension
install: build
	cp -f ./target/keycloak-tiktok-*.jar "$(INSTALL_TARGET)/$(PROJECT_NAME).jar"
	diff -q "$(INSTALL_TARGET)/$(PROJECT_NAME).jar" ./target/keycloak-tiktok-*.jar || \
		{ echo "Files differ, please check the installation."; exit 1; }
.PHONY: install

