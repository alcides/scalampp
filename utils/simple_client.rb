require "rubygems"
require 'xmpp4r'
include Jabber

Jabber::debug = true # Uncomment this if you want to see what's being sent and received!
jid = JID::new('alcides@localhost/teste')
password = 'tkhxbq'
cl = Client::new(jid)
cl.connect
cl.auth(password)
