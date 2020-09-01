default: build

ARTIFACT_NAME = nhood-engine

.PHONY: clean
clean:
	@echo "Cleaning:"
	mvn clean
	@echo "...done"

.PHONY: build
build:
	@echo "Building maven artifacts:"
	mvn clean install
	@echo "...done"

.PHONY: build-ci
build-ci:
	@test $(GITHUB_USERNAME) || ( echo "GITHUB_USERNAME not set" & exit 1 )
	@test $(GITHUB_TOKEN) || ( echo "GITHUB_TOKEN not set" & exit 2 )
	@echo "Building maven artifacts [CI]:"
	mvn -s .mvn.settings.xml clean install
	@echo "...done"

.PHONY: run-core-performance-tests
run-core-performance-tests:
	@echo "Running core performance tests:"
	cd nhood-engine-core-performance && \
	    java -Xmx8G -jar target/nhood-engine-core-performance.jar | tee performance.results
	@echo "...done"

.PHONY: run-matrix-performance-tests
run-matrix-performance-tests:
	@echo "Running matrix performance tests:"
	cd nhood-engine-matrix-performance && \
	    java -Xmx8G -jar target/nhood-engine-matrix-performance.jar | tee performance.results
	@echo "...done"

.PHONY: deploy
deploy:
	@echo "Deploying maven artifacts:"
	mvn deploy -DskipTests
	@echo "...done"

.PHONY: deploy-ci
deploy-ci:
	@test $(GITHUB_USERNAME) || ( echo "GITHUB_USERNAME not set" & exit 1 )
	@test $(GITHUB_TOKEN) || ( echo "GITHUB_TOKEN not set" & exit 2 )
	@echo "Deploying maven artifacts [CI]:"
	mvn -s .mvn.settings.xml deploy -DskipTests
	@echo "...done"

.PHONY: release-ci
release-ci:
	@test $(GITHUB_USERNAME) || ( echo "GITHUB_USERNAME not set" & exit 1 )
	@test $(GITHUB_TOKEN) || ( echo "GITHUB_TOKEN not set" & exit 2 )
	@test $(GITHUB_EMAIL) || ( echo "GITHUB_EMAIL not set" & exit 3 )
	@echo "Releasing maven artifacts [CI]:"
	git config --global user.email ${GITHUB_EMAIL} && \
	git config --global user.name ${GITHUB_USERNAME} && \
	mvn -s .mvn.settings.xml -B release:prepare -Darguments="-DskipTests"
	@echo "...done"

.PHONY: trigger-circle-ci-maven-release
trigger-circle-ci-maven-release:
	@test $(ARTIFACT_NAME) || ( echo "ARTIFACT_NAME not set" & exit 1 )
	@test $(CIRCLE_CI_USER_TOKEN) || ( echo "CIRCLE_CI_USER_TOKEN not set" & exit 2 )
	@echo "Triggering docker release:"
	curl -u ${CIRCLE_CI_USER_TOKEN}: \
		-d build_parameters[CIRCLE_JOB]=release \
    		https://circleci.com/api/v1.1/project/github/nhood-org/${ARTIFACT_NAME}/tree/master
	@echo "...done"
