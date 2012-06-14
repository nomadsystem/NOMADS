include makefile.conf

JC = javac
.SUFFIXES: .java .class
.java.class:
		$(JC) $(JFLAGS) $*.java

CLASSES = \
        NomadServer.java\
        NomadServerThread.java\
	TextClient.java\
	TextClientThread.java

default: classes

classes: $(CLASSES:.java=.class)

clean:
		$(RM) *.class