#!/usr/bin/env bash
# Start the market-data-gateway service (builds if JAR missing, runs in background)
set -e
SCRIPT_DIR="$(cd "$(dirname "$0")" && pwd)"
source "$SCRIPT_DIR/config.sh"

if [ ! -f "$GATEWAY_JAR" ]; then
  log_warn "Gateway JAR not found, building first..."
  "$SCRIPT_DIR/build-gateway.sh"
fi

# Check if already running
if "$JPS" -l 2>/dev/null | grep -q "market-data-gateway"; then
  log_warn "Gateway is already running. Stop it first or use redeploy."
  exit 1
fi

log_info "Starting market-data-gateway (profile: $SPRING_PROFILE)..."
cd "$GATEWAY_DIR"
"$JAVA" -jar "$GATEWAY_JAR" --spring.profiles.active="$SPRING_PROFILE" > /dev/null 2>&1 &
GATEWAY_PID=$!
sleep 2

if kill -0 "$GATEWAY_PID" 2>/dev/null; then
  log_ok "Gateway started (PID: $GATEWAY_PID) on port $GATEWAY_PORT"
else
  log_error "Gateway failed to start. Check logs."
  exit 1
fi
