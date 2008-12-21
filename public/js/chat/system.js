var prefix = "/chat/";

var $get = function(url,callback) {
	
	new Ajax.Request(prefix + url, {
		method:'get',
		onSuccess: function(transport) {
			callback(transport.responseText.evalJSON());
		}
	});
}

var $post = function(url,pars,callback) {
	
	new Ajax.Request(prefix + url, {
		method:'post',
		parameters: pars,
		onSuccess: function(transport) {
			callback(transport.responseText.evalJSON());
		}
	});
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