#!/usr/bin/env bash
# Build the backend Spring Boot JAR
set -e
SCRIPT_DIR="$(cd "$(dirname "$0")" && pwd)"
source "$SCRIPT_DIR/config.sh"

log_info "Building backend..."
cd "$BACKEND_DIR"
"$MAVEN" clean package -DskipTests -q
log_ok "Backend JAR built: $BACKEND_JAR"
