include makefile.conf

.SUFFIXES: .java .class
.java.class:
		$(JC) $(JFLAGS) $*.java

CLASSES = \
	NCommand.java\
	NGlobals.java\
	NAppID.java\
	NDataType.java\
	NSand.java\
	NGrain.java

default: classes

classes: $(CLASSES:.java=.class)

clean:
		$(RM) *.class