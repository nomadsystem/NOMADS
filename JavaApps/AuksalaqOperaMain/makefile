JFLAGS = -source 1.5 -target 1.5
JC = javac

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
	jar cf OperaMain.jar *.class

classes: $(CLASSES:.java=.class)

clean:
		$(RM) *.class *.jar
