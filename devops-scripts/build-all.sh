#!/usr/bin/env bash
# Build all services
set -e
SCRIPT_DIR="$(cd "$(dirname "$0")" && pwd)"

"$SCRIPT_DIR/build-backend.sh"
"$SCRIPT_DIR/build-auth.sh"
"$SCRIPT_DIR/build-gateway.sh"
"$SCRIPT_DIR/build-frontend.sh"

source "$SCRIPT_DIR/config.sh"
log_ok "All services built successfully."
