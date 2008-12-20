<?xml version="1.0" encoding="UTF-8"?>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.1//EN"
	"http://www.w3.org/TR/xhtml11/DTD/xhtml11.dtd">

<html xmlns="http://www.w3.org/1999/xhtml" xml:lang="en">
<head>
<script type="text/javascript" src="js/dojo/dojo.js"></script>
<script type="text/javascript">

dojo.require("dojox.cometd");
dojo.require("dojox.cometd.timestamp");


var handle = dojo.subscribe("/cometd/meta", this, function(event){
	switch(event.action)
	{
	case "connect":
	  break;    
	case "disconnect":
	  break;
	case "handshake":
	  break;
	default:
		alert(event.action)
	}
});


var loc = "http://localhost:8080/chat";
dojox.cometd.init(loc);
dojox.cometd.subscribe('/chat', function(msg) {alert("hi");}); 


</script>
</head>
<body>
<input type="button" onclick="dojox.cometd.publish('/chat',{ 'test': 'hello world' })" value="Click Me!">
</body>
</html>