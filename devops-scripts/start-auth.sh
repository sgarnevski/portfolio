#!/usr/bin/env bash
# Start the auth-service (builds if JAR missing, runs in background)
set -e
SCRIPT_DIR="$(cd "$(dirname "$0")" && pwd)"
source "$SCRIPT_DIR/config.sh"

if [ ! -f "$AUTH_JAR" ]; then
  log_warn "Auth-service JAR not found, building first..."
  "$SCRIPT_DIR/build-auth.sh"
fi

# Check if already running
if "$JPS" -l 2>/dev/null | grep -q "auth-service"; then
  log_warn "Auth-service is already running. Stop it first or use redeploy."
  exit 1
fi

log_info "Starting auth-service (profile: $SPRING_PROFILE)..."
cd "$AUTH_DIR"
"$JAVA" -jar "$AUTH_JAR" --spring.profiles.active="$SPRING_PROFILE" > /dev/null 2>&1 &
AUTH_PID=$!
sleep 2

if kill -0 "$AUTH_PID" 2>/dev/null; then
  log_ok "Auth-service started (PID: $AUTH_PID) on port $AUTH_PORT"
else
  log_error "Auth-service failed to start. Check logs."
  exit 1
fi
