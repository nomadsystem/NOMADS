JFLAGS = -source 1.5 -target 1.5
JC = javac

.SUFFIXES: .java .class
.java.class:
		$(JC) $(JFLAGS) $*.java

CLASSES = \
		DiscussDisplayOnly.java\
		OperaClient.java\
		OC_Discuss.java\
		OC_Cloud.java\
		OC_Pointer.java\
		DiscussImagePanel.java\
		CloudImagePanel.java\
		DiscussDisplayImagePanel.java
		
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
	jar cf OperaClient.jar *.class

classes: $(CLASSES:.java=.class)

clean:
		$(RM) *.class *.jar
