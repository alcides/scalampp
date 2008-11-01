class Client(var args:Array[String]) extends Thread {
	override def run = {
		BootClient.main( args )
	}
}

object Benchmark {
	def main(args: Array[String]) = {
		for ( i <- 1 to 10 )
			(new Client(args)).start
	}
}