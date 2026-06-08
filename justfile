# fresh build
build:
    mvn clean compile

# run the test suite and generate coverage
test:
    mvn clean test
    mvn jacoco:report
    @echo "coverage report file://$(pwd)/target/site/jacoco/index.html"
