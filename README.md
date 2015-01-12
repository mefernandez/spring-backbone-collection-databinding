# spring-backbone-collection-databinding
A case study on collection data binding with Spring and Backbone

## Introduction

While developing a web application with Spring boot as the main framework, I came across the need to add, remove and modify a list of elements in a web page and submit these changes to the server. Although it's a common task for a web framework, I found it to be non-trivial, and documentation is scarse. So, I set out to write a case study to help me organize what I've leanrt and maybe help others.

## The case

Consider a web page showing a table of users like this one:

| Name | Email         |
|------|---------------|
| John | john@mail.com |
| Mike | mike@mail.com |
| Lisa | lisa@mail.com |

This is the set of actions to perform on this list of users:

1. **Add** a new user. That means, adding a row to the table with a new name and email, and then submit that change to the server. **Also**, you may wish to add more than one user before submitting all changes at once.
2. **Remove** a user. That means, removing a row from the table. The row may be of an existing user, or a user that's just been added on step 1. **Again**, more than one row can be deleted before submitting these changes to the server.
3. **Modify** the data of a user.
 
