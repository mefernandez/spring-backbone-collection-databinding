/**
 * A dynamic table to perform CRUD operations 
 * with Spring databinding capabilities.
 * In this case, the databinding is performed
 * upon a Map at _server-side_.
 */
$(function(){
	// A prototype to describe the model properties to bind at _server-side_.
	var User = Backbone.Model.extend({
		defaults: function() {
			return {
				name: "",
				email: "",
				// the id of this item
				id: null,
				// key is the position in the Map, which may be the id of this item.
				key: null
			};
		}
	});
	
	// A prototype to hold the collection of model instances. 
	var UserList = Backbone.Collection.extend({
		model: User,
		// Prevents Backbone error: "A url property or function must be specified"
		// since no REST API is present to persist the changes 
		localStorage: new Backbone.LocalStorage("spring-map-databinding")
		
	});
	
	// An instance of the collection.
	var users = new UserList;
	
	// A prototype for the view that renders the model, one for each row of the table
	var UserRowView = Backbone.View.extend({
		// Use `tagName` instead of `el` for the DOM elements does not exist until render is called.
		tagName: "tr",
		template: Handlebars.compile($('#user-row-template').html()),
		events: {
			'click .delete-user-btn': 'deleteUser'
		},
		render: function() {
			var html = this.template(this.model.attributes);
			// WARN: Don't use `this.$el.html(html)`, for it will unbind all events!!!
			this.$el.empty();
			this.$el.append(html);
			return this;
		},
		initialize: function() {
			this.listenTo(this.model, 'change', this.render);
			this.listenTo(this.model, 'destroy', this.remove);
		},
		deleteUser : function(e) {
			var id = this.model.get('id');
			if (id < 0) {
				// It's a newly added User, so let's just destroy the model
				this.model.destroy();
			} else {
				// It's a User retrieved from _server-side_, 
				// so let's keep its `key` on the Map, but set `id=null` 
				this.model.set('id', null);
			}
		},
		removeOne : function(user) {
			var view = new UserRowView({
				model : user
			});
			this.$el.append(view.render());
			this.key--;
		}
	});
	
	var UserTableView = Backbone.View.extend({
		el: $("#user-table-template"),
		key: -1,
		
		events : {
			'click #add-new-user-btn': 'createOnClickAddButton',
		},
		initialize: function() {
			this.addUsersFromTable();
			this.listenTo(users, 'add', this.addOne);
		},
		addUsersFromTable: function() {
			this.$el.find('tbody > tr').each(function(index, row) {
				var id = parseInt($(row).find('td:nth-child(1)').text());
				var name = $(row).find('td:nth-child(2)').text();
				var email = $(row).find('td:nth-child(3)').text();
				var user = users.create({
					name : name,
					email: email,
					id: id,
					key: id
				});
				var view = new UserRowView({
					model : user,
					el: row
				});
			});
		},
		createOnClickAddButton : function(e) {
			var name = $('#new-user-form input[name=name]').val();
			var email = $('#new-user-form input[name=email]').val();
			users.create({
				name : name,
				email: email,
				id: app.key,
				key: app.key
			});
		},
		addOne : function(user) {
			var view = new UserRowView({
				model : user
			});
			view.render();
			this.$el.append(view.el);
			this.key--;
		}
	});
	
	var app = new UserTableView;
	
});