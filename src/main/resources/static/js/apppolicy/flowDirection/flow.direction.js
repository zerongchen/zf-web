/***********************************************************************************
TableObject  * zTree:
		     *      变量 setting: 树的设置
		     *      方法 getNodes：根据类型获取树节点
			 *          initTree：初始化树
			 *          getCheckedData：获取选中的树节点
			 *          initCheck：初始化选中的树
			 * action:
			 *     方法  clearData：清空添加成功后的页面数据
			 *          saveData：保存数据（新增或修改）
			 *          closeOrCancel：关闭或者取消操作
			 *          refreshData：刷新数据
			 *          getData：表格数据的获取和初始化
			 *          editInit：编辑页面的初始化
			 *          batchDeleteData：批量删除数据
			 *          searchData：查询数据
			 *          init：初始化对象
 **********************************************************************************
 function    *      editRow:编辑选中行的数据
             *     deleteRow：删除选中行的数据
 ********************************************************************************/  
 
 /******************* code beginning*********************************/
var TableObject = {
		
		setting: {
				view: {
					expandSpeed: "", //zTree 节点展开、折叠时的动画速度，设置方法同 JQuery 动画效果中 speed 参数。
					selectedMulti: false // 禁止多点同时选中的功能
				},
		    	check: {
					enable: true
			   },
				data: {
				    key: {
				   		isParent: "isParent",
				   		name: "areaName"
			        },
					simpleData: {
						enable: true,
						idKey: "areaId",
						pIdKey: "pareaId",
						rootPId: '0'
					}
				}
		},
		getNodes:function(areaType){
			var nodeData;
			$.ajax({
				url: "obtainTree",
				async:false,
				type:'POST',
				dataType:"json",
				data:{
					areaType:areaType
				},
				success : function(result) {
					nodeData = result;
				},
				error : function() {
					alert("对不起，没有数据！");
				}
			});
			return nodeData;
		},
		initTree: function(){
			$.fn.zTree.init($("#internalAreaTree"), TableObject.setting, TableObject.getNodes(2)).expandAll(true);
			$.fn.zTree.init($("#internalIdcTree"), TableObject.setting, TableObject.getNodes(3)).expandAll(true);
			$.fn.zTree.init($("#externalAreaTree"), TableObject.setting, TableObject.getNodes(2)).expandAll(true);
			$.fn.zTree.init($("#externalIdcTree"), TableObject.setting, TableObject.getNodes(3)).expandAll(true);
		},
		 //获取节点
	    getCheckedData:function(ztreeId) {
	        var treeObj = $.fn.zTree.getZTreeObj(ztreeId);
	        var nodes = treeObj.getCheckedNodes(true);

	        /**********************************构造一个构造函数，用来实例化json对象 *****************************/
	        var jsonArray = new Array();
	        function AreaObject(areaName,areaId,pareaId,isParent,areaType){
	            this.areaName = areaName;
	            this.areaId = areaId;
	            this.pareaId = pareaId;
	            this.isParent = isParent;
	            this.areaType = areaType;
	          };
	        for(var i=0; i<nodes.length; i++){
	        	var node = nodes[i];

	        	jsonArray.push(new AreaObject(node.areaName,node.areaId,node.pareaId,node.isParent,node.areaType));
	        	
	        }
	        var json = JSON.stringify(jsonArray);
	        return json;
	    },
	    //初始化节点
		initCheck:function(ztreeId,list){
			var treeObj = $.fn.zTree.getZTreeObj(ztreeId);
			var nodes = treeObj.transformToArray(treeObj.getNodes());
			//返回根节点集合 
			$.each(list,function(i,value){
				for (var n=0;n<nodes.length;n++){
					if(nodes[n].areaId == value.areaId && nodes[n].pareaId == value.pareaId){
						treeObj.checkNode(nodes[n], true, true);
					}
				}
			});
	    },
	    /*****************zTree code ---end********************************/
	    
	    /*****************************策略的操作 *************************/
	  //清空添加成功后的页面数据
		clearData:function(){
			$('#title').text("新增");
			$('button[name="saveBtn"]').attr('id','addBtn');
			//zTree的重置
			 var internalAreaTree = $.fn.zTree.getZTreeObj("internalAreaTree"),internalIdcTree = $.fn.zTree.getZTreeObj("internalIdcTree"),
			 	 externalAreaTree = $.fn.zTree.getZTreeObj("externalAreaTree"),externalIdcTree = $.fn.zTree.getZTreeObj("externalIdcTree");
			 	 internalAreaTree.checkAllNodes(false);
			 	 internalIdcTree.checkAllNodes(false);
			 	 externalAreaTree.checkAllNodes(false);
			 	 externalIdcTree.checkAllNodes(false);
			 	$("#messageName").val("");
			 	$("#messageNo").val("");
			 	$(".tab-pane").each(function(){
			 		if($(this).hasClass("active")){
			 			$(this).removeClass("active");
			 		}
			 	});
			 	$(".nav-tabs").children().each(function(){
			 		if($(this).hasClass("active")){
			 			$(this).removeClass("active");
			 		}
			 	});
			 	$(".nav-tabs").children().eq(0).addClass("active");
			 	$(".nav-tabs").children().eq(2).addClass("active");
			 	$("#tab1").addClass("active");
			 	$("#tab3").addClass("active");
			 //	$('#mainTab').addClass("active");
                $('#mainTab').parent().addClass('active');
                $('#mainPolicy').addClass('active');
			 	
		},
	    saveData:function(){
	    	$("#saveBtn").click(function(){
    			$(".has-error").remove();
    			var internalAreaGroupList = TableObject.getCheckedData("internalAreaTree"),
    			internalIdcGroupList = TableObject.getCheckedData("internalIdcTree"),
    			externalAreaGroupList = TableObject.getCheckedData("externalAreaTree"),
    			externalIdcGroupList = TableObject.getCheckedData("externalIdcTree"),
    			messageNo = $("#messageNo").val(),
    			messageName = $("#messageName").val();
    			

    			
    			if(messageName == "" || messageName == undefined){
    				customWarn('messageNameMsg','请输入策略名称','col-md-4'); 
    				return false;
    			}else if(messageName.length>50){
    				customWarn('messageNameMsg','策略名称不能超过50个字符','col-md-4'); 
    				return false;
    			}else if(internalAreaGroupList == '[]' && internalIdcGroupList == '[]'){
    				swal({
    					title: "添加失败！",
    					text: "请至少选中一个目的区域",
    					type: "error"
    				});
    				return false;
    			}else if(externalAreaGroupList == '[]' && externalIdcGroupList == '[]'){
    				swal({
    					title: "添加失败！",
    					text: "请至少选中一个目的区域",
    					type: "error"
    				});
    				return false;
    			}
    			var URL = "";
    			if(messageNo==undefined || messageNo==""){
    				URL = "save";
    			}else{
    				URL = "update";
    			}
    			$.ajax({
    				url: URL,
    				async:false,
    				type:'POST',
    				dataType:'json',
    				data:{
    					messageNo: messageNo,
    					messageName: messageName,
    					internalAreaGroupJson: internalAreaGroupList,
    					internalIdcGroupJson: internalIdcGroupList,
    					externalAreaGroupJson: externalAreaGroupList,
    					externalIdcGroupJson: externalIdcGroupList
    				},
    				success : function(data) {
    					if(data.result==0){
    						$("#myModaladd").modal('hide');
    						swal({
    							title: "添加成功",
    							text: data.message,
    							type: "success"
    						});
    						TableObject.clearSearch();
    						TableObject.refreshData();
    						TableObject.clearData();
    					}else if(data.result==1){
    						swal({
    							title: "添加失败",
    							text: data.message,
    							type: "error"
    						});
    					}else if(data.result==2){
    						swal({
    							title: "添加失败",
    							text: data.message,
    							type: "error"
    						});
    					}
    				}
    				
    			});
    		});
	    },
	    refreshButton:function(){
	    	 $('.newRefreshButton').on('click', function() {
	    		 TableObject.refreshData();
	         })
	    },
	    closeOrCancel:function(){
	    	$("#closeBtn,#cancelBtn").click(function(){
	    		TableObject.clearData();
	    	});
	    },
	    clearSearch:function(){
	    	$('#searchMessageName').val("");
	    },
		refreshData:function(){
			var messageName = $("#searchMessageName").val() ;
			var searchStartTime = $("#searchStart").val();
			if (searchStartTime==''){
				searchStartTime = undefined;
			}
			var searchEndTime = $('#searchEnd').val();
			if(searchEndTime==''){
				searchEndTime = undefined;
			}
			$("#table").bootstrapTable('refresh',
				{
					url: "list",
					query: {
						'messageName':messageName,
						'searchStartTime':searchStartTime,
						'searchEndTime':searchEndTime
					}
				});
		},
		getData: function(){
			//主页列表初始化
			var listInit = {
					url: "list",
					method: 'post',
					contentType : "application/x-www-form-urlencoded",
					dataType: "json",
					pagination: true,
					showColumns: true,  //显示下拉框勾选要显示的列
					icons: {
						columns: "glyphicon-list"
					},
					toolbar: "#table-toolbar",
					iconSize: "outline",
					sidePagination: "server",
					dataField: "list",
	                pageNumber: 1,
	                pageSize: 10,
	                pageList: [10, 25, 50, 100, 200],	
	                queryParamsType:'limit',
	    			queryParams:queryParams,//请求服务器时所传的参数
	    			responseHandler: responseHandler,
					striped:true,  //是否行间隔色显示
					sortable: true,//是否启用排序
	                sortOrder: "asc",//排序方式
					clickToSelect:false, //是否启用点击选中行
					onLoadSuccess:function(){
			            $('.bootstrap-table tr td').each(function () {
			                $(this).attr("title", $(this).text());
			                $(this).css("cursor", 'pointer');
			            });
			        },
					columns: [
						{ // 列设置
		                    checkbox: true // 使用单选框
		                },
						{ // 列设置
							field: 'messageNo',
		                    title: '策略ID',formatter:function(value,row,index){
		            			return "<span title='"+value+"'>"+value+"</span>";
		            		}
		                },{
		                    field: 'messageName',
		                    title: '<span title="策略名称">策略名称</span>',formatter:function(value,row,index){
		            			return "<span title='"+value+"'>"+value+"</span>";
		            		}
		                },{
		                    field: 'internalAreaGroupInfoStr',
		                    title: '<span title="来源区域">来源区域</span>',formatter:function(value,row,index){
		            			return "<span title='"+value+"'>"+value+"</span>";
		            		}
		                },{
		                    field: 'externalAreaGroupInfoStr',
		                    title: '<span title="目的区域">目的区域</span>',formatter:function(value,row,index){
		            			return "<span title='"+value+"'>"+value+"</span>";
		            		}
		                },{
		                    field: 'probeType',
		                    title: '<span title="DPI采集类型">DPI采集类型</span>',
		                    visible: false,
		                    formatter: function (value, row, index) {  
	                        	if(value==0){
	                        		return 'DPI';
	                        	}else if(value==1){
	                        		return 'IDC ';
	                        	}
	                        }   
		                },{
		                	field:'policyCount',
		                	title:'<span title="策略成功数/异常数">策略成功数/异常数</span>',
		                	formatter:function(value,row,index){	
		                		if(value=="0/0"){
		                			return "0/0";
		                		}else{
		                			return "<a href='#' onclick='getDetail("+index+")' title='详情'>"+value+"</a>";
		                		}
		                	}
		                },{
		                    field: 'createyTime',
		                    title: '<span title="创建时间">创建时间</span>',formatter:function(value,row,index){
		            			return "<span title='"+value+"'>"+value+"</span>";
		            		}
		                },{
		                    field: 'modifyOper',
		                    title: '<span title="操作账号">操作账号</span>',formatter:function(value,row,index){
		            			return "<span title='"+value+"'>"+value+"</span>";
		            		}
		                },{
		                    field: 'internalIdcGroupList',
		                    title: '<span title="来源IDC">来源IDC</span>',
		                    visible: false,formatter:function(value,row,index){
		            			return "<span title='"+value+"'>"+value+"</span>";
		            		}
		                },{
		                    field: 'internalAreaGroupList',
		                    title: '<span title="来源区域">来源区域</span>',
		                    visible: false,formatter:function(value,row,index){
		            			return "<span title='"+value+"'>"+value+"</span>";
		            		}
		                }, {
		                    field: 'externalAreaGroupList',
		                    title: '<span title="目的区域">目的区域</span>',
		                    visible: false,formatter:function(value,row,index){
		            			return "<span title='"+value+"'>"+value+"</span>";
		            		}
		                }, {
		                    field: 'externalIdcGroupList',
		                    title: '<span title="目的IDC">目的IDC</span>',
		                    visible: false,formatter:function(value,row,index){
		            			return "<span title='"+value+"'>"+value+"</span>";
		            		}
		                }, {           
		                    title:'操作',
		                	formatter:function(value,row,index){
		                		var redo = $("#redoFlag").val();
		                		var deleteFlag = $("#deleteFlag").val();
		                		var modify = $("#modifyFlag").val();
		                		var format="";
		                		if(redo==1){
	                		        format +="<a href='#' title='重发' class='m-r' onclick='resendPolicy("+row.messageNo+")'><i class='fa  fal fa-share resend'></i></a>";
		                		}
		                		if(modify==1){
		                			 format+="<a href='#' title='编辑' data-toggle='modal' data-target='#myModaladd' onclick='editRow("+index+")' class='m-r'><i class='fa fa-edit fa-lg'></i></a>" ;
		                		}
		                		if(deleteFlag==1){
		                			format+="<a href='#' title='删除' onclick='deleteRow("+row.messageNo+")' ><i class='fa fa-close fa-lg'></i></a>";
		                		}
		                		return format;
		                	}
		                }
		            ] 
				};
			$("#table").bootstrapTable(listInit);
			//请求服务数据时所传参数
			function queryParams(params){

			    return{
			        //每页多少条数据
			        pageSize: params.limit,
			        //请求第几页
			        pageIndex:params.offset/params.limit+1
			    }
			};

			function responseHandler(result) { //数据筛选

			    if (result) {
			        return {
			            "list" : result.rows,
			            "total" : result.total
			        };
			    } else {
			        return {
			            "list" : [],
			            "total" : 0
			        };
			    }
			};
			
			
		},
		editInit:function(tableObj){
			$(".has-error").remove();
			$('#title').text("修改");
			$("#messageName").val(tableObj.messageName);
			$("#messageNo").val(tableObj.messageNo);

			if(tableObj.internalAreaGroupList != null){
				TableObject.initCheck("internalAreaTree",tableObj.internalAreaGroupList);
			}
			if(tableObj.internalIdcGroupList != null){
				TableObject.initCheck("internalIdcTree",tableObj.internalIdcGroupList);
			}
			if(tableObj.externalAreaGroupList != null){
				TableObject.initCheck("externalAreaTree",tableObj.externalAreaGroupList);
			}
			if(tableObj.externalIdcGroupList != null){
				TableObject.initCheck("externalIdcTree",tableObj.externalIdcGroupList);
			}	
		},
		batchDeleteData:function(){
			$('.deleteButton').click(function(){
				var selectDatas = $("#table").bootstrapTable('getSelections');
				var ids = new Array();
				if(selectDatas.length == 0){
					swal({
						title: "删除失败！",
						text: "请至少选中一行数据",
						type: "error"
					});
	    		   return false;
				}else{
					$(selectDatas).each(function(i, e){
						ids.push(e.messageNo);
					});
				}
				swal({
		    		title: "确定要删除这条信息吗",
		    		text: "删除后将无法恢复，请谨慎操作！",
		    		type: "warning",
		    		showCancelButton: true,
		    		confirmButtonColor: "#DD6B55",
		    		confirmButtonText: "是的，我要删除！",
		    		cancelButtonText: "让我再考虑一下…",
		    		closeOnConfirm: false,
		    		closeOnCancel: false
		    	},function(isConfirm) {
		    		if(isConfirm) {
		    			$.ajax({
							url: "deleteList",
							async:false,
							type:'POST',
							dataType:"json",
							data:{
								messageNos: ids.toString()
							},
							success : function(data) {
								if(data.result==0){
									$("#myModaladd").modal('hide');
									swal({
										title: "删除成功",
										text: data.message,
										type: "success"
									});
									TableObject.refreshData();
									TableObject.clearData();
								}else if(data.result==1){
									swal({
										title: "删除失败",
										text: data.message,
										type: "error"
									});
								}else if(data.result==2){
									swal({
										title: "删除失败",
										text: data.message,
										type: "error"
									});
								}
							}
							
						});
		    		} else {
		    			swal("已取消", "取消了删除操作！", "error")
		    		}
				
			   });
			});	
		},
		//查询
		searchData:function(){
			$("#searchFormButton").click(function(){
				TableObject.refreshData();
			});
		},

		//初始化
		init:function(){
			TableObject.refreshButton();
			TableObject.getData();
			TableObject.searchData();
			TableObject.initTree();
			TableObject.saveData();
			TableObject.batchDeleteData();
			TableObject.closeOrCancel();
            // 初始化查询时间条
            initDefinedEleIdDate(3,false,false,"searchStart","searchEnd");
		}
	};

TableObject.init();


function editRow(index){
	var obj = $("#table").bootstrapTable('getData');
	var tableObj=obj[index];
	TableObject.editInit(tableObj);
}

function deleteRow(messageNo){
	swal({
		title: "确定要删除这条信息吗",
		text: "删除后将无法恢复，请谨慎操作！",
		type: "warning",
		showCancelButton: true,
		confirmButtonColor: "#DD6B55",
		confirmButtonText: "是的，我要删除！",
		cancelButtonText: "让我再考虑一下…",
		closeOnConfirm: false,
		closeOnCancel: false
	},function(isConfirm){
		if(isConfirm){
			$.ajax({
				url: "delete",
				async:false,
				type:'POST',
				dataType:"json",
				data:{
					messageNo: messageNo
				},
				success : function(data) {
					if(data.result==0){
						$("#myModaladd").modal('hide');
						swal({
							title: "删除成功",
							type: "success"
						});
						TableObject.refreshData();
						TableObject.clearData();
					}else if(data.result==1){
						swal({
							title: "删除失败",
							text: data.message,
							type: "error"
						});
					}
				}
				
			});
		}else {
			swal("已取消", "取消了删除操作！", "error")
		}
	});
}

function resendPolicy(messageNo){
	$.ajax({
		url: "resend",
		async:false,
		type:'POST',
		dataType:"json",
		data:{
			messageNo: messageNo
		},
		success : function(data) {
			if(data.result==0){
				swal({
					title: "重发成功",
					text: data.message,
					type: "success"
				});
			}else if(data.result==1){
				swal({
					title: "重发失败",
					text: data.message,
					type: "error"
				});
			}
		}
		
	});
}

function getDetail(index){
	var flowDirection = $("#table").bootstrapTable('getData');
	var redo = $("#redoFlag").val();
    if(redo==1){
    	PolicyDetail.showDetail(flowDirection[index].messageNo,69,1,1);
    }else{
    	PolicyDetail.showDetail(flowDirection[index].messageNo,69,1,0);
    }
}

