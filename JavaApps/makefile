DIRS = `find . -mindepth 1 -maxdepth 1 -type d | grep -v .svn`

#CLASSDIRS = Bindle CloudDiscuss CloudDisplay CloudPrompt GroupDiscuss GroupDiscussInstructor GroupDiscussPrompt InstructorControlPanel Join PollDisplay PollPrompt PollStudent SoundMosaic SoundMosaicInstructor SoundSwarm SoundSwarmDisplay UnityGroovePrompt UnityGrooveStudent

CLASSDIRS = InstructorControlPanel2.0 Bindle2.0

CLASS_INSTALL_DIR = "/var/www/classroom/JavaApps"

all:
	@for DIR in $(DIRS); \
	do \
	( cd $$DIR; echo "making all in ===== $$DIR ===================="; \
	 make; echo "done in $$DIR"; echo "===============================" ); \
	 done

class:
	@for DIR in $(CLASSDIRS); \
	do \
	( cd $$DIR; echo "making all in ===== $$DIR ===================="; \
	 make; echo "done in $$DIR"; echo "===============================" ); \
	 done

class_install:
	@for DIR in $(CLASSDIRS); \
	do \
	( cp -r $$DIR/*.class $(CLASS_INSTALL_DIR)/$$DIR; echo "cp $$DIR/*.class $(CLASS_INSTALL_DIR)/$$DIR"; echo "done in $$DIR"; echo "===============================" ); \
	( cp -r $$DIR/*.jar $(CLASS_INSTALL_DIR)/$$DIR; echo "cp $$DIR/*.jar $(CLASS_INSTALL_DIR)/$$DIR"; echo "done in $$DIR"; echo "===============================" ); \
	( cp -r $$DIR/*.html $(CLASS_INSTALL_DIR)/$$DIR; echo "cp $$DIR/*.html $(CLASS_INSTALL_DIR)/$$DIR"; echo "done in $$DIR"; echo "===============================" ); \
	 done


clean:
	@for DIR in $(DIRS); \
	do \
	( cd $$DIR; echo "making clean in $$DIR ===================="; \
	make clean;  echo "done in $$DIR" ); \
	done
