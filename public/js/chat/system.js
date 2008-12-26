var prefix = "/chat/";

var $get = function(url,callback) {
	
	new Ajax.Request(prefix + url, {
		method:'get',
		onSuccess: function(transport) {
			callback(transport.responseText.evalJSON());
		}
	});
}

var $subpost = function(url,method,pars,callback) {
	
	new Ajax.Request(prefix + url, {
		method:method,
		parameters: pars,
		onSuccess: function(transport) {
			callback(transport.responseText.evalJSON());
		}
	});
}

var $post = function(url,pars,callback) {
	$subpost(url,"post",pars,callback);
}

var $put = function(url,pars,callback) {
	$subpost(url,"put",pars,callback);
}


var $delete = function(url,pars,callback) {
	$subpost(url,"delete",pars,callback);
}

Array.prototype.contains = function (element) {
	for (var i = 0; i < this.length; i++) 
	{
		if (this[i] == element) 
		{
			return true;
		}
	}
	return false;
};