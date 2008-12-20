<?xml version="1.0" encoding="UTF-8"?>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.1//EN"
	"http://www.w3.org/TR/xhtml11/DTD/xhtml11.dtd">

<html xmlns="http://www.w3.org/1999/xhtml" xml:lang="en">
<head>
<script type="text/javascript" src="js/dojo/dojo.js"></script>
<script type="text/javascript">

dojo.require("dojox.cometd");

dojo.addOnLoad(function() {
  dojo.byId("message").style.visibility='hidden';
  dojox.cometd.init("/chat");
});


function trim(str) {
    return str.replace(/(^\s+|\s+$)/g,'');
}


function clear() {
  dojo.byId("msgtext").value = "";
  dojo.byId("msgtext").focus();
}


function enterKeyHandler(e) {
if (!e) e = window.event;
   if (e.keyCode == 13) {
      send(trim(dojo.byId("msgtext").value));
      clear();
   }

}


function connect() {
  dojox.cometd.subscribe("/chat/demo", onMsgEvent);
  dojo.byId("login").style.visibility='hidden';
  dojo.byId("message").style.visibility='visible';
  send("Has joined the chat room");
  clear();
  dojo.byId("msgtext").onkeydown = enterKeyHandler;
  dojo.byId("myname").appendChild(document.createTextNode("-> " + dojo.byId("scrname").value + " <-"));
}


function onMsgEvent(event) {

   // Break apart the text string into screen name and message parts.
   var str = trim(event.data.msg);
   var scrname = ""; 
   if (str.match(/^.*[|].*$/)) {
       var spl = str.split("|");
       scrname = spl[0];
       str = " - " + spl[1];
   }

   // Insert the screen name in red and the message black into the DOM
   var newP = document.createElement("p");
   var fnt1 = document.createElement("font");
   var attr1 = document.createAttribute("color");
   attr1.nodeValue = "red";
   fnt1.setAttributeNode(attr1);

   var newT = document.createTextNode(scrname);
   fnt1.appendChild(newT);

   newP.appendChild(fnt1);  

   var fnt2 = document.createElement("font");
   var attr2 = document.createAttribute("color");
   attr2.nodeValue = "black";
   fnt2.setAttributeNode(attr2);

   var newT2 = document.createTextNode(str);
   fnt2.appendChild(newT2);

   newP.appendChild(fnt2);  

   dojo.byId("dialog").appendChild(newP)
}


function send(msg) {
  var scrname = dojo.byId("scrname").value;
  var evt = {'data': { 'msg': trim(scrname) + '|' + msg }};
  onMsgEvent(evt);  // Echo local
  dojox.cometd.publish("/chat/demo", evt.data);
}


</script>

</head>
</header>
<body>
<form>
<div id="login">
Screen name:&nbsp;<input type="text" id="scrname">
<input type=Button Id=logbtn value=Connect onClick=connect()><br/>Type a screen name and click the 'Connect' button
</div>

<div id="dialog"></div>
<hr/>
<div id="message">
<div id="myname"></div> Is my screen name<br/>
<textarea rows="3" cols="50" id="msgtext"></textarea>[ENTER] sends message</div>
</form>
</body>
</html>

