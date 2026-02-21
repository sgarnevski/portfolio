#!/usr/bin/env bash
# Full redeploy: stop all -> build all -> start all
set -e
SCRIPT_DIR="$(cd "$(dirname "$0")" && pwd)"
source "$SCRIPT_DIR/config.sh"

log_info "========================================="
log_info "  Full Redeploy — All Services"
log_info "========================================="
echo ""

"$SCRIPT_DIR/redeploy-auth.sh"
echo ""
"$SCRIPT_DIR/redeploy-gateway.sh"
echo ""
"$SCRIPT_DIR/redeploy-backend.sh"
echo ""
"$SCRIPT_DIR/redeploy-frontend.sh"
echo ""

log_ok "========================================="
log_ok "  All services redeployed successfully"
log_ok "========================================="
"$SCRIPT_DIR/status.sh"
