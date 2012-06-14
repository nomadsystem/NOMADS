include ../makefile.conf

.SUFFIXES: .java .class
.java.class:
		$(JC) $(JFLAGS) $*.java

CLASSES = \
        CloudPrompt.java

default: classes

classes: $(CLASSES:.java=.class)

clean:
		$(RM) *.class