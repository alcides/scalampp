package com.alcidesfonseca.xmpp

class Benchmark {
	var time:Long = 0
	def start = {
		time =  System.nanoTime
	}
	def stop = {
		time =  System.nanoTime - time
		println("took " + time.toString)
		BenchmarkManager.add(this)
	}	
}

object BenchmarkManager {
	var benchs:List[Benchmark] = List()
	var requests = 0
	var time:Double = 0
	
	def add(b:Benchmark) = {
		benchs = benchs.::(b)
		printMedian
	}
	
	def printMedian = {
		var times = benchs.map { b => b.time }
		var sum:Long = 0
		for (t <- times) sum = sum + t
		println("median: " + (sum/times.length) )
	}
	
	def time_in_seconds = {
		System.nanoTime / Math.pow(  10.asInstanceOf[Double], 9.asInstanceOf[Double] )
		
	}
	
	def request = {
		if ( time == 0 ) time = time_in_seconds 
		requests = requests + 1
		var rps = requests / ( time_in_seconds - time)
		println("Requests per second: " + rps )
	}
	
}