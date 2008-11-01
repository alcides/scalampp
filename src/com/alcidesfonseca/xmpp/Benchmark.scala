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
	
}