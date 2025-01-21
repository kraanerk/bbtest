# bbtest

Prerequisites:
* JDK 21

To run the application:
* in Windows:  
  ```console
  > .\gradlew.bat bootRun
  ```
* in Mac/Linux: 
  ```console
  $ ./gradlew bootRun
  ```

Supported application arguments (optional):
* 'nrOfGamesToRun', default value is 1
* 'nrOfGamesToRunInParallel', default value is 1

Example: 
```console
$ ./gradlew bootRun --args='--nrOfGamesToRun=100 --nrOfGamesToRunInParallel=10'
```