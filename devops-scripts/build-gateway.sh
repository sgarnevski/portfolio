#!/usr/bin/env bash
# Build the market-data-gateway Spring Boot JAR
set -e
SCRIPT_DIR="$(cd "$(dirname "$0")" && pwd)"
source "$SCRIPT_DIR/config.sh"

log_info "Building market-data-gateway..."
cd "$GATEWAY_DIR"
"$MAVEN" clean package -DskipTests -q
log_ok "Gateway JAR built: $GATEWAY_JAR"
