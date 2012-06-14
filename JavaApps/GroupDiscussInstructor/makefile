include ../makefile.conf

.SUFFIXES: .java .class
.java.class:
		$(JC) $(JFLAGS) $*.java

CLASSES = \
        DiscussClientInstructor.java

default: classes

classes: $(CLASSES:.java=.class)

clean:
		$(RM) *.class