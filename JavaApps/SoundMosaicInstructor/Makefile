include ../makefile.conf

.SUFFIXES: .java .class
.java.class:
	$(JC) $(JFLAGS) $*.java

CLASSES = \
	sequencer.java\
	sequencer_thread.java

default: classes

classes: $(CLASSES:.java=.class)

clean:
	$(RM) *.class
