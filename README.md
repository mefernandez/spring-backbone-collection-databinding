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
 
## Components

The main components involved in this case study are:

1. A `@Controller` with methods to handle `GET` and `POST` requests.
2. A `User` class to hold the values for `name` and `email`.
3. A `Form` class. This is just a `POJO` to hold a `List` of `User`s.
4. An `HTML` template using [Thymeleaf](http://www.thymeleaf.org/) to render `@Controller` results.
5. Some `JavaScript` to dynamically add or remove rows from the table of users.

## Add a new user

Let's start with an empty `List` of `User`s. Adding a new `User` to this `List` means to send a `POST` request to the `@Controller` with the `name` and `email` values for the new user. In order for `Spring` to bind this data, the request parameters should follow the convention described in [section Beans of the Spring Framework documentation](http://docs.spring.io/spring/docs/current/spring-framework-reference/htmlsingle/#beans-beans-conventions). For this case, the `POST` request parameters look like:

```
users[0].name=John
users[0].email=john@mail.com
```

Upon request, Spring will try to bind this data to the `@ModelAtrtibute` object defined inside the `@Controller` class, which in this case is an object of type `Form`.

```java
	@ModelAttribute("form")
	public Form getForm() {
		Form form = new Form();
		List<User> users = new ArrayList<User>();
		form.setUsers(users);
		return form;
	}
```

Spring will create an instance of `User` and set the value "John" for property `name` and "john@mail.com" for property `email`. It will then insert this new `User` instance at index 0 in the `users` property of the `form` instance of type `Form`.

**This is the most simple scenario**, since the `List` is empty. But:
- What if the `List` contains items?
- What if the order of the items change between requests?
- What if the items are removed from the `List` between requests?

Let's address these questions.
