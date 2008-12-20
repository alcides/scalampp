window.onload = function() {
	$('enter_chat').onclick = function() {

		new Ajax.Request('/chat/login', {
			method:'post',
			parameters: {
				user: $("user").value,
				pwd: $("pwd").value
			},
		    onSuccess: function(transport){
				var response = transport.responseText || "no response text";
				alert("Success! \n\n" + response);
		    }
		});
		

	} ;
};