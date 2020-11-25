
.PHONY: frontend clean

clean:
	rm -rf backend/src/main/resources/frontend; \
	rm -rf frontend/build

frontend: clean
	cd frontend; \
	npm i; \
	npm run-script build

add-frontend-to-backend: frontend
	cp -r frontend/build backend/src/main/resources/frontend

backend:
	cd backend; \
	mvn clean verify

start: backend
	java -jar backend/target/ultimate-store-backend-0.1.0-SNAPSHOT-jar-with-dependencies.jar
