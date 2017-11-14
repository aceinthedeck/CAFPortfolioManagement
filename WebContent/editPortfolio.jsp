<%@page import= "com.caf.model.PortfolioDto,com.caf.data.PortfolioDao,java.util.List,java.util.ArrayList"%>
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



<title>CAF PMT</title>

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
				out.println("<p>No Portfolio Found</p>");
			} 
			else {
				int portfolioIdReq = Integer.parseInt(request.getParameter("portfolioId")); //portfolioId num
				String portfolioName="";
				
				PortfolioDao portDao = new PortfolioDao();
				PortfolioDto portfolioN = null;
				
				portfolioN = portDao.getPortfolioById(portfolioIdReq);
				portfolioName= portfolioN.getPortfolioName();
				
				out.println("<h2>"  + request.getParameter("portfolioName") + "</h2>" );
				out.println("<input id='portfolioId' type='hidden' value=" + request.getParameter("portfolioId")+ ">");
				out.println("<input id='portfolioName' type='hidden' value=" + request.getParameter("portfolioName")+ ">");
			}
		
		%>
		<select id="exchangeList" name="exchangeList" class="form-control" >
		</select>
		<h3>Search companies</h3>
		<hr />
		<div class="form-horizontal" role="form">
			<div class="form-group">
				<label class="control-label col-sm-2" for="email">Company
					name:</label>
				<div class="col-sm-10">
					<input type="text" class="form-control" id="txtSearchString" />
				</div>
			</div>
			<div id="search_results" class="form-horizontal"></div>
		</div>


		<div class="row">

			<div class="col-md-6"></div>
			<div class="col-md-6">

				<div class="panel panel-default">
					<div class="panel-heading">Added Stocks</div>
					<div class="panel-body">
						<div class="list-group" id="stocksList">
						<%
						
						if (request.getParameter("portfolioId") == null) {
							out.println("<p>No Portfolio Found</p>");
						} 
						else {
							int portfolioIdReq = Integer.parseInt(request.getParameter("portfolioId")); //portfolioId num
							List<String> stockList=new ArrayList<String>();
							List<Integer> stockId=new ArrayList<Integer>();
							
							PortfolioDao portDao = new PortfolioDao();
							PortfolioDto portfolioDto = null;
							
							portfolioDto = portDao.getPortfolioById(portfolioIdReq);
							stockList=portfolioDto.getStockNames();
							
							for(int i=0;i<stockList.size();i++){
								
								stockId.add(portDao.getStockIdByTicker(stockList.get(i)));
								
								
							}
							
							for(int i=0;i<stockList.size();i++){
								
								out.println("<a href='#' id='"+stockId.get(i)+"' class='list-group-item'>"+stockList.get(i)+"</a>");
							}
							
							//portfolioName= portfolioN.getPortfolioName();
							
						}
						
						%>
						
						
						</div>
						<input id="btnSavePortfolio" type="button" class="btn btn-primary"
							value="Save"></input>
					</div>
				</div>
			</div>

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

	<script>
		$(document).ready(function() {

			//fill the exchange dropdown
			
			$.ajax({
				
				type:"get",
				url:"rest/caf/allexchanges",
				success: function(data){
					var strOption = "";
					for(i=0;i<data.length;i++){
						for(i=0; i<data.length; i++){
							
				     		strOption +="<OPTION VALUE=" + data[i].exchangeId + ">" + data[i].exchangeName + "</OPTION>";
				     	}
				     	$("#exchangeList").append(strOption);
						
						
					}
				}
				
			
			});
			
			
			
		
			
			
				$("#txtSearchString").on('keyup',function() {

					var key = $('#txtSearchString').val();
					var exchangeId=$('#exchangeList').val();
					var req = null;
					var output = "";

					if (key && key.length > 0) {

						req = $.ajax({
							
							type : "get",
							url : "rest/caf/stocksbyexchange/"+ exchangeId + "/" + key,
							success : function(data) {
							//JSON.stringify(data);
							   
							for (i = 0; i < data.length; i++) {
								
								if(data[i].date=='20170911'){
									output += "<li onClick=selectTicker('"+ data[i].ticker+ "',"+ data[i].stockId+ ")><b>"
											+ data[i].ticker+ "</b> - Open: $"+data[i].open+"</li>";
								}

							}
							/*
							thisObject= document.getElementById('txtSearchString');
							if(!thisObject.suggestion) {
	           					 var rect = thisObject.getBoundingClientRect();
	            				 var left = rect.left;
	            				 var top = rect.bottom;
	            				 thisObject.suggestion = document.createElement('DIV');            
	            				 thisObject.suggestion.innerHTML = output;
	            				 thisObject.suggestion.setAttribute('style', 'background: #B0B0B0; padding: 6px; position: absolute; top: ' + top + 'px; left: ' + left + 'px;');
	            				 thisObject.parentNode.appendChild(thisObject.suggestion);
	        					} else {
	           						 thisObject.suggestion.innerHTML = output;
	           						 thisObject.suggestion.style.display = 'block';
	        					} 
							*/
							$('#search_results').html(output);
							}
						});
					}
					else{
						$('#search_results').html('');
					}

				});

							$('#stocksList').on('click', 'a', function(e) {

								this.remove();

							});

							$('#btnSavePortfolio').click(function(e) {
								
								count = 0;
								$('#stocksList').children('a').each(function(){ 
									count++;
									
								});
								if(count != 10){
									BootstrapDialog
									.alert("Portfolio must contain ten stocks.");
								}
								else{
											
								var portfolioId = $('#portfolioId').val();

												$.ajax({
													
													type:"get",
													url:"rest/caf/deletestock/"+portfolioId,
													success: function(data){
														
														if(data){
															//success
														}
														else{
															//error in deleting
														}
												
													}
													
												
												});
												
												var fail = false;

												$('#stocksList').children('a').each(
																function() {
																	var stockId = this.id;

																	$
																			.ajax({
																				type : "get",
																				url : "rest/caf/addstock/"
																						+ portfolioId
																						+ "/"
																						+ stockId,//change the url
																				success : function(
																						data) {

																					if (data > 1) {
																						//success
																					} else {
																						fail = true;
																					}

																				}
																			});

																});

												if (!fail) {
													BootstrapDialog.show({
											            message: 'Portfolio Saved Successfully',
											            buttons: [ {
											                label: 'Next',
											                // no title as it is optional
											                cssClass: 'btn-primary',
											               
											                action: function(){
											                	window.location.href="viewPortfolioPerformance.jsp?portfolioId="+portfolioId+"&portfolioName="+$('#portfolioName').val();
											                }
											            }, {
											                label: 'Back',
											                action: function(dialogItself){
											                    dialogItself.close();
											                }
											            }]
											        });
													//window.location.href="viewPortfolioPerformance.jsp?portfolioId="+portfolioId+"&portfolioName="+$('#portfolioName').val();

												} else {

													BootstrapDialog
															.alert("Saving portfolio failed. Please contact support");
												}
												
								}

											});
							

						});

		function selectTicker(ticker, stockID) {
			count = 0;
			dontAdd=false;
			output = "<a href='#' id=" + stockID
					+ " class='list-group-item'>"
					+ ticker + "</a>";
			$('#stocksList').children('a').each(function(){ 
				count++;
				
				if(this.id == stockID){
					BootstrapDialog
					.alert("Stock already added to list.");
					
					dontAdd=true;
				}
				
			});
			
			if(count >=10){
				BootstrapDialog
				.alert("Limit of ten stocks reached.");
				dontAdd=true;
			}
			if(!dontAdd){
				$('#stocksList').append(output);
				
			}
			

		}

		
	</script>
</body>
</html>