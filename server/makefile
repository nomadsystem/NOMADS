include makefile.conf

JC = javac
.SUFFIXES: .java .class
.java.class:
		$(JC) $(JFLAGS) $*.java

CLASSES = \
        NomadServer.java\
        NomadServerThread.java\
	TextClient.java\
	TextClientThread.java

default: myliblink classes

topliblink:
	rm -f ./nomads
	/bin/ln -s /var/www/NOMADS/javalib/nomads .

myliblink:
	rm -f ./nomads
	/bin/ln -s /var/www/$(USER)/NOMADS/javalib/nomads .


classes: $(CLASSES:.java=.class)

clean:
		$(RM) *.class