<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN"
        "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=utf-8"/>
    <title>Home</title>
    <link rel="shortcut icon" href="favicon.ico">
    <style type="text/css">
        <!--
        @import url("css/core.css");
        -->
    </style>
    <link href="css/core.css" rel="stylesheet" type="text/css"/>
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
                ['Passing', ${stories.successCount}],
                ['Pending', ${stories.pendingCount}],
                ['Failing', ${stories.failureCount}]
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
            }
        });
    });</script>
</head>

<body>
<div id="topheader">
    <div id="topbanner">
        <div id="menu">
            <table border="0">
                <tr>
                    <td><a href="index.html"><img src="images/menu_h.png" width="105" height="28" border="0"/></a></td>
                    <td><a href="features.html"><img src="images/menu_f.png" width="105" height="28" border="0"/></a>
                    </td>
                    <td><a href="stories.html"><img src="images/menu_s.png" width="105" height="28" border="0"/></a>
                    </td>
                </tr>
            </table>
        </div>
        <div id="logo"><a href="index.html"><img src="images/logo.jpg" border="0"/></a></div>
    </div>
</div>

<div class="middlecontent">
    <div id="contenttop">
        <div class="leftbg"></div>
        <div class="middlebg">
            <div style="height:30px;"><span class="bluetext"><a href="index.html" class="bluetext">Home</a></span> /
            </div>
        </div>
        <div class="rightbg"></div>
    </div>
    <div class="clr"></div>

    <!--/* starts second table*/-->
    <div id="contentbody">
        <div class="titlebar">
            <div class="leftbgm"></div>
            <div class="middlebgm"><span class="orangetext">Overall Test Results</span></div>
            <div class="rightbgm"></div>
        </div>
    </div>
    <div class="menu">
        <ul>
            <li><a href="#" class="current">Test Results</a></li>
            <li><a href="treemap.html">Tree Map</a></li>
            <li><a href="dashboard.html">Progress</a></li>
            <li><a href="history.html">History</a></li>
        </ul>
        <br style="clear:left"/>
    </div>
    <div class="clr"></div>
    <div id="beforetable"></div>
    <div id="contenttilttle">
        <div class="topb"><img src="images/topm.jpg"/></div>
        <div class="middlb">
            <div class="table">
                <div class="middlb">
                    <div class="table">

                        <table border="0">
                            <tr>
                                <td width="375px">
                                    <table>
                                        <tr>
                                            <td>
                                                <div class="bluetext"><strong>Coverage</strong></div>
                                            </td>
                                            <td width="250px">
                                                <#assign redbar = (1-stories.percentagePendingStepCount)*250>
                                                <#assign greenbar = stories.percentagePassingStepCount*250>
                                                <#assign passing = stories.percentagePassingStepCount*250>
                                                <#assign failing = stories.percentageFailingStepCount*250>
                                                <#assign pending = stories.percentagePendingStepCount*250>
                                                <div class="percentagebar" title="${stories.formatted.percentPendingCoverage}% pending steps" style="width: 250px;">
                                                    <div class="failingbar" style="width: ${redbar}px;" title="${stories.formatted.percentFailingCoverage}% failing steps">
                                                        <div class="passingbar" style="width: ${greenbar}px;" title="${stories.formatted.percentPassingCoverage}% passing steps"></div>
                                                    </div>
                                                </div>
                                            </td>
                                        </tr>
                                        <tr>
                                            <td>
                                                <div class="bluetext"><strong>Test Results</strong></div>
                                            </td>
                                            <td>
                                                <div class="result_summary">
                                                    <h4>${stories.totalTestCount} tests</h4> ${stories.successCount} passed, ${stories.pendingCount} pending, ${stories.failureCount} failed
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
                                    <table class="test-summary-table">
                                        <tr>
                                            <td colspan="3">
                                                <div class="bluetext"><a href="features.html"><strong>Tested Features</strong></a></div>
                                            </td>
                                        </tr>
                                        <#foreach featureResult in features>
                                            <#if featureResult.result == "FAILURE">
                                                <#assign outcome_icon = "fail.png">
                                                <#assign outcome_text = "failing-color">
                                            <#elseif featureResult.result == "SUCCESS">
                                                <#assign outcome_icon = "success.png">
                                                <#assign outcome_text = "success-color">
                                            <#elseif featureResult.result == "PENDING" || featureResult.result == "IGNORED" >
                                                <#assign outcome_icon = "pending.png">
                                                <#assign outcome_text = "pending-color">
                                            <#else>
                                                <#assign outcome_icon = "ignor.png">
                                                <#assign outcome_text = "ignore-color">
                                            </#if>
                                            <tr>
                                                <td class="bluetext" witdh="100">
                                                    <div>
                                                        <img src="images/${outcome_icon}" class="summary-icon"/>
                                                          <span class="${featureResult.result}-text">
                                                            <a href="${featureResult.storyReportName}">${featureResult.feature.name}</a>
                                                          </span>
                                                    </div>
                                                </td>
                                                <td width="150px" class="lightgreentext">
                                                    <#assign redbar = (1-featureResult.percentPendingCoverage)*150>
                                                    <#assign greenbar = featureResult.percentPassingCoverage*150>
                                                    <#assign passing = featureResult.formatted.percentPassingCoverage>
                                                    <#assign failing = featureResult.formatted.percentFailingCoverage>
                                                    <#assign pending = featureResult.formatted.percentPendingCoverage>
                                                    <table>
                                                        <tr>
                                                            <td width="50px">${passing}</td>
                                                            <td width="10px">
                                                                <a href="${featureResult.storyReportName}">
                                                                    <div class="percentagebar" title="${pending} pending" style="width: 150px;">
                                                                        <div class="failingbar" style="width: ${redbar}px;"  title="${failing} failing">
                                                                            <div class="passingbar" style="width: ${greenbar}px;" title="${passing} passing">
                                                                            </div>
                                                                        </div>
                                                                </a>
                                                            </td>
                                                        </tr>
                                                    </table>
                                                </td>
                                            </tr>
                                        </#foreach>
                                    </table>

                                    <table class="test-summary-table">
                                        <tr>
                                            <td colspan="3">
                                                <div class="bluetext"><a href="stories.html"><strong>Tested Stories</strong></a></div>
                                            </td>
                                        </tr>
                                        <#foreach story in stories.stories>
                                            <#if story.result == "PENDING" || story.result == "IGNORED">
                                                <#assign story_outcome_icon = "pending.png">
                                                <#assign story_outcome_text = "pending-color">
                                            <#elseif story.result == "FAILURE">
                                                <#assign story_outcome_icon = "fail.png">
                                                <#assign story_outcome_text = "failing-color">
                                            <#elseif story.result == "SUCCESS">
                                                <#assign story_outcome_icon = "success.png">
                                                <#assign story_outcome_text = "success-color">
                                            <#else>
                                                <#assign story_outcome_icon = "ignor.png">
                                                <#assign story_outcome_text = "ignore-color">
                                            </#if>
                                            <tr>
                                                <td class="bluetext" witdh="100">
                                                    <div>
                                                        <img src="images/${story_outcome_icon}" class="summary-icon"/>
                                                        <span class="${story.result}-text"><a href="${story.reportName}.html">${story.title}</a><span class="related-issues">${story.formattedIssues}</span></span>
                                                    </div>
                                                </td>
                                                <td width="150px" class="lightgreentext">
                                                    <#assign redbar = (1-story.percentPendingCoverage)*150>
                                                    <#assign greenbar = story.percentPassingCoverage*150>
                                                    <#assign passing = story.formatted.percentPassingCoverage>
                                                    <#assign failing = story.formatted.percentFailingCoverage>
                                                    <#assign pending = story.formatted.percentPendingCoverage>
                                                    <table>
                                                        <tr>
                                                            <td width="50px">${passing}</td>
                                                            <td width="150px">
                                                                <a href="${story.reportName}.html">
                                                                    <div class="percentagebar" title="${pending} pending" style="width: 150px;">
                                                                        <div class="failingbar" style="width: ${redbar}px;"  title="${failing} failing">
                                                                            <div class="passingbar" style="width: ${greenbar}px;" title="${passing} passing">
                                                                            </div>
                                                                        </div>
                                                                </a>
                                                            </td>
                                                        </tr>
                                                    </table>
                                                </td>
                                            </tr>
                                        </#foreach>
                                    </table>
                                </td>
                            <tr>
                        </table>
                    </div>
                </div>
            </div>
        </div>

        <div class="bottomb"><img src="images/bottomm.jpg"/></div>
    </div>
</div>
<div id="beforefooter"></div>
<div id="bottomfooter"></div>

</body>
</html>
