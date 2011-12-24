<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN"
        "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=utf-8"/>
    <title>${testOutcome.title}</title>
    <link rel="shortcut icon" href="favicon.ico">
    <style type="text/css">
        <!--
        @import url("core.css");
        -->
    </style>
    <link href="css/core.css" rel="stylesheet" type="text/css"/>
    <script src="scripts/jquery.js" type="text/javascript"></script>
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

<#-- HEADER
-->
<div id="topheader">
    <div id="topbanner">
        <div id="menu">
            <table border="0">
                <tr>
                    <td><a href="index.html"><img src="images/menu_h.png" width="105" height="28" border="0"/></a></td>
                    <td><a href="Features.html"><img src="images/menu_f.png" width="105" height="28" border="0"/></a>
                    </td>
                    <td><a href="Stories.html"><img src="images/menu_s.png" width="105" height="28" border="0"/></a>
                    </td>
                </tr>
            </table>
        </div>
        <div id="logo"><a href="index.html"><img src="images/logo.jpg" width="265" height="96" border="0"/></a></div>
    </div>
</div>
<#-- END OF HEADER
-->
<div class="middlecontent">
<#-- BREADCRUMBS
-->
    <div id="contenttop">
        <div class="leftbg"></div>
        <div class="middlebg">
            <div style="height:30px;"><span class="bluetext"><a href="stories.html" class="bluetext">Home</a></span>
                / <span
                        class="bluetext"><a href="#" class="bluetext">Features</a></span> /
                <span class="bluetext"><a href="stories.html">Stories</a></span> /
            <span class="bluetext"><#if (testOutcome.userStory.name)??><a href="${testOutcome.userStory.reportName}.html"
                                      class="bluetext">${testOutcome.userStory.name}</a></#if></span></div>
        </div>
        <div class="rightbg"></div>
    </div>
<#-- END OF BREADCRUMBS
-->
    <div class="clr"></div>

<#if testOutcome.result == "FAILURE"><#assign outcome_icon = "fail.png"><#assign outcome_text = "failing-color">    <#elseif testOutcome.result == "SUCCESS"><#assign outcome_icon = "success.png"><#assign outcome_text = "success-color">    <#elseif testOutcome.result == "PENDING"><#assign outcome_icon = "pending.png"><#assign outcome_text = "pending-color">    <#else><#assign outcome_icon = "ignor.png"><#assign outcome_text = "ignore-color">    </#if>
<#-- TEST TITLE
-->
    <div id="contentbody">
        <div class="titlebar">
            <div class="tall_leftbgm"></div>
            <div class="tall_middlebgm">
                <table width="1005">
                    <tr>
                        <td width="25"><img class="story-outcome-icon" src="images/${outcome_icon}" width="25"
                                            height="25"/>
                        </td>
                        <td width="%"><span class="test-case-title"><span
                                class="${outcome_text}">${testOutcome.titleWithLinks}<span class="related-issue-title">${testOutcome.formattedIssues}</span></span></span>
                        </td>
                        <td width="100"><span class="test-case-duration"><span class="greentext">${testOutcome.duration / 1000}
                            seconds</span></span>
                        </td>
                    </tr>
                </table>
            </div>
            <div class="tall_rightbgm"></div>
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
                            <td width="40">&nbsp;</td>
                            <td width="%" class="greentext">Steps</td>
                            <td width="150" class="greentext">Screenshot</td>
                            <td width="150" class="greentext">Outcome</td>
                            <td width="75" class="greentext">Duration</td>
                        </tr>
                    </table>
                </div>

            <#assign level = 1>
            <#macro write_step(step)>
                <@step_details step=step level=level />
                <#if step.isAGroup()>
                    <#assign level = level + 1>
                    <#foreach nestedStep in step.children>
                        <@write_step step=nestedStep />
                    </#foreach>
                    <#assign level = level-1>
                </#if>
            </#macro>
            <#macro step_details(step, level)>
                <#if step.result == "FAILURE">
                    <#assign step_outcome_icon = "fail.png">
                <#elseif step.result == "SUCCESS">
                    <#assign step_outcome_icon = "success.png">
                <#elseif step.result == "PENDING">
                    <#assign step_outcome_icon = "pending.png">
                <#else>
                    <#assign step_outcome_icon = "ignor.png">
                </#if>
                <#assign step_icon_size = 20>
                <#if (level>1)>
                    <#if step.isAGroup()>
                        <#assign step_class_root = "nested">
                    <#else>
                        <#assign step_class_root = "nested-group">
                    </#if>
                <#else>
                    <#assign step_class_root = "top-level">
                </#if>
                <#assign step_indent = level*20>
                <div class="tablerow">
                    <table border="0" width="980" height="40">
                        <tr class="test-${step.result}">
                            <td width="40"><img style="margin-left: ${step_indent}px; margin-right: 5px;"
                                                src="images/${step_outcome_icon}" class="${step_class_root}-icon"/></td>
                            <td width="%"><span class="${step_class_root}-step">${step.description}</span></td>
                            <td width="100" class="bluetext">
                                <#if !step.isAGroup() && step.firstScreenshot??>
                                    <a href="${testOutcome.screenshotReportName}.html#screenshots">
                                        <img src="${step.firstScreenshot.screenshot.name}"
                                             class="screenshot"
                                             width="48" height="48"/>
                                    </a>
                                </#if>
                            </td>
                            <td width="150"><span class="${step_class_root}-step">${step.result}</span></td>
                            <td width="100"><span class="${step_class_root}-step">${step.duration/ 1000} seconds</span></td>
                        </tr>
                        <#if step.result == "FAILURE" && !step.isAGroup()>
                            <tr class="test-${step.result}">
                                <td width="40">&nbsp</td>
                                <td width="%" colspan="4"><span class="error-message">${step.shortErrorMessage!''}</span></td>
                            </tr>
                        </#if>
                    </table>
                </div>
            </#macro>
            <#-- Test step results
            -->
            <#foreach step in testOutcome.testSteps>
                <@write_step step=step />
            </#foreach>
                <div class="bottomb"><img src="images/bottomm.jpg"/></div>
            </div>
        </div>
        <div id="beforefooter"></div>
        <div id="bottomfooter"></div>


        <script src="scripts/imgpreview.full.jquery.js" type="text/javascript"></script>

        <script type="text/javascript">
            //<![CDATA[
            jQuery.noConflict();
            (function($) {
                $('a').imgPreview({
                    imgCSS: {
                        width: '500px'
                    },
                    distanceFromCursor: {top:10, left:-200}
                });
            })(jQuery);
            //]]>
        </script>
        <div id="imgPreviewContainer" style="position: absolute; top: 612px; left: 355px; display: none; " class=""><img
                src="" style="display: none; "></div>
        <div id="imgPreviewContainer2" style="position: absolute; top: 925px; left: 320px; display: none; " class="">
            <img
                    style="width: 200px; display: none; " src=""></div>
        <div id="imgPreviewWithStyles" style="position: absolute; top: 1272px; left: 321px; display: none; " class="">
            <img
                    style="height: 200px; opacity: 1; display: none; " src=""></div>
        <div id="imgPreviewWithStyles2" style="display: none; position: absolute; "><img style="height: 200px; "></div>
        <div id="imgPreviewWithStyles3" style="display: none; position: absolute; "><img style="height: 200px; "></div>

</body>
</html>
