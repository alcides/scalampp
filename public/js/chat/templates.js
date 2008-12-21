var roster_item_template = new Template('<li class="#{status}"><a href="#" onclick="open_contact(\'#{jid}\');">#{jid}</a></li>');

var roster_template = new Template('<div class="roster"> <div class="header"><b>#{jid}</b></div> #{list}</div>');

var message_template = new Template('<p><b>#{from} said:</b> #{content}</p>');
var message_sent_template = new Template('<p><b class="you">You said:</b> #{content}</p>');

var window_template = new Template('<div class="log"></div><div class="insert"><input name="chatty" class="insert_input"/></div>');

