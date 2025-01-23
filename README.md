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
* **nrOfGamesToRun**
  * Defines a number of games to execute in total.
  * Default value is 1.
  * A valid value is an integer which is greater than or equal to 0.
* **nrOfGamesToRunInParallel**
  * Defines a number of games to be executed in parallel. 
  * Default value is 1.
  * A valid value is an integer greater than 0.
* For example, to run 10 games in total executing 5 games in parallel, run the following command:
  ```console
  ./gradlew bootRun --args='--nrOfGamesToRun=10 --nrOfGamesToRunInParallel=5'
  ```