var prefix = "/admin/";
var servers = [];

var populate_servers = function() {
	$("servers_table").update();
	h = [];
	servers.each(function(i){
		i['memory'] = Math.round(i.memory*100)/100
		h.push(server_row_template.evaluate(i));
	});
	$("servers_table").innerHTML = h.join("");
};


var get_servers = function() {
	$get("servers",function(r) {
		servers = r.servers;
		populate_servers();
	});
	
};


document.observe('dom:loaded',function() {
	new Control.Tabs('tabs_admin');
	get_servers();
	new PeriodicalExecuter(get_servers, 5);
});