#!/usr/bin/env bash
# Stop all services
SCRIPT_DIR="$(cd "$(dirname "$0")" && pwd)"

"$SCRIPT_DIR/stop-java.sh"
"$SCRIPT_DIR/stop-frontend.sh"

source "$SCRIPT_DIR/config.sh"
log_ok "All services stopped."
