
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
