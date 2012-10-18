include ../makefile.conf

.SUFFIXES: .java .class
.java.class:
		$(JC) $(JFLAGS) $*.java

CLASSES = \
	DiscussTest.java\
	CloudTest.java\
	SwarmTest.java\
	TimeCheck.java

default: myliblink comlink classes

topliblink:
	rm -f ./nomads
#	/bin/ln -s /var/www/NOMADS/javalib/nomads .
	/bin/ln -s /Users/$(USER)/NOMADS/javalib/nomads .

myliblink:
	rm -f ./nomads
#	/bin/ln -s /var/www/$(USER)/NOMADS/javalib/nomads .
	/bin/ln -s /Users/$(USER)/NOMADS/javalib/nomads .

comlink:
	rm -f ./com
#	/bin/ln -s /var/www/$(USER)/NOMADS/javalib/com .
	/bin/ln -s /Users/$(USER)/NOMADS/javalib/com .

classes: $(CLASSES:.java=.class)

clean:
		$(RM) *.class *.jar
		rm -f ./nomads
		rm -f ./com