<p>Scenario simulations using Gov-Test-Scenario headers is only available in the sandbox environment.</p>
<table>
    <thead>
        <tr>
            <th>Header Value (Gov-Test-Scenario)</th>
            <th>Scenario</th>
        </tr>
    </thead>
    <tbody> 
        <tr>
            <td><p>N/A - DEFAULT</p></td>
            <td><p>Simulates returning a tax calculation with info and warning messages.</p></td>
        </tr>
        <tr>
            <td><p>NOT_FOUND</p></td>
            <td><p>Simulates the scenario where no data can be found for this calculations ID.</p></td>
        </tr> 
        <tr>
            <td><p>NO_MESSAGES_PRESENT</p></td>
            <td><p>Simulates the scenario where a tax calculation is found, but no messages exist. This error can also be returned by the use of filters.</p></td>
        <tr>
        <tr>
            <td><p>MESSAGES_INFO</p></td>
            <td><p>Simulates returning a tax calculation with info messages.</p></td>
        <tr>        
        <tr>
            <td><p>MESSAGES_INFO_WARNINGS</p></td>
            <td><p>Simulates returning a tax calculation with info and warning messages.</p></td>
        <tr>       
        <tr>
            <td><p>MESSAGES_WARNINGS</p></td>
            <td><p>Simulates returning a tax calculation with warning messages.</p></td>
        <tr>
        <tr>
            <td><p>MESSAGES_WARNINGS_ERRORS</p></td>
            <td><p>Simulates returning a tax calculation with warning and error messages.</p></td>
        <tr>
        <tr>
            <td><p>MESSAGES_ERRORS</p></td>
            <td><p>Simulates returning a tax calculation with error messages.</p></td>
        <tr>
    </tbody>
</table>