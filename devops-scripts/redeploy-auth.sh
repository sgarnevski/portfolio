#!/usr/bin/env bash
# Redeploy auth-service: stop -> build -> start
set -e
SCRIPT_DIR="$(cd "$(dirname "$0")" && pwd)"
source "$SCRIPT_DIR/config.sh"

log_info "=== Redeploying Auth-Service ==="

# Stop
PIDS=$("$JPS" -l 2>/dev/null | grep "auth-service" | awk '{print $1}')
if [ -n "$PIDS" ]; then
  for PID in $PIDS; do
    log_info "Stopping auth-service (PID: $PID)..."
    "$TASKKILL" //PID "$PID" //F > /dev/null 2>&1
  done
  sleep 2
  log_ok "Auth-service stopped."
else
  log_warn "Auth-service was not running."
fi

# Build
"$SCRIPT_DIR/build-auth.sh"

# Start
log_info "Starting auth-service (profile: $SPRING_PROFILE)..."
cd "$AUTH_DIR"
"$JAVA" -jar "$AUTH_JAR" --spring.profiles.active="$SPRING_PROFILE" > /dev/null 2>&1 &
AUTH_PID=$!
sleep 3

if kill -0 "$AUTH_PID" 2>/dev/null; then
  log_ok "Auth-service redeployed (PID: $AUTH_PID) on port $AUTH_PORT"
else
  log_error "Auth-service failed to start after redeploy."
  exit 1
fi
