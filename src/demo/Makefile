JAVAC = javac
JAVACFLAGS =
SRC= dbpack/CostaExpress.java	\
	dbpack/AddCustomer.java	\
	dbpack/AddReservation.java	\
	dbpack/AdminLogin.java	\
	dbpack/AdminScreen.java	\
	dbpack/AgentLogin.java	\
	dbpack/AgentScreen.java	\
	dbpack/EditCustomer.java	\
	dbpack/UpdateReservation.java	\
	dbpack/SearchDatabase.java	\
	dbpack/SingleRoute.java	\
	dbpack/ComboRoute.java	\
	dbpack/AdvA.java	\
	dbpack/AdvB.java	\
	dbpack/AdvC.java	\
	dbpack/AdvD.java	\
	dbpack/AdvE.java	\
	dbpack/AdvF.java	\
	dbpack/ImportAllText.java	\
	dbpack/AdvG.java	\
	dbpack/AdvH.java	\
	dbpack/AdvI.java	\

CLS= $(SRC:.java=.class)

all:  $(CLS)

.SUFFIXES : .class .java
.java.class :
	$(JAVAC) $(JAVACFLAGS) $<

jar: $(classes)
	jar -cp postgresql-42.2.18.jar $(classes)