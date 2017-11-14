<!DOCTYPE html>
<html lang="en">
<head>
<meta charset="utf-8">
<meta http-equiv="X-UA-Compatible" content="IE=edge">
<link rel="shortcut icon" href="favicon.ico" type="image/x-icon" />
<meta name="viewport" content="width=device-width, initial-scale=1">
<!-- The above 3 meta tags *must* come first in the head; any other head content must come *after* these tags -->
<meta name="description" content="">
<meta name="author" content="">



<title>CAF - PMT</title>

<!-- Bootstrap core CSS -->
<link href="styles/bootstrap.css" rel="stylesheet">
<link
	href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/4.7.0/css/font-awesome.css"
	rel="stylesheet">
<link
	href="https://cdnjs.cloudflare.com/ajax/libs/bootstrap3-dialog/1.35.4/css/bootstrap-dialog.css"
	rel="stylesheet">
<style>
#search_results {
	float: left;
	list-style: none;
	margin-top: -3px;
	padding: 0;
	position: absolute;
}

#search_results li {
	padding: 10px;
	background: #cccccc;
	border-bottom: #bbb9b9 1px solid;
}

#search_results li:hover {
	background: #428BCA;
	cursor: pointer;
}

#horizontal-style {
	display: table;
	width: 100%;
	/*table-layout: fixed;*/
}

#horizontal-style li {
	display: table-cell;
}

#horizontal-style a {
	display: block;
	border: 1px solid red;
	text-align: center;
	margin: 0 5px;
	background: #999;
}
</style>

<!-- Custom styles for this template -->
<link href="styles/custom.css" rel="stylesheet">


<!-- HTML5 shim and Respond.js for IE8 support of HTML5 elements and media queries -->
<!--[if lt IE 9]>
      <script src="https://oss.maxcdn.com/html5shiv/3.7.3/html5shiv.min.js"></script>
      <script src="https://oss.maxcdn.com/respond/1.4.2/respond.min.js"></script>
    <![endif]-->
</head>

<body>

	<nav class="navbar navbar-inverse navbar-fixed-top">
		<div class="container">
			<div class="navbar-header">
				<button type="button" class="navbar-toggle collapsed"
					data-toggle="collapse" data-target="#navbar" aria-expanded="false"
					aria-controls="navbar">
					<span class="sr-only">Toggle navigation</span> <span
						class="icon-bar"></span> <span class="icon-bar"></span> <span
						class="icon-bar"></span>
				</button>
				<a class="navbar-brand" href="index.html"><i class="fa fa-paper-plane-o"></i> CAF</a>
			</div>
			<div id="navbar" class="collapse navbar-collapse">
				<ul class="nav navbar-nav">
					<li><a href="index.html">Home</a></li>
					<li><a href="comparePortfolio.html">Analysis</a></li>
					
				</ul>
			</div>
			
			<!--/.nav-collapse -->
		</div>
	</nav>

	<div class="container">


		<%
			if (request.getParameter("portfolioId") == null) {
				out.println("<p>Please enter your Portfolio.</p>");
			} else {

				out.println("<h2>" + request.getParameter("portfolioName") + "</h2>");
				out.println("<input id='portfolioId' type='hidden' value=" + request.getParameter("portfolioId") + ">");
				out.println("<input id='portfolioName' type='hidden' value=" + request.getParameter("portfolioName") + ">");
			}
		%>
		<button id='btnDeletePortfolio' class="btn btn-danger" >Delete</button>
		<button id='btnEditPortfolio' class="btn btn-info" >Modify</button>
		
		<hr>
		<div class="row">
		<div id="comparisonChart" class="col-md-6">


			
			
		</div>
		<div id="pieStockChart" class="col-md-6">
		</div>
		</div>

		<div id="divTable" class="row">
		<input id="btnExport" onclick="tableToExcel('comparisonTable', 'Portfolio Performance')" type="button" class="btn btn-primary" value="Export to excel" />
		<table id="comparisonTable" class='table table-striped'>
		</table>
		
		</div>


		

	</div>
	<!-- /.container -->


	<!-- Bootstrap core JavaScript
    ================================================== -->
	<!-- Placed at the end of the document so the pages load faster -->
	<script
		src="https://ajax.googleapis.com/ajax/libs/jquery/1.12.4/jquery.min.js"></script>
	<script src="scripts/bootstrap.js"></script>
	<script
		src="https://cdnjs.cloudflare.com/ajax/libs/bootstrap3-dialog/1.35.4/js/bootstrap-dialog.min.js"></script>

	<script src="https://code.highcharts.com/highcharts.js"></script>
	<!-- <script src="https://code.highcharts.com/modules/series-label.js"></script>  -->
	<script src="https://code.highcharts.com/modules/exporting.js"></script>
	<script>
	$(document).ready(function() {
		
		
		var portfolioId=$('#portfolioId').val();
		var stockCount=0;
		
		var lineChartJsonObject=[];
		
		var dataCount=0;
		var jsonoutput='[';
		$.ajax({
			
			type:'get',
			url:'rest/caf/stockcompare/'+portfolioId,
			success:function(data){
				console.log(data);
				for(i=0;i<data.length;i++){
					if(i>=data.length-1) break;
					jsonoutput+='{"name":"'+data[i].ticker+'","data":[';
					var currentStockId=data[i].stockId;
					var stockGainList=[];
					while(currentStockId==data[i].stockId){
						date=data[i].endDate
					
						jsonoutput+='["'+date.substring(0,4)+'-'+date.substring(4,6)+'-'+date.substring(6,8)+'",'+data[i].percentageChange+'],';
						//performanceData[portfolioCount][dataCount++]=data[i].percentageChange;
						//sconsole.log('portfolioId '+data[i].portfolioId+' '+data[i].percentageChange);
						stockGainList.push([Date.UTC(date.substring(0,4),date.substring(4,6)-1,date.substring(6,8)),data[i].percentageChange]);
						i++;
						if(i>=data.length-1) break;
						
					}
					
					jsonoutput = jsonoutput.slice(0, -1);
					jsonoutput+=']},';
					randomColor=genColor(data[i].stockId);
					//console.log(randomColor);
					
					lineChartJsonObject.push({"name":data[i].ticker, "data":stockGainList});
					
				}
				jsonoutput = jsonoutput.slice(0, -1);
				jsonoutput+=']';
				 
				 chartData=JSON.parse(jsonoutput);
				 console.log(lineChartJsonObject);
				 createLineChart(lineChartJsonObject);
				//console.log(performanceData);
				
				
			}
		});
		
		var stringPie=' [';
		var jsonObject=[];
		$.ajax({
			
			type:'get',
			url:'rest/caf/stockcomparepie/'+portfolioId,
			success:function(data){
				
				var total=0;
				
			for(i=0;i<data.length;i++){
				
				total+=data[i].marketValue
				
			}
			
			for(i=0;i<data.length;i++){
				
				jsonObject.push({"name":data[i].ticker,"y":(data[i].marketValue/total)*100});
				//stringPie+='{"name":'+'"'+data[i].ticker+'","y":'+(data[i].marketvalue/total)*100+'},';
				
				
			}
			
			stringPie=stringPie.slice(0,-1);
			stringPie+=']';
			//jsonData=JSON.parse(stringPie);
			console.log(jsonObject);
			createPieChart(jsonObject);
			
			},
			error: function(data){
 				console.log("Error");
 				alert("Failed to get data. Please contact support")
 			}
		
			});
		

  			var tableString="";
  			tableString+="<thead><tr><th>Ticker</th><th>Exchange</th><th>% Gain</th><th>Invested Amount</th><th>Current Market Value</th><th>Absolute Change</th><th>Yearly Yield</th></tr></thead>";
  			
  			$.ajax({
 				 
 				 type:'get',
 				 url:'rest/caf/stockcomparepie/' + portfolioId,
 				datatype:'json',
	 			cache:false,
	 			success:function(data){
	 				for(i=0;i<data.length;i++){
	 					tableString+="<tr>";
	 					if(data[i].absoluteChange<0){
	 						tableString+="<td>"+data[i].ticker+"</td><td>"+data[i].exchangeName+"</td><td style='color:#ff4444;'>"+data[i].percentageChange+"%</td>";
		 			    	tableString+="<td>$"+data[i].initialInvestment+"</td><td>$"+data[i].marketValue+"</td>";
		 			    	tableString+="<td style='color:#ff4444;'>$"+data[i].absoluteChange+"</td><td style='color:#ff4444;'>"+Math.round(((data[i].yield)/11)*365).toFixed(2)+"%</td>";
		 			    	tableString+="</tr>" ;
	 						
	 					}
	 					else{
	 						tableString+="<td>"+data[i].ticker+"</td><td>"+data[i].exchangeName+"</td><td style='color:#00C851;'>"+data[i].percentageChange+"%</td>";
		 			    	tableString+="<td>$"+data[i].initialInvestment+"</td><td>$"+data[i].marketValue+"</td>";
		 			    	tableString+="<td style='color:#00C851;'>$"+data[i].absoluteChange+"</td><td style='color:#00C851;'>"+Math.round(((data[i].yield)/11)*365).toFixed(2)+"%</td>";
		 			    	tableString+="</tr>" ;
	 					}
	 			    	
	 					
	 				}
	 					
   
   	
   	$('#comparisonTable').html(tableString);	
	 				

	 			},
	 			
	 			error: function(data){
	 				console.log("Error");
	 				alert("Failed to get data. Please contact support")
	 			}

 			 });
  			
  			$('#btnDeletePortfolio').click(function(e){
				
				  BootstrapDialog.confirm('Do you want to delete the portfolio?', function(result){
			            if(result) {
			            	
			            	$.ajax({
			            		type:'get',
			            		url:'rest/caf/deleteportfolio/'+portfolioId,//delete portfolio url
			            		success:function(data){
			        				
			        				if(data){
			        					//success
			        					window.location.href = "index.html"; //redirect to home
			        				}
			        				else{
			        					//failure
			        				}
			        				
			        			},
			        			error: function(data){
			         				console.log("Error");
			         				Bootstrap.alert("Failed to get data. Please contact support")
			         			}
			        			
			            		
			            	});
			                
			            }
			        });
			});
			
			$('#btnEditPortfolio').click(function(e){
				
				
				window.location.href = "editPortfolio.jsp?portfolioId="+portfolioId+"&portfolioName="+$('#portfolioName').val();
				
				
			});

	});
  			
  		
  			
  			
			//$('#comparisonTable').html(tableString);	
  			/*ajax
  			
  			 $.ajax({
	 			
	 			type:"get",
	 			url:url,
	 			datatype:'json',
	 			cache:false,
	 			success:function(data){
	 				
	 				if(data>0){
	 					//success
	 					
	 					//for loop for json
	 					
	 					/*
    	//ajax code for database
    	tableString+="<tr>";
    	tableString+="<td>"+stockTicker+"</td><td>"+tradeDate+"</td>";
    	tableString+="<td>"+open+"</td><td>"+high+"</td>";
    	tableString+="<td>"+low+"</td><td>"+close+"</td><td>"+volume+"</td>";
    	tableString+="</tr>" ;
    	*/
    	/*
    	tableString+="</table>";
	 					$('#stockTable').html(tableString);	
	 				}
	 				else{
	 					//failed
	 				}
	 				
	 			},
	 			error: function(data){
	 				console.log("Error");
	 				alert("Failed to add portfolio. Please contact support")
	 			}
	 			*/
  			
  			
  			
	
	
	
  			
	var tableToExcel = (function() {
		  var uri = 'data:application/vnd.ms-excel;base64,'
		    , template = '<html xmlns:o="urn:schemas-microsoft-com:office:office" xmlns:x="urn:schemas-microsoft-com:office:excel" xmlns="http://www.w3.org/TR/REC-html40"><head><!--[if gte mso 9]><xml><x:ExcelWorkbook><x:ExcelWorksheets><x:ExcelWorksheet><x:Name>{worksheet}</x:Name><x:WorksheetOptions><x:DisplayGridlines/></x:WorksheetOptions></x:ExcelWorksheet></x:ExcelWorksheets></x:ExcelWorkbook></xml><![endif]--><meta http-equiv="content-type" content="text/plain; charset=UTF-8"/></head><body><table>{table}</table></body></html>'
		    , base64 = function(s) { return window.btoa(unescape(encodeURIComponent(s))) }
		    , format = function(s, c) { return s.replace(/{(\w+)}/g, function(m, p) { return c[p]; }) }
		  return function(table, name) {
		    if (!table.nodeType) table = document.getElementById(table)
		    var ctx = {worksheet: name || 'Worksheet', table: table.innerHTML}
		    window.location.href = uri + base64(format(template, ctx))
		  }
		})()
		
		
		function createLineChart(data){
		
		Highcharts.chart('comparisonChart', {

		    title: {
		        text: 'Portfolio Comparison'
		    },
		    
		    xAxis:{
		    	
		    	 type: 'datetime'
		           
		    },

		    yAxis: {
		        title: {
		            text: '% return'
		        }
		    },
		    legend: {
		        layout: 'vertical',
		        align: 'right',
		        verticalAlign: 'middle'
		    },

		    plotOptions: {
		        series: {
		            label: {
		                connectorAllowed: false
		            },
		           
		        }
		    },

		    series: data,

		    responsive: {
		        rules: [{
		            condition: {
		                maxWidth: 500
		            },
		            chartOptions: {
		                legend: {
		                    layout: 'horizontal',
		                    align: 'center',
		                    verticalAlign: 'bottom'
		                }
		            }
		        }]
		    }
		});
		
		
		
	}
	
	
	function createPieChart(data){
		
		 // Pie chart
	    Highcharts.chart('pieStockChart', {
	        chart: {
	            plotBackgroundColor: null,
	            plotBorderWidth: null,
	            plotShadow: false,
	            type: 'pie'
	        },
	        title: {
	            text: 'Stock Distribution By Current Value'
	        },
	        tooltip: {
	            pointFormat: '{series.name}: <b>{point.percentage:.1f}%</b>'
	        },
	        plotOptions: {
	            pie: {
	                allowPointSelect: true,
	                cursor: 'pointer',
	                dataLabels: {
	                    enabled: false
	                },
	                showInLegend: true
	            }
	        },
	        series: [{
	            name: 'Stocks',
	            colorByPoint: true,
	            data: data
	        }]
	    });
	
		
		
	}
	
	function genColor (seed) {
	    color = Math.floor((Math.abs(Math.sin(seed) * 16777215)) % 16777215);
	    color = color.toString(16);
	    // pad any colors shorter than 6 characters with leading 0s
	    while(color.length < 6) {
	        color = '0' + color;
	    }
	}
	   
		
	</script>
</body>
</html>