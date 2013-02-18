DIRS = `find . -mindepth 1 -maxdepth 1 -type d | grep -v .svn`

#CLASSDIRS = Bindle CloudDiscuss CloudDisplay CloudPrompt GroupDiscuss GroupDiscussInstructor GroupDiscussPrompt InstructorControlPanel Join PollDisplay PollPrompt PollStudent SoundMosaic SoundMosaicInstructor SoundSwarm SoundSwarmDisplay UnityGroovePrompt UnityGrooveStudent

CLASSDIRS = InstructorControlPanel2.0 Bindle2.0
AUKDIRS = AuksalaqOperaMain AuksalaqOperaCntrl AuksalaqOperaClient

AUKLIBDIRS = nomads/v210_auk
CLASSLIBDIRS = nomads/v210

CLASS_INSTALL_DIR = "/var/www/classroom/JavaApps"
AUK_INSTALL_DIR = "/var/www/AuksalaqNOMADS"

all:
	@for DIR in $(DIRS); \
	do \
	( cd $$DIR; echo "making all in ===== $$DIR ===================="; \
	 make; echo "done in $$DIR ===============================" ); \
	 done

class:
	@for DIR in $(CLASSDIRS); \
	do \
	( cd $$DIR; echo "making all in ===== $$DIR ===================="; \
	 make; echo "done in $$DIR ===============================" ); \
	 done

class_clean:
	@for DIR in $(CLASSDIRS); \
	do \
	( cd $$DIR; echo "making clean in ===== $$DIR ===================="; \
	 make clean; echo "done in $$DIR ===============================" ); \
	 done

class_lib:
	@for DIR in $(CLASSLIBDIRS); \
	do \
	( cd $$DIR; echo "making all in ===== $$DIR ===================="; \
	 make; echo "done in $$DIR ===============================" ); \
	 done

class_lib_clean:
	@for DIR in $(CLASSLIBDIRS); \
	do \
	( cd $$DIR; echo "making clean in ===== $$DIR ===================="; \
	 make clean; echo "done in $$DIR ===============================" ); \
	 done

auk:
	@for DIR in $(AUKDIRS); \
	do \
	( cd $$DIR; echo "making all in ===== $$DIR ===================="; \
	 make; echo "done in $$DIR ===============================" ); \
	 done

auk_lib:
	@for DIR in $(AUKLIBDIRS); \
	do \
	( cd $$DIR; echo "making all in ===== $$DIR ===================="; \
	 make; echo "done in $$DIR ===============================" ); \
	 done

auk_lib_clean:
	@for DIR in $(AUKLIBDIRS); \
	do \
	( cd $$DIR; echo "making all in ===== $$DIR ===================="; \
	 make clean; echo "making clean in $$DIR ===============================" ); \
	 done

auk_clean:
	@for DIR in $(AUKDIRS); \
	do \
	( cd $$DIR; echo "making clean in ===== $$DIR ===================="; \
	 make clean; echo "done in $$DIR ===============================" ); \
	 done

auk_install:
	@for DIR in $(AUKDIRS); \
	do \
	( cp -r $$DIR/*.class $(AUK_INSTALL_DIR)/$$DIR; echo "cp $$DIR/*.class $(AUK_INSTALL_DIR)/$$DIR"; echo "done in $$DIR ===============================" ); \
	( cp -r $$DIR/*.jar $(AUK_INSTALL_DIR)/$$DIR; echo "cp $$DIR/*.jar $(AUK_INSTALL_DIR)/$$DIR"; echo "done in $$DIR"; echo "===============================" ); \
	( cp -r $$DIR/*.html $(AUK_INSTALL_DIR)/$$DIR; echo "cp $$DIR/*.html $(AUK_INSTALL_DIR)/$$DIR"; echo "done in $$DIR ===============================" ); \
	 done

class_install:
	@for DIR in $(CLASSDIRS); \
	do \
	( cp -r $$DIR/*.class $(CLASS_INSTALL_DIR)/$$DIR; echo "cp $$DIR/*.class $(CLASS_INSTALL_DIR)/$$DIR"; echo "done in $$DIR ===============================" ); \
	( cp -r $$DIR/*.jar $(CLASS_INSTALL_DIR)/$$DIR; echo "cp $$DIR/*.jar $(CLASS_INSTALL_DIR)/$$DIR"; echo "done in $$DIR"; echo "===============================" ); \
	( cp -r $$DIR/*.html $(CLASS_INSTALL_DIR)/$$DIR; echo "cp $$DIR/*.html $(CLASS_INSTALL_DIR)/$$DIR"; echo "done in $$DIR ===============================" ); \
	 done


clean:
	@for DIR in $(DIRS); \
	do \
	( cd $$DIR; echo "making clean in $$DIR ===================="; \
	make clean;  echo "done in $$DIR ======================" ); \
	done
