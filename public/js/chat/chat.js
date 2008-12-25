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

var add_contact = function() {
	$post("roster/add",{ jid: prompt("Enter the jid: ", "") }, function(r) {
		if ( r.status == "error") alert(r.message);	
	});
}

var del_contact = function() {
	$post("roster/del",{ jid: prompt("Enter the jid: ", "") }, function(r) {
		if ( r.status == "error") alert(r.message);	
	});
}

var add_to_log = function(j,s) {
	var w = open_window(shortJid(j));
	var log = w.getContent().select(".log")[0];
	log.innerHTML += s
	log.scrollTop = log.scrollHeight;
	
};

var change_status_to = function(st) {
	$post("status",{ presence: st },function(r) {
		if ( r.status == "error") alert(r.message);	
	});
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


var get_updates = function() {
	$get("updates", function(m) {
		get_messages(m);
		get_roster(m);
	});
}

var get_messages = function(m) {
	m.messages.each(function(m) {
		receive_message(m)
	})
}

var get_roster = function(m) {
	var r = "<ul>";
	m.roster.each(function(c) {
		r += roster_item_template.evaluate(c);
	});
	r += "</ul>";
	
	$('roster').select(".list")[0].innerHTML = r;
	$('roster').select(".jid")[0].innerHTML = m.myJid;
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

var initialize_roster = function() {
	var sel = $('roster').select(".status")[0];
	for (var i=0;i<statuses.length;i++) {
		option = new Option(statuses[i],statuses[i]);
		sel.options[i] = option;
	}
	sel.observe("change",function(e) {  
		change_status_to(this.value);	
	});
	
	$('roster').select('.add')[0].observe("click",add_contact)
	$('roster').select('.del')[0].observe("click",del_contact)
	
}; 

var switch_to_chat = function() {
	$('chat').show();
	$("roster").innerHTML = roster_template.evaluate({});	
	initialize_roster();
	new PeriodicalExecuter(get_updates, 5);
}


var start_login = function() {
	log_retry = 0;
	$('login').hide();
	wait_for_login.delay(3);
}

window.onload = function() {
	
	var cb= function() {
		
		$post('login',{
			user: $("user").value,
			pwd: $("pwd").value
		}, function(c) {
			if (c.status == "error") {
				alert(c.message);
			} else {
				start_login();
			}
		});
	};
	
	$('enter_chat').onclick = cb;
	$$("#login input").each(function(e) {
		e.observe("keydown", function(e) {
			if ( e.keyCode == 13 ) {
				cb();
			}
		});
	});
};