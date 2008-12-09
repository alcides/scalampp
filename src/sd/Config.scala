package sd;

object Config {
	val vers = "nio"
	val debug = false
	
	val checkServerRate = 5 // seconds 
	val updateRate = 5 // seconds 	
	
	val retryTimeOut = 10 // seconds
	val retryNSTimeOut = 5 // seconds
	
	val registryURL = "//localhost/"
	val namingServerPrefix = "ns"
}