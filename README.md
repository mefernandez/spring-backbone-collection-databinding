# spring-backbone-collection-databinding
A case study on collection data binding with Spring and Backbone

[ ![Codeship Status for mefernandez/spring-backbone-collection-databinding](https://codeship.com/projects/c6c0f240-8694-0132-2a45-6e1293eebb57/status?branch=master)](https://codeship.com/projects/58995)

## Introduction

While developing a web application with Spring boot as the main framework, I came across the need to add, remove and modify a list of elements in a web page and submit these changes to the server. Although it's a common task for a web framework, I found it to be non-trivial. So, I set out to write a case study to help me organize what I've learnt and maybe help others.

The application resulting from this case study is [running live thanks to Heroku](http://obscure-reef-8002.herokuapp.com/).

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
3. A `Form` class. This is just a `POJO` to hold a _collection_ of `User`s.
4. An `HTML` template using [Thymeleaf](http://www.thymeleaf.org/) to render `@Controller` results.
5. `JavaScript` code using [Backbone](http://backbonejs.org/) to dynamically add or remove table rows.

Note that the first three items in the list above address the _server-side_ aspect of the problem, while the last two refer to the _client-side_.

The following sections will focus on the _server-side_ where the databinding occurs, taking the `POST` request parameters as the starting point, no matter how the _client-side_ managed to produce these. At the end of this article, a section will be devoted to describe the _client-side_.

## Scenarios

This is the list of cases to study:

1. [An empty `List`](#an-empty-list), where the collection of `User`s is actually an empty `java.util.List`. It's worth studying this case for its simplicity and also because there may be times where the collection will only get created once and not edited thereafter.
2. [A non-empty `Map`](#a-non-empty-map), where the collection of `User`s is actually a non-empty `java.util.Map`. The case will start showing the problems with a non-empty `List` and indexed-based databinding, and then overcome these problems using `Map` instead of `List`.
3. [Using `JPA`](#using-jpa), and how to perform databinding when the collection of `User`s is retrieved directly from a `@Repository` using [spring-data-jpa](http://projects.spring.io/spring-data-jpa/) module.

## The _server-side_

## An empty List

Let's start with an empty `List` of `User`s. Adding a new `User` to this `List` means to send a `POST` request to the `@Controller` with the `name` and `email` values for the new user. In order for `Spring` to bind this data, the request parameters should follow the convention described in [section Beans of the Spring Framework documentation](http://docs.spring.io/spring/docs/current/spring-framework-reference/htmlsingle/#beans-beans-conventions). For this case, the `POST` request parameters look like:

```
users[0].name=John
users[0].email=john@mail.com
```

Upon request, Spring will try to bind this data to the `@ModelAttribute` object of type `Form` defined inside the `@Controller` class.

Spring will create an instance of `User` and set the value "John" for property `name` and "john@mail.com" for property `email`. It will then insert this new `User` instance at index 0 in the `users` property of the `form` instance of type `Form`.

Here's the relevant code:

```html
<form method="post">
<input type="hidden" name="users[0].name" value="Mike">
<input type="hidden" name="users[0].email" value="mike@mail.com">
</form>
```

```java
public class User {
	// Getters and Setter ommited for the sake of brevity.
	private String name;

	private String email;
}
```

```
public class Form {

	private List<User> users;

	public List<User> getUsers() {
		return users;
	}

	public void setUsers(List<User> users) {
		this.users = users;
	}

}
```

```java
@Controller
public class DataBindingController {

	private List<User> users;

	@RequestMapping(value = "/", method = RequestMethod.POST)
	public String updateUsers(@ModelAttribute("form") Form form) {
		this.users = form.getUsers();
		return "redirect:/";
	}
	
	@ModelAttribute("form")
	public Form getForm() {
		Form form = new Form();
		form.setUsers(this.users);
		return form;
	}
}
```

And that's it. The method `updateUsers` gets a fully populated `Form` instance by parameter, with all the databinding job done.

However, **this is the most simple scenario**, since the `List` is empty. But:
- What if the `List` already contains items?
- What if the order of the items changes between `GET` and `POST` requests?
- What if the items are removed from the `List` between those two requests?

Let's address these questions.

## A non-empty `Map`

Binding a collection of objects would be as simple as described just before if only the `List` passed to the view on the `GET` request would be empty **and stayed empty** until the databinding process finished processing the `POST` request. This is so because the binding of the objects is done according to the `index` each object is stored in the `List`. If indexes change between `GET` and `POST`, the reference is lost, and the databinder will confuse the objects. 

Let's set an example to illustrate the problem with changing `indexes`. This is a `List` returned by the `GET` request and rendered as a table in the web page

| Index | Name | Email         |
|-------|------|---------------|
| 0     | John | john@mail.com |

Now a new row is added at with data about Lisa at _client-side_.

| Index | Name | Email         |
|-------|------|---------------|
| 0     | John | john@mail.com |
| 1(new)| Lisa | lisa@mail.com |

Just before sending the data above as a `POST` request to the server, the `List` in the _server-side_ is changed, so that Mike also gets added at `index` 1.

| Index | Name | Email         |
|-------|------|---------------|
| 0     | John | john@mail.com |
| 1(new)| Mike | mike@mail.com |

When the `POST` request sends the data about Lisa, it will overwrite Mike as a result of databinding based on `indexes`, and **Mike's data will get lost**:

| Index | Name | Email         |
|-------|------|---------------|
| 0     | John | john@mail.com |
| 1     | Lisa | lisa@mail.com |

Let's try to overcome the problems with `indexes` by introducing an **identifier** for objects of class `User`.

```java
public class User {

	private Long id;
	
	private String name;

	private String email;
	
	// Setters and Getters omitted for the sake of brevity
}
```

In order to leverage identifiers, let's change the type of the users from `List` to `Map`. The `Map` will be populated with exising users storing each at **key=id**.

```java
public class Form {

	private Map<Long, User> users;

	public Map<Long, User> getUsers() {
		return users;
	}

	public void setUsers(Map<Long, User> users) {
		this.users = users;
	}

}
```

Going back to the example, the view will again render this table upon `GET`request, but this time the `id` for John will be 1, which will match with the `key` in the Map.

| Key | Id | Name | Email         |
|-----|----|------|---------------|
| 1   | 1  | John | john@mail.com |

Again, a new row is added with data about Lisa at _client-side_. Since we need to store it with some **key** in the `Map` that won't collide with the existing **ids**, let's choose **negative integers as keys** for new users, taking for granted that **identifiers will always be positive integers**, generated and assigned at _server-side_.

| Key | Id | Name | Email         |
|-----|----|------|---------------|
| 1   | 1  | John | john@mail.com |
| -1  | 1  | Lisa | lisa@mail.com |

Once more, just before sending the data above as a `POST` request to the server, the `Map` at the _server-side_ is changed, so that Mike also gets added with **id=2**, since John already has **id=1**.

| Key | Id | Name | Email         |
|-----|----|------|---------------|
| 1   | 1  | John | john@mail.com |
| 2   | 2  | Mike | mike@mail.com |

When the `POST` request sends the data about Lisa, Spring will create a new `User` object for Lisa and put it at **key=-1** inside the `Map`.

| Key | Id   | Name | Email         |
|-----|------|------|---------------|
| 1   | 1    | John | john@mail.com |
| 2   | 2    | Mike | mike@mail.com |
| -1  | null | Lisa | lisa@mail.com |

Once databinding is done, the _server-side_ will assign Lisa an **id=3**.

### Modifying users

Modifying users is given for free with this setup. The _client-side_ only needs to send the data of the row that's changed. Spring will update the data in the object that's stored in the `Map` at the specific key that matches the object's id. 

For instance, if Mike's email gets changed:

| Key | Id   | Name | Email                 |
|-----|------|------|-----------------------|
| 2   | 2    | Mike | mike_changed@mail.com |

Then the form should submit:

```html
<form method="post">
<input type="hidden" name="users[2].email" value="mike_changed@mail.com">
</form>
```

And that's it!

### Removing users

In order to tell which users are removed, the _client-side_ will set **id=null**, but keeping the **key** value. For instance, if John gets removed:

| Key | Id   | Name | Email         |
|-----|------|------|---------------|
| 1   | null | John | john@foo.bar  |
| 2   | 2    | Mike | mike@mail.com |
| 3   | 3    | Lisa | lisa@mail.com |

The _server-side_ will then remove all instances with null id.

To sum it up, **this is the databinding contract** for `Map`-backed collection of items:

1. **New** items are stored in the `Map` with **negative key values** and **id=null**
2. **Deleted** items are kept in the `Map` with the **same key**, but setting **id=null**
3. **Modified** items are kept in the `Map` with the **same key and same id**, setting the **new values for the modified properties**.

## Using `JPA`

:children_crossing: _work in progress_

## The _client-side_

Now that there's a **databinding contract** in place, let's see how to play by these rules at _client-side_.

We'll be using:

1. [Thymeleaf](http://www.thymeleaf.org/) to render the initial table with the collection of `User`s retrieved from the `Repository`.
2. [Backbone.js](http://backbonejs.org/) to add dynamic capabilities to the table rendered by Thymeleaf to perform CRUD (CReate, Update, Delete) operations and to abide by Spring's databinding contract. Take a look at the [annotated, side-by-side commented Backbone code](http://www.explainjs.com/explain?src=https%3A%2F%2Fraw.githubusercontent.com%2Fmefernandez%2Fspring-backbone-collection-databinding%2Fmaster%2Fsrc%2Fmain%2Fresources%2Fstatic%2Fjs%2Fmap-databinding.js).
