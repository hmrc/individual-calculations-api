The individual calculations API allows a self-assessment taxpayer via software, to:
- trigger a self-assessment tax calculation
- list all their self-assessment tax calculations for a Tax year
- retrieve their self-assessment tax calculation result using multiple endpoints

A calculation result is linked to a Calculation ID. The Calculation ID will be returned when triggering a self-assessment tax calculation or it can be retrieved by listing all self-assessment tax calculations for a Tax year.

A Calculation ID will not always have a calculation result. Errors in previously submitted income data can prevent a calculation from being performed. Calculation errors that are present can be returned to the taxpayer via the Retrieve self-assessment tax calculation messages endpoint.