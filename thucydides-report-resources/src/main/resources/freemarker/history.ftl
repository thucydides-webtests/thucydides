<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8" />
    <title>History</title>
    <link rel="shortcut icon" href="favicon.ico">
    </style>
    <link href="css/core.css" rel="stylesheet" type="text/css"/>
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
    <script type="text/javascript" src="jqplot/plugins/jqplot.categoryAxisRenderer.min.js"></script>
    <script type="text/javascript" src="jqplot/plugins/jqplot.dragable.min.js"></script>
    <script type="text/javascript" src="jqplot/plugins/jqplot.highlighter.min.js"></script>
    <script type="text/javascript" src="jqplot/plugins/jqplot.dateAxisRenderer.min.js"></script>
   	<script type="text/javascript" src="jqplot/plugins/jqplot.cursor.min.js"></script>



    <link type="text/css" href="jqueryui/css/start/jquery-ui-1.8.18.custom.css" rel="Stylesheet" />
    <script type="text/javascript" src="jqueryui/js/jquery-ui-1.8.18.custom.min.js"></script>

    <script class="code" type="text/javascript">$(document).ready(function () {

        var specified = [];
        var done = [];
        var skipped = [];
        var failing = [];
        var min_date;

        <#assign row = 0>
        <#assign max_specified = 0>
        <#foreach snapshot in history>
        var date = new Date(${snapshot.time.toString('yyyy')}, ${snapshot.time.toString('M')?number - 1}, ${snapshot.time.toString('d')},${snapshot.time.toString('H')}, ${snapshot.time.toString('m')}, ${snapshot.time.toString('s')});

        specified.push([date, ${snapshot.specifiedSteps}]);
        done.push([date,${snapshot.passingSteps}]);
        skipped.push([date,${snapshot.skippedSteps}]);
        failing.push([date,${snapshot.failingSteps}]);

        <#if row == 0 >
            min_date = date;
        </#if>

        <#if snapshot.specifiedSteps &gt; max_specified >
            <#assign max_specified = snapshot.specifiedSteps>
        </#if>

        <#assign row = row + 1>
        </#foreach>

        targetPlot = $.jqplot('chart_div', [failing,skipped,done,specified], {

            axesDefaults : {
                labelRenderer : $.jqplot.CanvasAxisLabelRenderer
            },

            axes : {

                xaxis : {
                    renderer : $.jqplot.DateAxisRenderer,
                    tickOptions: {formatString:'%b %#d'},
                    min : min_date,
                    tickInterval: '1 week'
                },

                yaxis : {
                    min: 0,
                    max: ${max_specified},
                    tickInterval: ${max_specified} / 5,
                    tickOptions: {formatString: '%d' }
                }

            },

            legend: {
                show:true,
                location: 'nw'
            },

			cursor:{
					show: true,
					zoom:true,
					showTooltip:true
    		},

            series: [
                {color:'#ff0000', label:'Failing'},
   				{color:'#ff9131', label:'Skipped'},
                {color:'#00ff00', label:'Done'},
                {color:'#0000ff', label:'Specified'}

            ]


        });

        controllerPlot = $.jqplot('controller_div', [failing,skipped,done,specified], {

            seriesDefaults:{ showMarker: false },

            series: [
                {color:'#ff0000', label:'Failing'},
				{color:'#ff9131', label:'Skipped'},
				{color:'#00ff00', label:'Done'},
				{color:'#0000ff', label:'Specified'}
            ],

            cursor:{
                show: true,
                showTooltip: false,
                zoom:true,
                constrainZoomTo: 'x'
            },

            axesDefaults: {
                useSeriesColor:true,
                rendererOptions: {
                    alignTicks: true
                }
            },

            axes : {

                xaxis : {
                    renderer : $.jqplot.DateAxisRenderer,
                    tickOptions: {formatString:'%b %#d'},
                    min : min_date,
                    tickInterval: '1 week'
                },

                yaxis : {
				show:false,
                    min: 0,
                    max: ${max_specified},
                    tickInterval: ${max_specified} / 5,
                    tickOptions: {formatString: '%d' }
                }
            } //axes
        }); //conroller plot

        $.jqplot.Cursor.zoomProxy(targetPlot, controllerPlot);

        $.jqplot._noToImageButton = true;

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
    <div id="contenttop">
        <div class="leftbg"></div>
        <div class="middlebg">
            <div style="height:30px;"><span class="bluetext"><a href="index.html" class="bluetext">Home</a></span> > History
            </div>
        </div>
        <div class="rightbg"></div>
    </div>

    <div class="clr"></div>

    <!--/* starts second table*/-->
    <div id="contentbody">
        <div class="titlebar">
            <div class="middlebgm"><span class="orangetext">Overview - History</span></div>
            <div class="rightbgm"></div>
        </div>
    </div>
    <div class="menu">
        <ul>
            <li><a href="index.html">Test Results</a></li>
            <#--<li><a href="treemap.html">Tree Map</a></li>-->
            <#--<li><a href="dashboard.html">Progress</a></li>-->
            <li><a href="#" class="current">History</a></li>
        </ul>
        <br style="clear:left"/>
    </div>
    <div class="clr"></div>
    <div id="beforetable"></div>
    <div id="results-dashboard">
        <div class="middlb">
            <div class="table">
                 <table class='overview'>
                  <tr>
                     <td>
                       <div id='chart_div' style='width: 700px; height: 400px;'></div>
                     </td>
                  <tr>
                  <tr>
                     <td>
                       <div id='controller_div' style='width: 700px; height: 100px;'></div>
                     </td>
                  <tr>


                 </table>
            </div>
        </div>
    </div>
</div>
<div id="beforefooter"></div>
<div id="bottomfooter"></div>

</body>
</html>
