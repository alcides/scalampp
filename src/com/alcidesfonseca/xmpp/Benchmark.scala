package com.alcidesfonseca.xmpp

class Benchmark {
	var time:Long = 0
	def start = {
		time =  System.nanoTime
	}
	def stop = {
		time =  System.nanoTime - time
		println("took " + time.toString)
	}	
}