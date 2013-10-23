
<#assign totalCount = testOutcomes.totalTests.total >
<#assign successCount = testOutcomes.totalTests.withResult("success") >
<#assign pendingCount = testOutcomes.totalTests.withResult("pending") >
<#assign failureCount = testOutcomes.totalTests.withResult("failure") >
<#assign errorCount = testOutcomes.totalTests.withResult("error") >
<#assign failureOrErrorCount = testOutcomes.totalTests.withFailureOrError() >

<#assign autoTotalCount = testOutcomes.count("AUTOMATED").total >
<#assign autoSuccessCount = testOutcomes.count("AUTOMATED").withResult("success") >
<#assign autoPendingCount = testOutcomes.count("AUTOMATED").withResult("pending") >
<#assign autoFailureOrErrorCount = testOutcomes.count("AUTOMATED").withFailureOrError() >

<#assign manualTotalCount = testOutcomes.count("MANUAL").total >
<#assign manualSuccessCount = testOutcomes.count("MANUAL").withResult("success") >
<#assign manualPendingCount = testOutcomes.count("MANUAL").withResult("pending") >
<#assign manualFailureOrErrorCount = testOutcomes.count("MANUAL").withFailureOrError() >

<div>
    <h4>Test Result Summary</h4>
    <table class="summary-table">
        <head>
            <tr>
                <th>Test Type</th>
                <th>Total</th>
                <th>Pass&nbsp;<i class="icon-check"/> </th>
                <th>Fail&nbsp;<i class="icon-thumbs-down"/></th>
                <th>Pending&nbsp;<i class="icon-ban-circle"/></th>
            </tr>
        </head>
        <body>
        <tr>
            <td class="summary-leading-column">Automated</td>
            <td>${autoTotalCount}</td>
            <td>${autoSuccessCount}</td>
            <td>${autoFailureOrErrorCount}</td>
            <td>${autoPendingCount}</td>
        </tr>
        <tr>
            <td class="summary-leading-column">Manual</td>
            <td>${manualTotalCount}</td>
            <td>${manualSuccessCount}</td>
            <td>${manualFailureOrErrorCount}</td>
            <td>${manualPendingCount}</td>
        </tr>
        <tr>
            <td class="summary-leading-column">Total</td>
            <td>${totalCount}</td>
            <td>${successCount}</td>
            <td>${failureOrErrorCount}</td>
            <td>${pendingCount}</td>
        </tr>
        </body>
    </table>
</div>