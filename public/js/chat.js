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


window.onload = function() {
	$('enter_chat').onclick = function() {
		
		$post('login',{
			user: $("user").value,
			pwd: $("pwd").value
		}, function(c) {
			if (c.status == "error") alert(c.message);
			else {
				alert("wait for login")
			}
		});
	} ;
};