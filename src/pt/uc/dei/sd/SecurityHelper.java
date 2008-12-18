package pt.uc.dei.sd;

import java.io.IOException;
import java.net.Socket;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocket;

import org.publicdomain.Base64;

import pt.uc.dei.sd.external.ConnectionConfiguration;
import pt.uc.dei.sd.external.ServerTrustManager;
import pt.uc.dei.sd.external.StringUtils;

public class SecurityHelper
{
    /**
     * Use this method for the TLS negotiation step referred in the XMPP Core RFC (Section 5.3).
     * Use the streams of the resulting socket to proceed with communication.
     * @param socket Your currently open socket to the destiny XMPP server
     * @param server Your destiny XMPP server
     * @param port The port of the destiny XMPP server 
     * @return A secure socket
     * @throws NoSuchAlgorithmException
     * @throws KeyManagementException
     * @throws IOException
     */
    public static Socket executeTLSNegotiation(Socket socket, String server, int port) throws NoSuchAlgorithmException,
	    KeyManagementException, IOException
    {
	Socket result;

	SSLContext context = SSLContext.getInstance("TLS");

	// Verify certificate presented by the server
	context.init(
		null, // KeyManager not required
		new javax.net.ssl.TrustManager[]
		{ new ServerTrustManager(server, new ConnectionConfiguration(server, port)) },
		new java.security.SecureRandom());
	Socket plain = socket;
	
	// Secure the plain connection
	result = context.getSocketFactory().createSocket(plain, plain.getInetAddress().getHostName(), plain.getPort(),
		true);
	result.setSoTimeout(0);
	result.setKeepAlive(true);

	// Proceed to do the handshake
	((SSLSocket) result).startHandshake();

	return result;
    }

    
    /**
     * Builds a SASL PLAIN authentication message given a username and a password.
     * @param username the username of the XMPP service
     * @param password the user's password
     * @return A SASL PLAIN authentication String
     */
    public static String buildSASLAuthText(String username, String password)
    {
	String result;

	result = StringUtils.encodeBase64("\0" + username + "\0" + password);

	return result;
    }

}
