# Integration test image for keycloak-tiktok.
# Builds Keycloak with the mounted provider jar and verifies it registers.
ARG KEYCLOAK_VERSION=26.5.2
FROM quay.io/keycloak/keycloak:${KEYCLOAK_VERSION}

# The built provider jar is copied into the Keycloak providers directory.
COPY keycloak-tiktok.jar /opt/keycloak/providers/keycloak-tiktok.jar

# Augment Keycloak with the provider. This scans /opt/keycloak/providers and
# fails the build if the jar is malformed/incompatible.
RUN /opt/keycloak/bin/kc.sh build
