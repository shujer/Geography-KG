<%@page contentType="text/html; charset=UTF-8" %>
<%@ taglib uri="http://kwonnam.pe.kr/jsp/template-inheritance" prefix="layout"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<layout:extends name="base">
    <layout:put block="head" type="APPEND">
        <title>首页</title>
        <link rel="stylesheet" type="text/css" href="<c:url value="app/css/home.css"/> "/>
    </layout:put>
    <layout:put block="content" type="REPLACE">
        <h1>Hello,${name}</h1>
        <form class="tdb-connect">
            <button class="btn"><a href="${pageContext.request.contextPath}/geokg">Echarts</a></button>
        </form>
    </layout:put>
</layout:extends>