<%@page contentType="text/html; charset=UTF-8" %>
<%@ taglib uri="http://kwonnam.pe.kr/jsp/template-inheritance" prefix="layout"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html>
<head>
    <layout:block name="head">
        <meta charset="UTF-8">
        <meta name="viewport" content="width=device-width, initial-scale=1, shrink-to-fit=no">
        <link rel="stylesheet" type="text/css" href="<c:url value="app/css/base.css"/> "/>
        <link rel="stylesheet" type="text/css" href="<c:url value="/bootstrap/css/bootstrap.min.css"/> "/>
    </layout:block>
</head>
<body>
<header>
    <layout:block name="topnav">
        <nav class="navbar navbar-expand-lg navbar-dark">
            <a class="navbar-brand" href="#">Geography-KG</a>
            <button class="navbar-toggler" type="button" data-toggle="collapse" data-target="#navbarTogglerDemo02" aria-controls="navbarTogglerDemo02" aria-expanded="false" aria-label="Toggle navigation">
                <span class="navbar-toggler-icon"></span>
            </button>
            <div class="collapse navbar-collapse" id="navbarTogglerDemo02">
                <ul class="navbar-nav mr-auto mt-2 mt-lg-0">
                </ul>
                <form class="form-inline my-2 my-lg-0">
                    <input class="form-control mr-sm-2" type="search" placeholder="Search">
                    <button id="search_button" class="btn my-2 my-sm-0 btn-success" type="submit">Search</button>
                </form>
            </div>
        </nav>
    </layout:block>
</header>

<main role="main" class="container">
    <layout:block name="content">CONTENT</layout:block>
</main>

<footer class="footer">
    <layout:block name="footer">
        <div class="container">
            <span class="text-muted">Copyright © 1998 - 2018 SYSU. All Rights Reserved.GeoKG 版权所有</span>
        </div>
        <script src="<c:url value="app/js/jquery-3.2.1.slim.min.js"/>"></script>
        <script src="<c:url value="/bootstrap/js/bootstrap.min.js"/>"></script>
    </layout:block>
</footer>

</body>
</html>

<style>
    h1 {
        background-color: aquamarine;
        font-size:30px;
    }

    button {
        background-color: #0c5460;
    }
    button a {
        color: whitesmoke;
    }
</style>