include ../makefile.conf

.SUFFIXES: .java .class
.java.class:
		$(JC) $(JFLAGS) $*.java

CLASSES = \
        CloudDiscuss.java

default: classes

classes: $(CLASSES:.java=.class)

clean:
		$(RM) *.class