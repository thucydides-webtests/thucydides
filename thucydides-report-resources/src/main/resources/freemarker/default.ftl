 <!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8" />
    <title>${testOutcome.title}</title>
    <link rel="shortcut icon" href="favicon.ico">
    <link rel="stylesheet" href="css/core.css"/>
    <link rel="stylesheet" type="text/css" href="jqplot/jquery.jqplot.min.css"/>

    <script src="scripts/jquery.js" type="text/javascript"></script>
    <script type="text/javascript" src="datatables/media/js/jquery.dataTables.min.js"></script>
    <script src="scripts/imgpreview.full.jquery.js" type="text/javascript"></script>

    <link type="text/css" href="jqueryui/css/start/jquery-ui-1.8.18.custom.css" rel="Stylesheet" />
    <script type="text/javascript" src="jqueryui/js/jquery-ui-1.8.18.custom.min.js"></script>

</head>

<body>
<div id="topheader">
    <div id="topbanner">
        <div id="logo"><a href="index.html"><img src="images/logo.jpg" border="0"/></a></div>
    </div>
</div>

<#-- HEADER
-->
<div class="middlecontent">
    <div id="contenttop">
        <div class="middlebg">
            <span class="bluetext"><a href="index.html" class="bluetext">Home</a> > ${testOutcome.title} </span>
        </div>
        <div class="rightbg"></div>
    </div>

    <div class="clr"></div>

    <!--/* starts second table*/-->
    <div class="menu">
        <ul>
            <li><a href="index.html" class="current">Test Results</a></li>
            <li><a href="capabilities.html">Requirements</a></li>
            <li><a href="progress-report.html">Progress</a></li>
            <#list allTestOutcomes.tagTypes as tagType>
                <#assign tagReport = reportName.forTagType(tagType) >
                <#assign tagTypeTitle = inflection.of(tagType).inPluralForm().asATitle() >
                <li><a href="${tagReport}">${tagTypeTitle}</a></li>
            </#list>
            <li><a href="history.html">History</a></li>
        </ul>
        <br style="clear:left"/>
    </div>

    <div class="clr"></div>

<#if testOutcome.result == "FAILURE"><#assign outcome_icon = "fail.png"><#assign outcome_text = "failing-color">
<#elseif testOutcome.result == "ERROR"><#assign outcome_icon = "cross.png"><#assign outcome_text = "error-color">
<#elseif testOutcome.result == "SUCCESS"><#assign outcome_icon = "success.png"><#assign outcome_text = "success-color">
<#elseif testOutcome.result == "PENDING"><#assign outcome_icon = "pending.png"><#assign outcome_text = "pending-color">
<#else><#assign outcome_icon = "ignor.png"><#assign outcome_text = "ignore-color">
</#if>
<#-- TEST TITLE-->
    <div id="contentbody">
        <div class="titlebar">
            <div class="story-title">
                <table width="1005">
                        <td width="50"><img class="story-outcome-icon" src="images/${outcome_icon}" width="25"
                                            height="25"/>
                        </td>
                        <#if (testOutcome.videoLink)??>
                            <td width="25"><a href="${testOutcome.videoLink}"><img class="story-outcome-icon" src="images/video.png" width="25" height="25" alt="Video"/></a></td>
                        </#if>
                        <td width="%"><span class="test-case-title"><span
                                class="${outcome_text}">${testOutcome.titleWithLinks}<span class="related-issue-title">${testOutcome.formattedIssues}</span></span></span>
                        </td>
                        <td width="100"><span class="test-case-duration"><span class="greentext">${testOutcome.durationInSeconds}s</span></span>
                        </td>
                    </tr>
                    <tr>
                        <td colspan="3">
                            <#if (parentRequirement.isPresent())>
                                <div>
                                    <#assign parentTitle = inflection.of(parentRequirement.get().name).asATitle() >
                                    <#assign parentType = inflection.of(parentRequirement.get().type).asATitle() >
                                    <#if (parentRequirement.get().cardNumber?has_content) >
                                        <#assign issueNumber = "[" + formatter.addLinks(parentRequirement.get().cardNumber) + "]" >
                                    <#else>
                                        <#assign issueNumber = "">
                                    </#if>
                                    <h3>${parentType}: ${issueNumber} ${parentTitle}</h3>
                                    <div class="requirementNarrativeTitle">
                                    ${formatter.addLineBreaks(parentRequirement.get().narrativeText)}
                                    </div>
                                </div>
                            </#if>
                        </td>
                    </tr>
                    <tr>
                        <td colspan="3">
                            <#list testOutcome.tags as tag>
                                <#assign tagReport = reportName.forTag(tag.name) />
                                <a class="tagLink" href="${tagReport}">${tag.name} (${tag.type})</a>
                            </#list>
                        </td>
                    </tr>
                </table>
            </div>
        </div>
    </div>

    <div class="clr"></div>

    <#if (testOutcome.isDataDriven())>
        <div class="story-title">
            <h3>Scenario:</h3>
            <div class="scenario">${formatter.addLineBreaks(testOutcome.dataDrivenSampleScenario)}</div>

        </div>
    </#if>

    <div id="beforetable"></div>

    <#if (testOutcome.isDataDriven())>
        <h3>Examples:</h3>
        <div class="example-table">
            <table>
                <thead>
                <tr>
                    <#list testOutcome.dataTable.headers as header>
                        <th>${inflection.of(header).asATitle()}</th>
                    </#list>
                </tr>
                </thead>
                <tbody>
                <#list testOutcome.dataTable.rows as row>
                    <tr class="test-${row.result}">
                    <#list row.values as value>
                        <td><a href="#${row_index}">${formatter.htmlCompatible(value)}</a></td>
                    </#list>
                    </tr>
                </#list>
                </tbody>
            </table>
        </div>
    </#if>

    <div id="tablecontents">
        <div>
            <table class="step-table">
                <tr class="step-titles">
                    <th width="40">&nbsp;</th>
                    <th width="%" class="greentext">Steps</th>
                    <#if testOutcome.hasScreenshots()>
                        <th width="150" class="greentext">Screenshot</th>
                    </#if>
                    <th width="150" class="greentext">Outcome</th>
                    <th width="75" class="greentext">Duration</th>
                </tr>
                <tr class="step-table-separator"><td colspan="5"></td></tr>
            <#if testOutcome.stepCount == 0>
                <#if testOutcome.result == "FAILURE">
                    <#assign step_outcome_icon = "fail.png">
                <#elseif testOutcome.result == "ERROR">
                    <#assign step_outcome_icon = "cross.png">
                </#if>
                <#if step_outcome_icon?has_content>
                    <tr class="test-${testOutcome.result}">
                        <td width="40">
                            <img style="margin-left: 20px; margin-right: 5px;"
                                 src="images/${step_outcome_icon}" class="top-level-icon"/>
                        </td>
                        <td width="%">
                            <span class="top-level-step">Exception occurred before any Steps could be executed.</span>
                        </td>
                        <td width="150"><span class="top-level-step">${testOutcome.result}</span></td>
                        <td width="100"><span class="top-level-step">${testOutcome.durationInSeconds}s</span></td>
                    </tr>
                    <tr class="test-${testOutcome.result}">
                        <td width="40">&nbsp</td>
                        <td width="%" colspan="4">
                                    <span class="error-message"
                                          title="${testOutcome.testFailureCause}">${testOutcome.testFailureCause}</span>
                        </td>
                    </tr>
                </#if>
            <#else>
                <#assign level = 1>
                <#assign screenshotCount = 0>
                <#macro write_step(step, step_number)>
                    <@step_details step=step step_number=step_number level=level/>
                    <#if step.isAGroup()>
                        <#if level == 1>
                        <tr>
                            <td colspan="5">
                                <table id="stepSection${step_number}" style="display:none; width:100%">

                        </#if>
                        <#assign level = level + 1>
                        <#list step.children as nestedStep>
                            <@write_step step=nestedStep step_number=""/>
                        </#list>
                        <#assign level = level-1>

                        <#if level == 1>
                                </table>
                            </td>
                        <tr>
                        </#if>
                    </#if>
                </#macro>
                <#macro step_details(step, step_number, level)>
                    <#if step.result == "FAILURE">
                        <#assign step_outcome_icon = "fail.png">
                    <#elseif step.result == "ERROR">
                        <#assign step_outcome_icon = "cross.png">
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
                    <#if level == 1 && step.isAGroup()>
                        <#assign showAccordion = true/>
                    <#else>
                        <#assign showAccordion = false/>
                    </#if>
                        <tr class="test-${step.result}">
                            <td width="40">
                                <#if step_number?has_content><a name="${step_number}"/></#if>
                                <#if showAccordion>
                                    <a href="javaScript:void(0)" onClick="toggleDiv('stepSection${step_number}')" style="display:block">
                                        <img src="images/plus.png" width="24" class="imgstepSection${step_number}" style="margin-left: 20px; float:left;  padding-right:5px"/>
                                    </a>
                                <#else>
                                    <img style="margin-left: ${step_indent}px; margin-right: 5px;"
                                         src="images/${step_outcome_icon}" class="${step_class_root}-icon"/>
                                </#if>
                            </td>
                            <td width="%">
                                <#if showAccordion>
                                    <a href="javaScript:void(0)" onClick="toggleDiv('stepSection${step_number}')" style="display:block">
                                </#if>
                                    <span class="${step_class_root}-step">${formatter.htmlCompatible(step.description)}</span>
                                <#if showAccordion>
                                    </a>
                                </#if>
                            </td>
                            <#if testOutcome.hasScreenshots()>
                            <td width="100" class="${step.result}-text">
                                <#if !step.isAGroup() && step.firstScreenshot??>
                                    <a href="${testOutcome.screenshotReportName}.html#screenshots?screenshot=${screenshotCount}">
                                        <img src="${step.firstScreenshot.screenshotFile.name}"
                                             class="screenshot"
                                             width="48" height="48"/>
                                        <#assign screenshotCount = screenshotCount + step.screenshotCount />
                                    </a>
                                </#if>
                            </td>
                            </#if>
                            <td width="150"><span class="${step_class_root}-step">${step.result}</span></td>
                            <td width="100"><span class="${step_class_root}-step">${step.durationInSeconds}s</span></td>
                        </tr>
                        <#if (step.result == "FAILURE" || step.result == "ERROR") && !step.isAGroup()>
                            <tr class="test-${step.result}">
                                <td width="40">&nbsp</td>
                                <#if step.errorMessage?has_content>
                                    <#assign errorMessageTitle = step.errorMessage?html>
                                <#else>
                                    <#assign errorMessageTitle = "">
                                </#if>
                                <td width="%" colspan="4">
                                    <span class="error-message" title="${errorMessageTitle}">${step.shortErrorMessage!''}</span>
                                </td>
                            </tr>
                        </#if>
                </#macro>
                <#-- Test step results -->
                <#list testOutcome.testSteps as step>
                    <@write_step step=step step_number=step_index />
                </#list>
                </#if>
                </table>
            </div>
        <div id="beforefooter"></div>
        <div id="bottomfooter"></div>


        <script type="text/javascript">
            function toggleDiv(divId) {
                $("#"+divId).toggle();
                var imgsrc=$(".img"+divId).attr('src');
                if(imgsrc=='images/plus.png'){
                    $(".img"+divId).attr("src", function() {
                        return "images/minus.png";
                    });

                }
                else{
                    $(".img"+divId).attr("src", function() {
                        return "images/plus.png";
                    });
                }
            }
        </script>

        <script type="text/javascript">
            $('.example-table table').dataTable( {
                "aaSorting": [[ 1, "asc" ]],
                "bJQueryUI": true,
                "sScrollX": "100%",
                "sScrollXInner": "120%",
                "bScrollCollapse": true
            } );
        </script>

        <script type="text/javascript">
            //<![CDATA[

            function($) {
                $('a').imgPreview({
                    imgCSS: {
                        width: '500px'
                    },
                    distanceFromCursor: {top:10, left:-200}
                });
            }
            //]]>
        </script>
        <div id="imgPreviewContainer" style="position: absolute; top: 612px; left: 355px; display: none; " class=""><img
                src="" style="display: none; "></div>
        <div id="imgPreviewContainer2" style="position: absolute; top: 925px; left: 320px; display: none; " class="">
            <img style="width: 200px; display: none; " src=""></div>
        <div id="imgPreviewWithStyles" style="position: absolute; top: 1272px; left: 321px; display: none; " class="">
            <img style="height: 200px; opacity: 1; display: none; " src=""></div>
        <div id="imgPreviewWithStyles2" style="display: none; position: absolute; "><img style="height: 200px; "></div>
        <div id="imgPreviewWithStyles3" style="display: none; position: absolute; "><img style="height: 200px; "></div>

</body>
</html>
