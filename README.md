individual-calculations-api
========================

[![Apache-2.0 license](http://img.shields.io/badge/license-Apache-blue.svg)](http://www.apache.org/licenses/LICENSE-2.0.html)

The Individual Calculations API allows a developer to:

- trigger a self-assessment tax calculation
- list all their self-assessment tax calculations for a tax year
- retrieve their self-assessment tax calculation result using multiple endpoints

## Requirements
- Scala 2.12.x
- Java 8
- sbt 1.3.13
- [Service Manager](https://github.com/hmrc/service-manager)

## Development Setup
Run the microservice from the console using: `sbt run` (starts on port 9767 by default)

Start the service manager profile: `sm --start MTDFB_CALC`
 
## Run Tests
Run unit tests: `sbt test`

Run integration tests: `sbt it:test`

## To view the RAML
To view documentation locally, ensure the Individual Calculations API is running, and run api-documentation-frontend:

```
./run_local_with_dependencies.sh
```

Then go to http://localhost:9680/api-documentation/docs/preview and enter the full URL path to the RAML file with the appropriate port and version:

```
http://localhost:9767/api/conf/2.0/application.raml
```

## Changelog

You can see our changelog [here](https://github.com/hmrc/income-tax-mtd-changelog/wiki)

## Support and Reporting Issues

You can create a GitHub issue [here](https://github.com/hmrc/income-tax-mtd-changelog/issues)

## API Reference / Documentation 
Available on the [HMRC Developer Hub](https://developer.service.hmrc.gov.uk/api-documentation/docs/api/service/individual-calculations-api/2.0)

## License
This code is open source software licensed under the [Apache 2.0 License]("http://www.apache.org/licenses/LICENSE-2.0.html")
