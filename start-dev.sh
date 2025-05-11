#!/bin/bash

# Set colors for better visual output
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
NC='\033[0m' # No Color

echo -e "${GREEN}=== AdOpt Development Environment ===${NC}"
echo -e "${YELLOW}Starting services...${NC}"

# Check if Docker is running
if ! docker info > /dev/null 2>&1; then
  echo -e "${YELLOW}Docker is not running. Starting Docker...${NC}"
  open -a Docker
  
  # Wait for Docker to start
  echo -e "${YELLOW}Waiting for Docker to start...${NC}"
  until docker info > /dev/null 2>&1; do
    sleep 1
  done
fi

# Check if docker-compose exists 
if command -v docker-compose &> /dev/null; then
  DOCKER_COMPOSE="docker-compose"
elif command -v docker compose &> /dev/null; then
  DOCKER_COMPOSE="docker compose"
else
  echo -e "${YELLOW}Docker Compose not found. Using 'docker-compose'...${NC}"
  DOCKER_COMPOSE="docker-compose"
fi

# Build and start containers
echo -e "${YELLOW}Building containers...${NC}"
$DOCKER_COMPOSE build

echo -e "${YELLOW}Starting containers...${NC}"
$DOCKER_COMPOSE up -d

echo -e "${GREEN}Services started!${NC}"
echo -e "${GREEN}Frontend: http://localhost:3000${NC}"
echo -e "${GREEN}Backend: http://localhost:8080${NC}"
echo -e "${GREEN}Database: localhost:5432${NC}"
echo ""
echo -e "${YELLOW}Displaying logs, press Ctrl+C to stop viewing logs (services will continue running)${NC}"

# Show logs
$DOCKER_COMPOSE logs -f 