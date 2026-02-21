#!/usr/bin/env bash
# Quick compilation check for all services (no tests, no JAR packaging)
set -e
SCRIPT_DIR="$(cd "$(dirname "$0")" && pwd)"
source "$SCRIPT_DIR/config.sh"

log_info "Checking backend compilation..."
cd "$BACKEND_DIR"
"$MAVEN" compile -q
log_ok "Backend compiles."

log_info "Checking auth-service compilation..."
cd "$AUTH_DIR"
"$MAVEN" compile -q
log_ok "Auth-service compiles."

log_info "Checking market-data-gateway compilation..."
cd "$GATEWAY_DIR"
"$MAVEN" compile -q
log_ok "Market-data-gateway compiles."

log_info "Checking frontend TypeScript..."
cd "$FRONTEND_DIR"
"$NODE" node_modules/typescript/bin/tsc --noEmit
log_ok "Frontend TypeScript OK."

echo ""
log_ok "All compilation checks passed."
