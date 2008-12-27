//
//  XMLStrings
//
//  Created by  on 2008-10-05.
//  Copyright (c) 2008 Alcides Fonseca. All rights reserved.
//
package com.alcidesfonseca.xmpp

import java.net.InetAddress
import scala.xml._
import org.publicdomain._
import com.alcidesfonseca.db._
import scala.collection.mutable.{Queue}

object XMLStrings {
	
	val xml_init = "<?xml version='1.0'?>"
	
	def stream_start(id:String) = xml_init + "<stream:stream from='"+InetAddress.getLocalHost.getHostName+"' id='"+id+"' xmlns='jabber:client' xmlns:stream='http://etherx.jabber.org/streams' version='1.0'>"
	
	def stream_start_to(host:String) = xml_init + "<stream:stream to='"+host+"' xmlns='jabber:client' xmlns:stream='http://etherx.jabber.org/streams' version='1.0'>"
	
	def stream_end = "</stream:stream>"
	
	def check_start(x:String):Boolean = {
		XMLValidator.validate(x + stream_end)
	}
	
	def stream_auth(user:String,pass:String) = <auth mechanism="PLAIN" xmlns="urn:ietf:params:xml:ns:xmpp-sasl">{ Base64.encodeBytes( ( '\0' + user + '\0' + pass ).getBytes("ISO-8859-1") ) }</auth>
	
 	def start_tls = <starttls xmlns="urn:ietf:params:xml:ns:xmpp-tls"/>
	
	val stream_auth_methods = <stream:features>
			<mechanisms xmlns="urn:ietf:params:xml:ns:xmpp-sasl">
				<mechanism>PLAIN</mechanism>
			</mechanisms>
			<register xmlns="http://jabber.org/features/iq-register/" />
		</stream:features>
		
	val stream_auth_methods_alt = "<stream:features><mechanisms xmlns='urn:ietf:params:xml:ns:xmpp-sasl'><mechanism>PLAIN</mechanism></mechanisms></stream:features>"
		
	val stream_auth_accepted =  <success xmlns="urn:ietf:params:xml:ns:xmpp-sasl"/>
	
	val stream_auth_failed = <failure xmlns="urn:ietf:params:xml:ns:xmpp-sasl"><not-authorized/></failure>
	
	val stream_auth_incorrect_encoding = <failure xmlns="urn:ietf:params:xml:ns:xmpp-sasl">
	     <incorrect-encoding/>
	   </failure>
		
	val stream_features =  <stream:features>
	     <bind xmlns="urn:ietf:params:xml:ns:xmpp-bind"/>
	     <session xmlns="urn:ietf:params:xml:ns:xmpp-session"/>
	   </stream:features>
	
	
	def session_bind_request(resource:String) = <iq type="set" id="bind_1" >
			<bind xmlns="urn:ietf:params:xml:ns:xmpp-bind">
				<resource>{ resource }</resource>
			</bind>
		</iq>
	
	def session_bind(id:String,jid:String) = <iq xmlns="jabber:client" type="result" id={ id } >
		<bind xmlns="urn:ietf:params:xml:ns:xmpp-bind">
			<jid>{jid}</jid>
		</bind>
	</iq>

	def session_request(id:String) = <iq type="set" id={ id } >
			<session xmlns="urn:ietf:params:xml:ns:xmpp-session"/>
		</iq>
	
		
	def session_set(id:String) = <iq type="result" id={ id } >
		<session xmlns="urn:ietf:params:xml:ns:xmpp-session"/>
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
		

	def roster(id:String,fs:List[Friend]) = {
		var roster = new Queue[Node]()
		fs.foreach{ f => roster += roster_item(f) }
		<iq type="result" id={ id } ><query xmlns="jabber:iq:roster">{ roster }</query></iq>
	}

	def roster_set(fs:List[Friend]):Elem = roster_set(fs,"none")	
	def roster_set(fs:List[Friend],s:String):Elem = {
		var roster = new Queue[Node]()
		fs.foreach{ f => if (s.equals("remove")) roster += roster_item_remove_raw(f) else roster += roster_item(f,s) }
		<iq type="set" ><query xmlns="jabber:iq:roster">{ roster }</query></iq>
	}
	
	def roster_item(f:Friend,s:String):Elem = <item jid={ f.jid } name={ f.name } subscription={ s }><group>Contacts</group></item>
	
	def roster_item(f:Friend):Elem = roster_item(f,f.subscription)
	
	def roster_item_request(id:String,jid:String) = <iq type="set" id={ id }>
		<query xmlns="jabber:iq:roster">
			<item jid={ jid }><group>Contacts</group></item>
		</query>
	</iq>
	
	def roster_item_remove_raw(jid:String) = <item jid={ jid } subscription="remove"/>
	
	def roster_item_remove(id:String,jid:String):Elem = <iq type="set" id={ id }>
		<query xmlns="jabber:iq:roster"> { roster_item_remove_raw(jid) } </query>
	</iq>
	
	def roster_item_remove_raw(f:Friend):Elem = roster_item_remove_raw(f.jid)
	
	def roster_item_sent(id:String,jid:String) = <iq to={ jid } type="result" id={ jid } />
	
	def roster_request(id:String) = <iq type="get" id={ id } >
		<query xmlns="jabber:iq:roster"/>
	</iq>
	
	def presence():Elem = <presence><priority>5</priority></presence>
	def presence(from:String):Elem = <presence from={ from }><priority>5</priority></presence>
	def presence(from:String,to:String):Elem = <presence from={ from } to={ to }></presence>
	def presence(from:String,to:String,content:Any):Elem = <presence from={ from } to={ to }>{ content }</presence>
	def presence_probe(from:String,to:String) = <presence type="probe" from={ from } to={ to }></presence>
	def presence_set(status:String) = <presence><show>{ status }</show></presence>
	def presence_unavailable(from:String):Elem = <presence from={ from } type="unavailable"></presence>
	def presence_unavailable(from:String,to:String):Elem = <presence from={ from } to={ to } type="unavailable"></presence>
	def presence_unavailable(from:String,to:String,content:Any):Elem = <presence from={ from } to={ to } type="unavailable">{ content }</presence>

	
	def presence_subscribe(to:String,from:String) = <presence from={ from } to={ to } type="subscribe" />
	def presence_unsubscribe(to:String,from:String) = <presence from={ from } to={ to } type="unsubscribe" />
	def presence_subscribed(to:String,from:String) = <presence from={ from } to={ to } type="subscribed" />
	def presence_unsubscribed(to:String,from:String) = <presence from={ from } to={ to } type="unsubscribed" />
	
	def message_chat(from:String,to:String,content:String) = <message to={ to } from={ from } type="chat"><body>{ content }</body></message>
	def message_chat(to:String,content:String) = <message to={ to } type="chat"><body>{ content }</body></message>
	
	def message_error_body(target:String) = "Unable to devier your message to " + target + " as this mailbox does not exist."
	
	def message_error(to:String, target:String) = <message from={ InetAddress.getLocalHost.getHostName } to={ to } type="error">
		<body>{ message_error_body(target) }</body>
		<semantics xmlns="http://jabber.org/protocol/msg-delivery" status="error">
			<rule action="error" condition="match-resource" value="exact"/>
		</semantics>
		<error code="500" type="cancel">
			<internal-server-error xmlns="urn:ietf:params:xml:ns:xmpp-stanzas" />
			<condition xmlns="http://jabber.org/protocol/msg-delivery" >
				<rule action="error" condition="match-resource" value="exact" />
			</condition>
		</error>
	</message>
}