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

    <!-- CSS Files -->
    <link type="text/css" href="jit/css/base.css" rel="stylesheet"/>
    <link type="text/css" href="jit/css/Treemap.css" rel="stylesheet"/>

    <!--[if IE]>
    <script language="javascript" type="text/javascript" src="jit/Extras/excanvas.js"></script><![endif]-->

    <!-- JIT Library File -->
    <script language="javascript" type="text/javascript" src="jit/jit.js"></script>

    <!-- Data File -->
    <script language="javascript" type="text/javascript" src="progress.js"></script>


</head>

<body onload="init();">
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
            <div class="middlebgm"><span class="orangetext">Overview - Progress</span></div>
            <div class="rightbgm"></div>
        </div>
    </div>

    <div class="menu">
        <ul>
            <li><a href="index.html">Test Results</a></li>
            <li><a href="treemap.html">Tree Map</a></li>
            <li><a href="#" class="current">Progress</a></li>
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
                                <td>
                                    <div id="graph">
                                        <div class="legend-zone">
                                            <div id="progress-gradient" class="legend">
                                                <div class="legend-minimum">Planned</div>
                                                <div class="legend-maximum">Done</div>
                                            </div>
                                        </div>
                                        <div id="infovis" class="result-graph"></div>
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
