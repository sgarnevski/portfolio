#!/usr/bin/env bash
# Build the frontend production bundle
set -e
SCRIPT_DIR="$(cd "$(dirname "$0")" && pwd)"
source "$SCRIPT_DIR/config.sh"

log_info "Building frontend..."
cd "$FRONTEND_DIR"
"$NPM" run build --silent
log_ok "Frontend built: $FRONTEND_DIR/dist/"
