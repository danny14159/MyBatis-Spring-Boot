<#list pageInfo.list as country>
<tr>
    <td>${country.id}</td>
    <td>${country.countryname}</td>
    <td>${country.countrycode}</td>
    <td style="text-align:center;">[<a
            href="${request.contextPath}/countries/view/${country.id}">修改</a>] -
        [<a href="${request.contextPath}/countries/delete/${country.id}">删除</a>]
    </td>
</tr>
</#list>