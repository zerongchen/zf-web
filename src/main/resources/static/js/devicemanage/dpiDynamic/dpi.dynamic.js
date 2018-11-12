var TableObject = {
		//1:上报周期设置方法的页面
		setData:function(){
			$("#setFormButton").click(function(){
				$.ajax({
					url: "obtain",
					async:false,
					type:'POST',
					dataType:'json',
					success:function(data){
						  $("#rFreq").val(data.rfreq);	
						  $("#messageNo").val(data.messageNo);
						  $("#setFormButton").val("1");//设置为update类型
						}
						
					});
				
				$(".tips").text("");
			});
		},
		//2:保存数据
		saveData:function(){	
			$("#submitBut").click(function(){				
				var flag = $("#setFormButton").val(), urlPath = "add?";
				if(flag==1){
					urlPath = "modify?";
				}
				var rFreq=$("#rFreq").val();
				var messageNo=$("#messageNo").val();
				
				$(".tips").text("");
				if(rFreq == -1 || rFreq == undefined){
					$("#statictips").text("请选择静态信息上报周期！");
					return false;
				} 
				
				var jsonData = {
						rType:2,
						rFreq:rFreq,
						messageNo:messageNo
				};
				
				$.ajax({
					url: urlPath + Math.random(),
					type:'POST',
					data: jsonData,
					dataType:'json',
					success:function(data){
						if(data.result==1){
							$("#myModaladd").modal('hide');
							swal({
								title: "设置成功",
								type: "success"
							});
						}else if(data.result==0){
							swal({
								title: "设置失败",
								text: data.message,
								type: "error"
							});
						}
					}
				});
				
				
			});
		},
		//Port
		initHtmlPort:function(){
			$.ajax({
				url: "obtainAllPort",
				async:false,
				type:'POST',
				success : function(result) {
					$.each(result, function(i, item){						
						$("#tab1-example").append('<div class="ibox float-e-margins"><div class="ibox-title m-t-xs"><div class="ibox-tools"><a class="collapse-link"><i class="fa fa-chevron-up"></i></a></div></div><div class="ibox-content h250" id="port-echart-'+item.portno+'" style="width: 800px;"></div></div>');
						TableObject.initEchartPort(item);
					});
					
				}
			});
		},
		initEchartPort:function(dpiDynamicPort){
			$("#port-echart-"+dpiDynamicPort.portno).css('width', $(window).width() - 36);
			var myChart = echarts.init(document.getElementById('port-echart-'+dpiDynamicPort.portno));
			$.ajax({
				url: "obtainEchartByPort",
				async:false,
				data:{
					portno : dpiDynamicPort.portno,
					portinfo : dpiDynamicPort.portinfo
				},
				type:'POST',
				success : function(result) {
					if(result == null){
						myChart.innerHTML = '<span style="margin-left:600px;color:gray;">暂 无 数 据</span>';
					}else{
						option = {
							   title: result.title,
							   xAxis: {
							        type: 'category',
							        data: result.axis
							    },
							    yAxis: {
							        type: 'value'
							    },
							    dataZoom: result.dataZoom,
							    series: result.series
							};
						myChart.setOption(option);
						
					}
						
				}
			});
		},
		//CPU
		initHtmlCpu:function(){
			$.ajax({
				url: "obtainAllCPU",
				async:false,
				type:'POST',
				success : function(result) {
					$.each(result, function(i, item){						
						$("#tab2-example").append('<div class="ibox float-e-margins"><div class="ibox-title m-t-xs"><div class="ibox-tools"><a class="collapse-link"><i class="fa fa-chevron-up"></i></a></div></div><div class="ibox-content h250" id="cpu-echart-'+item.cpuNo+'" style="width: 800px;"></div></div>');
						TableObject.initEchartCpu(item.cpuNo);
					});
					
				}
			});
		},
		initEchartCpu:function(cpuNo){
			$("#cpu-echart-"+cpuNo).css('width', $(window).width() - 36);
			var myChart = echarts.init(document.getElementById('cpu-echart-'+cpuNo));
			$.ajax({
				url: "obtainEchartByCpu",
				async:false,
				data:{
					cpuNo : cpuNo
				},
				type:'POST',
				success : function(result) {
					if(result == null){
						myChart.innerHTML = '<span style="margin-left:600px;color:gray;">暂 无 数 据</span>';
					}else{
						option = {
							   title: result.title,
							   xAxis: {
							        type: 'category',
							        data: result.axis
							    },
							    yAxis: {
							        type: 'value'
							    },
							    dataZoom: result.dataZoom,
							    series: result.series
							};
						myChart.setOption(option);
						
					}
						
				}
			});
		},
		//初始化
		init:function(){
			TableObject.setData();
			TableObject.saveData();
			TableObject.initHtmlCpu();
			TableObject.initHtmlPort();
		}
	};

TableObject.init();