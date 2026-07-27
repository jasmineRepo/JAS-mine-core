# fresh build
build:
    mvn clean compile

# run the test suite and generate coverage
test:
    mvn clean test
    mvn jacoco:report
    @echo "coverage report file://$(pwd)/target/site/jacoco/index.html"

# build documentation
doc:
    mvn clean javadoc:javadoc
    @echo "documentation at file://$(pwd)/target/reports/apidocs/index.html"
