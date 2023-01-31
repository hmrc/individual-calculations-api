This API enables you to:

* trigger a customer’s self assessment tax calculation
  * for version 3.0, a query parameter is used to control whether the calculation is to be used for a final declaration
  * for prior versions, a separate `Intent to Crystallise` endpoint is used to trigger a calculation to be used as a final declaration
* list a customer’s self assessment tax calculations for a tax year
* retrieve a customer’s self assessment tax calculation result
  * for version 3.0, a single endpoint retrieves the entire calculation
  * for prior versions, the calculation is retrieved using multiple endpoints
* submit a self assessment final declaration for a tax year

In versions prior to version 3.0, the term ‘crystallise’ was used to mean submit a final declaration, and the term ‘intent to crystallise’ 
was used to mean trigger a final declaration self-assessment tax calculation.

For information on how to connect to this API [see the Income Tax MTD end-to-end service guide](https://developer.service.hmrc.gov.uk/guides/income-tax-mtd-end-to-end-service-guide/).
