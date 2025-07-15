#!/bin/bash

echo "Building the application..."
mvn clean package -DskipTests

echo "Starting all services..."
docker-compose up --build

echo "All services are running!"
echo "Application: http://localhost:8080"
echo "Fraud API Mock: http://localhost:8081"
echo "PostgreSQL: localhost:5432"
echo "Kafka: localhost:9092"
