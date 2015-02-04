$(function(){
	var User = Backbone.Model.extend({
		defaults: function() {
			return {
				name: "",
				email: "",
				id: null
			};
		}
	});
	
	var UserList = Backbone.Collection.extend({
		model: User,
		// Prevents Backbone error A "url" property or function must be specified
		localStorage: new Backbone.LocalStorage("todos-backbone")
		
	});
	
	var users = new UserList;
	
	var UserRowView = Backbone.View.extend({
		tagName: "tr",
		template: Handlebars.compile($('#user-row-template').html()),
		events: {
			'click input': 'deleteUser'
		},
		render: function() {
			var html = this.template(this.model.attributes);
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
			// It's a newly added User, so let's just destroy the model
			if (id < 0) {
				this.model.destroy();
			} else {
				this.model.set('id', -id);
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
			//'click .delete-user-btn': 'deleteExistingUser'
		},
		deleteExistingUser : function(e) {
			var id = $(e.target).attr('data-id');
			// It's a newly added User, so let's just destroy the model
			if (id < 0) {
				this.model.destroy();
			} else {
				var user = users.get(id);
				user.set(id, -id);
			}
		},
		initialize: function() {
			this.addUsersFromTable();
			this.listenTo(users, 'add', this.addOne);
		},
		addUsersFromTable: function() {
			this.$el.find('tbody > tr').each(function(index, row) {
				var id = $(row).find('td:nth-child(1)').text();
				var name = $(row).find('td:nth-child(2)').text();
				var email = $(row).find('td:nth-child(3)').text();
				var user = users.create({
					name : name,
					email: email,
					id: parseInt(id)
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
				id: app.key
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