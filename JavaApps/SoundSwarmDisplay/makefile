include ../makefile.conf

JC = javac
.SUFFIXES: .java .class
.java.class:
	$(JC) $(JFLAGS) $*.java

CLASSES = \
        SoundSwarmDisplay.java

default: classes myliblink

topliblink:
	/bin/ln -fs /var/www/NOMADS/javalib/nomads .
	/bin/ln -fs /var/www/NOMADS/javalib/com .

myliblink:
	/bin/ln -fs /var/www/$(USER)/NOMADS/javalib/nomads .
	/bin/ln -fs /var/www/$(USER)/NOMADS/javalib/com .

classes: $(CLASSES:.java=.class)

clean:
		$(RM) *.class com nomads