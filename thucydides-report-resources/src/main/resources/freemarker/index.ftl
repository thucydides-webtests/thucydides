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


<#--<script type="text/javascript" src="https://www.google.com/jsapi"></script>-->
<#--<script type="text/javascript">-->

<#--// Load the Visualization API and the piechart package.-->
<#--google.load('visualization', '1.0', {'packages':['corechart']});-->

<#--// Set a callback to run when the Google Visualization API is loaded.-->
<#--google.setOnLoadCallback(drawChart);-->

<#--// Callback that creates and populates a data table,-->
<#--// instantiates the pie chart, passes in the data and-->
<#--// draws it.-->
<#--function drawChart() {-->

<#--// Create the data table.-->
<#--var data = new google.visualization.DataTable();-->
<#--data.addColumn('string', 'Tests');-->
<#--data.addColumn('number', 'Results');-->
<#--data.addRows([-->
<#--['Success', ${stories.successCount}],-->
<#--['Failures', ${stories.failureCount}],-->
<#--['Pending', ${stories.pendingCount}]-->
<#--]);-->

<#--// Set chart options-->
<#--var options = {'title':'Overall Test Results',-->
<#--'width':375,-->
<#--'is3D':true,-->
<#--'fontSize':16,-->
<#--'legend':{position:'none'},-->
<#--'title':'Whatever',-->
<#--backgroundColor:{'stroke':'#3688BA', 'strokeWidth':1},-->
<#--chartArea:{left:20, top:0, width:"100%", height:"100%"},-->
<#--colors:['#00DD00', 'red', 'blue'],-->
<#--'height':300};-->

<#--// Instantiate and draw our chart, passing in some options.-->
<#--var chart = new google.visualization.PieChart(document.getElementById('pie_chart'));-->
<#--chart.draw(data, options);-->


<#--}-->
<#--</script>-->


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
            <div class="middlebgm"><span class="orangetext">Overview - Test Results</span></div>
            <div class="rightbgm"></div>
        </div>
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

                                <td width="750px">
                                    <table>
                                        <tr>
                                            <td>
                                                <div class="bluetext"><strong>Coverage</strong></div>
                                            </td>
                                            <td width="250px">
                                                <div class="percentagebar" title="${stories.formatted.percentPendingCoverage} pending">
                                                    <div class="failingbar" style="width: 100px;" title="${stories.formatted.percentFailingCoverage}% failing">
                                                        <div class="passingbar" style="width: 20px;"
                                                             title="${stories.formatted.percentPassingCoverage}% passing"></div>
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


                                <td class="graphlinks">
                                    <div id="link_menu">
                                        <ul>
                                            <li><a href="#" class="selected">Test Results</a></li>
                                            <li><a href="treemap.html">Tree Map</a></li>
                                            <li><a href="dashboard.html">Progress</a></li>
                                            <li><a href="history.html">History</a></li>
                                        </ul>
                                    </div>
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
