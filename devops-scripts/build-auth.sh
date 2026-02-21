#!/usr/bin/env bash
# Build the auth-service Spring Boot JAR
set -e
SCRIPT_DIR="$(cd "$(dirname "$0")" && pwd)"
source "$SCRIPT_DIR/config.sh"

log_info "Building auth-service..."
cd "$AUTH_DIR"
"$MAVEN" clean package -DskipTests -q
log_ok "Auth-service JAR built: $AUTH_JAR"
