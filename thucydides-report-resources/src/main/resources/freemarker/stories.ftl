<#assign hash = '#'>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN"
        "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=utf-8"/>
    <title>Stories</title>
    <script type="text/javascript" src="scripts/jquery.js"></script>
    <link rel="shortcut icon" href="favicon.ico" >

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
            <div class="middlebgm"><span class="orangetext">Stories - ${storyContext}</span></div>
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
                            <td width="440" class="greentext">Stories</td>
                            <td width="75" class="greentext">Tests</td>
                            <td width="75" class="greentext">Failed</td>
                            <td width="75" class="greentext">Pending</td>
                            <td width="75" class="greentext">Skipped</td>
                            <td width="200" class="greentext">Coverage</td>
                        </tr>
                    </table>
                </div>

                <#foreach story in stories>
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
                    <div class="tablerow">
                        <table border="0" height="40" width="980">
                            <tr>
                                <td width="10">&nbsp;</td>
                                <td width="440" class="bluetext">

                                    <ul id="accordion">
                                        <li>
                                            <div></div>
                                            <ul></ul>
                                        </li>
                                        <li>
                                            <div>
                                                <img src="images/${story_outcome_icon}" class="summary-icon"/>
                                                <span class="${story.result}-text"><a href="${story.reportName}.html">${story.title}</a><span class="related-issues">${story.formattedIssues}</span></span>
                                            </div>
                                            <ul>
                                                <#foreach testOutcome in story.testOutcomes>
                                                    <#if testOutcome.result == "PENDING" || testOutcome.result == "IGNORED" || testOutcome.stepCount == 0>
                                                        <#assign outcome_icon = "pending.png">
                                                        <#assign outcome_text = "pending-color">
                                                    <#elseif testOutcome.result == "FAILURE">
                                                        <#assign outcome_icon = "fail.png">
                                                        <#assign outcome_text = "failing-color">
                                                    <#elseif testOutcome.result == "SUCCESS">
                                                        <#assign outcome_icon = "success.png">
                                                        <#assign outcome_text = "success-color">
                                                    <#else>
                                                        <#assign outcome_icon = "ignor.png">
                                                        <#assign outcome_text = "ignore-color">
                                                    </#if>
                                                    <li>
                                                        <img src="images/${outcome_icon}" class="summary-icon"/>
                                                        <a href="${testOutcome.reportName}.html" class="${testOutcome.result}-item-text">${testOutcome.titleWithLinks}</a><span class="related-issues">${testOutcome.formattedIssues}</span>
                                                    </li>
                                                </#foreach>
                                            </ul>
                                        </li>

                                    </ul>
                                </td>
                                <td width="75" class="bluetext">${story.total}</td>
                                <td width="75" class="redtext"><span class="lightgreentext">${story.failureCount}</span></td>
                                <td width="75" class="lightgreentext">${story.pendingCount}</td>
                                <td width="75" class="lightgreentext">${story.skipCount}</td>
                                <td width="200" class="lightgreentext">

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
                        </table>
                    </div>
               </#foreach>            </div>
        </div>

        <div class="bottomb"><img src="images/bottomm.jpg"/></div>
    </div>
</div>
<div id="beforefooter"></div>
<div id="bottomfooter"></div>

</body>
</html>
<SCRIPT>
    $("#accordion > li > div > img").click(function() {

        if (false == $(this).parent().next().is(':visible')) {
            $('#accordion ul').slideUp(300);
        }
        $(this).parent().next().slideToggle(300);
    });

    $('#accordion ul:eq(0)').show();

</SCRIPT>
