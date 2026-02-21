#!/usr/bin/env bash
# ============================================================
# Shared configuration for all devops scripts
# Edit paths here if your environment changes
# ============================================================

PROJECT_ROOT="$(cd "$(dirname "${BASH_SOURCE[0]}")/.." && pwd)"
BACKEND_DIR="$PROJECT_ROOT/backend"
AUTH_DIR="$PROJECT_ROOT/auth-service"
GATEWAY_DIR="$PROJECT_ROOT/market-data-gateway"
FRONTEND_DIR="$PROJECT_ROOT/frontend"

# Tools
MAVEN="C:/opt/maven/apache-maven-3.9.12/bin/mvn"
JAVA="C:/Program Files/Common Files/Oracle/Java/javapath/java.exe"
JPS="C:/Program Files/Java/jdk-25.0.2/bin/jps.exe"
NODE="C:/Program Files/nodejs/node.exe"
NPM="C:/Program Files/nodejs/npm.cmd"
TASKKILL="C:/Windows/System32/taskkill.exe"

# JAR files
BACKEND_JAR="$BACKEND_DIR/target/portfolio-rebalancer-backend-0.0.1-SNAPSHOT.jar"
AUTH_JAR="$AUTH_DIR/target/auth-service-0.0.1-SNAPSHOT.jar"
GATEWAY_JAR="$GATEWAY_DIR/target/market-data-gateway-0.0.1-SNAPSHOT.jar"

# Ports
BACKEND_PORT=8080
AUTH_PORT=8090
GATEWAY_PORT=8060
FRONTEND_PORT=3000

# Spring profiles
SPRING_PROFILE="${SPRING_PROFILE:-dev}"

# Colors
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

log_info()  { echo -e "${BLUE}[INFO]${NC}  $1"; }
log_ok()    { echo -e "${GREEN}[OK]${NC}    $1"; }
log_warn()  { echo -e "${YELLOW}[WARN]${NC}  $1"; }
log_error() { echo -e "${RED}[ERROR]${NC} $1"; }
