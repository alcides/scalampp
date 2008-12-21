var active_chats = new Hash();


var open_window = function(jid) {
	
	if (active_chats.keys().contains(jid)) {
		var win = active_chats.get(jid);
		win.show();
		win.toFront();
	} else {
		win = new Window({
			className: "mac_os_x",
			title: "Chat with " + jid, 
			width:300, 
			height:200,
			minWidth: 100,
			minHeight: 100,
			recenterAuto:false,
			resizable:false
		});
		active_chats.set(jid,win);
		win.setHTMLContent(window_template.evaluate({jid: jid}));
		win.getContent().select(".insert_input")[0].observe('keydown', function (e) {
                if ( e.keyCode == 13 ) {
					send_message(jid,this.value)
					this.value="";
                }
        });
		win.show();
	}
	return win;
}

var open_contact = function(jid) {
	return open_window(jid);
}


var add_to_log = function(j,s) {
	var w = open_window(shortJid(j));
	var log = w.getContent().select(".log")[0];
	log.innerHTML += s
	log.scrollTop = log.scrollHeight;
	
};


var send_message = function(jid,content) {
	$post("messages",{ to: jid, content: content },function(r) {
		if ( r.status == "error") alert(r.message);
		else add_to_log(jid,message_sent_template.evaluate({ content: content }));
	});
};

var receive_message = function(m) {
 	add_to_log(m.from,message_template.evaluate(m));
};

var get_messages = function() {
	$get("messages",function(m) {
		m.messages.each(function(m) {
			receive_message(m)
		})
	});
}

var get_roster = function() {
	$get("roster",function(m) {
		var r = "<ul>";
		m.roster.each(function(c) {
			r += roster_item_template.evaluate(c);
		});
		r += "</ul>";
		$("roster").innerHTML = roster_template.evaluate({list: r, jid: m.myJid });
	});
}


var log_retry = 0
var wait_for_login = function() {
	$get('login',function(m) {
		if (m.status == "ok")
		 	switch_to_chat();
		else if (m.status == "fail") {
			alert("Wrong credentials.");
			$('login').show();
		} else {
			log_retry++;
			if (log_retry < 6) {
				wait_for_login.delay(3)
			} else {
				alert("Login Failed. Try again.");
				$('login').show();
			}
		}
			
	});
}

var switch_to_chat = function() {
	$('chat').show();
	new PeriodicalExecuter(get_roster, 5);
	new PeriodicalExecuter(get_messages, 3);
}

window.onload = function() {
	
	var cb= function() {
		
		$post('login',{
			user: $("user").value,
			pwd: $("pwd").value
		}, function(c) {
			if (c.status == "error") alert(c.message);
			else {
				$('login').hide();
				wait_for_login.delay(3);
			}
		});
	};
	
	$('enter_chat').onclick = cb;
	$$("#login input").each(function(e) {
		e.observe("keydown", function(e) {
			if ( e.keyCode == 13 ) cb();
		});
	});
};