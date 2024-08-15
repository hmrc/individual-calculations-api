# Shared Code Changelog

This file contains a log of changes made to the MTD shared code
(that is, the 'shared' folder that is propagated across to each MTD API).

For the Shared Code update steps, see: https://confluence.tools.tax.service.gov.uk/display/MTE/Shared+Code

Place new items at the top, and auto-format the file...


## July 10 2024: Updates from individual-calculations-api

Added the following functions from individual-calculations-api:
- RequestHandler.withResponseModifier 
- JsonErrorValidators.testOptionalFields
- JsonErrorValidators.testOptionalJsonFields
- JsonErrorValidators.testAllOptionalJsonFieldsExcept
- JsonErrorValidators.testMandatoryJsonFields
- JsonErrorValidators.testAllMandatoryJsonFieldsExcept

## July 9 2024: Added FlattenedGenericAuditDetail

Added FlattenedGenericAuditDetail from self-assessment-individual-details-api.


## July 1 2024:  Additional TY resolvers

Added ResolveTaxYearMinMax & ResolveTaxYearMaximum, from property-business-api.

## June 24 2024:  Updates from self-employment-business-api

Minor changes and additions to BaseDownstreamConnector, RequestHandlerBuilder,
TaxYear and CommonMtdErrors.

## June 20 2024:  Increased code coverage

Increased the coverage so that introducing the shared code into other APIs won't
reduce their coverage % quite so much.

## June 14 2024:  Shared test code

Updated the shared test code to work "as-is" for all APIs rather than just BSAS;
e.g. removed assumptions of which API versions are available, enabled etc.

## June 10 2024:  New changelog

Added the shared-code-changelog.md file.
