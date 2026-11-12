#!/usr/bin/env bash

set -euo pipefail

project_dir="$(cd -- "$(dirname -- "${BASH_SOURCE[0]}")" && pwd)"
cd "$project_dir"

# Prefer an explicitly selected JDK 25, then check macOS and local JetBrains IDEs.
java_candidates=("${JAVA_HOME:-}")
if [[ -x /usr/libexec/java_home ]]; then
    java_candidates+=("$(/usr/libexec/java_home -v 25 2>/dev/null || true)")
fi
java_candidates+=(
    "$HOME/Applications/IntelliJ IDEA.app/Contents/jbr/Contents/Home"
    "/Applications/IntelliJ IDEA.app/Contents/jbr/Contents/Home"
)
release_java_home=""
for candidate in "${java_candidates[@]}"; do
    if [[ -x "$candidate/bin/javac" ]] &&
        [[ "$("$candidate/bin/javac" -version 2>&1)" == javac\ 25* ]]; then
        release_java_home="$candidate"
        break
    fi
done
if [[ -z "$release_java_home" ]]; then
    echo "JDK 25 is required. Set JAVA_HOME to a JDK 25 installation." >&2
    exit 1
fi
export JAVA_HOME="$release_java_home"

for signing_file in key/chain.crt key/private_encrypted.pem key/pwd; do
    if [[ ! -s "$signing_file" || ! -r "$signing_file" ]]; then
        echo "Missing, empty, or unreadable signing file: $signing_file" >&2
        exit 1
    fi
done

CERTIFICATE_CHAIN="$(cat key/chain.crt)"
PRIVATE_KEY="$(cat key/private_encrypted.pem)"
PRIVATE_KEY_PASSWORD="$(cat key/pwd)"
export CERTIFICATE_CHAIN PRIVATE_KEY PRIVATE_KEY_PASSWORD

# The first Marketplace release is uploaded manually; no publishing token is needed.
./gradlew signPlugin verifyPluginSignature "$@"
