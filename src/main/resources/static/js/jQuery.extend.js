
/* 自定义JQuery插件 */
(function($){
	/**
	 * 将表单元素序列化并转换成json对象，
	 * 相同name的值用英文半角逗号隔开
	 */
	$.fn.extend({
		formToJSON: function(){
			var $this = this;
			var obj = {};
			var formJson = $this.serializeArray();
			$.each(formJson, function(i, n){
				var val = obj[n.name];
				if(val){
					obj[n.name] = val + ',' + $.trim(n.value);
				}else{
					obj[n.name] = $.trim(n.value);
				}
			});
			return obj;
		}
	});
})(jQuery);