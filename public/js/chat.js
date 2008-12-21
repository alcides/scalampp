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

var get_roster = function() {
	$get("roster",function(m) {
		$('messages').update(m.message)
	});
}

var get_messages = function() {
	$get("messages",function(m) {
		m.messages.each(function(i) {
			$('messages').innerHTML += i + "<br />"
		})
	});
}

var log_retry = 0
var wait_for_login = function() {
	$get('login',function(m) {
		if (m.status == "ok")
		 	switch_to_chat();
		else if (m.status == "fail")
			alert("Wrong credentials.");
		else {
			log_retry++;
			if (log_retry < 6) wait_for_login.delay(3)
			else alert("Login Failed. Try again.");
		}
			
	});
}

var switch_to_chat = function() {
	$('login').hide();
	$('chat').show();
	//new PeriodicalExecuter(get_roster, 5);
	new PeriodicalExecuter(get_messages, 3);
}

window.onload = function() {
	
	$('enter_chat').onclick = function() {
		
		$post('login',{
			user: $("user").value,
			pwd: $("pwd").value
		}, function(c) {
			if (c.status == "error") alert(c.message);
			else {
				wait_for_login.delay(3)
			}
		});
	} ;
};