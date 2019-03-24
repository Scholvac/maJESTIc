from demo.de.sos.script.nativeclasses import Coordinate

def eval(origin):
    test = Coordinate(0, 0, 0)
    dist = test.getDistance(origin) 
    return dist.value < 10