#!/usr/bin/env bash
# Stop Java services (backend + auth-service) by finding their PIDs
SCRIPT_DIR="$(cd "$(dirname "$0")" && pwd)"
source "$SCRIPT_DIR/config.sh"

stop_java_process() {
  local name="$1"
  local jar_pattern="$2"

  PIDS=$("$JPS" -l 2>/dev/null | grep "$jar_pattern" | awk '{print $1}')
  if [ -z "$PIDS" ]; then
    log_warn "$name — not running"
    return 0
  fi

  for PID in $PIDS; do
    log_info "Stopping $name (PID: $PID)..."
    "$TASKKILL" //PID "$PID" //F > /dev/null 2>&1
    log_ok "$name stopped."
  done
}

stop_java_process "Backend" "portfolio-rebalancer-backend"
stop_java_process "Auth-Service" "auth-service"
stop_java_process "Gateway" "market-data-gateway"
