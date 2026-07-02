Individual Calculations API
========================

[![Apache-2.0 license](http://img.shields.io/badge/license-Apache-blue.svg)](http://www.apache.org/licenses/LICENSE-2.0.html)

The Individual Calculations API allows a developer to:

- trigger a self-assessment tax calculation
- list all their self-assessment tax calculations for a tax year
- retrieve their self-assessment tax calculation result using multiple endpoints

## Requirements

- Scala 3.5.x
- Java 21
- sbt 1.10.x
- [Service Manager V2](https://github.com/hmrc/sm2)

## Development Setup

Run the microservice from the console using: `sbt run` (starts on port 9767 by default)

Start the service manager profile: 
```bash
sm2 --start MTDFB_CALC
```

## Run Tests

Run unit tests: `sbt test`

Run integration tests: `sbt it/test`

## View OpenAPI Specification (OAS) documentation

To view the OpenAPI documentation locally, ensure the API is running.

Start the `api-documentation-frontend` and `api-definition` services using the Service Manager profile:

```bash
sm2 -start DEVHUB_PREVIEW_OPENAPI
```
Then navigate to the preview page:

```text
http://localhost:9680/api-documentation/docs/openapi/preview
```
Enter the specification URL using the appropriate port and API version:

```text
http://localhost:9767/api/conf/8.0/application.yaml
```

## Changelog

You can see our changelog [here](https://github.com/hmrc/income-tax-mtd-changelog)

## Support and Reporting Issues

You can create a GitHub issue [here](https://github.com/hmrc/income-tax-mtd-changelog/issues)

## API Reference / Documentation

Available on
the [HMRC Developer Hub](https://developer.service.hmrc.gov.uk/api-documentation/docs/api/service/individual-calculations-api)

## License

This code is open source software licensed under
the [Apache 2.0 License](http://www.apache.org/licenses/LICENSE-2.0.html)
