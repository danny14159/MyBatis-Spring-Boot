
<table>
<#list list as item1>
    <#if item1??>
<tr>
    <td>${item1_index + 1}</td>
    <#list item1.keySet() as testKey>
        <td>${item1.get(testKey)}${testKey}</td>
    </#list>
</tr>
    </#if>
</#list>
</table>