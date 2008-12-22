var shortJid = function(jid) {
	var idx = jid.indexOf("/");
	if (idx < 0)
	    return jid;
	return jid.substring(0, idx)
};

var statuses = ["online","away","busy","offline"];