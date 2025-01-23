# Bigbank Test Task Solution

Prerequisites:
* JDK 17

To run the application:
* in Mac/Linux, run the following command in a terminal window (in the **bbtest** directory):
  ```console
  ./gradlew bootRun
  ```
* in Windows, run the following command in a terminal window (in the **bbtest** directory):
  ```console
  .\gradlew.bat bootRun
  ```

Supported application arguments (optional):
* 'nrOfGamesToRun'
  * Default value is 1
  * Valid value is at least 0
* 'nrOfGamesToRunInParallel'
  * Default value is 1
  * Valid value is at least 1
* Example, to run 10 games in total executing 5 games in parallel:
  ```console
  ./gradlew bootRun --args='--nrOfGamesToRun=10 --nrOfGamesToRunInParallel=5'
  ```