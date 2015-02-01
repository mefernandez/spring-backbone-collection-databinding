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
		render: function() {
			return this.template(this.model.toJSON());
		},
		
	});
	
	var UserTableView = Backbone.View.extend({
		el: $("#user-table-template"),
		key: -1,
		
		events : {
		},
		
		initialize: function() {
			$('#add-new-user-btn').on('click', this.createOnEnter);
			this.listenTo(users, 'add', this.addOne);
		},
		
		render : function() {
		},
		
		createOnEnter : function(e) {
			var name = $('#new-user-form input[name=name]').val();
			var email = $('#new-user-form input[name=email]').val();
			users.create({
				name : name,
				email: email,
				id: app.key
			});
			e.preventDefault();
		},
		

		addOne : function(user) {
			var view = new UserRowView({
				model : user
			});
			this.$el.append(view.render());
			this.key--;
		},
	});
	
	var app = new UserTableView;
	
});