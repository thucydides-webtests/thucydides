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

    <script type="text/javascript" src="https://www.google.com/jsapi"></script>
    <script type='text/javascript'>
        google.load('visualization', '1', {'packages':['annotatedtimeline']});
        google.setOnLoadCallback(drawChart);
        function drawChart() {
            var data = new google.visualization.DataTable();
            data.addColumn('date', 'Date');
            data.addColumn('number', 'Specified');
            data.addColumn('number', 'Done');
            data.addColumn('number', 'Skipped');
            data.addColumn('number', 'Failing');
            data.addRows(${rowcount})
            <#assign row = 0>
            <#foreach snapshot in history>
                   data.setValue(${row}, 0, new Date(${snapshot.time.toString('yyyy')}, ${snapshot.time.toString('M')?number - 1}, ${snapshot.time.toString('d')},
                                 ${snapshot.time.toString('H')}, ${snapshot.time.toString('m')}, ${snapshot.time.toString('s')}));
                   data.setValue(${row}, 1, ${snapshot.specifiedSteps});
                   data.setValue(${row}, 2, ${snapshot.passingSteps});
                   data.setValue(${row}, 3, ${snapshot.skippedSteps});
                   data.setValue(${row}, 4, ${snapshot.failingSteps});
                   <#assign row = row + 1>
            </#foreach>
            var chart = new google.visualization.AnnotatedTimeLine(document.getElementById('chart_div'));
            chart.draw(data, {displayAnnotations:false, thickness:2, fill:5, allowRedraw:true , colors: ['blue','green','#FF9131','red']});
        }
    </script>

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
            <div class="middlebgm"><span class="orangetext">Overview - History</span></div>
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
					     <td>
                           <div id='chart_div' style='width: 700px; height: 400px;'></div>
					     </td>
					     <td class="graphlinks">
							<div id="link_menu">
							  <ul>
							 	<li><a href="index.html">Test Results</a></li>
                                <li><a href="dashboard.html">Progress</a></li>
                                <li><a href="#" class="selected">History</a></li>
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
