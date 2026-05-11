# fresh build
[working-directory: 'microsim-core']
build:
    mvn clean compile

# run the test suite and generate coverage
[working-directory: 'microsim-core']
test:
    mvn clean test
    mvn jacoco:report
    @echo "coverage report file://$(pwd)/target/site/jacoco/index.html"
