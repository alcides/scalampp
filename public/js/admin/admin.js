var prefix = "/admin/";
var servers = [];
var accounts = [];

var change_pass = function(uname,passf) {
	$post("accounts",{username: uname, password: passf.value}, function(r) {
		if ( r.status == "error") alert(r.message);	
		else passf.value = "";
	});
}

var populate_servers = function() {
	var tbody = $("servers_table_body");
	tbody.update();
	servers.each(function(i){
		i['memory'] = Math.round(i.memory*100)/100;
		i['jids_list'] = i.jids.join("<br />");
		
		tbody.innerHTML += server_row_template.evaluate(i);
		
	});
};

var populate_clients = function() {
	var tbody = $("clients_table_body");
	tbody.update();
	servers.each(function(i){
		i.jids.each(function(j) {
			tbody.innerHTML += client_row_template.evaluate({ jid:j, server: i.address});
		});
	});
};


var populate_accounts = function() {
	var tbody = $("accounts_table_body");
	tbody.update();
	accounts.each(function(i){
		tbody.innerHTML += account_row_template.evaluate({ uname: i});
	});	
};

var get_servers = function() {
	$get("servers",function(r) {
		servers = r.servers;
		populate_servers();
		populate_clients();
	});
};


var get_accounts = function() {
	$get("accounts",function(r) {
 		accounts= r.accounts;
		populate_accounts();
	});
}

document.observe('dom:loaded',function() {
	var tabs = new Control.Tabs('tabs_admin');
	tabs.observe('beforeChange',function(old_container,new_container) {  
		if (new_container.id == "accounts") {
			get_accounts();
		}
	});
	
	
	get_servers();
	new PeriodicalExecuter(get_servers, 5);
});