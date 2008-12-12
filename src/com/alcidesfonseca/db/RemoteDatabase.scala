package com.alcidesfonseca.db

import java.rmi._
import sd.NS._

object RemoteDatabase {

	var lb:ILoadBalancer = null
	
	def update(sql:String) = {
		if (lb != null)
			try {
				lb.database_update(sql)
			} 
			catch {
				case e : RemoteException => {
					lb = null
					false
				}
			}
		else false
	}
	
	def getUsers(): List[User]= {
		if (lb != null) 
			try {
				lb.database_getUsers
			} 
			catch {
				case e : RemoteException => {
					lb = null
					List()
				} 
			}
		else List()
	}
	
	def getFriends(name:String):List[Friend] = {
		if (lb != null)
			try {
				lb.database_getFriends(name)
			} 
			catch {
				case e : RemoteException => {
					lb = null
					List()
				}
			}
		else List()
	}	
}