#!/bin/bash

set -ex

pom_version=$(xmllint --xpath "/*[local-name()='project']/*[local-name()='version']/text()" pom.xml)
java_version=$(grep -o "${pom_version}" src/main/java/org/keycloak/social/tiktok/TikTokIdentityProviderFactory.java)

if [[ "$pom_version" != "$java_version" ]]; then
  echo "Version mismatch: pom.xml ($pom_version) != TikTokIdentityProviderFactory.java ($java_version)"
  exit 1
else
  echo "✔ Versions match: $pom_version"
fi
