#!/usr/bin/env bash
# Redeploy backend: stop -> build -> start
set -e
SCRIPT_DIR="$(cd "$(dirname "$0")" && pwd)"
source "$SCRIPT_DIR/config.sh"

log_info "=== Redeploying Backend ==="

# Stop
PIDS=$("$JPS" -l 2>/dev/null | grep "portfolio-rebalancer-backend" | awk '{print $1}')
if [ -n "$PIDS" ]; then
  for PID in $PIDS; do
    log_info "Stopping backend (PID: $PID)..."
    "$TASKKILL" //PID "$PID" //F > /dev/null 2>&1
  done
  sleep 2
  log_ok "Backend stopped."
else
  log_warn "Backend was not running."
fi

# Build
"$SCRIPT_DIR/build-backend.sh"

# Start
log_info "Starting backend (profile: $SPRING_PROFILE)..."
cd "$BACKEND_DIR"
"$JAVA" -jar "$BACKEND_JAR" --spring.profiles.active="$SPRING_PROFILE" > /dev/null 2>&1 &
BACKEND_PID=$!
sleep 3

if kill -0 "$BACKEND_PID" 2>/dev/null; then
  log_ok "Backend redeployed (PID: $BACKEND_PID) on port $BACKEND_PORT"
else
  log_error "Backend failed to start after redeploy."
  exit 1
fi
