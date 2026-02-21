#!/usr/bin/env bash
# Start the frontend dev server in background
set -e
SCRIPT_DIR="$(cd "$(dirname "$0")" && pwd)"
source "$SCRIPT_DIR/config.sh"

# Check if already running
PID=$(netstat -ano 2>/dev/null | grep "LISTENING" | grep -E ":${FRONTEND_PORT}[[:space:]]" | awk '{print $NF}' | head -1)
if [ -n "$PID" ]; then
  log_warn "Frontend already running on port $FRONTEND_PORT (PID: $PID). Stop it first or use redeploy."
  exit 1
fi

log_info "Starting frontend dev server..."
cd "$FRONTEND_DIR"
"$NPM" run dev > /dev/null 2>&1 &
sleep 3
log_ok "Frontend started on port $FRONTEND_PORT"
