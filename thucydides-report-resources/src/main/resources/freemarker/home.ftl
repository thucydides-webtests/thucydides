<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8" />
    <title>Home</title>
    <link rel="shortcut icon" href="favicon.ico">
    <link rel="stylesheet" href="css/core.css"/>
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
    <script type="text/javascript" src="jqplot/jquery.jqplot.min.js"></script>
    <script type="text/javascript" src="jqplot/plugins/jqplot.pieRenderer.min.js"></script>
    <link rel="stylesheet" type="text/css" href="jqplot/jquery.jqplot.min.css"/>

    <script class="code" type="text/javascript">$(document).ready(function () {
        var plot1 = $.jqplot('test_results_pie_chart', [
            [
                ['Passing', ${testOutcomes.successCount}],
                ['Pending', ${testOutcomes.pendingCount}],
                ['Failing', ${testOutcomes.failureCount}]
            ]
        ], {
            gridPadding:{top:0, bottom:38, left:0, right:0},
            seriesColors:['#00C000', 'orange', 'red'],
            seriesDefaults:{
                renderer:$.jqplot.PieRenderer,
                trendline:{ show:false },
                rendererOptions:{ padding:8, showDataLabels:true }
            },
            legend:{
                show:true,
                placement:'outside',
                rendererOptions:{
                    numberRows:1
                },
                location:'s',
                marginTop:'15px'
            },
            series:[
                {label:'${testOutcomes.successCount} / ${testOutcomes.total} passed' },
                {label:'${testOutcomes.pendingCount} / ${testOutcomes.total} pending'},
                {label:'${testOutcomes.failureCount} / ${testOutcomes.total} failed'}
            ]
        });
    })
    ;
    </script>

    <script>
        $(document).ready(function()
        {
            //hide the all of the element with class msg_body
            $("#test_list_tests").hide();
            //toggle the componenet with class msg_body
            $("#test_list_title").click(function()
            {
                $(this).next("#test_list_tests").slideToggle(600);
                $(this).text($(this).text() == 'Click to View Tests' ? 'Click to Hide Tests' : 'Click to View Tests');
            });
        });
    </script>
</head>

<body>
<div id="topheader">
    <div id="topbanner">
        <#--<div id="menu">-->
            <#--<table border="0">-->
                <#--<tr>-->
                    <#--<td><a href="index.html"><img src="images/menu_h.png" width="105" height="28" border="0"/></a></td>-->
                    <#--<td><a href="features.html"><img src="images/menu_f.png" width="105" height="28" border="0"/></a>-->
                    <#--</td>-->
                    <#--<td><a href="stories.html"><img src="images/menu_s.png" width="105" height="28" border="0"/></a>-->
                    <#--</td>-->
                <#--</tr>-->
            <#--</table>-->
        <#--</div>-->
        <div id="logo"><a href="index.html"><img src="images/logo.jpg" border="0"/></a></div>
    </div>
</div>

<div class="middlecontent">

    <#if (testOutcomes.label == '')>
        <#assign resultsContext = ''>
    <#else>
        <#assign resultsContext = '- ' + testOutcomes.label>
    </#if>
    <div id="contenttop">
        <#--<div class="leftbg"></div>-->
        <div class="middlebg">
            <span class="bluetext"><a href="index.html" class="bluetext">Home</a> > Test Results ${resultsContext}</span>
        </div>
        <div class="rightbg"></div>
    </div>

    <div class="clr"></div>

    <!--/* starts second table*/-->
    <#--<div id="contentbody">-->
        <#--<div class="titlebar">-->
            <#--<div class="leftbgm"></div>-->
            <#--<#if (testOutcomes.label == '')>-->
                <#--<#assign resultsContext = ''>-->
            <#--<#else>-->
                <#--<#assign resultsContext = '- ' + testOutcomes.label>-->
            <#--</#if>-->
            <#--<div class="middlebgm"><span class="orangetext">Test Results ${resultsContext}</span></div>-->
            <#--<div class="rightbgm"></div>-->
        <#--</div>-->
    <#--</div>-->
    <#--<div class="menu">-->
        <#--<ul>-->
            <#--<li><a href="#" class="current">Test Results</a></li>-->
            <#--<li><a href="treemap.html">Tree Map</a></li>-->
            <#--<li><a href="dashboard.html">Progress</a></li>-->
            <#--<li><a href="history.html">History</a></li>-->
        <#--</ul>-->
        <#--<br style="clear:left"/>-->
    <#--</div>-->

    <div class="clr"></div>
    <div id="beforetable"></div>
    <div id="results-dashboard">
        <div class="middlb">
            <div class="table">
                <table class='overview'>
                    <tr>
                        <td width="375px">
                            <table>
                                <tr>
                                    <td>
                                        <div class="bluetext"><strong>Coverage</strong></div>
                                    </td>
                                    <td width="250px">
                                    <#assign redbar = (1-testOutcomes.percentagePendingStepCount)*250>
                                    <#assign greenbar = testOutcomes.percentagePassingStepCount*250>
                                    <#assign passing = testOutcomes.percentagePassingStepCount*250>
                                    <#assign failing = testOutcomes.percentageFailingStepCount*250>
                                    <#assign pending = testOutcomes.percentagePendingStepCount*250>

                                    <#assign totalTests = testOutcomes.total>
                                    <#assign passingTests = testOutcomes.successCount>
                                    <#assign failingTests = testOutcomes.failureCount>
                                    <#assign pendingTests = testOutcomes.pendingCount>

                                    <#assign tests = inflection.of(totalTests).times('test') >

                                    <#assign pendingCaption = "${pendingTests} out of ${totalTests} ${tests} pending;&#10;Pending tests involve ${testOutcomes.formatted.percentPendingCoverage} of all test steps">
                                    <#assign passingCaption = "${passingTests} out of ${totalTests} ${tests} passing;&#10;Passing tests involve ${testOutcomes.formatted.percentPassingCoverage} of all test steps">
                                    <#assign failingCaption = "${failingTests} out of ${totalTests} ${tests} failing;&#10;Failing tests involve ${testOutcomes.formatted.percentFailingCoverage} of all test steps">

                                        <a href="#" alt="Pending tests">
                                            <div class="percentagebar"
                                                 title="${pendingTests} out of ${totalTests} ${tests} pending;&#10;Pending tests involve ${testOutcomes.formatted.percentPendingCoverage} of all test steps"
                                                 style="width: 250px;">
                                                <a href="#" alt="Failing tests">
                                                    <div class="failingbar" style="width: ${redbar}px;"
                                                         title="${failingTests} out of ${totalTests} ${tests} failing;&#10;Failing tests involve ${testOutcomes.formatted.percentFailingCoverage} of all test steps">
                                                        <a href="#" alt="Passing tests">
                                                            <div class="passingbar"
                                                                 style="width: ${greenbar}px;"
                                                                 title="${passingTests} out of ${totalTests} ${tests} passing;&#10;Passing tests involve ${testOutcomes.formatted.percentPassingCoverage} of all test steps">
                                                            </div>
                                                        </a>
                                                    </div>
                                                </a>
                                            </div>
                                        </a>
                                    </td>
                                </tr>
                                <tr>
                                    <td>
                                        <div class="bluetext"><strong>Test Results</strong></div>
                                    </td>
                                    <td>
                                    <#assign successReport = reportName.withPrefix(testOutcomes.label).forTestResult("success") >
                                    <#assign failureReport = reportName.withPrefix(testOutcomes.label).forTestResult("failure") >
                                    <#assign pendingReport = reportName.withPrefix(testOutcomes.label).forTestResult("pending") >

                                    <div class="result_summary">
                                            <h4>${testOutcomes.total} tests</h4>
                                            ${testOutcomes.successCount}
                                            <#if (testOutcomes.successCount > 0 && report.shouldDisplayResultLink)>
                                                <a href="${successReport}">passed</a>
                                            <#else>passed</#if>,

                                            ${testOutcomes.pendingCount}
                                            <#if (testOutcomes.pendingCount > 0 && report.shouldDisplayResultLink)>
                                                <a href="${pendingReport}">pending</a>
                                            <#else>pending</#if>,
                                            ${testOutcomes.failureCount}
                                            <#if (testOutcomes.failureCount > 0 && report.shouldDisplayResultLink)>
                                                <a href="${failureReport}">failed</a>
                                            <#else>failed</#if>
                                        </div>
                                    </td>
                                <tr>
                                    <td colspan="2">
                                        <div id="pie_chart">
                                            <div id="test_results_pie_chart"
                                                 style="margin-top:20px; margin-left:20px; width:375px; height:375px;"></div>
                                        </div>
                                    </td>
                                </tr>
                            </table>
                        </td>
                        <td width="25px">&nbsp;</td>
                        <td width="625px" valign="top">
                        <#foreach tagType in testOutcomes.tagTypes>
                            <#assign outcomesForType = testOutcomes.withTagType(tagType) >
                            <#assign tagTypeTitle = inflection.of(tagType).inPluralForm().asATitle() >
                            <table class="test-summary-table">
                                <tr>
                                    <td colspan="3">
                                        <div class="tagTypeTitle">
                                            ${tagTypeTitle}
                                        </div>
                                    </td>
                                </tr>
                                <#foreach tagName in testOutcomes.getTagsOfType(tagType)>
                                    <#assign tagTitle = inflection.of(tagName).asATitle() >
                                    <#assign tagReport = reportName.forTag(tagName) >
                                    <#assign outcomesForTag = outcomesForType.withTag(tagName) >
                                    <#if outcomesForTag.result == "FAILURE">
                                        <#assign outcome_icon = "fail.png">
                                        <#assign outcome_text = "failing-color">
                                    <#elseif outcomesForTag.result == "SUCCESS">
                                        <#assign outcome_icon = "success.png">
                                        <#assign outcome_text = "success-color">
                                    <#elseif outcomesForTag.result == "PENDING" || outcomesForTag.result == "IGNORED" >
                                        <#assign outcome_icon = "pending.png">
                                        <#assign outcome_text = "pending-color">
                                    <#else>
                                        <#assign outcome_icon = "ignor.png">
                                        <#assign outcome_text = "ignore-color">
                                    </#if>
                                    <tr>
                                        <td class="bluetext" width="350px">
                                            <div class="tagTitle">
                                                <img src="images/${outcome_icon}" class="summary-icon">
                                                <span class="${outcomesForTag.result}-text">
                                                    <a href="${tagReport}">${tagTitle}</a>
                                                </span>
                                            </div>
                                        </td>
                                        <td width="150px" class="lightgreentext">
                                            <#assign redbar = (1-outcomesForTag.percentagePendingStepCount)*150>
                                            <#assign greenbar = outcomesForTag.percentagePassingStepCount*150>
                                            <#assign passing = outcomesForTag.formatted.percentPassingCoverage>
                                            <#assign failing = outcomesForTag.formatted.percentFailingCoverage>
                                            <#assign pending = outcomesForTag.formatted.percentPendingCoverage>

                                            <#assign pendingCaption = "${outcomesForTag.pendingCount} out of ${outcomesForTag.total} ${tests} pending (${pending})">
                                            <#assign passingCaption = "${outcomesForTag.successCount} out of ${outcomesForTag.total} ${tests} passing (${passing})">
                                            <#assign failingCaption = "${outcomesForTag.failureCount} out of ${outcomesForTag.total} ${tests} failing (${failing})">
                                            <#assign tests = inflection.of(outcomesForTag.total).times('test') >

                                            <table>
                                                <tr>
                                                    <td width="50px">${passing}</td>
                                                    <td width="10px">
                                                        <a href="${tagReport}">
                                                            <div class="percentagebar"
                                                                 title="${pendingCaption}"
                                                                 style="width: 150px;">
                                                                <div class="failingbar"
                                                                     style="width: ${redbar}px;"
                                                                     title="${failingCaption}">
                                                                    <div class="passingbar"
                                                                         style="width: ${greenbar}px;"
                                                                         title="${passingCaption}">
                                                                    </div>
                                                                </div>
                                                        </a>
                                                    </td>
                                                </tr>
                                            </table>
                                        </td>
                                    </tr>
                                    </#foreach>
                                </#foreach>
                            </table>
                        </td>
                    </tr>
                </table>
                <#--- Test Results -->
                <table><tr><td>
                    <div id="test_list_title"><h3>Click to view tests</h3></div>
                    <div id="test_list_tests" class="table">
                        <div class="toptablerow">
                            <table width="1025" height="50" border="0">
                                <tr>
                                    <td width="35">&nbsp;</td>
                                    <td width="%" class="greentext">Acceptance Criteria</td>
                                    <td width="80" class="greentext">Steps</td>
                                    <td width="80" class="greentext">Failed</td>
                                    <td width="80" class="greentext">Pending</td>
                                    <td width="80" class="greentext">Ignored</td>
                                    <td width="80" class="greentext">Skipped</td>
                                    <td width="95" class="greentext">Duration</td>
                                </tr>
                            </table>
                        </div>
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
                        <div class="tablerow">
                            <table border="0" height="40" width="1025" >
                                <tr class="test-${testOutcome.result}">
                                    <td width="35"><img src="images/${testrun_outcome_icon}" class="summary-icon"/></td>
                                    <td width="%" class="${testOutcome.result}-text"><a href="${testOutcome.reportName}.html">${testOutcome.titleWithLinks} ${testOutcome.formattedIssues}</a></td>

                                    <td width="80" class="lightgreentext"><b>${testOutcome.stepCount}</b></td>
                                    <td width="80" class="redtext">${testOutcome.failureCount}</td>
                                    <td width="80" class="bluetext">${testOutcome.pendingCount}</td>
                                    <td width="80" class="bluetext">${testOutcome.skippedCount}</td>
                                    <td width="80" class="bluetext">${testOutcome.ignoredCount}</td>
                                    <td width="95" class="lightgreentext">${testOutcome.duration / 1000} seconds</td>
                                </tr>
                            </table>
                        </div>
                    </#foreach>
                    </div>
                </td></tr></table>
            </div>
            <#--- Test Results end -->
            </div>
        </div>
    </div>
</div>
<div id="beforefooter"></div>
<div id="bottomfooter"></div>

</body>
</html>
