#!/bin/sh
set -eu

PORT="${PORT:-${SERVER_PORT:-8080}}"
READY_MARKER="/tmp/app-ready"

if [ ! -f "$READY_MARKER" ]; then
    if wget -q -O /dev/null "http://localhost:${PORT}/actuator/health/readiness"; then
        touch "$READY_MARKER"
    else
        exit 1
    fi
fi

exec wget -q -O /dev/null "http://localhost:${PORT}/actuator/health/liveness"
