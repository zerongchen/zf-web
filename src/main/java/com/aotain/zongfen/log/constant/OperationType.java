package com.aotain.zongfen.log.constant;

import lombok.Getter;
import lombok.Setter;

public enum OperationType {
	
		CREATE(1,"新增"),
		MODIFY(2,"修改"),
		DELETE(3,"修改"),
		QUERY(4,"查询"),
		ALL_IMPORT(5,"全量导入"),
		INC_IMPORT(6,"增量导入"),
		EXPORT(7,"导出"),
		RESEND(8,"重发"),
		PRODUCT(9,"生成"),
		DOWNLOAD(10,"下载"),
		DEAL(11,"处理"),
		SEND(12,"下发");
		
		@Getter
		@Setter
		private Integer type;
		
		@Getter
		@Setter
		private String description;
		
		private OperationType(Integer type, String description){
			this.type = type;
			this.description = description;
		}	
	
}
