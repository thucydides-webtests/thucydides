<!DOCTYPE html>
<html xmlns="http://www.w3.org/1999/html">
<head>
    <meta charset="UTF-8"/>
    <title>Releases</title>
    <link rel="shortcut icon" href="favicon.ico">
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
    <script type="text/javascript" src="jqplot/jquery.jqplot.min.js"></script>
    <script type="text/javascript" src="jqplot/plugins/jqplot.pieRenderer.min.js"></script>

    <link type="text/css" href="jqueryui/css/start/jquery-ui-1.8.18.custom.css" rel="Stylesheet"/>
    <script type="text/javascript" src="jqueryui/js/jquery-ui-1.8.18.custom.min.js"></script>

    <script src="jqtree/tree.jquery.js"></script>
    <link rel="stylesheet" href="jqtree/jqtree.css">

    <link rel="stylesheet" href="css/core.css"/>

    <style type="text/css" media="screen">
        .dataTables_info {
            padding-top: 0;
        }

        .dataTables_paginate {
            padding-top: 0;
        }

        .css_right {
            float: right;
        }
    </style>

    <script type="text/javascript">
        $(document).ready(function () {
            $(".read-more-link").click(function () {
                $(this).nextAll("div.read-more-text").toggle();
                var isrc = $(this).find("img").attr('src');
                if (isrc == 'images/plus.png') {
                    $(this).find("img").attr("src", function () {
                        return "images/minus.png";
                    });
                } else {
                    $(this).find("img").attr("src", function () {
                        return "images/plus.png";
                    });
                }
            });
        });
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
        <span class="bluetext"><a href="index.html" class="bluetext">Releases</a> </span>
    </div>
    <div class="rightbg"></div>
</div>

<div class="clr"></div>

<!--/* starts second table*/-->
<div class="menu">
    <ul>
        <li><a href="index.html">Test Results</a></li>
        <li><a href="capabilities.html">Requirements</a></li>
        <li><a href="releases.html" class="current">Releases</a></li>
        <li><a href="progress-report.html">Progress</a></li>
    <#foreach tagType in allTestOutcomes.tagTypes>
        <#assign tagReport = reportName.forTagType(tagType) >
        <#assign tagTypeTitle = inflection.of(tagType).inPluralForm().asATitle() >
        <li><a href="${tagReport}">${tagTypeTitle}</a></li>
    </#foreach>
        <li><a href="history.html">History</a></li>
    </ul>
    <span class="date-and-time">Tests run ${timestamp}</span>
    <br style="clear:left"/>
</div>

<div class="clr"></div>


<div id="beforetable"></div>
<div id="results-dashboard">
<div class="middlb">
<div class="table">

    <div id="releases">
        <h3>Releases</h3>

        <div id="release-tree"></div>
        <script>
            var releaseData = ${releaseData};
            $(function() {
                $('#release-tree').tree({
                    data: releaseData
                });
            });

            $('#release-tree').bind(
                    'tree.click',
                    function(event) {
                        window.location.href = event.node.reportName;
                    }
            );

        </script>
    </div>

    <#if testOutcomes.tests?has_content >
    <#--- Test Results -->

    <div id="test-tabs">
        <div id="test_list_tests" class="table">
            <div class="test-results">
                <table id="test-results-table">
                    <thead>
                    <tr>
                        <th width="30" class="test-results-heading">&nbsp;</th>
                        <th width="%" class="test-results-heading">Tests</th>
                        <th width="70" class="test-results-heading">Steps</th>
                        <#if reportOptions.showStepDetails>
                            <th width="65" class="test-results-heading">Fail</th>
                            <th width="65" class="test-results-heading">Errors</th>
                            <th width="65" class="test-results-heading">Pend</th>
                            <th width="65" class="test-results-heading">Ignore</th>
                            <th width="65" class="test-results-heading">Skip</th>
                        </#if>
                        <th width="65" class="test-results-heading">Stable</th>
                        <th width="100" class="test-results-heading">Duration<br>(seconds)</th>
                    </tr>
                    </thead>
                    <tbody>
                        <#assign testResultSet = testOutcomes.tests >
                        <#foreach testOutcome in testResultSet>
                            <#if testOutcome.result == "PENDING" || testOutcome.result == "IGNORED">
                                <#assign testrun_outcome_icon = "pending.png">
                            <#elseif testOutcome.result == "FAILURE">
                                <#assign testrun_outcome_icon = "fail.png">
                            <#elseif testOutcome.result == "ERROR">
                                <#assign testrun_outcome_icon = "cross.png">
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
                            <td><img src="images/${testrun_outcome_icon}" title="${testOutcome.result}"
                                     class="summary-icon"/><span style="display:none">${testOutcome.result}</span></td>
                            <td class="${testOutcome.result}-text"><a
                                    href="${relativeLink!}${testOutcome.reportName}.html">${testOutcome.titleWithLinks} ${testOutcome.formattedIssues}</a>
                            </td>

                            <td class="lightgreentext">${testOutcome.nestedStepCount}</td>
                            <#if reportOptions.showStepDetails>
                                <td class="redtext">${testOutcome.total.withResult("FAILURE")}</td>
                                <td class="redtext">${testOutcome.total.withResult("ERROR")}</td>
                                <td class="bluetext">${testOutcome.total.withResult("PENDING")}</td>
                                <td class="bluetext">${testOutcome.total.withResult("SKIPPED")}</td>
                                <td class="bluetext">${testOutcome.total.withResult("IGNORED")}</td>
                            </#if>
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
    </div>
    </#if>
</div>
</div>
</div>
</div>
</div>
</div>
</div>
<div id="beforefooter"></div>
<div id="bottomfooter"></div>

</body>
</html>
