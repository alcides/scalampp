//
//  XMLStrings
//
//  Created by  on 2008-10-05.
//  Copyright (c) 2008 Alcides Fonseca. All rights reserved.
//
package com.alcidesfonseca.xmpp

import java.net.InetAddress;
import scala.xml._
import org.publicdomain._

object XMLStrings {
	
	val xml_init = "<?xml version='1.0'?>"
	
	def stream_start(id:String) = xml_init + "<stream:stream from='"+InetAddress.getLocalHost.getHostName+"' id='"+id+"' xmlns='jabber:client' xmlns:stream='http://etherx.jabber.org/streams' version='1.0'>"
	
	def stream_start(id:String,host:String) = xml_init + "<stream:stream to='"+host+"' id='"+id+"' xmlns='jabber:client' xmlns:stream='http://etherx.jabber.org/streams' version='1.0'>"
	
	val stream_end = "</stream:stream>"
	
	def check_start(x:String):Boolean = {
		try {
			var xml = XML.loadString(x + stream_end)
			true
		} 
		catch {
			case e : Exception => false
		}
	}
	
	def stream_auth(user:String,pass:String) = <auth>{ Base64.encodeBytes( new String( "\0" + user + "\0" + pass ).getBytes ) }</auth>
	
	val stream_auth_methods = <stream:features>
			<mechanisms xmlns="urn:ietf:params:xml:ns:xmpp-sasl">
				<mechanism>PLAIN</mechanism>
			</mechanisms>
			<register xmlns="http://jabber.org/features/iq-register/" />
		</stream:features>
		
	val stream_auth_methods_alt = "<stream:features><mechanisms xmlns='urn:ietf:params:xml:ns:xmpp-sasl'><mechanism>PLAIN</mechanism></mechanisms></stream:features>"
		
	val stream_auth_accepted =  <success xmlns="urn:ietf:params:xml:ns:xmpp-sasl"/>
	
	val stream_auth_failed = <failure xmlns="urn:ietf:params:xml:ns:xmpp-sasl">
	     <not-authorized/>
	   </failure>
	
	val stream_auth_incorrect_encoding = <failure xmlns="urn:ietf:params:xml:ns:xmpp-sasl">
	     <incorrect-encoding/>
	   </failure>
		
	val stream_features =  <stream:features>
	     <bind xmlns="urn:ietf:params:xml:ns:xmpp-bind"/>
	     <session xmlns="urn:ietf:params:xml:ns:xmpp-session"/>
	   </stream:features>
	
	def session_bind(id:String,jid:String) = <iq xmlns="jabber:client" type="result" id={ id } >
		<bind xmlns="urn:ietf:params:xml:ns:xmpp-bind">
			<jid>{jid}</jid>
		</bind>
	</iq>
		
		
	def register_info(id:String) = <iq type="result" id={ id }>
	        <query xmlns="jabber:iq:register">
	          <username/>
	          <password/>
	        </query>
	      </iq>
	
	def register_success(id:String) = <iq type="result" id={ id }>
		        <query xmlns="jabber:iq:register"/>
		      </iq>
		
	def register_error(id:String,us:String,pw:String) = <iq type="error" id={ id }>
			<query xmlns="jabber:iq:register">
				<username>{ us }</username>
				<password>{ pw }</password>
			</query>
			<error code="409">Username Not Available</error>
		</iq>	
		
		
		
	def session_set(id:String) = <iq type="result" id={ id } >
		<session xmlns="urn:ietf:params:xml:ns:xmpp-session"/>
	</iq>
	
	def roster(id:String) = <iq type="result" id={ id } >
		<query xmlns="jabber:iq:roster"/>
	</iq>
	
	def message_chat(from:String,to:String,content:String) = <message to={ to } from={ from } type="chat"><body>{ content }</body></message>
	
}