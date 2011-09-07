<#assign hash = '#'>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN"
        "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=utf-8"/>
    <title>Stories</title>
    <link rel="shortcut icon" href="favicon.ico" >
    <script type="text/javascript" src="scripts/jquery.js"></script>

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
</head>

<body>
<div id="topheader">
    <div id="topbanner">
        <div id="menu">
            <table border="0">
                <tr>
                    <td><a href="index.html"><img src="images/menu_h.png" width="105" height="28" border="0"/></a></td>
                    <td><a href="features.html"><img src="images/menu_f.png" width="105" height="28" border="0"/></a></td>
                    <td><a href="stories.html"><img src="images/menu_s.png" width="105" height="28" border="0"/></a></td>
                </tr>
            </table>
        </div>
        <div id="logo"><a href="index.html"><img src="images/logo.jpg" width="265" height="96" border="0"/></a></div>
    </div>
</div>

<div class="middlecontent">
    <div id="contenttop">
        <div class="leftbg"></div>
        <div class="middlebg">
            <div style="height:30px;"><span class="bluetext"><a href="index.html">Home</a></span> / <span class="lightgreentext"><a
                    href="features.html" class="lightgreentext">Features</a></span></div>
        </div>
        <div class="rightbg"></div>
    </div>
    <div class="clr"></div>
    <div id="contentbody">
        <div class="titlebar">
            <div class="leftbgm"></div>
            <div class="middlebgm"><span class="orangetext">Features</span></div>
            <div class="rightbgm"></div>
        </div>
    </div>
    <div class="clr"></div>
    <div id="beforetable"></div>
    <div id="contenttilttle">
        <div class="topb"><img src="images/topm.jpg"/></div>
        <div class="middlb">
            <div class="table">
                <div class="toptablerow">
                    <table width="980" height="50" border="0">
                        <tr>
                            <td width="10">&nbsp;</td>
                            <td width="500" class="bluetext">Features</td>
                            <td width="60" class="greentext">Stories</td>
                            <td width="60" class="greentext">Total tests</td>
                            <td width="60" class="greentext">Failed tests</td>
                            <td width="60" class="greentext">Pending tests</td>
                            <td width="200" class="greentext">Coverage</td>
                        </tr>
                    </table>
                </div>

                <div class="tablerow">
                    <table border="0" height="40" width="980">
                        <#foreach featureResult in features>                            <#if featureResult.result == "FAILURE"><#assign outcome_icon = "fail.png"><#assign outcome_text = "failing-color">                                 <#elseif featureResult.result == "SUCCESS"><#assign outcome_icon = "success.png"><#assign outcome_text = "success-color">                                 <#elseif featureResult.result == "PENDING"><#assign outcome_icon = "pending.png"><#assign outcome_text = "pending-color">                                 <#else><#assign outcome_icon = "ignor.png"><#assign outcome_text = "ignore-color">                                 </#if>
                        <tr>
                            <td width="10">&nbsp;</td>
                            <td class="bluetext" witdh=500>
                                <div>
                                  <img src="images/${outcome_icon}" class="summary-icon"/>
                                  <span class="${featureResult.result}-text">
                                    <a href="${featureResult.storyReportName}">${featureResult.feature.name}</a>
                                  </span>
                                </div>
                            </td>
                            <td width="60" class="bluetext">${featureResult.totalStories}</td>
                            <td width="60" class="bluetext">${featureResult.totalTests}</td>
                            <td width="60" class="redtext">${featureResult.failingTests}</td>
                            <td width="60" class="lightgreentext">${featureResult.pendingTests}</td>
                            <td width="200" class="lightgreentext">
                                    <#assign redbar = (1-featureResult.percentPendingCoverage)*150>
                                    <#assign greenbar = featureResult.percentPassingCoverage*150>
                                    <#assign passing = featureResult.formatted.percentPassingCoverage>
                                    <#assign failing = featureResult.formatted.percentFailingCoverage>
                                    <#assign pending = featureResult.formatted.percentPendingCoverage>
                                    <table>
                                        <tr>
                                            <td width="50px">${passing}</td>
                                            <td width="150px">
                                                <a href="${featureResult.storyReportName}">
                                                  <div class="percentagebar" title="${pending} pending">
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
                        </#foreach>                    </table>
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
<SCRIPT>
    $("${hash}accordion > li > div").click(function() {

        if (false == $(this).next().is(':visible')) {
            $('${hash}accordion ul').slideUp(300);
        }
        $(this).next().slideToggle(300);
    });

    $('${hash}accordion ul:eq(0)').show();

</SCRIPT>