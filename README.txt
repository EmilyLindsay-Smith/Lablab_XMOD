Installation Notes:
    - acquire jSerialComm jar
    - acquire junit jar
    - ensure run.sh has permission to execute


TESTING
Unit tests are available in test/ for most of the classes in this application. They can be run using the provided utility script ./test.sh

# RATIONALE FOR NO UNIT TESTS FOR SERIAL.JAVA 

Note that Serial.java does not have unit tests, though it has undergone manual testing. This is because it would require either:
    - connection to the control box to test it live (really integration testing)
    - mocking jSerialComm 

Mocking jSerialComm would be the best way to do unit tests for this class. However, managing Mockito and its dependencies would be easier with a build system
such as Maven or Gradle. For this project, we have opted to have as few dependencies as possible to reduce the risk of future issues. This application will
likely not have a Java programmer to maintain it ongoingly, thus needs to have as few moving parts as possible for the academic researchers who will use it. If a chosen
build system makes a breaking change, there will likely be no one to fix it.

To ensure that the connection to the control box is working as expected, users can click the following buttons on the GUI:
    - CHECK CONNECTION: this should cause the INT0 LED on the control box to flash 3 times.
    - CONTROLLER INFO: the box should report metadata that will be displayed on the central panel of the GUI. This metadata is:
        - GET_SOURCE
        - GET_VERSION
        - GET_CREATED
        - GET_MODIFIED
        - GET_BOXES
        - GET_KEYS

These commands check that the control box can receive and send bytes. E2E testing confirms that sending the trial timings works at time of writing;
if in doubt, create a short experiment of 3 items to confirm it is working as expected.

EMILY: INCLUDE SAMPLE EXPERIMENT AS A TESTER