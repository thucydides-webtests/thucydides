
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

<#if (autoTotalCount > 0)>
    <#assign autoPercentageSuccessCount = autoSuccessCount / autoTotalCount >
    <#assign autoPercentagePendingCount = autoPendingCount / autoTotalCount  >
    <#assign autoPercentageFailureOrErrorCount = autoPendingCount / autoTotalCount  >
<#else>
    <#assign autoPercentageSuccessCount = 0.0 >
    <#assign autoPercentagePendingCount = 0.0 >
    <#assign autoPercentageFailureOrErrorCount = 0.0 >
</#if>

<#assign manualTotalCount = testOutcomes.count("MANUAL").total >
<#assign manualSuccessCount = testOutcomes.count("MANUAL").withResult("success") >
<#assign manualPendingCount = testOutcomes.count("MANUAL").withResult("pending") >
<#assign manualFailureOrErrorCount = testOutcomes.count("MANUAL").withFailureOrError() >

<#if (manualTotalCount > 0)>
    <#assign manualPercentageSuccessCount = manualSuccessCount / manualTotalCount >
    <#assign manualPercentagePendingCount = manualPendingCount / manualTotalCount  >
    <#assign manualPercentageFailureOrErrorCount = manualPendingCount / manualTotalCount  >
<#else>
    <#assign manualPercentageSuccessCount = 0.0 >
    <#assign manualPercentagePendingCount = 0.0 >
    <#assign manualPercentageFailureOrErrorCount = 0.0 >
</#if>

<#if (totalCount > 0)>
    <#assign percentageSuccessCount = successCount / totalCount >
    <#assign percentagePendingCount = pendingCount / totalCount  >
    <#assign percentageFailureOrErrorCount = failureCount / totalCount  >
<#else>
    <#assign percentageSuccessCount = 0.0 >
    <#assign percentagePendingCount = 0.0 >
    <#assign percentageFailureOrErrorCount = 0.0 >
</#if>

<div>
    <h4>Test Result Summary</h4>
    <table class="summary-table">
        <head>
            <tr>
                <th>Test Type</th>
                <th>Total</th>
                <th>Pass&nbsp;<i class="icon-check"/> </th>
                <th>% Pass</th>
                <th>Fail&nbsp;<i class="icon-thumbs-down"/></th>
                <th>% Pass</th>
                <th>Pending&nbsp;<i class="icon-ban-circle"/></th>
                <th>% Pending</th>
            </tr>
        </head>
        <body>
        <tr>
            <td class="summary-leading-column">Automated</td>
            <td>${autoTotalCount}</td>
            <td>${autoSuccessCount}</td>
            <td>${autoPercentageSuccessCount?string.percent}</td>
            <td>${autoFailureOrErrorCount}</td>
            <td>${autoPercentageFailureOrErrorCount?string.percent}</td>
            <td>${autoPendingCount}</td>
            <td>${autoPercentagePendingCount?string.percent}</td>
        </tr>
        <tr>
            <td class="summary-leading-column">Manual</td>
            <td>${manualTotalCount}</td>
            <td>${manualSuccessCount}</td>
            <td>${manualPercentageSuccessCount?string.percent}</td>
            <td>${manualFailureOrErrorCount}</td>
            <td>${manualPercentageFailureOrErrorCount?string.percent}</td>
            <td>${manualPendingCount}</td>
            <td>${manualPercentagePendingCount?string.percent}</td>
        </tr>
        <tr>
            <td class="summary-leading-column">Total</td>
            <td>${totalCount}</td>
            <td>${successCount}</td>
            <td>${percentageSuccessCount?string.percent}</td>
            <td>${failureOrErrorCount}</td>
            <td>${percentageFailureOrErrorCount?string.percent}</td>
            <td>${pendingCount}</td>
            <td>${percentagePendingCount?string.percent}</td>
        </tr>
        </body>
    </table>
</div>