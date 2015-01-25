var gulp = require('gulp');
var series = require('stream-series');
var inject = require("gulp-inject");
var mainBowerFiles = require('main-bower-files');

gulp.task('inject', function() {
	var target = gulp.src('./src/main/resources/templates/index.html');
	
	var vendorStream = gulp.src(['./js/libs/**/*.js'], {read: false, cwd: './src/main/resources/static'});

	var appStream = gulp.src(['./js/*.js'], {read: false, cwd: './src/main/resources/static'});

	// It's not necessary to read the files (will speed up things), we're only
	// after their paths:
	var sources = gulp.src([ './js/**/*.js', './css/**/*.css' ], {
		read : false,
		cwd: './src/main/resources/static'
	});

	return target.pipe(inject(series(vendorStream, appStream), {selfClosingTag: true})).pipe(
			gulp.dest('./src/main/resources/templates'));
});

gulp.task('bower', function() {
	return gulp.src(mainBowerFiles())
			.pipe(gulp.dest('./src/main/resources/static/js/libs'))
});