#!/usr/bin/env bash
# Redeploy market-data-gateway: stop -> build -> start
set -e
SCRIPT_DIR="$(cd "$(dirname "$0")" && pwd)"
source "$SCRIPT_DIR/config.sh"

log_info "=== Redeploying Market-Data-Gateway ==="

# Stop
PIDS=$("$JPS" -l 2>/dev/null | grep "market-data-gateway" | awk '{print $1}')
if [ -n "$PIDS" ]; then
  for PID in $PIDS; do
    log_info "Stopping gateway (PID: $PID)..."
    "$TASKKILL" //PID "$PID" //F > /dev/null 2>&1
  done
  sleep 2
  log_ok "Gateway stopped."
else
  log_warn "Gateway was not running."
fi

# Build
"$SCRIPT_DIR/build-gateway.sh"

# Start
log_info "Starting market-data-gateway (profile: $SPRING_PROFILE)..."
cd "$GATEWAY_DIR"
"$JAVA" -jar "$GATEWAY_JAR" --spring.profiles.active="$SPRING_PROFILE" > /dev/null 2>&1 &
GATEWAY_PID=$!
sleep 3

if kill -0 "$GATEWAY_PID" 2>/dev/null; then
  log_ok "Gateway redeployed (PID: $GATEWAY_PID) on port $GATEWAY_PORT"
else
  log_error "Gateway failed to start after redeploy."
  exit 1
fi
