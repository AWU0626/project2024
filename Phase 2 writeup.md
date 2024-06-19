# Phase 2 Writeup

## Additional Tasks to be Graded
- Task 2.4
- Task 2.5
- Task 2.8

## Changes Made in This Phase

### Task 2.1:
- **Brief Description**: Added maps to org.attemptLogin to cache the elements found before.
  
### Task 2.2:
- **Brief Description**: Made small adjustments to methods to ensure correctness. More specifically, fixed typos and small bugs related to variable/field naming.

### Task 2.3:
- **Brief Description**: Added in parsing of the user input to quit the program if user enters 'q' or 'quit.' Also, added in error-checking for invalid input (i.e. non-numbers and numbers outside the range of current funds). 

### Task 2.4:
- **Brief Description**: Added maps to contributor.attemptLogin to cache the elements found before.
  
### Task 2.5:
- **Brief Description**: Created 3 new test files for the remaining methods in DataManager. For each of the method, create tests that tested for a success fetch, a failed fetch and an exception throw which covers all of the statements. Fixed minor part of the code where there is a mistake that resulted in incorrect test results.

### Task 2.8:
- **Brief Description**: Added three checks that would check if the name, description, and the fund target were adherent to the format described in the specifications. Any time an empty
- value for name and description were added, or a negative / non numeric input added for the fund target value, it will reprompt the user to enter a valid value for each variable.

## Known Bugs or Issues/ Additional Changes
- **Issue & Fix**: Reverted getting the fundName from Phase 1 by using getFundName() and updated the test file to reflect it by adding override to getFundName() mock object.

## Team Member Contributions
### Member 1: [Tahmid Ahamed]
- **Tasks Worked On**: Task 2.2, Task 2.5
- **Contributions**:

### Member 2: [Silvia Alemany] 
- **Tasks Worked On**: Task 2.3, Task 2.8
- **Contributions**: 

### Member 4: [Aaron Wu]
- **Tasks Worked On**: Task 2.1, Task 2.4
- **Contributions**: Finished tasks, reverted attemptLogin error from Phase 1, updated test for attemptLogin.
