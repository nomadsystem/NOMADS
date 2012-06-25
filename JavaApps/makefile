DIRS = `find . -mindepth 1 -maxdepth 1 -type d | grep -v .svn`

all:
	@for DIR in $(DIRS); \
	do \
	( cd $$DIR; echo "making all in ===== $$DIR ===================="; \
	 make; echo "done in $$DIR" ); \
	 done

clean:
	@for DIR in $(DIRS); \
   	do \
	( cd $$DIR; echo "making clean in $$DIR ===================="; \
     	make clean;  echo "done in $$DIR" ); \
 	done
