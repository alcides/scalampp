package com.alcidesfonseca.xmpp;

public final class Helper {
	static String asciiDecoder(int i) {
		if ( i >= 32 && i <=126 )
			return (new Character((char) i)).toString();
		else
			return "";
	}
}
