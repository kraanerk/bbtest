# Bigbank test assignment

Prerequisites:
* JDK 17

To run the application:
* in Windows:  
  ```console
  .\gradlew.bat bootRun
  ```
* in Mac/Linux: 
  ```console
  ./gradlew bootRun
  ```

Supported application arguments (optional):
* 'nrOfGamesToRun'
  * Default value is 1
  * Valid value is at least 0
* 'nrOfGamesToRunInParallel'
  * Default value is 1
  * Valid value is at least 1

Example:
```console
./gradlew bootRun --args='--nrOfGamesToRun=10 --nrOfGamesToRunInParallel=5'
```