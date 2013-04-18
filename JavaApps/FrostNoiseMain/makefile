include ../makefile.conf

.SUFFIXES: .java .class
.java.class:
		$(JC) $(JFLAGS) $*.java

CLASSES = \
		OperaMain.java

default: classes myliblink comlink jar

topliblink:
	rm -f ./nomads
	/bin/ln -s /var/www/NOMADS/javalib/nomads .

myliblink:
	rm -f ./nomads
	/bin/ln -s /var/www/$(USER)/NOMADS/javalib/nomads .

comlink:
	rm -f ./com
	/bin/ln -s /var/www/$(USER)/NOMADS/javalib/com .

jar:
	rm -rf *.jar
	jar cf OperaMain.jar *.class

classes: $(CLASSES:.java=.class)

clean:
		$(RM) *.class *.jar
		rm -f ./nomads
		rm -f ./com