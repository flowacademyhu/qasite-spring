# QA Site REST API with Spring Boot

![test](https://github.com/flowacademyhu/qasite-spring/workflows/Test/badge.svg)

### Epics
1. Authentication and Sign Up (with username, password)
2. Able to ask Question, 
   list, 
   edit,
   delete, 
   view asked questions
    - Edit and delete only available for owned questions
3. Able to add answer to question, 
   list answers under a question,
   list my own answers,
   edit answer, 
   set as answered,
   delete
   - Edit and delete only available for owned answers
   - Set answered, stores that answer is answering the question
4. Able to rate questions and answers,
   add positive, negative rating or dismiss the rating
   - Prevent user to rate own questions and answers