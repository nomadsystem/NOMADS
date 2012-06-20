include ../makefile.conf

.SUFFIXES: .java .class
.java.class:
		$(JC) $(JFLAGS) $*.java

CLASSES = \
        UnityGrooveStudent.java\
        colorBox.java
        

default: classes myliblink comlink

topliblink:
	rm -f ./nomads
	/bin/ln -s /var/www/NOMADS/javalib/nomads .

myliblink:
	rm -f ./nomads
	/bin/ln -s /var/www/$(USER)/NOMADS/javalib/nomads .
	
comlink:
	rm -f ./com
	/bin/ln -s /var/www/$(USER)/NOMADS/javalib/com .

classes: $(CLASSES:.java=.class)

clean:
		$(RM) *.class