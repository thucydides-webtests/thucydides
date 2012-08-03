<!DOCTYPE html>
<html>
<#assign pageTitle = inflection.of(requirements.type).inPluralForm().asATitle() >
<#assign requirementTypeTitle = inflection.of(requirements.type).asATitle() >
<head>
    <meta charset="UTF-8" />
    <title>${pageTitle}</title>
    <link rel="shortcut icon" href="favicon.ico">
    <link rel="stylesheet" href="css/core.css"/>
    <link rel="stylesheet" type="text/css" href="jqplot/jquery.jqplot.min.css"/>
    <style type="text/css">a:link {
        text-decoration: none;
    }

    a:visited {
        text-decoration: none;
    }

    a:hover {
        text-decoration: none;
    }

    a:active {
        text-decoration: none;
    }
    </style>


    <!--[if IE]>
    <script language="javascript" type="text/javascript" src="jit/Extras/excanvas.js"></script><![endif]-->

    <script type="text/javascript" src="scripts/jquery.js"></script>
    <script type="text/javascript" src="datatables/media/js/jquery.dataTables.min.js"></script>
    <#--<script type="text/javascript" src="jqplot/jquery.jqplot.min.js"></script>-->
    <#--<script type="text/javascript" src="jqplot/plugins/jqplot.pieRenderer.min.js"></script>-->

    <link type="text/css" href="jqueryui/css/start/jquery-ui-1.8.18.custom.css" rel="Stylesheet" />
    <script type="text/javascript" src="jqueryui/js/jquery-ui-1.8.18.custom.min.js"></script>

    <style type="text/css" media="screen">
        .dataTables_info { padding-top: 0; }
        .dataTables_paginate { padding-top: 0; }
        .css_right { float: right; }
    </style>

    <script class="code" type="text/javascript">$(document).ready(function () {
        // Results table
        $('#req-results-table').dataTable( {
            "aaSorting": [[ 2, "asc" ]],
            "bJQueryUI": true
        } );
        $('#test-results-table').dataTable( {
            "aaSorting": [[ 2, "asc" ]],
            "bJQueryUI": true
        } );
    })
    ;
    </script>
</head>

<body>
<div id="topheader">
    <div id="topbanner">
        <div id="logo"><a href="index.html"><img src="images/logo.jpg" border="0"/></a></div>
    </div>
</div>


<div class="middlecontent">
    <div id="contenttop">
        <div class="middlebg">
            <span class="bluetext"><a href="index.html" class="bluetext">Home</a> > ${pageTitle} </span>
        </div>
        <div class="rightbg"></div>
    </div>

    <div class="clr"></div>

    <!--/* starts second table*/-->
    <div class="menu">
        <ul>
            <li><a href="index.html">Test Results</a></li>
            <li><a href="requirements.html" class="current">Requirements</a></li>
            <#foreach tagType in allTestOutcomes.tagTypes>
                <#assign tagReport = reportName.forTagType(tagType) >
                <#assign tagTypeTitle = inflection.of(tagType).inPluralForm().asATitle() >
                <li><a href="${tagReport}">${tagTypeTitle}</a></li>
            </#foreach>
            <li><a href="history.html">History</a></li>
        </ul>
        <br style="clear:left"/>
    </div>

    <div class="clr"></div>
    <div id="beforetable"></div>
    <div id="results-dashboard">
        <div class="middlb">
            <div class="table">
                <#if (requirements.parentRequirement.isPresent())>
                <div>
                    <#assign parentTitle = inflection.of(requirements.parentRequirement.get().name).asATitle() >
                    <#assign parentType = inflection.of(requirements.parentRequirement.get().type).asATitle() >
                    <#if (requirements.parentRequirement.get().cardNumber?has_content) >
                        <#assign issueNumber = "[" + formatter.addLinks(requirements.parentRequirement.get().cardNumber) + "]" >
                    <#else>
                        <#assign issueNumber = "">
                    </#if>
                    <h2>${parentType}: ${issueNumber} ${parentTitle}</h2>
                    <div class="requirementNarrativeTitle">
                        ${requirements.parentRequirement.get().narrativeText}
                    </div>
                </div>
                </#if>

                <table class='overview'>
                    <tr>
                        <td width="375px" valign="top">
                            <div class="test-count-summary">
                                <span class="test-count-title">${testOutcomes.total} tests:</span>
                                <#assign successReport = reportName.withPrefix(testOutcomes.label).forTestResult("success") >
                                <#assign failureReport = reportName.withPrefix(testOutcomes.label).forTestResult("failure") >
                                <#assign pendingReport = reportName.withPrefix(testOutcomes.label).forTestResult("pending") >
                                <span class="test-count">
                                    ${testOutcomes.successCount}
                                    <#if (testOutcomes.successCount > 0)>
                                        <a href="${successReport}">passed</a>
                                    <#else>passed</#if>,
                                </span>
                                <span class="test-count">
                                ${testOutcomes.pendingCount}
                                <#if (testOutcomes.pendingCount > 0)>
                                    <a href="${pendingReport}">pending</a>
                                <#else>pending</#if>,
                                </span>
                                <span class="test-count">
                                    ${testOutcomes.failureCount}
                                    <#if (testOutcomes.failureCount > 0)>
                                        <a href="${failureReport}">failed</a>
                                    <#else>failed</#if>
                                </span>
                            </div>
                        </td>
                        <td width="25px">&nbsp;</td>
                        <td width="625px" valign="top"></td>
                    </tr>
                </table>

                <#--- Requirements -->
                <#if (requirements.requirementOutcomes?has_content)>
                    <h2>${pageTitle}</h2>
                    <table>
                     <tr>
                       <td>
                        <div id="req_list_tests" class="table">
                            <div class="test-results">
                                <table id="req-results-table">
                                    <thead>
                                        <tr>
                                            <th width="30" class="test-results-heading">&nbsp;</th>
                                            <th width="65" class="test-results-heading">ID</th>
                                            <th width="525" class="test-results-heading">${requirementTypeTitle}</th>
                                            <#if (requirements.childrenType?has_content) >
                                                <#assign childrenTitle = inflection.of(requirements.childrenType).inPluralForm().asATitle()>
                                                <th width="65" class="test-results-heading">${childrenTitle}</th>
                                            </#if>
                                            <th width="50px" class="test-results-heading">Tests</th>
                                            <th width="50px" class="test-results-heading">Pass</th>
                                            <th width="50px" class="test-results-heading">Fail</th>
                                            <th width="50px" class="test-results-heading">Pend</th>
                                            <th width="150px" class="test-results-heading">Coverage</th>
                                        </tr>
                                    </thead>
                                    <tbody>

                                    <#foreach requirementOutcome in requirements.requirementOutcomes>
                                        <#if requirementOutcome.testOutcomes.stepCount == 0 || requirementOutcome.testOutcomes.result == "PENDING" || requirementOutcome.testOutcomes.result == "IGNORED">
                                            <#assign status_icon = "traffic-orange.gif">
                                            <#assign status_rank = 0>
                                        <#elseif requirementOutcome.testOutcomes.result == "FAILURE">
                                            <#assign status_icon = "traffic-red.gif">
                                            <#assign status_rank = 1>
                                        <#elseif requirementOutcome.testOutcomes.result == "SUCCESS">
                                            <#assign status_icon = "traffic-green.gif">
                                            <#assign status_rank = 2>
                                        </#if>

                                        <tr class="test-${requirementOutcome.testOutcomes.result} requirementRow">
                                            <td class="requirementRowCell">
                                                <img src="images/${status_icon}" class="summary-icon"/>
                                                <span style="display:none">${status_rank}</span>
                                            </td>
                                            <td class="cardNumber requirementRowCell">${requirementOutcome.cardNumberWithLinks}</td>

                                            <#--<#if (requirementOutcome.requirement.children?has_content)>-->
                                                <#assign requirementReport = reportName.forRequirement(requirementOutcome.requirement) >
                                            <#--<#else>-->
                                                <#--<#assign requirementReport = "#" >-->
                                            <#--</#if>-->
                                            <td class="${requirementOutcome.testOutcomes.result}-text requirementRowCell">
                                                <span class="requirementName"><a href="${requirementReport}">${requirementOutcome.requirement.displayName}</a></span>
                                                <span class="requirementNarrative">${requirementOutcome.requirement.narrativeText}</span>
                                            </td>

                                            <#if (requirements.childrenType?has_content) >
                                                <td class="bluetext requirementRowCell">${requirementOutcome.requirement.childrenCount}</td>
                                            </#if>

                                            <td class="bluetext requirementRowCell">${requirementOutcome.testOutcomes.total}</td>
                                            <td class="greentext requirementRowCell">${requirementOutcome.testOutcomes.successCount}</td>
                                            <td class="redtext requirementRowCell">${requirementOutcome.testOutcomes.failureCount}</td>
                                            <td class="lightredtext requirementRowCell">${requirementOutcome.testOutcomes.pendingCount + requirementOutcome.testOutcomes.skipCount}</td>


                                            <td width="150px" class="lightgreentext requirementRowCell">
                                                <#assign redbar = (1-requirementOutcome.testOutcomes.percentagePendingStepCount)*120>
                                                <#assign greenbar = requirementOutcome.testOutcomes.percentagePassingStepCount*120>
                                                <#assign passing = requirementOutcome.testOutcomes.formatted.percentPassingCoverage>
                                                <#assign failing = requirementOutcome.testOutcomes.formatted.percentFailingCoverage>
                                                <#assign pending = requirementOutcome.testOutcomes.formatted.percentPendingCoverage>

                                                <#assign tests = inflection.of(requirementOutcome.testOutcomes.total).times("test") >
                                                <#assign pendingCaption = "${requirementOutcome.testOutcomes.pendingCount} out of ${requirementOutcome.testOutcomes.total} ${tests} pending (${pending})">
                                                <#assign passingCaption = "${requirementOutcome.testOutcomes.successCount} out of ${requirementOutcome.testOutcomes.total} ${tests} passing (${passing})">
                                                <#assign failingCaption = "${requirementOutcome.testOutcomes.failureCount} out of ${requirementOutcome.testOutcomes.total} ${tests} failing (${failing})">
                                                <#assign tests = inflection.of(requirementOutcome.testOutcomes.total).times('test') >

                                                <table>
                                                    <tr>
                                                        <td width="50px">${passing}</td>
                                                        <td width="10px">
                                                            <div class="percentagebar"
                                                                 title="${pendingCaption}"
                                                                 style="width: 120px;">
                                                                <div class="failingbar"
                                                                     style="width: ${redbar?string("0")}px;"
                                                                     title="${failingCaption}">
                                                                    <div class="passingbar"
                                                                         style="width: ${greenbar?string("0")}px;"
                                                                         title="${passingCaption}">
                                                                    </div>
                                                                </div>
                                                        </td>
                                                    </tr>
                                                </table>
                                            </td>

                                        </tr>
                                    </#foreach>
                                    </tbody>
                                </table>
                            </div>
                        </div>
                       </td>
                      </tr>
                     </table>
                <#else>

                <#--- Test Results -->
                    <table>
                        <tr>
                            <td>
                                <div><h2>Tests</h2></div>
                                <div id="test_list_tests" class="table">
                                    <div class="test-results">
                                        <table id="test-results-table">
                                            <thead>
                                            <tr>
                                                <th width="30" class="test-results-heading">&nbsp;</th>
                                                <th width="525" class="test-results-heading">Tests</th>
                                                <th width="70" class="test-results-heading">Steps</th>
                                                <th width="65" class="test-results-heading">Fail</th>
                                                <th width="65" class="test-results-heading">Pend</th>
                                                <th width="65" class="test-results-heading">Ignore</th>
                                                <th width="65" class="test-results-heading">Skip</th>
                                                <th width="65" class="test-results-heading">Stable</th>
                                                <th width="100" class="test-results-heading">Duration<br>(seconds)</th>
                                            </tr>
                                            </thead>
                                            <tbody>
                                                <#assign testResultSet = testOutcomes.tests >
                                                <#foreach testOutcome in testResultSet>
                                                    <#if testOutcome.stepCount == 0 || testOutcome.result == "PENDING" || testOutcome.result == "IGNORED">
                                                        <#assign testrun_outcome_icon = "pending.png">
                                                    <#elseif testOutcome.result == "FAILURE">
                                                        <#assign testrun_outcome_icon = "fail.png">
                                                    <#elseif testOutcome.result == "SUCCESS">
                                                        <#assign testrun_outcome_icon = "success.png">
                                                    <#else>
                                                        <#assign testrun_outcome_icon = "ignor.png">
                                                    </#if>

                                                    <#assign stability = testOutcome.recentStability>
                                                    <#if (testOutcome.recentTestRunCount == testOutcome.recentPendingCount)>
                                                        <#assign stability_icon = "traffic-in-progress.gif">
                                                        <#assign stability_rank = 0>
                                                    <#elseif stability < 0.25>
                                                        <#assign stability_icon = "traffic-red.gif">
                                                        <#assign stability_rank = 1>
                                                    <#elseif stability < 0.5 >
                                                        <#assign stability_icon = "traffic-orange.gif">
                                                        <#assign stability_rank = 2>
                                                    <#elseif stability < 0.5 >
                                                        <#assign stability_icon = "traffic-yellow.gif">
                                                        <#assign stability_rank = 3>
                                                    <#else>
                                                        <#assign stability_icon = "traffic-green.gif">
                                                        <#assign stability_rank = 4>
                                                    </#if>

                                                <tr class="test-${testOutcome.result}">
                                                    <td><img src="images/${testrun_outcome_icon}" title="${testOutcome.result}" class="summary-icon"/><span style="display:none">${testOutcome.result}</span></td>
                                                    <td class="${testOutcome.result}-text"><a href="${testOutcome.reportName}.html">${testOutcome.titleWithLinks} ${testOutcome.formattedIssues}</a></td>

                                                    <td class="lightgreentext">${testOutcome.nestedStepCount}</td>
                                                    <td class="redtext">${testOutcome.failureCount}</td>
                                                    <td class="bluetext">${testOutcome.pendingCount}</td>
                                                    <td class="bluetext">${testOutcome.skippedCount}</td>
                                                    <td class="bluetext">${testOutcome.ignoredCount}</td>
                                                    <td class="bluetext">
                                                        <img src="images/${stability_icon}"
                                                             title="Over the last ${testOutcome.recentTestRunCount} tests: ${testOutcome.recentPassCount} passed, ${testOutcome.recentFailCount} failed, ${testOutcome.recentPendingCount} pending"
                                                             class="summary-icon"/>
                                                        <span style="display:none">${stability_rank }</span>
                                                    </td>
                                                    <td class="lightgreentext">${testOutcome.durationInSeconds}</td>
                                                </tr>
                                                </#foreach>
                                            </tbody>
                                        </table>
                                    </div>
                                </div>
                            </td>
                        </tr>
                    </table>
                </#if>
               </div>
            </div>
        </div>
    </div>
</div>
<div id="beforefooter"></div>
<div id="bottomfooter"></div>

</body>
</html>
