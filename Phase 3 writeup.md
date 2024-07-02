# Phase 3 Writeup

## Tasks to be Graded
- Task 3.1
- Task 3.2
- Task 3.3
- Task 3.4


## Changes Made in This Phase

### Task 3.1:
- **Brief Description**: 
  
### Task 3.2:
- **Brief Description**: Implemented changing password function after a user successfully log into their profile.
  
### Task 3.3:
- **Brief Description**: Implemented updating organization information function after a user successfully log into their profile.

### Task 3.4:
- **Brief Description**: 

## Known Bugs or Issues/ Additional Changes
- **Fix**: Changed api's req.body to req.param to correctly retrieve information through `api.js`
- **Fix**: Added illegal expression .class to reflect null checks in test files to up to date changes made in phase 2
- **Additional Changes:** Added a parameter `password` in `start()` of `UserInterface` to track the password easier.

## Team Member Contributions
### Member 1: [Tahmid Ahamed]
- **Tasks Worked On**: Task 3.4
- **Contributions**: 

### Member 2: [Silvia Alemany] 
- **Tasks Worked On**: Task 3.1
- **Contributions**: 

### Member 3: [Aaron Wu]
- **Tasks Worked On**: Task 3.2, Task 3.3
- **Contributions**: Added `changePassword` function and `updateAccountInfo` function in `DataManager.java`. Added command-line interaction to allow users to select the said functions in `UserInterface.java`. Added api endpoint to `api.js` which updates the changes in the database. Added two test files, `DataManagerChangePasswordTest.java` and `DataManagerUpdateOrgInfoTest.java` to test the siad functions' correctness.
