<%@page contentType="text/html; charset=UTF-8" %>
<%@ taglib uri="http://kwonnam.pe.kr/jsp/template-inheritance" prefix="layout"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<layout:extends name="base">
    <layout:put block="head" type="APPEND">
        <title>首页</title>
        <link rel="stylesheet" type="text/css" href="<c:url value="app/css/home.css"/> "/>
    </layout:put>
    <layout:put block="content" type="REPLACE">
        <p>Hello,${name}</p>
        <form class="tdb-connect">
            <button class="btn" style="background-color: #3b8686"><a href="${pageContext.request.contextPath}/echartsview" style="color:whitesmoke">Echarts</a></button>
        </form>
    </layout:put>
</layout:extends>