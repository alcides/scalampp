package sd.NS.server

@serializable
class ServerData(
	val cpuLoad:Double, val memoryLoad:Double, val networkLoad:Double
) {}