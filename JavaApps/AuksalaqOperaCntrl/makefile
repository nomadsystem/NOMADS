include ../makefile.conf

.SUFFIXES: .java .class
.java.class:
		$(JC) $(JFLAGS) $*.java

CLASSES = \
        OperaCntrl.java

default: classes myliblink jar

topliblink:
	rm -f ./nomads
	/bin/ln -s /var/www/NOMADS/javalib/nomads .

myliblink:
	rm -f ./nomads
	/bin/ln -s /var/www/$(USER)/NOMADS/javalib/nomads .
	
jar:
	jar cf OperaCntrl.jar *.class

classes: $(CLASSES:.java=.class)

clean:
		$(RM) *.class *.jar