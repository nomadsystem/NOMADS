include ../makefile.conf

.SUFFIXES: .java .class
.java.class:
		$(JC) $(JFLAGS) $*.java

CLASSES = \
	JoinPanel.java\
        StudentControlPanel.java\
        DiscussClientPanel.java\
        CloudDiscussPanel.java\
        PollStudentPanel.java\
        SandPointerPanel.java\
	SoundMosaicPanel.java\
        UnityGroovePanel.java\
        colorBox.java\



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
	jar cf StudentControlPanel.jar *.class

classes: $(CLASSES:.java=.class)

clean:
		$(RM) *.class
		rm -f ./nomads