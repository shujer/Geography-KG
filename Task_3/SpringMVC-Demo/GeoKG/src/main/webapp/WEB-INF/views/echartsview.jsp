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
            <div class="col-md-7 float-left" style="overflow: hidden">
                <div id="myChart" class="graph" style="border: 2px solid gray; border-radius: 5px;width: 550px;height: 500px;"></div>
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
                    show: false
                },
                categories: [],
                tooltip: {
                    trigger: 'item',
                    formatter: function(params, ticket, callback) {
                        var node = params.data;
                        if (node.source != null && node.target != null) {   // 边
                            return node.name;
                        } else {    // 点
                            return '';
                        }
                    }
                },
                legend: {
                  tooltip: {
                      show: true
                  }
                },
                animationDurationUpdate: 1500,
                animationEasingUpdate: 'quinticInOut',
                series : [
                    {
                        type: 'graph',
                        layout: 'force',
                        name: "",
                        hoverAnimation: true,
                        force: {
                            repulsion: 3000
                        },
                        symbolSize: 35,
                        draggable: true,
                        focusNodeAdjacency: true,
                        roam: true,
                        edgeSymbol: ['circle', 'arrow'],
                        edgeSymbolSize: [4, 10],

                        lineStyle: {
                            normal: {
                                width: 2,
                                curveness: 0,
                                opacity: 0.9
                            }
                        },
                        label: {
                            normal: {
                                show:true
                            }
                        },
                        edgeLabel: {
                            normal: {
                                show: true,
                                formatter: function (params) {
                                    var node = params.data;
                                    if (node.source != null && node.target != null) {   // 边
                                        return node.name;
                                    }
                                    return "";
                                }
                            }
                        },
                        data: [
                            {name: "1"},
                            {name: "2"}
                        ],
                        links: [
                            {source: "1", target: "2", name: "connect"}
                        ]
                    }
                ]
            };

            $(document).ready(function() {
                console.log("ready!");
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
                        console.log("\n");
                        console.log(data.links);
                        console.log("\n");
                        console.log(data.categories);
                        myChart.hideLoading();
                        myChart.setOption({
                            series: [{
                                data: data.nodes,
                                links: data.links,
                                categories: data.categories
                            }]
                        });
                        myChart.setOption({
                            legend: {
                                data: categories.map(function(a) {
                                    return a.name;
                                })
                            }
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
