var TableObject = {
		
		//1:根据查询条件刷新列表
		refreshData:function(){
			var idcHouseid = $("#idcHouseid").val(),deploysitename = $("#deploysitename").val() ;
			$("#table").bootstrapTable('refresh', { url: "list",query:{'idcHouseid':idcHouseid, 'deploysitename':deploysitename }});
		},
		
		//2:上报周期设置方法的页面
		setData:function(){
			$("#setFormButton").click(function(){
				$.ajax({
					url: "obtain",
					async:false,
					type:'POST',
					dataType:'json',
					success:function(data){
						  $("#messageNo").val(data.messageNo);
						  $("#rFreq").val(data.rfreq);	
						  $("#setFormButton").val("1");//设置为update类型
						}
						
					});
				
				$(".tips").text("");
			});
		},
		//3:保存数据
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
						rType:1,
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
							TableObject.refreshData();
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
		
		//4:主页数据及初始化
		getData: function(){
			//主页列表初始化
			var listInit = {
					url: "list?"+Math.random(),
					method: 'post',
					contentType : "application/x-www-form-urlencoded",
					dataType: "json",
					pagination: true,
					showColumns: false,  //显示下拉框勾选要显示的列
		            showRefresh: false,  //显示刷新按钮
		            showToggle:false,  //设置数据显示格式
					toolbar: "#table-toolbar",
					iconSize: "outline",
					sidePagination: "server",
	                pageNumber: 1,
	                pageSize: 10,
	                pageList: [10, 25, 50, 100, 200],
					striped:true,  //是否行间隔色显示
					sortable: true,//是否启用排序
	                sortOrder: "asc",//排序方式
					clickToSelect:true, //是否启用点击选中行
					columns: [
						{ // 列设置
							field: 'deploysitename',
		                    title: '<span title="部署站点名称">部署站点名称</span>',formatter:function(value,row,index){
		            			return "<span title='"+value+"'>"+value+"</span>";
		            		}
		                },{
		                    field: 'probeType',
		                    title: '<span title="DPI采集类型">DPI采集类型</span>',
		                    /*visible: false,*/
		                    formatter: function (value, row, index) {  
	                        	if(value==0){
	                        		return 'DPI';
	                        	}else if(value==1){
	                        		return 'IDC ';
	                        	}
	                        }   
		                },{
                            field: 'softwareversion',
                            title: '版本号',formatter:function(value,row,index){
                                return "<span title='"+value+"'>"+value+"</span>";
                            },width:70
                        },{
		                    field: 'idcHouseid',
		                    title: '<span title="设备部署位置">设备部署位置</span>',formatter:function(value,row,index){
		            			return "<span title='"+value+"'>"+value+"</span>";
		            		},width:100
		                },{
		                    field: 'deployMode',
		                    title: '<span title="部署方式">部署方式</span>',
		                    formatter: function (value, row, index) {  
		                    	var mode = "";
	                        	if(value==1){
	                        		mode = '并行单向上行';
	                        	}else if(value==2){
	                        		mode = '并行单向下行 ';
	                        	}else if(value==3){
	                        		mode = '并行双向';
	                        	}else if(value==4){
	                        		mode = '串行单向上行 ';
	                        	}else if(value==5){
	                        		mode = '串行单向下行';
	                        	}else if(value==6){
	                        		mode = '串行双向';
	                        	}
	                        	return "<span title='"+mode+"'>"+mode+"</span>";
	                        },width:80   
		                },{
		                    field: 'totalCapability',
		                    title: '<span title="分析能力">分析能力</span>',formatter:function(value,row,index){
		            			return "<span title='"+value+"'>"+value+"</span>";
		            		},width:70
		                },{
		                    field: 'slotnum',
		                    title: '<span title="总槽位数">总槽位数</span>',formatter:function(value,row,index){
		            			return "<span title='"+value+"'>"+value+"</span>";
		            		},width:70
		                },{
		                    field: 'portNum',
		                    title: '<span title="占用的端口类型数">占用的端口类型数</span>',formatter:function(value,row,index){
		            			return "<span title='"+value+"'>"+value+"</span>";
		            		},width:130
		                },{
		                    field: 'preprocslotnum',
		                    title: '<span title="预处理模块数">预处理模块数</span>',formatter:function(value,row,index){
		            			return "<span title='"+value+"'>"+value+"</span>";
		            		},width:100
		                },{
		                    field: 'analysisslotnum',
		                    title: '<span title="分析模块数">分析模块数</span>',formatter:function(value,row,index){
		            			return "<span title='"+value+"'>"+value+"</span>";
		            		},width:90
		                }, {
		                    field: 'gpslotnum',
		                    title: '<span title="通用模块数">通用模块数</span>',formatter:function(value,row,index){
		            			return "<span title='"+value+"'>"+value+"</span>";
		            		},width:90
		                }, {           
		                    title:'操作',
		                	formatter:function(value,row,index){
		                		if($("#queryFlag").val()==undefined || $("#queryFlag").val()==""){
		                			return "";
		                		}else{
		                			var detailBtn = "<a href='#' title='详情' data-toggle='modal' data-target='#modal-detail' onclick='detail("+index+")' class='m-r'><i class='fa fa-file-text-o fa-lg'></i></a>";
			                		return detailBtn;
		                		}
		                		
		                	},width:50
		                }
		            ] 
				};
			
			$("#table").bootstrapTable(listInit);
		},
		editInit:function(dpiStatic){
			$("td[name='deploysitename']").text(dpiStatic.deploysitename);
			$("td[name='softwareversion']").text(dpiStatic.softwareversion);
			$("td[name='probeType']").text(dpiStatic.probeType == 0 ? "DPI" : "IDC");
			$("td[name='idcHouseid']").text(dpiStatic.idcHouseid);
			var deployMode=dpiStatic.deployMode;
			if(deployMode==1){
				deployMode = '并行单向上行';
        	}else if(deployMode==2){
        		deployMode = '并行单向下行 ';
        	}else if(deployMode==3){
        		deployMode = '并行双向';
        	}else if(deployMode==4){
        		deployMode = '串行单向上行 ';
        	}else if(deployMode==5){
        		deployMode = '串行单向下行';
        	}else if(deployMode==6){
        		deployMode = '串行双向';
        	}
			$("td[name='deployMode']").text(deployMode);
			$("td[name='totalCapability']").text(dpiStatic.totalCapability+" Gbps");
			$("td[name='slotnum']").text(dpiStatic.slotnum);
			$("td[name='preprocslotnum']").text(dpiStatic.preprocslotnum);
			$("td[name='analysisslotnum']").text(dpiStatic.analysisslotnum);
			$("td[name='gpslotnum']").text(dpiStatic.gpslotnum);
			$("td[name='portNum']").text(dpiStatic.portNum);
		},
		//查询
		searchData:function(){
			$("#searchFormButton").click(function(){
				TableObject.refreshData();
			});
		},
		//返回
		returnIndex:function(){
			$(":button[name='returnBtn']").click(function(){
				window.location.href = "/device/dpiStatic/index.html";
			});
		},
		//端口信息
		getPortsData:function(dpiStatic){
			var deploysitename = dpiStatic.deploysitename;
			//主页列表初始化
			var listPortInit = {
					url: "listPorts?"+Math.random(),
					method: 'post',
					contentType : "application/x-www-form-urlencoded",
					dataType: "json",
					pagination: true,
					showColumns: !0,
					toolbar: "#table-toolbar",
					iconSize: "outline",
					sidePagination: "server",
	                pageNumber: 1,	             
	                queryParams:{
	                	'deploysitename':deploysitename
	                },
	                pageSize: 10,
	                pageList: [10, 25, 50, 100, 200],
					icons: {
						refresh: "glyphicon-repeat",
						toggle: "glyphicon-list-alt",
						columns: "glyphicon-list",
					},
					striped:true,  //是否行间隔色显示
					sortable: true,//是否启用排序
	                sortOrder: "asc",//排序方式
					clickToSelect:true, //是否启用点击选中行
					columns: [
						{ // 列设置
							field: 'portno',
		                    title: '端口编号'
		                },{
		                    field: 'porttype',
		                    title: '端口类型'		                   
		                },{
		                    field: 'portdescription',
		                    title: '端口描述信息'
		                },{
		                    field: 'mlinkid',
		                    title: '链路编号'
		                },{
		                    field: 'mlinkdesc',
		                    title: '链路描述'
		                }
		            ] 
				};
			
			$("#ports-table").bootstrapTable(listPortInit);
		},
		//初始化
		init:function(){
			TableObject.getData();
			TableObject.returnIndex();
			TableObject.searchData();
			TableObject.setData();
			TableObject.saveData();
		}
	};

TableObject.init();

function detail(index){
	var dpiStatic = $("#table").bootstrapTable('getData');
	TableObject.editInit(dpiStatic[index]);
	TableObject.getPortsData(dpiStatic[index]);
}


