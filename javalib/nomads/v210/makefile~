include makefile.conf

.SUFFIXES: .java .class
.java.class:
		$(JC) $(JFLAGS) $*.java

CLASSES = \
	command.java\
	globals.java\
	appID.java\
	dataType.java\
	NomadsApp.java\
	NomadsAppThread.java

default: classes

classes: $(CLASSES:.java=.class)

clean:
		$(RM) *.class