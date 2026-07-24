#!/bin/bash

set -ex

pom_version=$(mvn help:evaluate -Dexpression=project.version -q -DforceStdout)
java_version=$(grep -o "${pom_version}" src/main/java/org/keycloak/social/tiktok/TikTokIdentityProviderFactory.java)

if [[ "$pom_version" != "$java_version" ]]; then
  echo "Version mismatch: pom.xml ($pom_version) != TikTokIdentityProviderFactory.java ($java_version)"
  exit 1
else
  echo "✔ Versions match: $pom_version"
fi
