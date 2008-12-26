var prefix = "/admin/";
var servers = [];


var populate_servers = function() {
	var tbody = $("servers_table");
	tbody.update();
	servers.each(function(i){
		i['memory'] = Math.round(i.memory*100)/100;
		i['jids_list'] = i.jids.join("<br />");
		
		tbody.innerHTML += server_row_template.evaluate(i);
		
	});
};

var populate_clients = function() {
	var tbody = $("clients_table");
	tbody.update();
	servers.each(function(i){
		i.jids.each(function(j) {
			tbody.innerHTML += client_row_template.evaluate({ jid:j, server: i.address});
		});
	});
};

var get_servers = function() {
	$get("servers",function(r) {
		servers = r.servers;
		populate_servers();
		populate_clients();
	});
	
};


document.observe('dom:loaded',function() {
	new Control.Tabs('tabs_admin');
	
	
	get_servers();
	new PeriodicalExecuter(get_servers, 5);
});