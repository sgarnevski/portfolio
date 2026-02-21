#!/usr/bin/env bash
# Stop the frontend dev server by killing the node process on port 3000
SCRIPT_DIR="$(cd "$(dirname "$0")" && pwd)"
source "$SCRIPT_DIR/config.sh"

PID=$(netstat -ano 2>/dev/null | grep "LISTENING" | grep -E ":${FRONTEND_PORT}[[:space:]]" | awk '{print $NF}' | head -1)
if [ -z "$PID" ]; then
  log_warn "Frontend — not running on port $FRONTEND_PORT"
else
  log_info "Stopping frontend (PID: $PID)..."
  "$TASKKILL" //PID "$PID" //F > /dev/null 2>&1
  log_ok "Frontend stopped."
fi
