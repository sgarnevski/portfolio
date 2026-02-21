#!/usr/bin/env bash
# Redeploy frontend: stop -> rebuild -> start dev server
set -e
SCRIPT_DIR="$(cd "$(dirname "$0")" && pwd)"
source "$SCRIPT_DIR/config.sh"

log_info "=== Redeploying Frontend ==="

# Stop
"$SCRIPT_DIR/stop-frontend.sh"

# Start (Vite picks up changes via HMR in dev, but this does a full restart)
log_info "Starting frontend dev server..."
cd "$FRONTEND_DIR"
"$NPM" run dev > /dev/null 2>&1 &
sleep 3
log_ok "Frontend redeployed on port $FRONTEND_PORT"
