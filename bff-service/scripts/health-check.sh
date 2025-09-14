#!/bin/bash

# Health check script
SERVICE_URL="http://localhost:8080/api/health"
MAX_RETRIES=3
RETRY_DELAY=5

for i in $(seq 1 $MAX_RETRIES); do
    if curl -f -s $SERVICE_URL > /dev/null; then
        echo "Service is healthy"
        exit 0
    else
        echo "Health check failed (attempt $i/$MAX_RETRIES)"
        if [ $i -lt $MAX_RETRIES ]; then
            sleep $RETRY_DELAY
        fi
    fi
done

echo "Service is unhealthy after $MAX_RETRIES attempts"
exit 1