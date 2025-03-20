# Define environment variables
DB_NAME=carhov
DB_USER=myuser
DB_PASSWORD=mypassword
DB_HOST=localhost
CONTAINER_NAME=my-postgres
POSTGRES_PORT=5432

# Start the PostgresSQL container
start-db:
	docker run --name $(CONTAINER_NAME) \
		-e POSTGRES_USER=$(DB_USER) \
		-e POSTGRES_PASSWORD=$(DB_PASSWORD) \
		-e POSTGRES_DB=$(DB_NAME) \
		-p $(POSTGRES_PORT):5432 \
		-d postgres:latest
	@echo "PostgresSQL container '$(CONTAINER_NAME)' started."

stop-db:
	@docker stop $(CONTAINER_NAME) 2>/dev/null || echo "PostgresSQL container '$(CONTAINER_NAME)' is not running."
	@echo "PostgresSQL container '$(CONTAINER_NAME)' stopped."

remove-db: stop-db
	@docker rm $(CONTAINER_NAME) 2>/dev/null || echo "PostgresSQL container '$(CONTAINER_NAME)' does not exist."

restart-db: stop-db start-db
	@echo "PostgresSQL container '$(CONTAINER_NAME)' restarted."
