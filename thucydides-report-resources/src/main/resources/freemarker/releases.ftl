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
            <#if reportOptions.showReleases><li><a href="releases.html" class="current">Releases</a></li></#if>
            <li><a href="progress-report.html">Progress</a></li>
        <#foreach tagType in allTestOutcomes.firstClassTagTypes>
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
                        $(function () {
                            $('#release-tree').tree({
                                data: releaseData
                            });
                        });

                        $('#release-tree').bind(
                                'tree.click',
                                function (event) {
                                    window.location.href = event.node.reportName;
                                }
                        );

                    </script>
                </div>
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
