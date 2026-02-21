#!/usr/bin/env bash
# Start the backend service (builds if JAR missing, runs in background)
set -e
SCRIPT_DIR="$(cd "$(dirname "$0")" && pwd)"
source "$SCRIPT_DIR/config.sh"

if [ ! -f "$BACKEND_JAR" ]; then
  log_warn "Backend JAR not found, building first..."
  "$SCRIPT_DIR/build-backend.sh"
fi

# Check if already running
if "$JPS" -l 2>/dev/null | grep -q "portfolio-rebalancer-backend"; then
  log_warn "Backend is already running. Stop it first or use redeploy."
  exit 1
fi

log_info "Starting backend (profile: $SPRING_PROFILE)..."
cd "$BACKEND_DIR"
"$JAVA" -jar "$BACKEND_JAR" --spring.profiles.active="$SPRING_PROFILE" > /dev/null 2>&1 &
BACKEND_PID=$!
sleep 2

if kill -0 "$BACKEND_PID" 2>/dev/null; then
  log_ok "Backend started (PID: $BACKEND_PID) on port $BACKEND_PORT"
else
  log_error "Backend failed to start. Check logs."
  exit 1
fi
