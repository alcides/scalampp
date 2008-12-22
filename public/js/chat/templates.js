var roster_item_template = new Template('<li class="#{status}"><a href="#" onclick="open_contact(\'#{jid}\');">#{jid}</a></li>');

var roster_template = new Template('<div class="header"><b class="jid"></b><br /><select class="status"></select></div> <div class="list"></div><div class="actions"><a class="add" href="#">Add a contact</a><a class="del" href="#">Remove a contact</a></div>');

var message_template = new Template('<p><b>#{from} said:</b> #{content}</p>');
var message_sent_template = new Template('<p><b class="you">You said:</b> #{content}</p>');

var window_template = new Template('<div class="log"></div><div class="insert"><input name="chatty" class="insert_input"/></div>');

