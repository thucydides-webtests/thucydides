<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
<title>Stories</title>
<link rel="shortcut icon" href="favicon.ico" >
<style type="text/css">
<!--
@import url("css/core.css");
-->
</style>
<link href="css/core.css" rel="stylesheet" type="text/css" />
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
</style></head>

<body>
<div id="topheader">
  <div id="topbanner">
      <div id="menu">
      <table border="0">
      <tr>
        <td><a href="index.html"><img src="images/menu_h.png" width="105" height="28" border="0" /></a></td>
        <td><a href="Features.html"><img src="images/menu_f.png" width="105" height="28" border="0" /></a></td>
        <td><a href="Stories.html"><img src="images/menu_s.png" width="105" height="28" border="0" /></a></td>
      </tr>
    </table>
    </div>
    <div id="logo"><a href="index.html"><img src="images/logo.jpg" width="265" height="96" border="0" /></a></div>
  </div>
</div>
    
  <div class="middlecontent">
    <div id="contenttop"> 
      <div class="tall_leftbg"></div>
        <div class="tall_middlebg"><div style="height:30px;"><span class="bluetext">Home</span> / <span class="lightgreentext"><a href="Features.html" class="lightgreentext">Features</a></span> / <span class="lightgreentext"><a href="Stories.html" class="lightgreentext">Stories</a></span></div>
      </div>
        <div class="tall_rightbg"></div>
    </div>
    <div class="clr"></div>
    
    <#if story.result == "FAILURE"><#assign outcome_icon = "fail.png"><#assign outcome_text = "failing-color">    <#elseif story.result == "SUCCESS"><#assign outcome_icon = "success.png"><#assign outcome_text = "success-color">    <#elseif story.result == "PENDING"><#assign outcome_icon = "pending.png"><#assign outcome_text = "pending-color">    <#else><#assign outcome_icon = "ignor.png"><#assign outcome_text = "ignore-color">    </#if>
    
    <div id="contentbody">
      <div class="titlebar">
        <div class="tall_leftbgm"></div>
        <div class="tall_middlebgm">
            <table width="980">
                <tr>
                    <td width="25px" valign="center" height="72px"><img class="story-outcome-icon" src="images/${outcome_icon}" width="25px" height="25px" /></td>
                    <td width="%"><span class="test-case-title"><span class="${outcome_text}">${story.titleWithLinks}<span class="related-issues">${story.formattedIssues}</span></span></span></td>
                    <td width="75px"><span class="test-case-duration"><span class="greentext">${story.duration / 1000}s</span></span></td>
                </tr>
            </table>
        </div>
        <div class="tall_rightbgm"></div>
      </div>
     </div>
     <div class="clr"></div>
     <div id="beforetable"></div>
    <div id="contenttilttle">
      <div class="topb"><img src="images/topm.jpg" /></div>
      <div class="middlb">
<div class="table">         
 <div class="toptablerow">
          <table width="980" height="50" border="0">
  <tr>
    <td width="35">&nbsp;</td>
    <td width="%" class="greentext">Acceptance Criteria</td>
    <td width="80" class="greentext">Steps</td>
    <td width="80" class="greentext">Failed</td>
    <td width="80" class="greentext">Pending</td>
    <td width="80" class="greentext">Ignored</td>
    <td width="80" class="greentext">Skipped</td>
    <td width="95" class="greentext">Duration</td>
  </tr>
</table>
</div>

<#foreach testOutcome in story.testOutcomes>    <#if testOutcome.result == "FAILURE"><#assign testrun_outcome_icon = "fail.png">    <#elseif testOutcome.result == "SUCCESS"><#assign testrun_outcome_icon = "success.png">    <#elseif testOutcome.result == "PENDING"><#assign testrun_outcome_icon = "pending.png">    <#else><#assign testrun_outcome_icon = "ignor.png">    </#if>    <div class="tablerow">
      <table border="0" height="40" width="980" >
      <tr class="test-${testOutcome.result}">
        <td width="35"><img src="images/${testrun_outcome_icon}" class="outcome-icon"/></td>
        <td width="%" class="bluetext"><a href="${testOutcome.reportName}.html">${testOutcome.titleWithLinks} ${testOutcome.formattedIssues}</a></td>
        <td width="80" class="lightgreentext">${testOutcome.stepCount}</td>
        <td width="80" class="redtext">${testOutcome.failureCount}</td>
        <td width="80" class="bluetext">${testOutcome.pendingCount}</td>
        <td width="80" class="bluetext">${testOutcome.skippedCount}</td>
        <td width="80" class="bluetext">${testOutcome.ignoredCount}</td>
        <td width="95" class="lightgreentext">${testOutcome.duration / 1000}s</td>
      </tr>
      </table>
    </div>
</#foreach></div>


</div>
      </div>

      <div class="bottomb"><img src="images/bottomm.jpg" /></div>
    </div>
</div>
<div id="beforefooter"></div>
<div id="bottomfooter"></div>

</body>
</html>
