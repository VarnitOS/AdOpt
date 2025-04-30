#!/bin/bash

# Start the backend server
echo "Starting the backend server..."
cd backend && mvn spring-boot:run &
BACKEND_PID=$!

# Wait for the backend to start
echo "Waiting for backend to start..."
sleep 10

# Start the frontend development server
echo "Starting the frontend development server..."
cd ../frontend && npm run dev &
FRONTEND_PID=$!

# Handle shutdown gracefully
function cleanup {
  echo "Shutting down servers..."
  kill $FRONTEND_PID
  kill $BACKEND_PID
  exit 0
}

# Register the cleanup function for SIGINT and SIGTERM
trap cleanup SIGINT SIGTERM

# Keep the script running
echo "Servers are running. Press Ctrl+C to stop."
wait $BACKEND_PID $FRONTEND_PID 