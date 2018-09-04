<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<%@ taglib uri="http://kwonnam.pe.kr/jsp/template-inheritance" prefix="layout"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<layout:extends name="base">
    <layout:put block="head" type="APPEND">
        <title>Echarts</title>
        <link rel="stylesheet" type="text/css" href="<c:url value="app/css/home.css"/> "/>
    </layout:put>
    <layout:put block="content" type="REPLACE">
        <div class="clearfix">
            <div class="query col-md-5 float-left">
                <%--TDB--%>
                <form >
                    <button class="btn btn-info" type="submit">连接TDB</button>
                </form>
                <%--SPARQL--%>
                <form id ="sparqlForm"
                      name = "sparqlForm">
                    <div class="form-group">
                        <label for="FormControlTextarea1">SPARQL查询</label>
                        <textarea class="form-control" id="FormControlTextarea1" rows="9" name="sparql"></textarea>
                    </div>
                    <button class="btn btn-success" type="button" onclick="sparqlQuery()">确认</button>
                </form>
                <%--QA--%>
                <form name="qaForm"
                      method="post"
                      action="${pageContext.request.contextPath}/echartsview/qaQyery.action">
                    <div class="form-group">
                        <label for="FormControlTextarea2">QA查询</label>
                        <textarea class="form-control" id="FormControlTextarea2" rows="1" name="qa"></textarea>
                    </div>
                    <button type="submit" class="btn btn-success">确认</button>
                </form>
            </div>
            <div class="col-md-7 float-left" style="background-color: #3b8686;overflow: hidden">
                <div id="myChart" class="graph" style="width: 550px;height: 500px"></div>
            </div>
        </div>
    </layout:put>
    <layout:put block="footer" type="APPEND">
        <script src="<c:url value="app/js/echarts.min.js"/> "></script>
        <script src="<c:url value="app/js/jquery-3.3.1.min.js"/> "></script>
        <script type="text/javascript">
            var myChart = echarts.init(document.getElementById("myChart"));
            var options = {
                title: {
                    show: false,
                },
                tooltip: {},
                animationDurationUpdate: 1500,
                animationEasingUpdate: 'quinticInOut',
                series : [
                    {
                        type: 'graph',
                        layout: 'force',
                        force: {
                            repulsion: 3000,
                        },
                        name: 'queryResult',
                        symbolSize: 35,
                        roam: true,
                        draggable: true,
                        focusNodeAdjacency: true,
                        hoverAnimation: true,
                        edgeSymbol: ['circle', 'arrow'],
                        edgeSymbolSize: [4, 10],
                        label: {
                            normal: {
                                show: true
                            }
                        },
                        edgeLabel: {
                            show: true,
                            normal: {
                                textStyle: {
                                    fontSize: 12
                                }
                            }
                        },
                        data: [{
                            name: "1"
                        }, {
                            name: "2"
                        }],
                        links: [{
                            source: "1",
                            target: "2"
                        }],
                        lineStyle: {
                            normal: {
                                opacity: 0.9,
                                width: 2,
                                curveness: 0
                            }
                        }
                    }
                ]
            };
            $( document ).ready(function() {
                console.log( "ready!" );
                myChart.setOption(options);
            });
            function sparqlQuery() {
                var mydata = $("#FormControlTextarea1").val();
                console.log(mydata);
                if(!mydata) {
                    alert("输入不能为空");
                    return;
                }
                myChart.showLoading();
                $.ajax({
                    type: "get",
                    url: "${pageContext.request.contextPath}/echartsview/sparql.do",
                    data: {"sparql": mydata},
                    success: function (data) {
                        console.log("成功");
                        // var obj = result.nodes;
                        console.log(data.nodes);
                        console.log(data.links);
                        myChart.hideLoading();
                        myChart.setOption({
                            series: [{
                                data: data.nodes,
                                links: data.links
                            }]
                        });
                    },
                    error: function () {
                        console.log("失败");
                    }
                })
            }
        </script>
    </layout:put>
</layout:extends>
