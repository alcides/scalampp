var server_row_template = new Template('<tr><td>#{address}</td><td>#{cpu}</td><td>#{memory}</td><td>#{network}</td><td><a onclick="this.parentNode.down(\'div\').toggle()" href="#">#{jids.length}</a><div style="display:none;">#{jids_list}</div></a></td></tr>');

var client_row_template = new Template('<tr><td>#{jid}</td><td>#{server}</td></tr>');

var account_row_template = new Template('<tr><td>#{uname}</td><td><input type="password" onkeypress="if(event.keyCode==13) change_pass(\'#{uname}\',this.value);"></td></tr>');