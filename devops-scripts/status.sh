#!/usr/bin/env bash
# Show status of all services (which Java processes are running, which ports are listening)
SCRIPT_DIR="$(cd "$(dirname "$0")" && pwd)"
source "$SCRIPT_DIR/config.sh"

echo ""
log_info "=== Java Processes ==="
"$JPS" -l 2>/dev/null | grep -v "jps" || log_warn "No Java processes found"

echo ""
log_info "=== Port Status ==="
for PORT_NAME in "Backend:$BACKEND_PORT" "Auth-Service:$AUTH_PORT" "Gateway:$GATEWAY_PORT" "Frontend:$FRONTEND_PORT"; do
  NAME="${PORT_NAME%%:*}"
  PORT="${PORT_NAME##*:}"
  if netstat -ano 2>/dev/null | grep "LISTENING" | grep -qE ":${PORT}[[:space:]]"; then
    PID=$(netstat -ano 2>/dev/null | grep "LISTENING" | grep -E ":${PORT}[[:space:]]" | awk '{print $NF}' | head -1)
    log_ok "$NAME (port $PORT) — RUNNING (PID: $PID)"
  else
    log_warn "$NAME (port $PORT) — NOT RUNNING"
  fi
done
echo ""
