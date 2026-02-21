#!/usr/bin/env bash
# Start all services (auth first, then gateway, then backend, then frontend)
set -e
SCRIPT_DIR="$(cd "$(dirname "$0")" && pwd)"

"$SCRIPT_DIR/start-auth.sh"
"$SCRIPT_DIR/start-gateway.sh"
"$SCRIPT_DIR/start-backend.sh"
"$SCRIPT_DIR/start-frontend.sh"

source "$SCRIPT_DIR/config.sh"
echo ""
log_ok "All services started."
"$SCRIPT_DIR/status.sh"
