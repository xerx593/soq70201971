# soq70201971
Answer for https://stackoverflow.com/q/70201971/592355

## Prerequisites
- jdk (configured 17)
- maven

## Clean Build and Test

    mvn clean install

## Integration Test    

    mvn failsafe:integration-test

## Run

    mvn spring-boot:run
    
## Use
Navigate to:
- http://localhost:8080/
- http://localhost:8080/foo
- http://localhost:8080/bar
... inspecting `Content-Security-Policy` header;)
